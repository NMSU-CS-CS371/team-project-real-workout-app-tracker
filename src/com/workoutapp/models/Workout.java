package com.workoutapp.models;
import java.util.LinkedList;

/* Represents a workout, which consists of a list of exercise instances.
*  Can be created manually, or generated from a routine object.
*  The class provides methods to add/remove exercises, as well as a toString method for displaying workout details.
*/
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
                exercises.add(new ExerciseInstance(e, 0));
            } else {
                exercises.add(new ExerciseInstance(e));
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

