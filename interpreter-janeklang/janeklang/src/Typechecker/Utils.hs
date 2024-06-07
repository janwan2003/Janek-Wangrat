module Typechecker.Utils where

import           Control.Monad             (unless)
import           Control.Monad.Except
import           Control.Monad.Trans.State (StateT, get, gets, modify, put)
import           Data.Map                  (Map)
import qualified Data.Map                  as Map
import           Exceptions
import           Janeklang.Abs

data EnvEntry = VarType Type | FuncType [Type] Type

data Context = Context {
    env        :: TypeEnv,
    isInLoop   :: Bool,
    returnType :: Maybe Type,
    hasReturn  :: Bool
}

type TypeEnv = Map Ident EnvEntry

type TypeCheckerMonad a = StateT Context (Except TypecheckingException) a

isVoid :: Type -> Bool
isVoid (TVoid _) = True
isVoid _         = False

modifyContext :: (TypeEnv -> TypeEnv) -> TypeCheckerMonad ()
modifyContext f = modify $ \ctx -> ctx { env = f (env ctx) }

getTypeFromEnv :: Ident -> Maybe (Int, Int) -> TypeCheckerMonad Type
getTypeFromEnv ident pos = do
  env <- gets env
  case Map.lookup ident env of
    Just (VarType t)    -> return t
    Just (FuncType _ _) -> throwError $ FunctionAsVariable ident pos
    Nothing             -> throwError $ IdentifierNotFound ident pos

typesEqual :: Type -> Type -> Bool
typesEqual (TInt _) (TInt _)   = True
typesEqual (TStr _) (TStr _)   = True
typesEqual (TBool _) (TBool _) = True
typesEqual (TVoid _) (TVoid _) = True
typesEqual _ _                 = False

withSavedEnv :: TypeCheckerMonad a -> TypeCheckerMonad a
withSavedEnv action = do
    oldEnv <- get
    result <- action
    ret <- gets hasReturn
    put oldEnv { hasReturn = ret }
    return result

argIdent :: Arg -> Ident
argIdent (PAr _ _ ident)    = ident
argIdent (PArRef _ _ ident) = ident

extractArgType :: Arg -> TypeCheckerMonad Type
extractArgType (PAr _ t _)    = return t
extractArgType (PArRef _ t _) = return t