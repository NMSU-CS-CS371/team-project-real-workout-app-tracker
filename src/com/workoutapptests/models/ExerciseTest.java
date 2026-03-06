package com.workoutapptests.models;

import com.workoutapp.models.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for class models.Exercise to cover methods 
 */
public class ExerciseTest {
    @BeforeEach
    public void setUp() {
        System.out.println("Test started");
    }

    @AfterEach
    public void tearDown() {
        System.out.println("Test finished");
    }
    
    @Test 
    public void testGetters() {
        Exercise e = new Exercise("Treadmill", "Incline: 3%, Speed 4 mph", ExerciseType.CARDIO);
        assertTrue(e.getName().equals("Treadmill"));
        assertTrue(e.getDesc().equals("Incline: 3%, Speed 4 mph"));
        assertTrue(e.getType() == ExerciseType.CARDIO);
    }

    @Test 
    public void testSetName() {
        Exercise e = new Exercise("Treadmill", "", ExerciseType.CARDIO);
        e.setName("Elliptical");
        assertTrue(e.getName().equals("Elliptical"));
    }

    @Test 
    public void testSetDesc() {
        Exercise e = new Exercise("Treadmill", "Treadmill", ExerciseType.CARDIO);
        e.setDesc("Incline walking");
        assertTrue(e.getDesc().equals("Incline walking"));
    }

    @Test 
    public void testMoreTypes() {
        Exercise[] e = new Exercise[7];
        e[0] = new Exercise("Treadmill", "", ExerciseType.CARDIO);
        e[1] = new Exercise("Bicep Curls", "", ExerciseType.ARMS);
        e[2] = new Exercise("Squats", "", ExerciseType.LEGS);
        e[3] = new Exercise("Planks", "", ExerciseType.CORE);
        e[4] = new Exercise("Bench Press", "", ExerciseType.CHEST);
        e[5] = new Exercise("Overhead Press", "", ExerciseType.SHOULDERS);
        e[6] = new Exercise("Pull Ups", "", ExerciseType.BACK);
        assertTrue(e[0].getType() == ExerciseType.CARDIO);
        assertTrue(e[1].getType() == ExerciseType.ARMS);
        assertTrue(e[2].getType() == ExerciseType.LEGS);
        assertTrue(e[3].getType() == ExerciseType.CORE);
        assertTrue(e[4].getType() == ExerciseType.CHEST);
        assertTrue(e[5].getType() == ExerciseType.SHOULDERS);
        assertTrue(e[6].getType() == ExerciseType.BACK);
    }

    @Test 
    public void testToString() {
        Exercise e = new Exercise("Bench Press", "Spotter required", ExerciseType.CHEST);
        assertTrue(e.toString().equals("Exercise: Bench Press\nDescription: Spotter required\nType: CHEST"));
    }
}
