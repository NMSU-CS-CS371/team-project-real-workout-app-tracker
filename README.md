# Workout Tracker - CSCI 3710 Team Project
Lightweight, offline, java-based workout tracking application focused on routine creation, simple exercise logging, and local JSON-based data storage.

## Project Summary

This product provides a minimal, distraction-free workout tracker allowing uwsers to create routines, define exercises, log workouts, and save/load data locally using JSON. Goal is to support quick, reliable workout tracking without accounts, servers, or network requirements.

Lightweight, offline, java-based workout tracking application focused on routine creation, simple exercise logging, and local binary-based data storage.


# Repository Structure
project-root/
|

|---src/com/workoutapp/            Source Code Files

|   |--- app/           Main.java

|   |--- controllers/   Controllers for UI 

|   |--- models/        Core domain classes

|   |--- services/      File I/O, JSON parsing and object management classes

|   |--- views/         FXML files for UI views

|

|---src/com/workoutapptests/          Junit Test Classes

|   |---models/        Test classes in models folder

|   |---services/        Test classes in services folder

|

|---data/           Serialized JSON Data Files

|   |--- <profileName>/     JSON files for each profile - routines, exercises, events

|   |---profiles.json       Stores list of profiles

|   |---defaultExercises.json      Default list of exercises

|

|---lib/           External Libraries (JUnit, GSON, etc)

|   |---junit-platform-console-standalone-6.1.0-M1.jar

|   |---gson-2.13.2.jar
    
|---build.xml       Ant build file with commands

|---.gitignore

|---.README.md

# Build & Run

Building and running program is done via Apache Ant using build.xml. 

### Ant commands
- **ant** defaults to *run*
  **ant clean** deletes old build directory and other junk files
- **ant compile** cleans build directory and compiles source code into build/classes
- **ant run** compiles and runs application
- **ant test** runs JUnit tests based on files in src/com/workoutapptests/

### Prequisites
- JDK 17 or above
- Apache Ant 1.10.x or newer (1.10.12 reccommended)
- src/com/workoutapp contains Java source files
- data/ contains *.json data files
- lib/ contains junit-platform-console-standalone-6.1.0-M1.jar
- tests/ contains files ending in *Test.java for JUnit tests

### Build Project
Through the command line, navigate to the project root "/team-project-real-workout-app-tracker"

Enter:
    **ant compile**

This will:
- Clean previous build directory
- Compiles source files into build/
- Compile all Java files using JDK >= 17

### Run Project
Type into terminal:
    **ant run**

### Running Tests
Make sure lib/ contains junit-platform-console-standalone-6.1.0-M1.jar

You can download from Maven Repository Site at
[https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/6.1.0-M1/](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/6.1.0-M1/)

Type into terminal:
    **ant test**

Tests can be found in src/com/workoutapptests/../*Test.java.

# Documentation
All sprint planning, user stories, and design documentation is maintained on GitHub Wiki
