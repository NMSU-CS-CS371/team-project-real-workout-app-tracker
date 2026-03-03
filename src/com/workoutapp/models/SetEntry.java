package com.workoutapp.models;

import java.io.Serializable;

/**
 * Represents a single set entry containing reps, weight, and optional notes.
 * Used within Exercise for tracking individual sets.
 */
public class SetEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private int reps;
    private double weight;
    private String notes;

    public SetEntry() {}

    public SetEntry(int reps, double weight, String notes) {
        this.reps = reps;
        this.weight = weight;
        this.notes = notes != null ? notes : "";
    }

    // Getters and setters
    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes != null ? notes : "";
    }

    @Override
    public String toString() {
        return "SetEntry{" +
                "reps=" + reps +
                ", weight=" + weight +
                ", notes='" + notes + '\'' +
                '}';
    }
}
