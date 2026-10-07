#!/bin/bash
echo "Cleaning bin directory..."
rm -rf bin
mkdir -p bin

echo "Compiling Java source files..."
find src -name "*.java" > sources.txt
javac -d bin -cp "lib/ojdbc11.jar:lib/flatlaf-3.2.5.jar" @sources.txt
rm sources.txt

echo "Build complete!"
