@echo off
echo Starting Tetris Game...
echo.

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 21 or later
    pause
    exit /b 1
)

REM Try to run with Maven
echo Trying to run with Maven...
call mvnw.cmd javafx:run

if %errorlevel% neq 0 (
    echo Maven run failed, trying direct Java execution...
    echo Please check your Java and JavaFX installation
    pause
    exit /b 1
)

echo Game started successfully!
pause
