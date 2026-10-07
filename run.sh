#!/bin/bash
echo "Starting CivicDesk..."
java --enable-native-access=ALL-UNNAMED -cp "bin:lib/ojdbc11.jar:lib/flatlaf-3.2.5.jar" ui.Main
