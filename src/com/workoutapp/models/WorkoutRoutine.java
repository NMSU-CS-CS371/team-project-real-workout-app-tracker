package com.workoutapp.models;
import java.util.*;

/**
 * Represents a workout routine, which consists of a name, description, and a list of routine entries.
 * Provides methods for managing the routine entries, calculating total sets, reps, and estimated duration,
 * and validating the routine to ensure all entries are complete and consistent with their exercise types.
 */
public class WorkoutRoutine {
    private String name;
    private String description;
    private List<RoutineEntry> entries;

    /**
     * Constructor for WorkoutRoutine. Name and description are required, while entries can be added later.
     * @param name
     * @param description
     * @throws NullPointerException if name or description is null
     */
    public WorkoutRoutine(String name, String description) throws Exception {
        if(name == null){
            throw new NullPointerException("Routine name cannot be null");
        } else if(description == null){
            throw new NullPointerException("Routine description cannot be null");
        }
        this.name = name;
        this.description = description;
        this.entries = new ArrayList<RoutineEntry>();
    }

    /**
     * Getters and setters for routine name, description, and entries. Also includes 
     * methods for adding, deleting, moving, and updating entries.
     * @return 
     * @throws IndexOutOfBoundsException if index is out of bounds for entries list
     */
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public RoutineEntry getEntry(int index) throws IndexOutOfBoundsException {
        if(index < 0 || index >= entries.size()) {
            throw new IndexOutOfBoundsException("Invalid index for getting entry.");
        }
        return entries.get(index);
    }

    public int getNumEntries() {
        return entries.size();
    }

    /**
     * Setters for name and description.
     * @param newName
     * @return
     * @throws NullPointerException if newName is null
     */
    public void setName(String newName) throws NullPointerException {
        if(newName == null) {
            throw new NullPointerException("New name cannot be null.");
        }
        this.name = newName;
    }

    /**
     * Setter for description.
     * @param newDescription
     * @return
     * @throws NullPointerException if newDescription is null
     */
    public void setDescription(String newDescription) throws NullPointerException {
        if(newDescription == null) {
            throw new NullPointerException("New description cannot be null.");
        }
        this.description = newDescription;
    }

    /**
     * Adds a new entry to the routine.
     * @param entry
     * @throws NullPointerException if entry is null or has a null exercise
     * @throws IllegalArgumentException if entry has invalid data with respect to the exercise type 
     * (e.g. sets/reps for cardio, duration for strength)
     */
    public void addEntry(RoutineEntry entry) throws NullPointerException, IllegalArgumentException {
        if(entry == null) {
            throw new NullPointerException("Entry cannot be null.");
        } else if(entry.getExercise() == null) {
            throw new NullPointerException("Entry must have a valid exercise.");
        } else if(entry.isValid() == false) {
            throw new IllegalArgumentException("Entry has invalid data. Please check sets, reps, duration, weight, and rest time for consistency with the exercise type.");
        }
        entries.add(entry);
    }

    /**
     * Deletes an entry from the routine at the specified index. 
     * @param index the index of the entry to delete
     * @throws IndexOutOfBoundsException if the index is out of bounds for the entries list
     */
    public void deleteEntry(int index) throws IndexOutOfBoundsException {
        if(index < 0 || index >= entries.size()) {
            throw new IndexOutOfBoundsException("Invalid index for deleting entry.");
        }
        entries.remove(index);
    }

    /**
     * Checks if the routine has any entries.
     * @return true if the routine has no entries, false otherwise
     */
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * Clears all entries from the routine. This can be used when resetting a routine or starting fresh.
     */
    public void clearEntries() {
        entries.clear();
    }

    /**
     * Moves an entry from one index to another within the routine. This allows for easy reordering of exercises
     * @param fromIndex
     * @param toIndex
     * @throws IndexOutOfBoundsException if either index is out of bounds for the entries list
     */
    public void MoveEntry(int fromIndex, int toIndex) throws IndexOutOfBoundsException {
        if(fromIndex < 0 || fromIndex >= entries.size() || toIndex < 0 || toIndex >= entries.size()) {
            throw new IndexOutOfBoundsException("Invalid index for moving entry.");
        }
        RoutineEntry entry = entries.remove(fromIndex);
        entries.add(toIndex, entry);
    }

