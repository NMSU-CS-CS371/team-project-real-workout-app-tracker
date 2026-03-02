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
    @SuppressWarnings("FieldMayBeFinal")
    private List<RoutineEntry> entries;

    /**
     * Constructor for WorkoutRoutine. Name and description are required, while entries can be added later.
     * @param name
     * @param description
     */
    public WorkoutRoutine(String name, String description) {
        this.name = name;
        this.description = description;
        this.entries = new ArrayList<RoutineEntry>();
    }

    /**
     * Getters and setters for routine name, description, and entries. Also includes 
     * methods for adding, deleting, moving, and updating entries.
     * @return 
     */
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public RoutineEntry getEntry(int index) {
        return entries.get(index);
    }

    public int getNumEntries() {
        return entries.size();
    }

    /**
     * Setters for name and description.
     * @param newName
     * @return
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Setter for description.
     * @param newDescription
     * @return
     */
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    /**
     * Adds a new entry to the routine.
     * @param entry
     */
    public void addEntry(RoutineEntry entry) {
        entries.add(entry);
    }

    /**
     * Deletes an entry from the routine at the specified index. 
     * @param index the index of the entry to delete
     */
    public void deleteEntry(int index) {
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
     * @return true if the entries were cleared successfully, false otherwise
     */
    public boolean clearEntries() {
        entries.clear();
        return true; // success
    }

    /**
     * Moves an entry from one index to another within the routine. This allows for easy reordering of exercises
     * @param fromIndex
     * @param toIndex
     */
    public void MoveEntry(int fromIndex, int toIndex) {
        RoutineEntry entry = entries.remove(fromIndex);
        entries.add(toIndex, entry);
    }

    /**
     * Updates an existing entry at the specified index with new data. This allows for modifying the 
     * exercise, sets, reps, duration, weight, and rest time of an entry without needing to delete and re-add it.
     * @param index the index of the entry to update
     * @param newEntry the new entry data to replace the existing entry
     */
    public void UpdateEntry(int index, RoutineEntry newEntry) {
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
