#!/bin/bash

# -----------------------------------------
#  JavaFX App Launcher for workoutApp.jar
# -----------------------------------------

# Resolve directory of this script
DIR="$(cd "$(dirname "$0")" && pwd)"

# Path to JavaFX SDK inside the project
FX="$DIR/lib/javafx-sdk-21.0.11/lib"

# Path to your JAR
APP="$DIR/WorkoutApp.jar"

# --- Checks --------------------------------------------------

# Check Java
if ! command -v java >/dev/null 2>&1; then
  echo "Error: Java is not installed or not in PATH."
  echo "Install OpenJDK 21 (or higher) and try again."
  exit 1
fi

# Check JavaFX directory
if [ ! -d "$FX" ]; then
  echo "Error: JavaFX not found at:"
  echo "  $FX"
  echo "Make sure javafx-sdk-21.0.11 is inside ./lib/"
  exit 1
fi

# Check JAR file
if [ ! -f "$APP" ]; then
  echo "Error: workoutApp.jar not found in:"
  echo "  $DIR"
  exit 1
fi

# --- Run the application -------------------------------------

java \
  --module-path "$FX" \
  --add-modules javafx.controls,javafx.fxml,javafx.swing \
  -jar "$APP"

