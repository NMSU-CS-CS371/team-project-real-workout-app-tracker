package com.workoutapp.models;

public class ExerciseInstance {

    private Exercise exercise;
    private int sets;
    private int reps;
    private double weight;
    private int durationMinutes;


    // Constructor for strength exercises
    public ExerciseInstance(Exercise exercise, int sets, int reps, double weight) {
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise cannot be null");
        } else if (exercise.getType() == ExerciseType.CARDIO) {
            throw new IllegalArgumentException("Strength exrcise cannot have type CARDIO");
        } else if (sets < 0 || reps < 0) {
            throw new IllegalArgumentException("Sets and reps must be non-negative");
        } else if (weight < 0) {
            throw new IllegalArgumentException("Weight must be non-negative");
        }

        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.durationMinutes = 0;
    }

    // Constructor for cardio exercises
    public ExerciseInstance(Exercise exercise, int durationMinutes) {
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise cannot be null");
        } else if (exercise.getType() != ExerciseType.CARDIO) {
            throw new IllegalArgumentException("Exercise must have type CARDIO");
        } else if (durationMinutes < 0) {
            throw new IllegalArgumentException("Duration must be non-negative");
        }

        this.exercise = exercise;
        this.sets = 0;
        this.reps = 0;
        this.weight = 0.0;
        this.durationMinutes = durationMinutes;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public String getExerciseName() {
        return exercise.getName();
    }

    public ExerciseType getExerciseType() {
        return exercise.getType();
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public double getWeight() {
        return weight;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setSets(int sets) {
        if (sets < 0) {
            throw new IllegalArgumentException("Sets must be non-negative");
        }
        this.sets = sets;
    }

    public void setReps(int reps) {
        if (reps < 0) {
            throw new IllegalArgumentException("Reps must be non-negative");
        }
        this.reps = reps;
    }

    public void setWeight(double weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight must be non-negative");
        }
        this.weight = weight;
    }

    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes < 0) {
            throw new IllegalArgumentException("Duration must be non-negative");
        }
        this.durationMinutes = durationMinutes;
    }

    @Override
    public String toString() {
        if (exercise.getType() == ExerciseType.CARDIO) {
            return "Exercise: " + exercise.getName()
                + " | Duration: " + durationMinutes + " min";
        }

        return "Exercise: " + exercise.getName()
            + " | Sets: " + sets
            + " | Reps: " + reps
            + " | Weight: " + weight;
    }
}
