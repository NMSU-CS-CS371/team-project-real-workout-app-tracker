package com.workoutapp.services;
import com.workoutapp.models.Exercise;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.LinkedList;

/*  
*   ExerciseService is used to manage a collection of exercise objects that are persisted to a json file.
*   The service provides methods to add, remove, and find/retrieve exercises, and uses an instance of DataStorage
*   to handle saving and loading exercises to and from the json file.
*/

public class ExerciseService {

    private LinkedList<Exercise> exercises; // list of exercises managed during runtime
    private DataStorage<Exercise> storage; // instance of DataStorage to handle saving and loading exercises
    private Type exerciseListType; // type object used to specify the type of data being loaded/saved, so that DataStorage can properly rebuild the list of exercises from JSON

    public ExerciseService() {
        this.storage = new DataStorage<>("data/exercises.json"); // Initializes DataStorage with the file path for the list of global exercises
        this.exerciseListType = new TypeToken<LinkedList<Exercise>>(){}.getType(); // Initializes the type object for a LinkedList of Exercise objects, which is used when loading/saving exercises
        this.exercises = storage.load(exerciseListType); // Loads the list of exercises from the specified file path by using DataStorage's load method with the specified object type.
    }

    public ExerciseService(String profileName) {
        this.storage = new DataStorage<>("data/" + profileName + "/exercises.json"); // Initializes DataStorage with the file path for exercises under the profile
        this.exerciseListType = new TypeToken<LinkedList<Exercise>>(){}.getType(); // Initializes the type object for a LinkedList of Exercise objects, which is used when loading/saving exercises
        this.exercises = storage.load(exerciseListType); // Loads the list of exercises from the specified file path by using DataStorage's load method with the specified object type.
    }

    // getExercises - returns the list of exercises currently being managed by the ExerciseService instance
    public LinkedList<Exercise> getExercises() {
        if (this.exercises == null) {
            this.exercises = new LinkedList<>();
        }
        return exercises;
    }

    // addExercise adds a new exercise to the list of all exercises, and then saves the updated list using the DataStorage instance
    public void addExercise(Exercise exercise) {
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise cannot be null");
        }
        if (checkExerciseExists(exercise.getName())) { // check if an exercise with the same name exists before adding the new exercise
            System.out.println("An exercise with the name " + exercise.getName() + " already exists."); // if the exercise already exists, print an error message
            return;
        }
        if (this.exercises == null) {
            this.exercises = new LinkedList<>();
        }
        this.exercises.add(exercise); // add the new exercise to the list of exercises
        System.out.println("Exercise " + exercise.getName() + " added successfully."); // print a success message confirming that the exercise was added
        storage.save(exercises); // save the new list of exercises to the file using DataStorage's save method
    }

    // checkExerciseExists - helper method for use in addExercise method: checks if an exercise with entered name already exists. Returns true or false.
    public boolean checkExerciseExists(String name) {
        if (name == null) {
            return false;
        }
        if (this.exercises == null) {
            this.exercises = new LinkedList<>();
        }
        for (Exercise e: exercises) {
            if (e.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    // removeExercise removes an exercise, then saves the updated list using the DataStorage instance
    public boolean removeExercise(Exercise exercise) {
        if (exercise == null || this.exercises == null) {
            return false;
        }
        boolean removed = this.exercises.remove(exercise); // remove the specified exercise from the list of exercises
        if (removed) {
            storage.save(exercises); // save the new list of exercises to the file using DataStorage
        }
        return removed;
    }

    //Save exercises to file
    public void saveExercises() {
        storage.save(exercises);
    }

} // end of ExerciseService class