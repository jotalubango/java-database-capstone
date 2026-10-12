@echo off
REM  ---------------------------------------------------------------------------------
REM  Descrição: Load prescriptions into mongodb
REM  Autor J
REM  Data: 06/10/2026
REM  ---------------------------------------------------------------------------------

REM Charset UTF-8
chcp 65001 > nul

set mongosh=C:\Users\jribeiro\bin\mongosh-2.9.2-win32-x64\bin\mongosh.exe

echo %mongosh% "mongodb://127.0.0.1:27017/prescriptions?authSource=admin" prescriptions.js
%mongosh% "mongodb://127.0.0.1:27017/prescriptions?authSource=admin" prescriptions.js

pause

