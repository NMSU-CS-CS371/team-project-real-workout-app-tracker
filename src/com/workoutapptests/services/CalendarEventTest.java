package com.workoutapptests.services;

import com.workoutapp.models.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CalendarEventTest {
    private CalendarEvent event;
    private Workout workout;
    private ExerciseInstance ex1, ex2;

    @BeforeEach
    public void setUp() {
        // Create sample exercises
        Exercise benchPress = new Exercise("Bench Press", "Chest exercise", ExerciseType.CHEST);
        Exercise squats = new Exercise("Squats", "Leg exercise", ExerciseType.LEGS);

        // Create exercise instances
        ex1 = new ExerciseInstance(benchPress, 3, 10, 135.0); // Strength exercise
        ex2 = new ExerciseInstance(squats, 4, 8, 185.0); // Strength exercise

        // Create workout
        workout = new Workout();
        workout.addExercise(ex1);
        workout.addExercise(ex2);

        // Create calendar event
        event = new CalendarEvent(LocalDateTime.of(2024, 3, 11, 10, 0), workout, "Morning workout");
    }

    @AfterEach
    public void tearDown() {
        event = null;
        workout = null;
        ex1 = null;
        ex2 = null;
    }

    @Test
    public void testCalendarEventCreation() {
        assertNotNull(event);
        assertEquals(LocalDateTime.of(2024, 3, 11, 10, 0), event.getDateTime());
        assertEquals(workout, event.getWorkout());
        assertEquals("Morning workout", event.getNotes());
    }

    @Test
    public void testCalendarEventGettersAndSetters() {
        // Test getters
        assertEquals(LocalDateTime.of(2024, 3, 11, 10, 0), event.getDateTime());
        assertEquals(workout, event.getWorkout());
        assertEquals("Morning workout", event.getNotes());

        // Test setters
        LocalDateTime newDateTime = LocalDateTime.of(2024, 3, 12, 14, 30);
        Workout newWorkout = new Workout();
        String newNotes = "Afternoon workout";

        event.setDateTime(newDateTime);
        event.setWorkout(newWorkout);
        event.setNotes(newNotes);

        assertEquals(newDateTime, event.getDateTime());
        assertEquals(newWorkout, event.getWorkout());
        assertEquals(newNotes, event.getNotes());
    }

    @Test
    public void testCalendarEventToString() {
        String expected = "Event[2024-03-11T10:00]\n" + workout.toString() + "\nNotes: Morning workout";
        assertEquals(expected, event.toString());
    }

    @Test
    public void testCalendarEventWithNullWorkout() {
        CalendarEvent nullWorkoutEvent = new CalendarEvent(LocalDateTime.of(2024, 3, 11, 10, 0), null, "Rest day");
        String expected = "Event[2024-03-11T10:00]\n(no workout)\nNotes: Rest day";
        assertEquals(expected, nullWorkoutEvent.toString());
    }
}
