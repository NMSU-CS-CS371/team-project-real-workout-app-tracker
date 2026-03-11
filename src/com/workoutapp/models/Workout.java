package com.workoutapp.models;
import java.util.LinkedList;

public class Workout {

    private LinkedList<ExerciseInstance> exercises;

    // Constructor
    public Workout() {
        this.exercises = new LinkedList<>();
    }
    
    // Returns a copy of the exercises in the workout
    public LinkedList<ExerciseInstance> getExercises() {
        return exercises;
    }

    // Returns number of exercises in workout
    public int getNumExercises() {
        return exercises.size();
    }

    // Adds an exercise to the workout
    public void addExercise(ExerciseInstance exercise) {
        exercises.add(exercise);
    }

    // Removes an exercise from the workout
    public void removeExercise(ExerciseInstance exercise) {
        exercises.remove(exercise);
    }

    // Removes an exercise from the workout at index
    public void removeExerciseAt(int index) {
        exercises.remove(index);
    }

    // Prints the workout details
    public String toString() {
        StringBuilder r = new StringBuilder();
        r.append("Workout\n-------------\n");
        for (int i = 0; i < exercises.size(); i++) {
            r.append("#" + (i+1) + " - ").append(exercises.get(i).toString()).append("\n\n");
        }
        return r.toString();
    }
}

