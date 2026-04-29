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
    public RoutineService(String profileName) { 
        this.storage = new DataStorage<>("data/" + profileName + "/routines.json");
        this.routineListType = new TypeToken<LinkedList<Routine>>() {}.getType();
        this.routines = storage.load(routineListType);
    }

    public LinkedList<Routine> getRoutines() {  // returns the list of routines currently in memory
        return routines;
    }

    public void addRoutine(Routine routine) {   // adds a new routine to the list of routines after performing a check to ensure that the routine isn't null or already created.

        if (routine == null || routine.getRoutineName() == null) {  // if the routine is empty or has no name, print a message and return without adding the routine
            System.out.println("Routine name is required.");
            return;
        }

        if (checkRoutineExists(routine.getRoutineName())) { // if a routine with the name already exists, print a message and return without adding the routine
            System.out.println("A routine with the name " + routine.getRoutineName() + " already exists.");
            return;
        }

        routines.add(routine);  // add the routine to the list of routines
        storage.save(routines); // save the updated routine list
    }

    
    // check if a routine with the entered name already exists, return true or false
    public boolean checkRoutineExists(String routineName) {

        // Iterate through the routine list and check for a duplicate routine name. Return true if a duplicate is found, otherwise return false.
        for (Routine r : routines) {
            if (r.getRoutineName().equalsIgnoreCase(routineName)) {
                return true;
            }
        }
        return false;
    }

    // find and return the routine with the entered name, or return null if not found
    public Routine findRoutine(String routineName) {

        // iterate through the routine list and check for a name match. if found, return the routine, otherwise return null
        for (Routine r : routines) {
            if (r.getRoutineName().equalsIgnoreCase(routineName)) {
                return r;
            }
        }
        return null;
    }  

    // remove the routine from the routine list and save the updated list
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

    //Get routine names
    public LinkedList<String> getRoutineNames(){
        LinkedList<String> names = new LinkedList<String>();
        for(Routine r : routines){
            names.add(r.getRoutineName());
        }
        return names;
    }

    //Save all routines in this service to file
    public void saveAll() {
        storage.save(routines);
    }
}