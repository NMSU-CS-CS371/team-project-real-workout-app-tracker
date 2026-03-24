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
        // placeholder implementation
        if (workout == null || workout.getNumExercises() == 0) {
            return "No workout data available; recovery time unknown.";
        }
        // a very simple default recommendation; real logic to come later
        return "Allow 48-72 hours for muscle recovery.";
    }
}
