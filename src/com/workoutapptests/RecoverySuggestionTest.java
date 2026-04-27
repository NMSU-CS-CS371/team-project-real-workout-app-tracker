package com.workoutapptests;

import com.workoutapp.models.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verify that RecoverySuggestion model works and test on empty/null workout, large and small muscle groups,
 * cardio and mixed workouts.
 */

public class RecoverySuggestionTest {
    //Test with null workout
    @Test
    public void testNullWorkout() {
        String result = RecoverySuggestion.suggestRecovery(null);
        assertTrue(result.contains("No workout data available"));
    }

    //Test on empty workout
    @Test
    public void testEmptyWorkout() {
        Workout w = new Workout();
        String result = RecoverySuggestion.suggestRecovery(w);
        assertTrue(result.contains("No workout data available"));
    }

    //Test with large muscle group workout
    @Test
    public void testLargeMuscleGroup() {
        Workout w = new Workout();
        w.addExercise(new ExerciseInstance(new Exercise("Bench", "Chest", ExerciseType.CHEST)));

        String result = RecoverySuggestion.suggestRecovery(w);
        assertTrue(result.contains("Large muscle group"));
        assertTrue(result.contains("72"));
    }

    //Test on small muscle group workout
    @Test
    public void testSmallMuscleGroup() {
        Workout w = new Workout();
        w.addExercise(new ExerciseInstance(new Exercise("Crunch", "Abs", ExerciseType.ABS)));

        String result = RecoverySuggestion.suggestRecovery(w);
        assertTrue(result.contains("Small muscle group"));
    }

    //Test on cardio-only workout
    @Test
    public void testCardioOnly() {
        Workout w = new Workout();
        w.addExercise(new ExerciseInstance(new Exercise("Run", "Cardio", ExerciseType.CARDIO), 20));

        String result = RecoverySuggestion.suggestRecovery(w);
        assertTrue(result.contains("Cardio-focused"));
    }

    //Test on mixed groups workout
    @Test
    public void testMixedGroups() {
        Workout w = new Workout();
        w.addExercise(new ExerciseInstance(new Exercise("Bench", "Chest", ExerciseType.CHEST)));
        w.addExercise(new ExerciseInstance(new Exercise("Crunch", "Abs", ExerciseType.ABS)));

        String result = RecoverySuggestion.suggestRecovery(w);
        assertTrue(result.contains("Mixed small and large"));
    }
}