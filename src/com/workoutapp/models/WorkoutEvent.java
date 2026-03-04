package com.workoutapp.models;
import java.util.*;
import java.io.Serializable;

/**
 * Represents a workout event, which consists of name, description, and list of exercise events.
 * Workout events contain a logged workout session with data, based on a routine - list of exercises.
 */
public class WorkoutEvent implements Serializable{
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private final WorkoutRoutine routine;
    private List<ExerciseEvent> entries;

    //Default constructor
    public WorkoutEvent(){
        this.name = "Untitled Workout";
        this.description = "";
        this.routine = new WorkoutRoutine("Routine", "");
        this.entries = new ArrayList<ExerciseEvent>();
    }

    /**
     * Main constructor
     * @param name  title of workout
     * @param description   description of workout
     * @throws Exception    for null parameters
     */
    public WorkoutEvent(String name, String description, WorkoutRoutine routine) throws Exception {
        this.name = name;
        this.description = description;
        this.entries = new ArrayList<ExerciseEvent>();
        this.routine = routine;
    }

    //Getters for name and description
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    //Returns exercise event in routine at specified index 
    public ExerciseEvent getEntry(int index) throws Exception {
        if(index < 0 || index >= entries.size()) 
            throw new IndexOutOfBoundsException("Invalid index for getting entry.");
        return entries.get(index);
    }

    //Get number of exercises in this workout event
    public int getNumEntries() {
        return entries.size();
    }

    //Set name of workout event
    public void setName(String newName){
        this.name = newName != null ? this.name : "Untitled Workout";
    }

    //Update description of routine
    public void setDescription(String newDescription) {
        this.description = newDescription != null ? this.description : "";
    }

    //Add exercise event to entry
    public void addEntry(ExerciseEvent entry) throws Exception {
        if(entry == null) throw new NullPointerException("Entry cannot be null.");
        else if(entry.getExercise() == null)
            throw new NullPointerException("Entry must have a valid exercise.");
        entries.add(entry);
    }

    //Check if routine has specific exercise, return exercise index else -1
    public int hasExercise(Exercise exercise){
        if(exercise == null) throw new NullPointerException("Exercise cannot be null");
        for(int i = 0; i < this.entries.size(); i++){
            if(this.entries.get(i).getExercise() == exercise || this.entries.get(i).getExercise().equals(exercise)){
                return i;
            }
        }
        return -1;  //Exercise not found
    }

    //Delete entry at specific index
    public void deleteEntry(int index) throws Exception {
        if(index < 0 || index >= entries.size())
            throw new IndexOutOfBoundsException("Invalid index for deleting entry.");
        entries.remove(index);
    }

    //Check if this routine has no exercises
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    //Delete all entries from this workout event
    public void clearEntries() {
        entries.clear();
    }

    //String representation of WorkoutRoutine
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Workout: ").append(name).append("\n");
        sb.append(description).append("\n");
        for (int i = 0; i < entries.size(); i++) {
            sb.append(i + 1).append(". ").append(entries.get(i).toString()).append("\n");
        }
        return sb.toString();
    }
}
