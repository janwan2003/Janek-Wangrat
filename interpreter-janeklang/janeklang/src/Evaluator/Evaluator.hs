module Evaluator.Evaluator where

import           Control.Applicative
import           Control.Applicative.Lift  (Lift (Other))
import           Control.Monad
import           Control.Monad.Except
import           Control.Monad.Trans
import           Control.Monad.Trans.State (StateT, evalStateT, get, gets,
                                            modify, put)
import           Data.Functor
import           Data.List
import           Data.Map                  (Map)
import qualified Data.Map                  as Map
import           Data.Maybe
import           Data.Sequence             (Seq (Empty))
import           Evaluator.Utils
import           Exceptions
import           Janeklang.Abs
import           Janeklang.ErrM
import           Janeklang.Print


evalProgram :: Program -> IO (Either EvaluatorException ())
evalProgram (PProg _ topDefs) = runExceptT $ evalStateT (executeMain topDefs) initialContext

executeMain :: [TopDef] -> EvalMonad ()
executeMain topDefs = do
  mainCount <- countMainFunctions topDefs
  when (mainCount > 1) $ throwError EMultipleMains
  mapM_ evalTopDef topDefs
  env <- gets env
  case Map.lookup (Ident "main") (_env env) of
    Just loc -> do
      store <- gets store
      case getStoreEntry loc store of
        FuncValue args block -> if null args then evalBlock block else throwError EMainWithArgs
        _ -> throwError EMainNotFound
    _ -> throwError EMainNotFound

countMainFunctions :: [TopDef] -> EvalMonad Int
countMainFunctions = return . length . filter isMainFunction
  where
    isMainFunction (PFnDef _ _ (Ident "main") _ _) = True
    isMainFunction _                               = False

evalTopDef :: TopDef -> EvalMonad ()
evalTopDef (PFnDef _ _ ident args block) = do
  (loc, newStore) <- gets store <&> allocStoreLocation
  modify $ \ctx -> ctx { store = putStoreEntry loc (FuncValue args block) newStore }
  modify $ \ctx -> ctx { env = putEnvLocation ident loc (env ctx) }
evalTopDef (PVarDef _ _ ident expr) = do
  val <- evalExpr expr
  (loc, newStore) <- gets store <&> allocStoreLocation
  modify $ \ctx -> ctx { store = putStoreEntry loc (VarValue val) newStore }
  modify $ \ctx -> ctx { env = putEnvLocation ident loc (env ctx) }

evalBlock :: Block -> EvalMonad ()
evalBlock (SBlk _ stmts) = mapM_ evalStmt stmts

evalStmt :: Stmt -> EvalMonad ()
evalStmt stmt = do
  ctx <- get
  unless (isJust (returnValue ctx) || breakLoop ctx) $ case stmt of
    SEmpty _ -> return ()
    SBStmt _ block -> evalBlock block
    SAss _ ident expr -> do
      val <- evalExpr expr
      loc <- gets (getEnvLocation ident . env)
      modify $ \ctx -> ctx { store = putStoreEntry loc (VarValue val) (store ctx) }
    SRet _ expr -> evalExpr expr >>= \val -> modify $ \ctx -> ctx { returnValue = Just val }
    SVRet _ -> modify $ \ctx -> ctx { returnValue = Just VVoid }
    SPrint _ expr -> evalExpr expr >>= liftIO . printValue
    SCond _ expr block -> evalExpr expr >>= \(VBool cond) -> when cond $ evalBlock block
    SCondElse _ expr block1 block2 -> evalExpr expr >>= \(VBool cond) -> if cond then evalBlock block1 else evalBlock block2
    SWhile _ expr block -> loop expr block
    SExp _ expr -> void $ evalExpr expr
    SNestedFnDef _ topDef -> evalTopDef topDef
    SBreak _ -> modify $ \ctx -> ctx { breakLoop = True }
    SContinue _ -> modify $ \ctx -> ctx { continueLoop = True }
  where
    loop e b = do
      VBool cond <- evalExpr e
      when cond $ do
        evalBlock b
        ctx <- get
        if continueLoop ctx
          then do
            modify $ \ctx -> ctx { continueLoop = False }
            loop e b
          else unless (breakLoop ctx || isJust (returnValue ctx)) $ loop e b
        modify $ \ctx -> ctx { breakLoop = False }

printValue :: Value -> IO ()
printValue value = case value of
  VInt n    -> print n
  VBool b   -> print b
  VString s -> putStrLn s
  VVoid     -> putStrLn "!void!"