    /**
     * Updates an existing entry at the specified index with new data. This allows for modifying the 
     * exercise, sets, reps, duration, weight, and rest time of an entry without needing to delete and re-add it.
     * @param index the index of the entry to update
     * @param newEntry the new entry data to replace the existing entry
     * @throws NullPointerException if newEntry is null or has a null exercise
     * @throws IllegalArgumentException if newEntry has invalid data with respect to the exercise type
     * @throws IndexOutOfBoundsException if the index is out of bounds for the entries list
     */
    public void UpdateEntry(int index, RoutineEntry newEntry) throws NullPointerException, IllegalArgumentException, IndexOutOfBoundsException {
        if(newEntry == null) {
            throw new NullPointerException("New entry cannot be null.");
        } else if(newEntry.getExercise() == null) {
            throw new NullPointerException("New entry must have a valid exercise.");
        } else if(newEntry.isValid() == false) {
            throw new IllegalArgumentException("New entry has invalid data. Please check sets, reps, duration, weight, and rest time for consistency with the exercise type.");
        } else if(index < 0 || index >= entries.size()) {
            throw new IndexOutOfBoundsException("Invalid index for updating entry.");
        }
        entries.set(index, newEntry);
    }

    /**
     * Calculates the total number of sets across all entries in the routine. For cardio exercises, this will
     * count the duration to estimate sets in increments of 2 minutes.
     * @return
     */
    public int getTotalSets() {
        int totalSets = 0;
        for (RoutineEntry entry : entries) {
            if(entry.getExercise().getType() == ExerciseType.CARDIO) {
                totalSets += entry.getDuration() / 120; // For cardio, we can estimate 1 set per 2 minutes of duration for simplicity
            } else {
                totalSets += entry.getSets();
            }
        }
        return totalSets;
    }

    /**
     * Calculates the total number of reps across all entries in the routine. For cardio exercises, this will
     * count the duration as reps for estimation purposes in increments of 20 seconds.
     * @return
     */
    public int getTotalReps() {
        int totalReps = 0;
        for (RoutineEntry entry : entries) {
            if(entry.getExercise().getType() == ExerciseType.CARDIO) {
                totalReps += entry.getDuration() / 20; // For cardio, we can estimate 1 rep per 20 seconds of duration for simplicity
            } else {
                totalReps += entry.getReps();
            }
            totalReps += entry.getReps();
        }
        return totalReps;
    }

    /**
     * Removes any entries that have invalid data, such as missing exercises or 
     * inconsistent sets/reps/duration
     */
    public void removeInvalidEntries() {
        entries.removeIf(entry -> entry.getExercise() == null ||
                (entry.getExercise().getType() == ExerciseType.STRENGTH && (entry.getSets() <= 0 || entry.getReps() <= 0 || entry.getWeight() < 0 || entry.getRestTime() < 0)) ||
                (entry.getExercise().getType() == ExerciseType.CARDIO && (entry.getDuration() <= 0 || entry.getWeight() < 0 || entry.getRestTime() < 0)));
    }

    /**
     * Validates the workout routine by checking that all entries have a valid exercise and that the 
     * sets, reps, duration, weight, and rest time are consistent with the exercise type.
     * @return
     */
    public boolean isValid() {
        for (RoutineEntry entry : entries) {
            if (entry.getExercise() == null) {
                return false;
            }
            if (entry.getExercise().getType() == ExerciseType.STRENGTH) {
                if (entry.getSets() <= 0 || entry.getReps() <= 0 || entry.getWeight() < 0 || entry.getRestTime() < 0) {
                    return false;
                }
            } else if (entry.getExercise().getType() == ExerciseType.CARDIO) {
                if (entry.getDuration() <= 0 || entry.getWeight() < 0 || entry.getRestTime() < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * String representation of the workout routine for easy debugging and display. Shows name, description, 
     * and all entries.
      * @return a string representation of the workout routine
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Workout Routine: ").append(name).append("\n");
        sb.append(description).append("\n");
        for (int i = 0; i < entries.size(); i++) {
            sb.append(i + 1).append(". ").append(entries.get(i).toString()).append("\n");
        }
        return sb.toString();
    }
}
