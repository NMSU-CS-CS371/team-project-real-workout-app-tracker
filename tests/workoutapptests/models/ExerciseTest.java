package com.workoutapptests.models;

import com.workoutapp.models.Exercise;
import com.workoutapp.models.ExerciseType;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.*;

public class ExerciseTest {
    private Exercise cardio;
    private Exercise strength;

    @BeforeEach
    public void setUp() {
        System.out.println("Setting up ExerciseTest");
        cardio = new Exercise("Running", ExerciseType.CARDIO);
        strength = new Exercise("Bench Press", ExerciseType.STRENGTH);
    }

    @AfterEach
    public void tearDown() {
        System.out.println("Finished ExerciseTest");
        cardio = null;
        strength = null;
    }

    @Test
    public void testGetTags() {
        cardio.addTag("High-intensity");
        List<String> tag = new ArrayList<String>();
        tag.add("High-intensity");
        assertTrue(cardio.getTags().equals(tag));
    }

    @Test
    public void testNullName() {
        boolean failure = false;
        try {
            cardio = new Exercise(null, ExerciseType.CARDIO);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testEmptyName() {
        boolean failure = false;;
        try {
            cardio = new Exercise("", ExerciseType.CARDIO);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testNullType() {
        boolean failure = false;;
        try {
            cardio = new Exercise("Elliptical", null);
        } catch (IllegalArgumentException e) {
            failure = true;
        }
        assertTrue(failure);
    }

    @Test
    public void testSuccessfulCreation() {
        Exercise test = new Exercise("Test", ExerciseType.STRENGTH);
        assertTrue(test.getName().equals("Test"));
        assertTrue(test.getType() == ExerciseType.STRENGTH);
    }

    @Test
    public void testAddHasTag() {
        cardio.addTag("Vigorous");
        assertTrue(cardio.hasTag("Vigorous"));
    }

    @Test
    public void testNullEmptyTag() {
        boolean failure[] = new boolean[2];
        try {
            cardio.addTag("");
        } catch (IllegalArgumentException e) {
            failure[0] = true;
        } try {
            cardio.addTag(null);
        } catch (IllegalArgumentException e) {
            failure[1] = true;
        }
        assertTrue(failure[0] && failure[1]);
    }

    @Test
    public void testHasTag(){
        cardio.addTag("Elliptical");
        assertTrue(cardio.hasTag("Elliptical") && !cardio.hasTag("Treadmill"));
    }

    @Test
    public void testClearTag() {
        strength.clearTags();
        assertTrue(strength.getTags().isEmpty());
    }

    @Test
    public void testRemoveExistingTag() {
        strength.addTag("Test");
        assertTrue(strength.removeTag("Test"));
    }
    
    @Test
    public void testRemoveNonExistingTag() {
        strength.addTag("Test");
        assertTrue(!strength.removeTag("Fail"));
    }

    @Test
    public void testEqualsIdenticalTags(){
        Exercise cardio1 = new Exercise("Running", ExerciseType.CARDIO);
        cardio.addTag("Jog");
        cardio1.addTag("Jog");
        assertTrue(cardio.equals(cardio1));
    }

    @Test
    public void testEqualsDifferentTags(){
        Exercise cardio1 = new Exercise("Running", ExerciseType.CARDIO);
        cardio.addTag("Jog");
        cardio1.addTag("Sprint");
        assertTrue(cardio.equals(cardio1));       
    }

    @Test
    public void testEquals(){
        Exercise cardio1 = new Exercise("Running", ExerciseType.CARDIO); 
        assertTrue(cardio.equals(cardio1));       
    }

    @Test
    public void testEqualsDifferentNames(){
        Exercise cardio1 = new Exercise("Walking", ExerciseType.CARDIO);
        assertFalse(cardio.equals(cardio1));        
    }

    @Test
    public void testEqualsDifferentTypes(){
        Exercise strength1 = new Exercise("Running", ExerciseType.STRENGTH);
        assertFalse(cardio.equals(strength1));        
    }

    @Test
    public void testEqualsIdenticalHashCode(){
        Exercise cardio1 = new Exercise("Running", ExerciseType.CARDIO);
        assertTrue(cardio.hashCode() == cardio1.hashCode());        
    }

    @Test
    public void testEqualsDifferentHashCode(){
        assertFalse(cardio.hashCode() == strength.hashCode());        
    }
}
