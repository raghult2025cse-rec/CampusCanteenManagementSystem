@echo off
title Campus Canteen Management System
echo Starting Campus Canteen Management System...
where mvn >nul 2>nul
if errorlevel 1 (
  echo Maven was not found. Install Maven and add it to PATH.
  pause
  exit /b 1
)
call mvn clean compile exec:java
pause
