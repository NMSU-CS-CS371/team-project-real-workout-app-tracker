package workoutapp.models;

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

    // Adds an exercise to the routine
    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
    }

    // Removes an exercise from the routine
    public void removeExercise(Exercise exercise) {
        exercises.remove(exercise);
    }

    // Prints the routine details
    public void printRoutine() {
        System.out.println(routineName);
        System.out.println("-------------");

        for (Exercise exercise : exercises) {
            System.out.println(exercise);
            System.out.println();
        }
    }
}
