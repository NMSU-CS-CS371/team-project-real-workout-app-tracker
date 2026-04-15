package com.workoutapp.models;
import java.util.LinkedList;

public class Workout {

    private LinkedList<ExerciseInstance> exercises;

    // No-arg constructor for creating a workout manually, initializes the exercises list
    public Workout() {
        this.exercises = new LinkedList<>();
    }

    // Constructor for creating a workout using a routine - takes a routine and creates a workout with the exercises as exercise instances
    public Workout(Routine routine) {
        this.exercises = new LinkedList<>();
        for (Exercise e : routine.getExercises()) {
            if (e.getType() == ExerciseType.CARDIO) {
                // if the exercise is a cardio exercise, initialize the exercise instances with a duration of 0.
                exercises.add(new ExerciseInstance(e, 0));
            } else {
                // if the exercise is a strength exercise, set default values for sets, reps, and weight.
                exercises.add(new ExerciseInstance(e, 0, 0, 0.0));
            }
        }  
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
        for (int i = 0; i < exercises.size(); i++) {
            r.append("#" + (i+1) + " - ").append(exercises.get(i).toString()).append("\n");
        }
        return r.toString();
    }
}

