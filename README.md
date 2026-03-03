# team-project-real-workout-app-tracker
## Workout Tracker - CSCI 3710 Team Project
Lightweight, offline, java-based workout tracking application focused on routine creation, simple exercise logging, and local JSON-based data storage.

## Project Summary
This product provides a minimal, distraction-free workout tracker allowing uwsers to create routines, define exercises, log workouts, and save/load data locally using JSON. Goal is to support quick, reliable workout tracking without accounts, servers, or network requirements.

# Repository Structure
project-root/
|

|---src/com/workoutapp/            Source Code Files

|   |--- models/        Core domain classes

|   |--- services/      File I/O, JSON parsing, etc.

|   |--- ui/            Future UI classes

|   |--- app/           Main.java

|

|---sec/com/workoutapptests/          Junit Test Classes

|   |---models/

|   |---services/

|

|---data/           JSON Data Files

|   |---

|

|---libs/           External Libraries (JUnit, etc)

|   |---junit jar file

|

|---docs/           Documents not on wiki

|

|---.gitignore

|---.README.md

# Data Files
Example JSON data files for testing

# Build & Run
### Prequisites
- JDK 17 or above
- Apache Ant 1.10.x or newer (1.10.12 reccommended)
- src/com/workoutapp contains Java source files
- data/ contains *.json data files
- lib/ contains junit-platform-console-standalone-6.1.0-M1.jar
- tests/ contains files ending in *Test.java for JUnit tests

### Build the Project
Type into terminal
ant compile

This will:
- Clean previous build directory
- Compiles source files into build/
- Compile all Java files using JDK >= 17

### Running Tests
Make sure lib/ contains junit-platform-console-standalone-6.1.0-M1.jar

You can download from Maven Repository Site at
[https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/6.1.0-M1/](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/6.1.0-M1/)

Type into terminal:
    ant test

# Documentation
All sprint planning, user stories, and design documentation is maintained on GitHub Wiki
