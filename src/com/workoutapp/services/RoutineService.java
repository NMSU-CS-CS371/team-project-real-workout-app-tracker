package com.workoutapp.services;

import com.google.gson.reflect.TypeToken;
import com.workoutapp.models.Exercise;
import com.workoutapp.models.Routine;

import java.lang.reflect.Type;
import java.util.LinkedList;

public class RoutineService {

    private LinkedList<Routine> routines;   // list of routines used for runtime management
    private DataStorage<Routine> storage;   // instance of DataStorage to handle saving and loading routines
    private Type routineListType;           // type object used to specify the type of data being loaded/saved, so that DataStorage can properly rebuild the list of routines from the Json file

    // constructor - initializes a DataStorage instance for routines, sets the routineListType using type token, and loads the existing routines from the routine json file
    public RoutineService() { 
        this.storage = new DataStorage<>("data/routines.json");
        this.routineListType = new TypeToken<LinkedList<Routine>>() {}.getType();
        this.routines = storage.load(routineListType);
    }

    public LinkedList<Routine> getRoutines() {  // returns the list of routines currently in memory
        return routines;
    }

    public void addRoutine(Routine routine) {   // adds a new routine to the list of routines after performing a check to ensure that the routine isn't null or already created.
        if (routine == null || routine.getRoutineName() == null || routine.getRoutineName().isBlank()) {
            System.out.println("Routine name is required.");
            return;
        }

        if (checkRoutineExists(routine.getRoutineName())) {
            System.out.println("A routine with the name " + routine.getRoutineName() + " already exists.");
            return;
        }

        routines.add(routine);
        storage.save(routines);
    }

    public boolean checkRoutineExists(String routineName) {
        for (Routine r : routines) {
            if (r.getRoutineName().equalsIgnoreCase(routineName)) {
                return true;
            }
        }
        return false;
    }

    public Routine findRoutine(String routineName) {
        for (Routine r : routines) {
            if (r.getRoutineName().equalsIgnoreCase(routineName)) {
                return r;
            }
        }
        return null;
    }

    public void removeRoutine(Routine routine) {
        routines.remove(routine);
        storage.save(routines);
    }

    public void addExercise(String routineName, Exercise exercise) {
        Routine routine = findRoutine(routineName);
        if (routine == null) {
            System.out.println("Routine not found: " + routineName);
            return;
        }

        if (exercise == null || exercise.getName() == null || exercise.getName().isBlank()) {
            System.out.println("Invalid exercise.");
            return;
        }

        for (Exercise e : routine.getExercises()) {
            if (e.getName().equalsIgnoreCase(exercise.getName())) {
                System.out.println("Exercise already exists in routine.");
                return;
            }
        }

        routine.addExercise(exercise);
        storage.save(routines);
    }

    public void removeExercise(String routineName, String exerciseName) {
        Routine routine = findRoutine(routineName);
        if (routine == null) {
            System.out.println("Routine not found: " + routineName);
            return;
        }

        Exercise ex = null;
        for (Exercise e : routine.getExercises()) {
            if (e.getName().equalsIgnoreCase(exerciseName)) {
                ex = e;
                break;
            }
        }

        if (ex == null) {
            System.out.println("Exercise not found in routine: " + exerciseName);
            return;
        }

        routine.removeExercise(ex);
        storage.save(routines);
    }
}