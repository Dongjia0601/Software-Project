@echo off
echo Starting Tetris Game with IDE configuration...
echo.

REM Set Maven repository path
set MAVEN_REPO=%USERPROFILE%\.m2\repository

REM Set JavaFX module path
set JAVAFX_MODULE_PATH=%MAVEN_REPO%\org\openjfx\javafx-controls\21.0.6\javafx-controls-21.0.6-win.jar;%MAVEN_REPO%\org\openjfx\javafx-fxml\21.0.6\javafx-fxml-21.0.6-win.jar;%MAVEN_REPO%\org\openjfx\javafx-graphics\21.0.6\javafx-graphics-21.0.6-win.jar

REM Try to run with Maven using IDE configuration
echo Trying to run with Maven using IDE configuration...
call mvnw.cmd clean compile

if %errorlevel% neq 0 (
    echo Maven compile failed
    pause
    exit /b 1
)

echo Compilation successful!
echo.

REM Try to run with JavaFX modules
echo Trying to run with JavaFX modules...
call mvnw.cmd javafx:run

if %errorlevel% neq 0 (
    echo JavaFX run failed
    pause
    exit /b 1
)

echo Game started successfully!
pause
