{-# LANGUAGE RecordWildCards #-}

module Evaluator.Utils where

import           Control.Monad.Except
import           Control.Monad.Trans.State
import           Data.Map                  (Map)
import qualified Data.Map                  as Map
import           Data.Maybe
import           Exceptions
import           Janeklang.Abs

type Location = Int

newtype Env = Env
  { _env :: Map Ident Location
  }

data Store = Store
  { _store        :: Map Location EnvEntry
  , _lastLocation :: Int
  }

data Context = Context
  { env          :: Env,
    store        :: Store,
    returnValue  :: Maybe Value,
    breakLoop    :: Bool,
    continueLoop :: Bool
  }

type EvalMonad a = StateT Context (ExceptT EvaluatorException IO) a

data EnvEntry = VarValue Value
              | FuncValue [Arg] Block
    deriving (Show, Eq)

data Value = VInt Integer | VBool Bool | VString String | VVoid
    deriving (Show, Eq)

argIdent :: Arg -> Ident
argIdent (PAr _ _ ident)    = ident
argIdent (PArRef _ _ ident) = ident

withSavedEnv :: EvalMonad a -> EvalMonad a
withSavedEnv action = do
  oldEnv <- get
  result <- action
  put oldEnv
  return result

emptyEnv :: Env
emptyEnv = Env { _env = Map.empty }

emptyStore :: Store
emptyStore = Store { _store = Map.empty, _lastLocation = 0 }

initialContext :: Context
initialContext = Context { env = emptyEnv, store = emptyStore, returnValue = Nothing, breakLoop = False, continueLoop = False }

getEnvLocation :: Ident -> Env -> Location
getEnvLocation name Env{..} = fromJust $ Map.lookup name _env

putEnvLocation :: Ident -> Location -> Env -> Env
putEnvLocation name location Env{..} = Env { _env = Map.insert name location _env }

getStoreEntry :: Location -> Store -> EnvEntry
getStoreEntry loc Store{..} = fromJust $ Map.lookup loc _store

putStoreEntry :: Location -> EnvEntry -> Store -> Store
putStoreEntry loc entry store@Store{..} = store { _store = Map.insert loc entry _store }

allocStoreLocation :: Store -> (Location, Store)
allocStoreLocation store@Store{..} =
  let newLoc = _lastLocation + 1
  in (newLoc, store { _lastLocation = newLoc })
