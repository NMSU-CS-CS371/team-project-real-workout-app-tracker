package com.workoutapp.services;
import com.workoutapp.models.Exercise;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.LinkedList;

/*  
*   ExerciseService
*/

public class ExerciseService {

    private LinkedList<Exercise> exercises; // list of exercises managed during runtime
    private DataStorage<Exercise> storage; // instance of DataStorage to handle saving and loading exercises
    private Type exerciseListType; // type object used to specify the type of data being loaded/saved, so that DataStorage can properly rebuild the list of exercises from JSON

    public ExerciseService() {
        this.storage = new DataStorage<>("data/exercises.json"); // Initializes DataStorage with the file path for the list of global exercises)
        this.exerciseListType = new TypeToken<LinkedList<Exercise>>(){}.getType(); // Initializes the type object for a LinkedList of Exercise objects, which is used when loading/saving exercises
        this.exercises = storage.load(exerciseListType); // Loads the list of exercises from the specified file path by using DataStorage's load method with the specified object type.
    }


// getExercises - returns the list of exercises currently being managed by the ExerciseService instance
public LinkedList<Exercise> getExercises() {
    return exercises;
}

// addExercise adds a new exercise to the list of all exercises, and then saves the updated list using the DataStorage instance
public void addExercise(Exercise exercise) {

    if (checkExerciseExists(exercise.getName())) { // check if an exercise with the same name exists before adding the new exercise

        System.out.println("An exercise with the name " + exercise.getName() + " already exists."); // if the exercise already exists, print an error message
        return;
    }

    exercises.add(exercise); // add the new exercise to the list of exercises
    storage.save(exercises); // save the new list of exercises to the file using DataStorage's save method

}

// checkExerciseExists - helper method for use in addExercise method: checks if an exercise with entered name already exists. Returns true or false.
public boolean checkExerciseExists(String name) {

    for (Exercise e: exercises) { // iterate through the list of exercises, and check if any of them have the name of the exercise being added

        if (e.getName().equalsIgnoreCase(name)) { // if an exercise with the same name is found, return true
            return true;
        }
        else { // if the exercise being checked does not have the same name as the exercise being added, continue checking the rest of the exercises
            continue;
        }

    }

    return false; // if there is no exercise with the same name already in the list, return false

}

// removeExercise removes an exercise, then saves the updated list using the DataStorage instance
public void removeExercise(Exercise exercise) {

    exercises.remove(exercise); // remove the specified exercise from the list of exercises
    storage.save(exercises); // save the new list of exercises to the file using DataStorage

}

} // end of ExerciseService class