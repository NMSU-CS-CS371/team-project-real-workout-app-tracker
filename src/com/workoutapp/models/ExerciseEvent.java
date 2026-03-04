package com.workoutapp.models;

/**
 * Exercise event representing an exercise in a logged workout session, with data like sets, reps, weight, duration, etc.
 * Different for cardio and strength exercises
 */
public class ExerciseEvent {
    private Exercise exercise;
    private int sets;       //Strength exercises only, for cardio this will be 0
    private int reps;       //Strength exercises only, for cardio this will be 0
    private double weight;  // in pounds for strength exercises, intensity for cardio exercises from 0-100
    private int restTime;   // in seconds, for strength exercises this is the rest time between sets, for cardio exercises this is the rest time after the exercise
    private double duration;   // in seconds, for strength exercises this will be 0
    private String notes;

    /**
     * Constructor for creating a cardio routine entry, where sets and reps are not applicable.
     * @throws NullPointerException if exercise is null, 
     * @throws IllegalArgumentException if exercise is not of type CARDIO, or if intensity, 
     * restTime, or duration are out of valid ranges
     */
    public ExerciseEvent(Exercise exercise, double intensity, int restTime, double duration, String notes) throws IllegalArgumentException, NullPointerException {
        if(exercise == null) {
            throw new NullPointerException("Exercise cannot be null.");
        } else if(exercise.getType() != ExerciseType.CARDIO) {
            throw new IllegalArgumentException("Exercise must be of type CARDIO for this constructor.");
        } else if(intensity < 0 || intensity > 100) {
            throw new IllegalArgumentException("Intensity must be between 0 and 100.");
        } else if(restTime < 0) {
            throw new IllegalArgumentException("Rest time cannot be negative.");
        } else if(duration <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0.");
        }   
        this.exercise = exercise;
        this.sets = 0;
        this.reps = 0;
        this.weight = intensity;
        this.restTime = restTime;
        this.duration = duration;
        this.notes = notes != null ? notes : "";
    }

    /**
     * Constructor for creating a strength routine entry, where duration is not applicable.
     * @throws NullPointerException if exercise is null, 
     * @throws IllegalArgumentException if exercise is not of type STRENGTH, or if sets, reps, 
     * weight, or restTime are out of valid ranges
     */
    public ExerciseEvent(Exercise exercise, int sets, int reps, double weight, int restTime, String notes) throws IllegalArgumentException, NullPointerException {
        if(exercise == null) {
            throw new NullPointerException("Exercise cannot be null.");
        } else if(exercise.getType() != ExerciseType.STRENGTH) {
            throw new IllegalArgumentException("Exercise must be of type STRENGTH for this constructor.");
        } else if(sets <= 0) {
            throw new IllegalArgumentException("Sets must be greater than 0.");
        } else if(reps <= 0) {
            throw new IllegalArgumentException("Reps must be greater than 0.");
        } else if(weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative.");
        } else if(restTime < 0) {
            throw new IllegalArgumentException("Rest time cannot be negative.");
        }
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.restTime = restTime;
        this.duration = 0;
        this.notes = notes != null ? notes : "";
    }

    //Get name of exercise
    public String getName() {
        return exercise.getName();
    }

    //Return exercise associated with this entry
    public Exercise getExercise() {
        return exercise;
    }

    //Get sets for strength exercises
    public int getSets() {
        return sets;
    }

    //Get reps for strength exercises
    public int getReps() {
        return reps;
    }

    //Get weight for strength exercise, intensity from 0-100 for cardio
    public double getWeight() {
        return weight;
    } 

    //Get exercise rest time
    public int getRestTime() {
        return restTime;
    }

    //Get duration in seconds for cardio exercises
    public double getDuration() {
        return duration;
    }

    //Get notes on exercise
    public String getNotes(){
        return notes;
    }

    /**
     * Setters for the routine entry parameters. Unused parameters are set to zero.
     * @throws NullPointerException if exercise is null
     * @throws IllegalArgumentException if new parameters are invalid with the type of exercise
     */
    public void setExercise(Exercise exercise) throws NullPointerException {
        if(exercise == null)   throw new NullPointerException("Exercise cannot be null.");
        this.exercise = exercise;

        //Zero unused parameters
        if(exercise.getType() == ExerciseType.CARDIO){
            reps = 0;
            sets = 0;
        } else if(exercise.getType() == ExerciseType.STRENGTH){
            duration = 0;
        }
    }

    //Sets sets for strength exercises, for cardio will be 0
    public void setSets(int sets) throws IllegalArgumentException {
        if(sets < 0)  throw new IllegalArgumentException("Sets cannot be negative.");
        if(this.getExercise().getType() == ExerciseType.CARDIO){    //Zero for cardio
            sets = 0;
            return;
        }
        this.sets = sets;
    }

    //Sets reps for strength exercises, for cardio will be 0
    public void setReps(int reps) throws IllegalArgumentException{
        if(reps < 0)  throw new IllegalArgumentException("Reps cannot be negative.");
        if(this.getExercise().getType() == ExerciseType.CARDIO){    //Zero for cardio exercises
            reps = 0;
            return;
        }
        this.reps = reps;
    }

    //Sets sets for strength exercises, for cardio will be 0-100.
    public void setWeight(double weight) throws IllegalArgumentException {
        if(weight < 0) throw new IllegalArgumentException("Weight cannot be negative.");
        if(exercise.getType() == ExerciseType.CARDIO && weight > 100){
            this.weight = 100;
            return;
        }
        this.weight = weight;
    }

    //Sets rest time
    public void setRestTime(int restTime) throws IllegalArgumentException {
        if(restTime < 0) throw new IllegalArgumentException("Rest time cannot be negative.");
        this.restTime = restTime;
    }

    //Sets duration for cardio exercises, for strength exercises this will be zero
    public void setDuration(double duration) throws IllegalArgumentException {
        if(duration < 0) throw new IllegalArgumentException("Duration cannot be negative.");
        if(this.getExercise().getType() == ExerciseType.STRENGTH){
            duration = 0;
            return;
        }
        this.duration = duration;
    }

    //Update user given notes
    public void setNotes(String newNotes) throws NullPointerException {
        this.notes = newNotes != null ? this.notes : "";
    }

    /**
     * toString method to provide a formatted string representation of the routine entry.
     * Format will differ based on whether the exercise is strength or cardio, showing
     * relevant parameters for each type.
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Exercise: ").append(exercise.getName()).append("\n");   
        sb.append("Type: ").append(exercise.getType()).append("\n");
        switch (exercise.getType()) {
            case STRENGTH:
                sb.append("Sets: ").append(sets).append("\n");
                sb.append("Reps: ").append(reps).append("\n");
                sb.append("Weight: ").append(weight).append(" lbs\n");
                sb.append("Rest Time: ").append(restTime).append(" seconds\n");
            case CARDIO:
                sb.append("Duration: ").append(duration).append(" seconds\n");
                sb.append("Intensity: ").append((int) weight).append("/100\n");
                sb.append("Rest Time: ").append(restTime).append(" seconds\n");
            default:
                sb.append("Rest Time: ").append(restTime).append(" seconds\n");
        }
        sb.append("Notes: ").append(this.notes);
        return sb.toString();
    }
}
