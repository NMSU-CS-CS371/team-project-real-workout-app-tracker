package com.workoutapp.services;
import com.workoutapp.models.Exercise;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.LinkedList;

public class ExerciseService {

    private LinkedList<Exercise> exercises; // list of exercises managed during runtime
    private DataStorage<Exercise> storage; // instance of DataStorage to handle saving and loading exercises
    private Type exerciseListType; // type object used to specify the type of data being loaded/saved, so that DataStorage can properly rebuild the list of exercises from JSON

    public ExerciseService() {
        this.storage = new DataStorage<>("data/exercises.json"); // Initializes DataStorage with the file path for the list of global exercises)
        this.exerciseListType = new TypeToken<LinkedList<Exercise>>(){}.getType(); // Initializes the type object for a LinkedList of Exercise objects, which is used when loading/saving exercises
        this.exercises = storage.load(exerciseListType); // Loads the list of exercises from the specified file path by using DataStorage's load method with the specified object type.
    }

    public void addExercise(Exercise exercise) {
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise cannot be null");
        }
        if (this.exercises == null) {
            this.exercises = new LinkedList<>();
        }
        this.exercises.add(exercise);
        storage.save(exercises);
    }

    public LinkedList<Exercise> getExercises() {
        if (this.exercises == null) {
            this.exercises = new LinkedList<>();
        }
        return this.exercises;
    }

    public boolean removeExercise(Exercise exercise) {
        if (exercise == null || this.exercises == null) {
            return false;
        }
        boolean removed = this.exercises.remove(exercise);
        if (removed) {
            storage.save(exercises);
        }
        return removed;
    }

} // end of ExerciseService class