module Typechecker.Typechecker where

import           Control.Applicative
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
import           Exceptions
import           Janeklang.Abs
import           Janeklang.ErrM
import           Janeklang.Print
import           Typechecker.Utils

ensureTypeExpr :: Expr -> Type -> TypeCheckerMonad ()
ensureTypeExpr expr expectedType = do
  let pos = hasPosition expr
  exprType <- typeCheckExpr expr
  unless (typesEqual exprType expectedType) $
    throwError (TypeMismatch exprType expectedType pos)

typeCheck :: Program -> Either TypecheckingException ()
typeCheck (PProg _ topDefs) = runExcept $ evalStateT (typeCheckTopDefs topDefs) (Context Map.empty False Nothing False)

typeCheckTopDefs :: [TopDef] -> TypeCheckerMonad ()
typeCheckTopDefs topDefs = do
  mapM_ handleTopDef topDefs

handleTopDef :: TopDef -> TypeCheckerMonad ()
handleTopDef topDef = case topDef of
  PFnDef pos retType ident args block -> do
    argTypes <- mapM extractArgType args
    modifyContext (Map.insert ident (FuncType argTypes retType))
    oldContext <- get
    let newEnv = foldr (\(arg, t) acc -> Map.insert arg (VarType t) acc) (env oldContext) (zip (map argIdent args) argTypes)
    put oldContext {env = newEnv, returnType = Just retType, hasReturn = False}
    typeCheckBlock block
    newContext <- get
    put oldContext
    unless (hasReturn newContext || isVoid retType) $
      throwError $
        FunctionWithoutReturn ident pos
  PVarDef pos declaredType varIdent expr -> do
    ensureTypeExpr expr declaredType
    modifyContext (Map.insert varIdent (VarType declaredType))

typeCheckBlock :: Block -> TypeCheckerMonad ()
typeCheckBlock (SBlk _ stmts) = mapM_ typeCheckStmt stmts

typeCheckStmt :: Stmt -> TypeCheckerMonad ()
typeCheckStmt stmt = case stmt of
  SEmpty _ -> return ()
  SBStmt _ block -> typeCheckBlock block
  SAss pos ident expr -> do
    expectedType <- getTypeFromEnv ident pos
    ensureTypeExpr expr expectedType
  SRet pos expr -> do
    context <- get
    case returnType context of
      Just retType -> do
        ensureTypeExpr expr retType
        modify (\ctx -> ctx {hasReturn = True})
      Nothing -> throwError $ ReturnNotWithinFunctionError pos
  SVRet pos -> do
    context <- get
    case returnType context of
      Just (TVoid _) -> do
        modify (\ctx -> ctx {hasReturn = True})
        return ()
      Just _ -> throwError $ VoidReturnInNonVoidFunction pos
      Nothing -> throwError $ ReturnNotWithinFunctionError pos
  SCond pos expr block -> do
    ensureTypeExpr expr (TBool undefined)
    withSavedEnv $ typeCheckBlock block
  SCondElse pos expr block1 block2 -> do
    ensureTypeExpr expr (TBool undefined)
    withSavedEnv $ typeCheckBlock block1
    withSavedEnv $ typeCheckBlock block2
  SWhile pos expr block -> do
    ensureTypeExpr expr (TBool undefined)
    modify (\ctx -> ctx {isInLoop = True})
    withSavedEnv $ typeCheckBlock block
    modify (\ctx -> ctx {isInLoop = False})
  SExp pos expr -> do
    void $ typeCheckExpr expr
  SNestedFnDef pos topDef -> handleTopDef topDef
  SBreak pos -> do
    context <- get
    unless (isInLoop context) $
      throwError $
        BreakNotWithinLoopError pos
  SContinue pos -> do
    context <- get
    unless (isInLoop context) $
      throwError $
        ContinueNotWithinLoopError pos
  SPrint pos expr -> do
    void $ typeCheckExpr expr

typeCheckExpr :: Expr -> TypeCheckerMonad Type
typeCheckExpr expr = case expr of
  EVar pos ident ->
    getTypeFromEnv ident pos
  ELitInt pos _ ->
    return (TInt pos)
  ELitTrue pos ->
    return (TBool pos)
  ELitFalse pos ->
    return (TBool pos)
  EString pos _ ->
    return (TStr pos)
  ENeg pos expr ->
    checkUnaryOperation pos expr (TInt pos)
  ENot pos expr ->
    checkUnaryOperation pos expr (TBool pos)
  EMul pos expr1 _ expr2 ->
    checkBinaryOperation pos expr1 expr2 (TInt pos) (TInt pos)
  EAdd pos expr1 _ expr2 ->
    checkBinaryOperation pos expr1 expr2 (TInt pos) (TInt pos)
  ERel pos expr1 op expr2 -> do
    checkBinaryOperation pos expr1 expr2 (TInt pos) (TBool pos)
  EAnd pos expr1 expr2 ->
    checkBinaryOperation pos expr1 expr2 (TBool pos) (TBool pos)
  EOr pos expr1 expr2 ->
    checkBinaryOperation pos expr1 expr2 (TBool pos) (TBool pos)
  EApp pos ident args ->
    handleFunctionApplication pos ident args

checkUnaryOperation :: BNFC'Position -> Expr -> Type -> TypeCheckerMonad Type
checkUnaryOperation pos expr expectedType = do
  exprType <- typeCheckExpr expr
  if typesEqual exprType expectedType
    then return expectedType
    else throwError $ TypeMismatch exprType expectedType pos

checkBinaryOperation :: BNFC'Position -> Expr -> Expr -> Type -> Type -> TypeCheckerMonad Type
checkBinaryOperation pos expr1 expr2 expectedType returnType = do
  exprType1 <- typeCheckExpr expr1
  exprType2 <- typeCheckExpr expr2
  if typesEqual exprType1 expectedType && typesEqual exprType2 expectedType
    then return returnType
    else throwError $ TypeMismatch exprType1 expectedType pos

handleFunctionApplication :: BNFC'Position -> Ident -> [Expr] -> TypeCheckerMonad Type
handleFunctionApplication pos ident args = do
  env <- gets env
  case Map.lookup ident env of
    Just (FuncType paramTypes returnType) -> do
      when (length args /= length paramTypes) $
        throwError $
          IncorrectArgumentCount ident (length paramTypes) (length args) pos
      argTypes <- mapM typeCheckExpr args
      let typeMatches = zipWith typesEqual argTypes paramTypes
      unless (and typeMatches) $
        throwError $
          TypeMismatch (head argTypes) (head paramTypes) pos
      return returnType
    Just _ ->
      throwError $ FunctionAsVariable ident pos
    Nothing ->
      throwError $ IdentifierNotFound ident pos
