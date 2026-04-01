package com.workoutapp.models;

/*
 * RecoverySuggestion is a primitive, non‑functional placeholder intended to
 * eventually provide guidance on how long a user should rest the muscle groups
 * targeted by a workout.  For now it simply returns a generic recommendation.
 *
 * When the workout logic is fleshed out with muscle grouping and intensity data,
 * this class will analyze the workout and produce an appropriate string such as
 * "Allow 48–72 hours for recovery of the chest and triceps."  Its current form
 * allows callers to compile and invoke the method without any real computation.
 */
public class RecoverySuggestion {

    private RecoverySuggestion() {
        // static helper only; prevent instantiation
    }

    public static String suggestRecovery(Workout workout) {
        if (workout == null || workout.getNumExercises() == 0) {
            return "No workout data available; recovery time unknown.";
        }

        boolean hasLargeGroup = false;
        boolean hasSmallGroup = false;
        boolean hasCardio = false;

        for (ExerciseInstance instance : workout.getExercises()) {
            ExerciseType type = instance.getExerciseType();
            switch (type) {
                case BACK:
                case CHEST:
                case LEGS:
                case SHOULDERS:
                    hasLargeGroup = true;
                    break;
                case ARMS:
                case BICEPS:
                case TRICEPS:
                case CORE:
                case ABS:
                    hasSmallGroup = true;
                    break;
                case CARDIO:
                    hasCardio = true;
                    break;
                default:
                    hasSmallGroup = true; // conservative fallback
            }
        }

        int minHours = 24;
        int maxHours = 48;
        String summary;

        if (hasLargeGroup) {
            maxHours = 72;
        }

        if (hasCardio && !hasLargeGroup && !hasSmallGroup) {
            summary = "Cardio-focused workout";
        } else if (hasLargeGroup && hasSmallGroup) {
            summary = "Mixed small and large muscle groups";
        } else if (hasLargeGroup) {
            summary = "Large muscle group focus";
        } else if (hasSmallGroup) {
            summary = "Small muscle group focus";
        } else if (hasCardio) {
            summary = "Cardio-focused workout";
        } else {
            summary = "General workout";
        }

        return String.format(
            "Recovery recommendation: %d-%d hours (%s).\n" +
            "General guideline: small muscle groups (e.g. biceps, abs, arms, core) typically need 24-48 hours; " +
            "large groups (e.g. legs, back, chest, shoulders) or high-intensity sessions often require 48-72 hours.",
            minHours,
            maxHours,
            summary
        );
    }
}
