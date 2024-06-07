module Interpreter where

import           Control.Monad           (when, (>=>))
import           Prelude                 (Either (..), FilePath, IO, Show,
                                          String, getContents, putStrLn,
                                          readFile, unlines, ($), (>>=))
import           System.Environment      (getArgs)
import           System.Exit             (exitFailure, exitSuccess)
import           System.IO               (hPrint, stderr)

import           Evaluator.Evaluator     (evalProgram)
import           Janeklang.Abs           (Program)
import           Janeklang.Par           (myLexer, pProgram)
import           Typechecker.Typechecker (typeCheck)

interpretFile :: FilePath -> IO ()
interpretFile = readFile >=> interpret

interpret :: String -> IO ()
interpret input =
  case pProgram (myLexer input) of
    Left parseErr -> printErrorAndExit parseErr
    Right parsedProgram ->
      case typeCheck parsedProgram of
        Left typeErr -> printErrorAndExit typeErr
        Right _ -> do
          executionResult <- evalProgram parsedProgram
          case executionResult of
            Left evalErr -> printErrorAndExit evalErr
            Right _      -> exitSuccess

printErrorAndExit :: Show e => e -> IO ()
printErrorAndExit err = do
  hPrint stderr err
  exitFailure
