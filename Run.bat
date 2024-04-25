@echo off
make
start cmd /k "java Server"
cd automation
start cmd /k "Client1.bat"
start cmd /k "Client2.bat"
start cmd /k "Client3.bat"