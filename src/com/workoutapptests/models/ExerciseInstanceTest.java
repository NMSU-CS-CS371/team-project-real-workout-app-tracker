package com.workoutapptests.models;

import com.workoutapp.models.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExerciseInstanceTest {
    private ExerciseInstance ei;
    private Exercise e;

    @BeforeEach
    public void setUp() {
        System.out.println("Test started");
        ei = null;
        e = null;
    }

    @AfterEach
    public void tearDown() {
        System.out.println("Test finished");
    }

    //Test cardio instance with invalid type
    @Test 
    public void testInvalidTypeCardioConstructor() {
        boolean error = false;
        try {
            e = new Exercise("Treadmill", "", ExerciseType.LEGS);
            ei = new ExerciseInstance(e, 30);
        } catch (IllegalArgumentException e) {
            error = true;
        }
        assertTrue(error);
    }

    //Test strength instance with CARDIO type
    @Test 
    public void testInvalidTypeStrengthConstructor() {
        boolean error = false;
        try {
            e = new Exercise("BenchPress", "", ExerciseType.CARDIO);
            ei = new ExerciseInstance(e, 5, 5, 45);
        } catch (IllegalArgumentException e) {
            error = true;
        }
        assertTrue(error);
    }

    //Test cardio instance with negative duration
    @Test 
    public void testCardioConstructorNegativeDuration() {
        boolean error = false;
        try {
            e = new Exercise("Treadmill", "", ExerciseType.CARDIO);
            ei = new ExerciseInstance(e, -1);
        } catch (IllegalArgumentException e) {
            error = true;
        }
        assertTrue(error);
    }

    //Test strength instance with negative sets, reps, and weight
    @Test 
    public void testStrengthConstructorNegativeParams() {
        boolean errorSets = false;
        boolean errorReps = false;
        boolean errorWeight = false;
        e = new Exercise("Bench Press", "", ExerciseType.CHEST);
        try {
            ei = new ExerciseInstance(e, -1, 5, 45);
        } catch (IllegalArgumentException e) {
            errorSets = true;
        }
        
        try {
            ei = new ExerciseInstance(e, 5, -1, 45);
        } catch (IllegalArgumentException e) {
            errorReps = true;
        }

        try {
            ei = new ExerciseInstance(e, 5, 5, -1);
        } catch (IllegalArgumentException e) {
            errorWeight = true;
        }
        assertTrue(errorSets);
        assertTrue(errorReps);
        assertTrue(errorWeight);
    }

    //Test cardio instance constructor + getters
    @Test 
    public void testCardioConstructor() {
        e = new Exercise("Treadmill", "", ExerciseType.CARDIO);
        ei = new ExerciseInstance(e, 60);
        assertTrue(ei.getExercise() == e);
        assertTrue(ei.getExerciseName().equals("Treadmill"));
        assertTrue(ei.getExerciseType().equals(ExerciseType.CARDIO));
        assertEquals(60, ei.getDurationMinutes());
        assertEquals(0, ei.getReps());
        assertEquals(0, ei.getSets());
        assertEquals(0, ei.getWeight());
    }

    //Test strength instance constructor + getters
    @Test 
    public void testStrengthConstructor() {
        e = new Exercise("Bench Press", "", ExerciseType.CHEST);
        ei = new ExerciseInstance(e, 5, 5, 45);
        assertTrue(ei.getExercise() == e);
        assertTrue(ei.getExerciseName().equals("Bench Press"));
        assertTrue(ei.getExerciseType().equals(ExerciseType.CHEST));
        assertEquals(0, ei.getDurationMinutes());
        assertEquals(5, ei.getReps());
        assertEquals(5, ei.getSets());
        assertEquals(45, ei.getWeight());
    }
    
    //Test setters for cardio instance - duration
    @Test 
    public void testCardioSetters() {
        e = new Exercise("Treadmill", "", ExerciseType.CARDIO);
        ei = new ExerciseInstance(e, 60);
        ei.setDurationMinutes(45);
        assertEquals(45, ei.getDurationMinutes());
    }
    
    //Test setters for strength instance - sets, reps, weight
    @Test 
    public void testStrengthSetters() {
        e = new Exercise("Bench Press", "", ExerciseType.CHEST);
        ei = new ExerciseInstance(e, 5, 5, 45);
        ei.setSets(8);
        ei.setReps(7);
        ei.setWeight(65);
        assertEquals(8, ei.getSets());
        assertEquals(7, ei.getReps());
        assertEquals(65, ei.getWeight());
    }
    
    //Test toString for cardio instance
    @Test 
    public void testToStringCardio() {
        e = new Exercise("Treadmill", "", ExerciseType.CARDIO);
        ei = new ExerciseInstance(e, 60);
        String s = ei.toString();
        assertTrue(s.equals("Exercise: Treadmill | Duration: 60 min"));
    }

    //Test toString for strength instance
    @Test 
    public void testToStringStrength() {
        e = new Exercise("Bench Press", "", ExerciseType.CHEST);
        ei = new ExerciseInstance(e, 5, 5, 45);
        String s = ei.toString();
        assertTrue(s.equals("Exercise: Bench Press | Sets: 5 | Reps: 5 | Weight: 45.0"));
    }
}
