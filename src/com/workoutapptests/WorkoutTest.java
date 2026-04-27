package com.workoutapptests;

import com.workoutapp.models.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verify that workout model works properly. Test on routine, adding/removing exercise to/from workout, 
 */

public class WorkoutTest {
    private Workout w;

    //Test empty workout (from scratch)
    @Test
    public void testEmptyWorkout() {
        w = new Workout();
        assertEquals(0, w.getNumExercises());
    }

    //Test workout from routine
    @Test
    public void testWorkoutFromRoutine() {
        Routine r = new Routine("Test");
        r.addExercise(new Exercise("Bench", "Chest", ExerciseType.CHEST));
        r.addExercise(new Exercise("Run", "Cardio", ExerciseType.CARDIO));
        w = new Workout(r);

        assertEquals(2, w.getNumExercises());
        assertEquals(ExerciseType.CHEST, w.getExercises().get(0).getExerciseType());
        assertEquals(ExerciseType.CARDIO, w.getExercises().get(1).getExerciseType());
    }

    //Test adding exercise to workout
    @Test
    public void testAddExercise() {
        w = new Workout();
        ExerciseInstance inst = new ExerciseInstance(new Exercise("Bench", "Chest", ExerciseType.CHEST));
        w.addExercise(inst);

        assertEquals(1, w.getNumExercises());
    }

    //Test removing exercise from workout
    @Test
    public void testRemoveExercise() {
        w = new Workout();
        ExerciseInstance inst = new ExerciseInstance(new Exercise("Bench", "Chest", ExerciseType.CHEST));
        w.addExercise(inst);
        w.removeExercise(inst);

        assertEquals(0, w.getNumExercises());
    }

    //Test removing exercise from workout at index
    @Test
    public void testRemoveExerciseAtIndex() {
        w = new Workout();
        ExerciseInstance inst = new ExerciseInstance(new Exercise("Bench", "Chest", ExerciseType.CHEST));
        w.addExercise(inst);
        w.removeExerciseAt(0);

        assertEquals(0, w.getNumExercises());
    }
}
