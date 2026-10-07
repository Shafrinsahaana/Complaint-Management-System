@echo off
echo Cleaning bin directory...
if exist bin rmdir /s /q bin
mkdir bin

echo Compiling Java source files...
dir /s /B src\*.java > sources.txt
javac -d bin -cp "lib/ojdbc11.jar;lib/flatlaf-3.2.5.jar" @sources.txt
del sources.txt

echo Build complete!
