#!/bin/bash

# Resolve directory of this script
DIR="$(cd "$(dirname "$0")" && pwd)"

# JavaFX module path
FX="$DIR/lib/javafx-sdk-21.0.11/lib"

java \
  --module-path "$FX" \
  --add-modules javafx.controls,javafx.fxml,javafx.swing \
  -jar "$DIR/workoutApp.jar"
