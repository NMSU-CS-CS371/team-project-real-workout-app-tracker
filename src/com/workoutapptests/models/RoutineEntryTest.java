package com.workoutapptests.models;

import com.workoutapp.models.Exercise;
import com.workoutapp.models.ExerciseType;
import com.workoutapp.models.RoutineEntry;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.*;

/**
 * Class to test constructors and methods of models/RoutineEntry.java
 * Requires Exercise.java
*/

public class RoutineEntryTest {
    private RoutineEntry cEntry;    //Cardio entry
    private RoutineEntry sEntry;    //Strength entry
    private Exercise cardio;
    private Exercise strength;

    @BeforeEach
    public void setUp() {
        System.out.println("Setting up RoutineEntryTest");
        cardio = new Exercise("Running", ExerciseType.CARDIO); 
        strength = new Exercise("Bench Press", ExerciseType.STRENGTH); 
        cEntry = new RoutineEntry(cardio, 50, 50, 500);
        sEntry = new RoutineEntry(strength, 5, 5, 100, 120);
    }

    @AfterEach
    public void tearDown() {
        System.out.println("Finished RoutineEntryTest");
        cEntry = null;
        sEntry = null;
        cardio = null;
        strength = null;
    }

    //Invalid input on constructor
    @Test
    public void testStrengthNullExercise() {
        boolean failure = false;
        try {
            RoutineEntry entry = new RoutineEntry(null, 5, 8, 100, 120);
        } catch (NullPointerException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testCardioNullExercise() {
        boolean failure = false;
        try {
            RoutineEntry entry = new RoutineEntry(null, 50, 0, 500);
        } catch (NullPointerException e) {
            failure = true;
        }
        assertTrue(failure);
    }
    
    @Test
    public void testStrengthNegativeSets() {
        boolean failure = false;
        try {
            RoutineEntry entry = new RoutineEntry(strength, -5, 5, 100, 100);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }
 
    @Test
    public void testStrengthNegativeReps() {
        boolean failure = false;
        try {
           RoutineEntry entry = new RoutineEntry(strength, 5, -5, 10, 50);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testStrengthNegativeWeight() {
        boolean failure = false;
        try {
            RoutineEntry entry = new RoutineEntry(strength, 5, 5,-50, 50);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testStrengthNegativeRestTime() {
        boolean failure = false;
        try {
            RoutineEntry entry = new RoutineEntry(strength, 5, 5, 50, -500);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testCardioNegativeIntensity() {
        boolean failure = false;
        try {
            RoutineEntry entry = new RoutineEntry(cardio, -50, 50, 500);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testCardioNegativeRestTime() {
        boolean failure = false;
        try {
            RoutineEntry entry = new RoutineEntry(cardio, 50, -50, 500);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testCardioNegativeDuration() {
        boolean failure = false;
        try {
            RoutineEntry entry = new RoutineEntry(cardio, 50, 50, -500);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testCardioIntensityOutOfBounds() {
        boolean failure = false;
        try {
            RoutineEntry entry = new RoutineEntry(cardio, 101, 50, 500);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testCardioIntensityAtBounds() {
        RoutineEntry entry = new RoutineEntry(cardio, 100, 50, 500);
        RoutineEntry entry1 = new RoutineEntry(cardio, 0, 50, 500);
        assertTrue(entry.getWeight() == 100 && entry1.getWeight() == 0);
    }

    @Test
    public void testSuccessfulCardioCreation() {
        assertTrue(cEntry.getWeight() == 50 && cEntry.getDuration() == 500 && cEntry.getRestTime() == 50 && cEntry.getSets() == 0 && cEntry.getReps() == 0);
    }

    @Test
    public void testSuccessfulStrengthCreation() {
        assertTrue(sEntry.getWeight() == 100 && sEntry.getDuration() == 0 && sEntry.getRestTime() == 120 && sEntry.getSets() == 5 && sEntry.getReps() == 5);
    }

    @Test
    public void testSetExerciseSameType() {
        Exercise test = new Exercise("Elliptical", ExerciseType.CARDIO);
        cEntry.setExercise(test);
        assertTrue(cEntry.getExercise().equals(test));
    }

    @Test
    public void testSetExerciseStrength() {
        cEntry.setExercise(strength);
        assertTrue(cEntry.getExercise().equals(strength) && cEntry.getDuration() == 0);
    }

    @Test
    public void testSetExerciseCardio() {
        sEntry.setExercise(cardio);
        assertTrue(sEntry.getExercise().equals(cardio) && sEntry.getSets() == 0 && sEntry.getReps() == 0);
    }

    @Test
    public void testSetSetsStrength() {
        sEntry.setSets(7);
        assertTrue(sEntry.getSets() == 7);
    }

    @Test
    public void testSetSetsCardio() {
        cEntry.setSets(7);
        assertTrue(cEntry.getSets() == 0);
    }

    @Test
    public void testSetRepsStrength() {
        sEntry.setSets(7);
        assertTrue(sEntry.getSets() == 7);
    }

    @Test
    public void testSetRepsCardio() {
        cEntry.setReps(7);
        assertTrue(cEntry.getSets() == 0);
    }

    @Test
    public void testSetDurationStrength() {
        sEntry.setDuration(500);
        assertTrue(sEntry.getDuration() == 0);
    }

    @Test
    public void testSetDurationCardio() {
        cEntry.setDuration(700);
        assertTrue(cEntry.getDuration() == 700);
    }

    @Test
    public void testSetWeight() {
        sEntry.setWeight(50);
        assertTrue(sEntry.getWeight() == 50);
    }

    @Test
    public void testSetRestTime() {
        sEntry.setRestTime(250);
        assertTrue(sEntry.getRestTime() == 250);
    }


    @Test
    public void testSetIntensityTooHighCardio() {
        boolean failure = false;
        try {
            cEntry.setWeight(101);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testSetNegativeSets() {
        boolean failure = false;
        try {
            sEntry.setSets(-10);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testSetNegativeReps() {
        boolean failure = false;
        try {
            sEntry.setReps(-10);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testSetNegativeWeight() {
        boolean failure = false;
        try {
            sEntry.setWeight(-10);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testSetNegativeRestTime() {
        boolean failure = false;
        try {
            cEntry.setRestTime(-10);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testSetNegativeDuration() {
        boolean failure = false;
        try {
            cEntry.setDuration(-10);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testIsValidCardio(){
        assertTrue(cEntry.isValid());
    }

    @Test
    public void testIsValidStrength(){
        assertTrue(sEntry.isValid());
    }

    @Test
    public void testIsValidNonZeroCardio(){
        cEntry = null;
        cEntry = new RoutineEntry(strength, 5, -10, 5, 0);
        assertFalse(cEntry.isValid());
    }

}
