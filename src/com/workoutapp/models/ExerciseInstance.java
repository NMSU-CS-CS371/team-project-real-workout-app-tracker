package com.workoutapp.models;

import java.util.LinkedList;
import java.util.List;

public class ExerciseInstance {

    private Exercise exercise;
    private List<WorkoutSet> workoutSets;
    private int durationMinutes;


    // Constructor for strength exercises
    public ExerciseInstance(Exercise exercise) {

        if (exercise == null) {
            throw new IllegalArgumentException("Exercise cannot be null");
        } else if (exercise.getType() == ExerciseType.CARDIO) {
            throw new IllegalArgumentException("Strength exrcise cannot have type CARDIO");
        }

        this.exercise = exercise;
        this.workoutSets = new LinkedList<>();
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
        this.workoutSets = new LinkedList<>();
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

    public int getSetCount() {
        return workoutSets.size();
    }

    public int getReps() {
        if (workoutSets.isEmpty()) {
            return 0;
        }
        return workoutSets.get(0).getReps();
    }

    public double getWeight() {
        if (workoutSets.isEmpty()) {
            return 0.0;
        }
        return workoutSets.get(0).getWeight();
    }

    public List<WorkoutSet> getWorkoutSets() {
        return new LinkedList<>(workoutSets);
    }

    public WorkoutSet getWorkoutSet(int index) {
        if (index < 0 || index >= workoutSets.size()) {
            throw new IndexOutOfBoundsException("Set index out of range: " + index);
        }
        return workoutSets.get(index);
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setSets(int sets) {
        if (sets < 0) {
            throw new IllegalArgumentException("Sets must be non-negative");
        }

        int reps = getReps();
        double weight = getWeight();

        if (workoutSets.size() < sets) {
            while (workoutSets.size() < sets) {
                workoutSets.add(new WorkoutSet(reps, weight));
            }
        } else {
            while (workoutSets.size() > sets) {
                workoutSets.remove(workoutSets.size() - 1);
            }
        }
    }

    public void setReps(int reps) {
        if (reps < 0) {
            throw new IllegalArgumentException("Reps must be non-negative");
        }

        for (WorkoutSet workoutSet : workoutSets) {
            workoutSet.setReps(reps);
        }
    }

    public void setWeight(double weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight must be non-negative");
        }

        for (WorkoutSet workoutSet : workoutSets) {
            workoutSet.setWeight(weight);
        }
    }

    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes < 0) {
            throw new IllegalArgumentException("Duration must be non-negative");
        }
        this.durationMinutes = durationMinutes;
    }

    public void addSet(WorkoutSet set) {
        if (exercise.getType() == ExerciseType.CARDIO) {
            throw new IllegalArgumentException("Cannot add sets to cardio exercise");
        }
        workoutSets.add(set);
    }

    public void removeSet(int index) {
        if (exercise.getType() == ExerciseType.CARDIO) {
            throw new IllegalArgumentException("Cannot remove sets from cardio exercise");
        }
        workoutSets.remove(index);
    }

    @Override
    public String toString() {
        if (exercise.getType() == ExerciseType.CARDIO) {
            return "Exercise: " + exercise.getName()
                + " | Duration: " + durationMinutes + " min";
        }

        return "Exercise: " + exercise.getName()
            + " | Sets: " + getSetCount()
            + " | Reps: " + getReps()
            + " | Weight: " + getWeight();
    }
}
