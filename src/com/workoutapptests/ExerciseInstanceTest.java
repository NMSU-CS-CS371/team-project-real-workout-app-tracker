package com.workoutapptests;

import com.workoutapp.models.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *  Verify that ExerciseInstance model works correctly. Tests creation, invalid arguments, 
 *  adding sets, and more for ExerciseInstances for cardio and strength exercises.
 */

public class ExerciseInstanceTest {
    private Exercise e;
    private ExerciseInstance inst;

    //Test creation of strength ExerciseInstance
    @Test
    public void testStrengthInstanceCreation() {
        e = new Exercise("Bench", "Chest", ExerciseType.CHEST);
        inst = new ExerciseInstance(e);

        assertEquals(0, inst.getDurationMinutes());
        assertEquals(0, inst.getSetCount());
    }

    //Test creation of cardio ExerciseInstance
    @Test
    public void testCardioInstanceCreation() {
        e = new Exercise("Run", "Cardio", ExerciseType.CARDIO);
        inst = new ExerciseInstance(e, 30);

        assertEquals(30, inst.getDurationMinutes());
        assertEquals(0, inst.getSetCount());
    }

    //Test invalid strength constructor on cardio exercise
    @Test
    public void testRejectStrengthConstructorForCardio() {
        e = new Exercise("Run", "Cardio", ExerciseType.CARDIO);
        assertThrows(IllegalArgumentException.class, () -> new ExerciseInstance(e));
    }

    //Test invalid cardio constructor on strength exercise
    @Test
    public void testRejectCardioConstructorForStrength() {
        e = new Exercise("Bench", "Chest", ExerciseType.CHEST);
        assertThrows(IllegalArgumentException.class, () -> new ExerciseInstance(e, 10));
    }

    //Test adding a set to a strength ExerciseInstance
    @Test
    public void testAddSetStrength() {
        e = new Exercise("Bench", "Chest", ExerciseType.CHEST);
        inst = new ExerciseInstance(e);
        inst.addSet(new WorkoutSet(10, 135));

        assertEquals(1, inst.getSetCount());
        assertEquals(10, inst.getReps());
    }

    //Test adding set to cardio ExerciseInstance throws exception
    @Test
    public void testRejectAddSetCardio() {
        e = new Exercise("Run", "Cardio", ExerciseType.CARDIO);
        inst = new ExerciseInstance(e, 20);

        assertThrows(IllegalArgumentException.class, () -> inst.addSet(new WorkoutSet(10, 100)));
    }

    //Test updating reps to all sets in an ExerciseInstance
    @Test
    public void testSetRepsUpdatesAllSets() {
        Exercise e = new Exercise("Bench", "Chest", ExerciseType.CHEST);
        ExerciseInstance inst = new ExerciseInstance(e);

        inst.addSet(new WorkoutSet(8, 135));
        inst.addSet(new WorkoutSet(8, 135));
        inst.setReps(12);

        assertEquals(12, inst.getWorkoutSet(0).getReps());
        assertEquals(12, inst.getWorkoutSet(1).getReps());
    }

    //Test total reps and volume accounting across individual sets
    @Test
    public void testTotalRepsAndVolumeAcrossSets() {
        Exercise e = new Exercise("Squat", "Legs", ExerciseType.LEGS);
        ExerciseInstance inst = new ExerciseInstance(e);

        inst.addSet(new WorkoutSet(10, 135));
        inst.addSet(new WorkoutSet(8, 155));

        assertEquals(18, inst.getTotalReps());
        assertEquals(10 * 135 + 8 * 155, inst.getTotalVolume(), 0.0001);
    }

    //Test exception on negative duration for cardio instance
    @Test
    public void testRejectNegativeDuration() {
        Exercise e = new Exercise("Run", "Cardio", ExerciseType.CARDIO);
        ExerciseInstance inst = new ExerciseInstance(e, 10);

        assertThrows(IllegalArgumentException.class, () -> inst.setDurationMinutes(-5));
    }
}