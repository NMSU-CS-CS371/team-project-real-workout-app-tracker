package com.workoutapp.models;

/**
 * Represents an entry in a workout routine, which can be either a strength exercise or a cardio exercise.
 * For strength exercises, sets, reps, and weight are used, while for cardio exercises,
 * duration and intensity (stored in weight) are used. Rest time is applicable to both types.
 * This class includes validation to ensure that the parameters are consistent with the type of exercise.
 * The toString method provides a formatted string representation of the routine entry for easy display.
 * This design allows for flexibility in representing different types of exercises while maintaining a clear structure for routine entries.
 */
public class RoutineEntry {
    private Exercise exercise;
    private int sets;       //Strength exercises only, for cardio this will be 0
    private int reps;       //Strength exercises only, for cardio this will be 0
    private double weight;  // in pounds for strength exercises, intensity for cardio exercises from 0-100
    private int restTime;   // in seconds, for strength exercises this is the rest time between sets, for cardio exercises this is the rest time after the exercise
    private double duration;   // in seconds, for strength exercises this will be 0

    /**
     * Constructor for creating a routine entry. The parameters should be consistent with the type of exercise.
     * @param exercise
     * @param sets
     * @param reps
     * @param weight
     * @param restTime
     * @param duration
     */
    public RoutineEntry(Exercise exercise, int sets, int reps, double weight, int restTime, double duration) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.restTime = restTime;
        this.duration = duration;
    }

    /**
     * Constructor for creating a cardio routine entry, where sets and reps are not applicable.
     * @param exercise
     * @param intensity
     * @param restTime
     * @param duration
     */
    public RoutineEntry(Exercise exercise, double intensity, int restTime, double duration) {
        this(exercise, 0, 0, intensity, restTime, duration);
    }

    /**
     * Constructor for creating a strength routine entry, where duration is not applicable.
     * @param exercise
     * @param sets
     * @param reps
     * @param weight
     * @param restTime
     */
    public RoutineEntry(Exercise exercise, int sets, int reps, double weight, int restTime) {
        this(exercise, sets, reps, weight, restTime, 0);
    }

    /**
     * @return the exercise associated with this routine entry
     */
    public Exercise getExercise() {
        return exercise;
    }

    /**
     * Getter for strength exercise parameters. For cardio exercises, these will return 0.
     * @return
     */
    public int getSets() {
        return sets;
    }

    /**
     * @return the number of reps for strength exercises, or 0 for cardio exercises
     */
    public int getReps() {
        return reps;
    }

    /**
     * Getters for weight/intensity. For strength exercises, this returns the weight in pounds, 
     * while for cardio exercises, it returns the intensity from 0-100.
     * @return
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Getter for rest time in seconds, applicable to both strength and cardio exercises.
     * @return
     */
    public int getRestTime() {
        return restTime;
    }

    /**
     * Getter for duration in seconds. For strength exercises, this will return 0, while for cardio exercises, it returns the duration of the exercise.
     * @return
     */
    public double getDuration() {
        return duration;
    }

    /**
     * Setters for the routine entry parameters. These should be used with caution, as changing the parameters may make the entry invalid if they are not consistent with the type of exercise.
     * @param exercise
     */
    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    /**
     * Validation method to ensure that the routine entry parameters are consistent with the type of 
     * exercise. For strength exercises, sets, reps, and weight must be greater than 0, while for 
     * cardio exercises, duration must be greater than 0 and intensity (weight) must be between 0 and 100. 
     * Rest time must be non-negative for both types.
     * @return true if the routine entry is valid, false otherwise
     */
    public boolean isValid() {
        if (exercise == null) {
            return false;
        }
        if (exercise.getType() == ExerciseType.STRENGTH) {
            return sets > 0 && reps > 0 && weight >= 0 && restTime >= 0 && duration == 0;
        } else if (exercise.getType() == ExerciseType.CARDIO) {
            return duration > 0 && weight >= 0 && restTime >= 0 && sets == 0 && reps == 0;
        }
        return false; // For any other exercise types, add validation as needed
    }

    /**
     * toString method to provide a formatted string representation of the routine entry, 
     * which can be used for display purposes. The format will differ based on whether 
     * the exercise is strength or cardio, showing the relevant parameters for each type.
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Exercise: ").append(exercise.getName()).append("\n");   
        sb.append("Type: ").append(exercise.getType()).append("\n");
        if (exercise.getType() == ExerciseType.STRENGTH) {
            sb.append("Sets: ").append(sets).append("\n");
            sb.append("Reps: ").append(reps).append("\n");
            sb.append("Weight: ").append(weight).append(" lbs\n");
            sb.append("Rest Time: ").append(restTime).append(" seconds\n");
        } else if (exercise.getType() == ExerciseType.CARDIO) {
            sb.append("Duration: ").append(duration).append(" seconds\n");
            sb.append("Intensity: ").append((int) weight).append("/100\n");
            sb.append("Rest Time: ").append(restTime).append(" seconds\n");
        } else {
            sb.append("Rest Time: ").append(restTime).append(" seconds\n");
        }
        return sb.toString();
    }
}
