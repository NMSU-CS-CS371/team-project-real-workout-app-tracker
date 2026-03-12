package com.workoutapp.models;

import java.util.LinkedList;

public class Routine {

    private String routineName;
    private LinkedList<Exercise> exercises;

    // Constructor
    public Routine(String routineName) {
        this.routineName = routineName;
        this.exercises = new LinkedList<>();
    }

    // Getter for routine name
    public String getRoutineName() {
        return routineName;
    }
    
    // Setter for routine name
    public void setRoutineName(String routineName) {
        this.routineName = routineName;
    }
    
    // Returns a copy of the exercises in the routine
    public LinkedList<Exercise> getExercises() {
        return exercises;
    }

    // Returns number of exercises in routine
    public int getNumExercises() {
        return exercises.size();
    }

    // Adds an exercise to the routine
    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
    }

    // Removes an exercise from the routine
    public void removeExercise(Exercise exercise) {
        exercises.remove(exercise);
    }

    // Removes an exercise from the routine at index
    public void removeExerciseAt(int index) {
        exercises.remove(index);
    }

    // Prints the routine details
    public String toString() {
        StringBuilder r = new StringBuilder();
        r.append(routineName).append("\n-------------\n");
        for (int i = 0; i < exercises.size(); i++) {
            r.append("#" + (i+1) + " - ").append(exercises.get(i).toString()).append("\n\n");
        }
        return r.toString();
    }
}
