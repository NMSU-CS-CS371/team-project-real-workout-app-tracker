package com.workoutapp.models;

public class WorkoutSet {

    private int reps;
    private double weight;

    public WorkoutSet() {
        this(0, 0.0);
    }

    public WorkoutSet(int reps, double weight) {
        setReps(reps);
        setWeight(weight);
    }

    public int getReps() {
        return reps;
    }

    public double getWeight() {
        return weight;
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

    @Override
    public String toString() {
        return reps + " reps @ " + weight + " lbs";
    }
}