evalExpr :: Expr -> EvalMonad Value
evalExpr expr = case expr of
  EVar pos ident -> do
    loc <- gets (getEnvLocation ident . env)
    entry <- gets (getStoreEntry loc . store)
    case entry of
      VarValue val -> return val
      _ -> throwError $ EOtherError "Expected variable, got function" pos
  ELitInt _ int -> return $ VInt int
  ELitTrue _ -> return $ VBool True
  ELitFalse _ -> return $ VBool False
  EString _ str -> return $ VString str
  ENeg _ subExpr -> do
    VInt val <- evalExpr subExpr
    return $ VInt (-val)
  ENot _ subExpr -> do
    VBool val <- evalExpr subExpr
    return $ VBool (not val)
  EMul pos expr1 op expr2 -> evalBinaryOp pos expr1 expr2 (applyMulOp op)
  EAdd pos expr1 op expr2 -> evalBinaryOp pos expr1 expr2 (applyAddOp op)
  ERel pos expr1 op expr2 -> evalBinaryOp pos expr1 expr2 (applyRelOp op)
  EAnd pos expr1 expr2 -> evalBinaryOp pos expr1 expr2 applyAndOp
  EOr pos expr1 expr2 -> evalBinaryOp pos expr1 expr2 applyOrOp
  EApp pos ident args -> do
    loc <- gets (getEnvLocation ident . env)
    entry <- gets (getStoreEntry loc . store)
    case entry of
      FuncValue params block -> applyFunction ident pos (FuncValue params block) args
      _ -> throwError $ EOtherError "Expected function, got variable" pos

evalBinaryOp :: BNFC'Position -> Expr -> Expr -> (BNFC'Position -> Value -> Value -> EvalMonad Value) -> EvalMonad Value
evalBinaryOp pos expr1 expr2 op = do
  val1 <- evalExpr expr1
  val2 <- evalExpr expr2
  op pos val1 val2

applyMulOp :: MulOp -> BNFC'Position -> Value -> Value -> EvalMonad Value
applyMulOp op pos (VInt val1) (VInt val2) = case op of
  OTimes _ -> return $ VInt (val1 * val2)
  ODiv _ -> if val2 == 0 then throwError $ EDivisionByZero pos else return $ VInt (val1 `div` val2)
  OMod _ -> if val2 == 0 then throwError $ EDivisionByZero pos else return $ VInt (val1 `mod` val2)
applyMulOp _ _ _ _ = throwError EEmptyError

applyAddOp :: AddOp -> BNFC'Position -> Value -> Value -> EvalMonad Value
applyAddOp op pos (VInt val1) (VInt val2) = case op of
  OPlus _  -> return $ VInt (val1 + val2)
  OMinus _ -> return $ VInt (val1 - val2)
applyAddOp _ _ _ _ = throwError EEmptyError

applyRelOp :: RelOp -> BNFC'Position -> Value -> Value -> EvalMonad Value
applyRelOp op pos (VInt val1) (VInt val2) = return $ VBool $ case op of
  OLTH _ -> val1 < val2
  OLE _  -> val1 <= val2
  OGTH _ -> val1 > val2
  OGE _  -> val1 >= val2
  OEQU _ -> val1 == val2
  ONE _  -> val1 /= val2
applyRelOp _ _ _ _ = throwError EEmptyError

applyAndOp :: BNFC'Position -> Value -> Value -> EvalMonad Value
applyAndOp pos (VBool val1) (VBool val2) = return $ VBool (val1 && val2)
applyAndOp _ _ _                         = throwError EEmptyError

applyOrOp :: BNFC'Position -> Value -> Value -> EvalMonad Value
applyOrOp pos (VBool val1) (VBool val2) = return $ VBool (val1 || val2)
applyOrOp _ _ _                         = throwError EEmptyError

applyFunction :: Ident -> BNFC'Position -> EnvEntry -> [Expr] -> EvalMonad Value
applyFunction ident pos (FuncValue params block) argExprs = do
  oldEnv <- gets env
  oldStore <- gets store
  oldReturnValue <- gets returnValue
  (newEnv, newStore) <- setupFunctionEnv oldEnv oldStore params argExprs
  modify $ \ctx -> ctx { env = newEnv, store = newStore, returnValue = Nothing }
  evalBlock block
  result <- gets returnValue
  modify $ \ctx -> ctx { env = oldEnv, returnValue = oldReturnValue }
  case result of
    Just val -> return val
    Nothing  -> throwError $ EFunctionNoReturn ident pos

setupFunctionEnv :: Env -> Store -> [Arg] -> [Expr] -> EvalMonad (Env, Store)
setupFunctionEnv initialEnv initialStore params argExprs = do
  let newBindings = zip params argExprs
  foldM allocateParam (initialEnv, initialStore) newBindings
  where
    allocateParam :: (Env, Store) -> (Arg, Expr) -> EvalMonad (Env, Store)
    allocateParam (env, store) (param, expr) = case param of
      PAr _ _ ident -> do
        val <- evalExpr expr
        let (loc, newStore) = allocStoreLocation store
        let updatedStore = putStoreEntry loc (VarValue val) newStore
        let updatedEnv = putEnvLocation ident loc env
        return (updatedEnv, updatedStore)
      PArRef _ _ ident -> case expr of
        EVar _ refIdent -> do
          let loc = getEnvLocation refIdent env
          let updatedEnv = putEnvLocation ident loc env
          return (updatedEnv, store)
        _ -> do
          val <- evalExpr expr
          let (loc, newStore) = allocStoreLocation store
          let updatedStore = putStoreEntry loc (VarValue val) newStore
          let updatedEnv = putEnvLocation ident loc env
          return (updatedEnv, updatedStore)