@echo off
chcp 65001 >nul
cd /d "%~dp0"
if not exist out mkdir out
javac -encoding UTF-8 -d out src\ru\edu\pr05\*.java
if errorlevel 1 exit /b 1
java -cp out ru.edu.pr05.Main
