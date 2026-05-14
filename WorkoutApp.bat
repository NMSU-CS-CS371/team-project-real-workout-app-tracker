@echo off
setlocal

REM Resolve directory of this script
set DIR=%~dp0

REM JavaFX module path
set FX=%DIR%lib\javafx-sdk-21.0.11-win\lib

java ^
  --module-path "%FX%" ^
  --add-modules javafx.controls,javafx.fxml,javafx.swing ^
  -jar "%DIR%WorkoutApp.jar"

endlocal
