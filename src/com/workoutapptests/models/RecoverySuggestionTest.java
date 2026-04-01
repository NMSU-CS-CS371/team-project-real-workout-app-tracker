package com.workoutapptests.models;

import com.workoutapp.models.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecoverySuggestionTest {

    @Test
    public void testRecoveryLargeGroup() {
        Routine r = new Routine("Leg Day");
        r.addExercise(new Exercise("Squat", "", ExerciseType.LEGS));
        Workout w = new Workout(r);

        String suggestion = RecoverySuggestion.suggestRecovery(w);
        assertTrue(suggestion.contains("48-72"), "Expected large group to require 48-72 hours");
        assertTrue(suggestion.contains("Large muscle group focus"));
    }

    @Test
    public void testRecoverySmallGroup() {
        Routine r = new Routine("Arm Day");
        r.addExercise(new Exercise("Bicep Curl", "", ExerciseType.BICEPS));
        Workout w = new Workout(r);

        String suggestion = RecoverySuggestion.suggestRecovery(w);
        assertTrue(suggestion.contains("24-48"), "Expected small group to require 24-48 hours");
        assertTrue(suggestion.contains("Small muscle group focus"));
    }

    @Test
    public void testRecoveryMixedGroup() {
        Routine r = new Routine("Mix");
        r.addExercise(new Exercise("Deadlift", "", ExerciseType.BACK));
        r.addExercise(new Exercise("Plank", "", ExerciseType.CORE));
        Workout w = new Workout(r);

        String suggestion = RecoverySuggestion.suggestRecovery(w);
        assertTrue(suggestion.contains("24-72") || suggestion.contains("24-72"), "Expected mixed group to range up to 72 hours");
        assertTrue(suggestion.contains("Mixed small and large muscle groups"));
    }
}
