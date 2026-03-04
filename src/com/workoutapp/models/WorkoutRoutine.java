package com.workoutapp.models;
import java.util.*;
import java.io.Serializable;

/**
 * Represents a workout routine, which consists of name, description, and list of exercises.
 * Provides methods for managing exercises, and parameters. Will beused by profiles, etc.
 */
public class WorkoutRoutine implements Serializable{
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private List<Exercise> exercises;

    //Default constructor
    public WorkoutRoutine(){
        this.name = "Untitled Workout";
        this.description = "";
        this.exercises = new ArrayList<Exercise>();
    }

    /**
     * Main constructor
     * @param name  title of workout
     * @param description   description of workout
     */
    public WorkoutRoutine(String name, String description) {
        this.name = name;
        this.description = description;
        this.exercises = new ArrayList<Exercise>();
    }

    //Getters for name and description
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    //Returns exercise in routine at specified index 
    public Exercise getExercise(int index) throws Exception {
        if(index < 0 || index >= exercises.size())
            throw new IndexOutOfBoundsException("Invalid index for getting entry.");
        return exercises.get(index);
    }

    // Return list of all the exercises
    public List<Exercise> getExercises() {
        return this.exercises;
    }

    //Get number of exercises in routine
    public int getNumEntries() {
        return this.exercises.size();
    }

    //Set title of routine
    public void setName(String newName){
        this.name = newName != null ? this.name : "Untitled Workout";
    }

    //Set description of routine
    public void setDescription(String newDescription) {
        this.description = newDescription != null ? this.description : "";
    }

    //Add exercise to end of routine - must have valid exercise
    public void addExercise(Exercise entry) throws Exception {
        if(entry == null)
            throw new NullPointerException("Exercise cannot be null.");
        exercises.add(entry);
    }

    //Check if routine has specific exercise, return exercise index else -1
    public int hasExercise(Exercise e){
        if(e == null) throw new NullPointerException("Exercise cannot be null");
        for(int i = 0; i < this.exercises.size(); i++){
            if(this.exercises.get(i) == e || this.exercises.get(i).equals(e)){
                return i;
            }
        }
        return -1;  //Exercise not found
    }

    //Delete exercise at specific index
    public void deleteExerciseFromIndex(int index) throws Exception {
        if(index < 0 || index >= exercises.size())
            throw new IndexOutOfBoundsException("Invalid index for deleting entry.");
        exercises.remove(index);
    }

    //Remove specific exercise from routine
    public void deleteEntry(Exercise e) {
        for(int i = 0; i < exercises.size(); i++){
            if(exercises.get(i) == e || exercises.get(i).equals(e)){
                exercises.remove(i);
            }
        }
    }

    //Check if this routine has no exercises
    public boolean isEmpty() {
        return exercises.isEmpty();
    }

    //Delete all exercises from this workout
    public void clearExercises() {
        exercises.clear();
    }

    //Replace exercise in routine
    public void updateExercise(int index, Exercise e) throws Exception {
        if(e == null) 
            throw new NullPointerException("New entry cannot be null.");
        else if(index < 0 || index >= exercises.size()) 
            throw new IndexOutOfBoundsException("Invalid index for updating exercise.");
        exercises.set(index, e);
    }

    //String representation of WorkoutRoutine
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Workout: ").append(name).append("\n");
        sb.append(description).append("\n");
        for (int i = 0; i < exercises.size(); i++) {
            sb.append(i + 1).append(". ").append(exercises.get(i).toString()).append("\n");
        }
        return sb.toString();
    }
}
