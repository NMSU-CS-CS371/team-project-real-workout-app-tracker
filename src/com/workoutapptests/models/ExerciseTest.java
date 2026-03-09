package com.workoutapptests.models;

import com.workoutapp.models.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for class models.Exercise to cover methods 
 */
public class ExerciseTest {
    private Exercise e;

    @BeforeEach
    public void setUp() {
        e = null;
        System.out.println("Test started");
    }

    @AfterEach
    public void tearDown() {
        System.out.println("Test finished");
    }
    
    @Test 
    public void testGetters() {
        e = new Exercise("Treadmill", "Incline: 3%, Speed 4 mph", ExerciseType.CARDIO);
        assertTrue(e.getName().equals("Treadmill"));
        assertTrue(e.getDesc().equals("Incline: 3%, Speed 4 mph"));
        assertTrue(e.getType() == ExerciseType.CARDIO);
    }

    @Test 
    public void testSetName() {
        e = new Exercise("Treadmill", "", ExerciseType.CARDIO);
        e.setName("Elliptical");
        assertTrue(e.getName().equals("Elliptical"));
    }

    @Test 
    public void testSetDesc() {
        e = new Exercise("Treadmill", "Treadmill", ExerciseType.CARDIO);
        e.setDesc("Incline walking");
        assertTrue(e.getDesc().equals("Incline walking"));
    }

    @Test 
    public void testMoreTypes() {
        Exercise[] exercises = new Exercise[7];
        exercises[0] = new Exercise("Treadmill", "", ExerciseType.CARDIO);
        exercises[1] = new Exercise("Bicep Curls", "", ExerciseType.ARMS);
        exercises[2] = new Exercise("Squats", "", ExerciseType.LEGS);
        exercises[3] = new Exercise("Planks", "", ExerciseType.CORE);
        exercises[4] = new Exercise("Bench Press", "", ExerciseType.CHEST);
        exercises[5] = new Exercise("Overhead Press", "", ExerciseType.SHOULDERS);
        exercises[6] = new Exercise("Pull Ups", "", ExerciseType.BACK);
        assertTrue(exercises[0].getType() == ExerciseType.CARDIO);
        assertTrue(exercises[1].getType() == ExerciseType.ARMS);
        assertTrue(exercises[2].getType() == ExerciseType.LEGS);
        assertTrue(exercises[3].getType() == ExerciseType.CORE);
        assertTrue(exercises[4].getType() == ExerciseType.CHEST);
        assertTrue(exercises[5].getType() == ExerciseType.SHOULDERS);
        assertTrue(exercises[6].getType() == ExerciseType.BACK);
    }

    @Test 
    public void testToString() {
        e = new Exercise("Bench Press", "Spotter required", ExerciseType.CHEST);
        assertTrue(e.toString().equals("Exercise: Bench Press\nDescription: Spotter required\nType: CHEST"));
    }
}
