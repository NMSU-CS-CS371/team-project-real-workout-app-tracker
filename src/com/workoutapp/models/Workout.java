package com.workoutapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a simple workout consisting of a title and a collection of workout exercises.
 * This is used for ad-hoc workouts created during calendar events.
 */
public class Workout implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private List<WorkoutExercise> exercises;

    public Workout() {
        this.exercises = new ArrayList<>();
    }

    public Workout(String title) {
        this.title = title;
        this.exercises = new ArrayList<>();
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<WorkoutExercise> getExercises() {
        return new ArrayList<>(exercises);
    }

    public void addExercise(WorkoutExercise exercise) {
        if (exercise != null) {
            exercises.add(exercise);
        }
    }

    public void removeExercise(WorkoutExercise exercise) {
        exercises.remove(exercise);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Workout: ").append(title).append("\n");
        for (WorkoutExercise ex : exercises) {
            sb.append("  ").append(ex.getName()).append(" - ").append(ex.getSets().size()).append(" sets\n");
        }
        return sb.toString();
    }

    /**
     * Inner class representing an exercise within a workout with its sets.
     */
    public static class WorkoutExercise implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private List<SetEntry> sets;

        public WorkoutExercise(String name) {
            this.name = name;
            this.sets = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<SetEntry> getSets() {
            return new ArrayList<>(sets);
        }

        public void addSet(SetEntry set) {
            if (set != null) {
                sets.add(set);
            }
        }

        public void removeSet(SetEntry set) {
            sets.remove(set);
        }

        @Override
        public String toString() {
            return "Exercise{" + name + ", sets=" + sets.size() + '}';
        }
    }
}
