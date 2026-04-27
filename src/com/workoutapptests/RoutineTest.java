package com.workoutapptests;

import com.workoutapp.models.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verify that routine model works correctly including creation, adding and removing exercises.
 */
public class RoutineTest {
    private Routine r;

    //Test creating a routine
    @Test
    public void testCreateRoutine() {
        r = new Routine("Upper Body");
        assertEquals("Upper Body", r.getRoutineName());
        assertEquals(0, r.getNumExercises());
    }

    //Test adding  exercise to the start of a routine;
    @Test
    public void testAddExercise() {
        r = new Routine("Test");
        Exercise e = new Exercise("Bench", "Chest press", ExerciseType.CHEST);
        r.addExercise(e);

        assertEquals(1, r.getNumExercises());
        assertEquals(e, r.getExercises().get(0));
    }

    //Test adding exercise at index (start of routine)
    @Test
    public void testAddExerciseAtIndex() {
        r = new Routine("Test");
        Exercise e1 = new Exercise("Bench", "Chest", ExerciseType.CHEST);
        Exercise e2 = new Exercise("Squat", "Legs", ExerciseType.LEGS);

        r.addExercise(e1);
        r.addExercise(e2, 0);

        assertEquals(e2, r.getExercises().get(0));
    }

    //Test deleting exercise 
    @Test
    public void testRemoveExercise() {
        r = new Routine("Test");
        Exercise e = new Exercise("Bench Press", "Chest", ExerciseType.CHEST);

        r.addExercise(e);
        r.removeExercise(e);

        assertEquals(0, r.getNumExercises());
    }

    //Test deleting exercise at an index
    @Test
    public void testRemoveExerciseAtIndex() {
        r = new Routine("Test");
        Exercise e = new Exercise("Bench Press", "Chest", ExerciseType.CHEST);

        r.addExercise(e);
        r.removeExerciseAt(0);

        assertEquals(0, r.getNumExercises());
    }
}
