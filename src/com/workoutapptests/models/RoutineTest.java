package com.workoutapptests.models;

import com.workoutapp.models.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoutineTest {
    private Routine r;

    @BeforeEach
    public void setUp() {
        r = null;
        System.out.println("Test started");
    }

    @AfterEach
    public void tearDown() {
        System.out.println("Test finished");
    }

    @Test 
    public void testGetRoutineName() {
        r = new Routine("Workout");
        assertTrue(r.getRoutineName().equals("Workout"));    
    }
    
    @Test 
    public void testSetRoutineName() {
        r = new Routine("Workout");
        r.setRoutineName("Upper Body");
        assertTrue(r.getRoutineName().equals("Upper Body")); 
    }
    
    @Test 
    public void testAddExerciseSingle() {
        r = new Routine("Workout");
        Exercise e = new Exercise("Bench Press", "", ExerciseType.CHEST);
        r.addExercise(e);
        assertTrue(r.getExercises().get(0).equals(e));
    }
    
    @Test 
    public void testAddExerciseMultiple() {
        r = new Routine("Workout");
        Exercise e1 = new Exercise("Treadmill", "Warm up", ExerciseType.CARDIO);
        Exercise e2 = new Exercise("Bench Press", "", ExerciseType.CHEST);
        Exercise e3 = new Exercise("Curls", "", ExerciseType.ARMS);
        r.addExercise(e1);
        r.addExercise(e2);
        r.addExercise(e3);
        assertTrue(r.getExercises().getLast().equals(e3));
    }

    @Test 
    public void testRemoveExercise() {
        r = new Routine("Workout");
        Exercise e = new Exercise("Bench Press", "", ExerciseType.CHEST);
        r.addExercise(e);
        r.removeExercise(e);
        assertEquals(0, r.getNumExercises());      
    }
    
    @Test 
    public void testRemoveExerciseFromIndex() {
        r = new Routine("Workout");
        Exercise e1 = new Exercise("Treadmill", "Warm up", ExerciseType.CARDIO);
        Exercise e2 = new Exercise("Bench Press", "", ExerciseType.CHEST);
        Exercise e3 = new Exercise("Curls", "", ExerciseType.ARMS);
        r.addExercise(e1);
        r.addExercise(e2);
        r.addExercise(e3);
        r.removeExerciseAt(0);
        assertEquals(2, r.getNumExercises());
        assertTrue(r.getExercises().getFirst().equals(e2));
    }
    
    @Test 
    public void testToString() {
        r = new Routine("Workout");
        Exercise e1 = new Exercise("Treadmill", "Warm up", ExerciseType.CARDIO);
        Exercise e2 = new Exercise("Bench Press", "", ExerciseType.CHEST);
        Exercise e3 = new Exercise("Curls", "", ExerciseType.ARMS);
        r.addExercise(e1);
        r.addExercise(e2);
        r.addExercise(e3);
        assertTrue(r.toString().equals("Workout\n" + 
                        "-------------\n" + 
                        "Exercise: Treadmill\n" + 
                        "Description: Warm up\n" + 
                        "Type: CARDIO\n" + 
                        "Exercise: Bench Press\n" + 
                        "Description: \n" + 
                        "Type: CHEST\n" + 
                        "Exercise: Curls\n" + 
                        "Description: \n" + 
                        "Type: ARMS\n"));    
    }
}
