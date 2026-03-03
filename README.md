# team-project-real-workout-app-tracker
## Workout Tracker - CSCI 3710 Team Project
<<<<<<< HEAD
Lightweight, offline, java-based workout tracking application focused on routine creation, simple exercise logging, and local JSON-based data storage.

## Project Summary
This product provides a minimal, distraction-free workout tracker allowing uwsers to create routines, define exercises, log workouts, and save/load data locally using JSON. Goal is to support quick, reliable workout tracking without accounts, servers, or network requirements.
=======
Lightweight, offline, java-based workout tracking application focused on routine creation, simple exercise logging, and local binary-based data storage.

## Project Summary
This product provides a minimal, distraction-free workout tracker allowing users to create routines, define exercises, log workouts, and save/load data locally. Goal is to support quick, reliable workout tracking without accounts, servers, or network requirements.

## Sprints
### Sprint 1 
Sprint 1 establishes foundation of the system.

Features implemented in sprint 1: 
>>>>>>> 74a8fc3 (initial calendar application commit)

## Repository Structure
project-root/
|

|---src/com/workoutapp/            Source Code Files

|   |--- models/        Core domain classes
<<<<<<< HEAD

|   |--- services/      File I/O, JSON parsing, etc.

|   |--- ui/            Future UI classes

|   |--- app/           Main.java

=======
|   |--- services/      File I/O, serialization, etc.
|   |--- ui/            UI classes
|   Main.java
>>>>>>> 74a8fc3 (initial calendar application commit)
|

|---sec/com/workoutapptests/          Junit Test Classes

|   |---models/

|   |---services/

|
<<<<<<< HEAD

|---data/           JSON Data Files

|   |---

|

|---libs/           External Libraries (JUnit, etc)

|   |---junit jar file

=======
|---data/           Serialized data files
|
|---lib/            External Libraries (JUnit, etc)
>>>>>>> 74a8fc3 (initial calendar application commit)
|

|---docs/           Documents not on wiki
<<<<<<< HEAD

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
=======
|---.gitignore

## Running Tests
Instructions to run tests in JUnit

## Data Files
Example data files for testing

## Build & Run
Instructions to build/run

## Calendar CLI (added)
This repository now includes a simple command-line calendar and workout entry tool implemented in Java.

- **Main class:** [src/ui/CalendarCLI.java](src/ui/CalendarCLI.java)
- **Persistence:** events are saved to `data/events.dat` using Java serialization (binary file)

Usage (build and run with `javac`/`java`):

```bash
javac -d out src/models/*.java src/services/*.java src/ui/CalendarCLI.java
java -cp out ui.CalendarCLI
```

Commands in the CLI:
- `add` : create a dated event with a `Workout`, `Exercise`s, and `Set` entries (reps, weight, notes)
- `list`: list all events
- `date`: list events for a specific date
- `quit`: save and exit

The CLI is minimal and intended to demonstrate a working calendar-backed workout logger. You can extend the `PersistenceService` to use JSON or XML if human-readable files are preferred.
>>>>>>> 74a8fc3 (initial calendar application commit)
