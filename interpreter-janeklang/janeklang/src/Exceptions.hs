module Exceptions where

import           Data.List     (intercalate)
import           Janeklang.Abs

data TypecheckingException
    = FunctionAsVariable Ident (Maybe (Int, Int))
    | IdentifierNotFound Ident (Maybe (Int, Int))
    | TypeMismatch {
        foundType    :: Type,
        expectedType :: Type,
        positionInfo :: Maybe (Int, Int)
        }
    | VoidReturnInNonVoidFunction (Maybe (Int, Int))
    | NonVoidReturnInNonVoidFunction (Maybe (Int, Int))
    | IncorrectArgumentCount Ident Int Int (Maybe (Int, Int))
    | BreakNotWithinLoopError (Maybe (Int, Int))
    | ContinueNotWithinLoopError (Maybe (Int, Int))
    | ReturnNotWithinFunctionError (Maybe (Int, Int))
    | FunctionWithoutReturn Ident (Maybe (Int, Int))
    | OtherError String (Maybe (Int, Int))
    | EmptyError

instance Show TypecheckingException where
    show (FunctionAsVariable ident pos) =
        "Identifier " ++ showIdent ident ++ " is a function, cannot assign at " ++ showPosition pos ++ "."
    show (IdentifierNotFound ident pos) =
        "Identifier " ++ showIdent ident ++ " not found in the environment at " ++ showPosition pos ++ "."
    show (TypeMismatch found expected pos) =
        "Type mismatch " ++ showPosition pos ++ ": Expected type " ++ showType expected ++ ", but found " ++ showType found
    show (VoidReturnInNonVoidFunction pos) =
        "Void return in non-void function " ++ showPosition pos ++ "."
    show (IncorrectArgumentCount func expected provided pos) =
        "Function " ++ showIdent func ++ " called with incorrect number of arguments: expected " ++ show expected ++ ", but got " ++ show provided
    show (BreakNotWithinLoopError pos) = "Break statement not within a loop at " ++ showPosition pos ++ "."
    show (ContinueNotWithinLoopError pos) = "Continue statement not within a loop at " ++ showPosition pos ++ "."
    show (ReturnNotWithinFunctionError pos) = "Return statement not within a function at " ++ showPosition pos ++ "."
    show (FunctionWithoutReturn ident pos) = "Function " ++ showIdent ident ++ " does not have a return statement at " ++ showPosition pos ++ "."
    show (OtherError msg pos) = msg ++  " at " ++ showPosition pos ++ "."
    show EmptyError = "An unexpected error occurred, which should not happen."

data EvaluatorException
    = EOtherError String (Maybe (Int, Int))
    | EEmptyError
    | EMainNotFound
    | EMainWithArgs
    | EDivisionByZero (Maybe (Int, Int))
    | EFunctionNoReturn Ident (Maybe (Int, Int))
    | EMultipleMains

instance Show EvaluatorException where
    show (EOtherError msg pos) = msg ++ " at " ++ showPosition pos ++ "."
    show EEmptyError = "An unexpected error occurred, which should not happen."
    show EMainNotFound = "Main function not found."
    show EMainWithArgs = "Main function should not take arguments."
    show (EDivisionByZero pos) = "Division by zero at " ++ showPosition pos ++ "."
    show (EFunctionNoReturn ident pos) = "Function " ++ showIdent ident ++ " did not return a value at " ++ showPosition pos ++ "."
    show EMultipleMains = "Multiple mains found."

showPosition :: Maybe (Int, Int) -> String
showPosition Nothing = "at an unknown position"
showPosition (Just (line, col)) = "at position line " ++ show line ++ ", column " ++ show col

showIdent :: Ident -> String
showIdent (Ident name) = name

showArg :: Arg -> String
showArg (PAr pos typ ident) = showIdent ident ++ ": " ++ showType typ
showArg (PArRef pos typ ident) = "ref " ++ showIdent ident ++ ": " ++ showType typ

showType :: Type -> String
showType (TInt pos) = "int"
showType (TStr pos) = "string"
showType (TBool pos) = "boolean"
showType (TVoid pos) = "void"
showType (Fun pos returnType args) =
    "function(" ++ intercalate ", " (map showType args) ++ ") -> " ++ showType returnType
