package com.workoutapptests.services;

import com.workoutapp.models.*;
import com.workoutapp.services.CalendarService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class CalendarServiceTest {
    private CalendarService calendarService;
    private CalendarEvent event1, event2, event3;
    private Workout workout1, workout2, workout3;

    @BeforeEach
    public void setUp() {
        calendarService = new CalendarService();

        // Create exercises
        Exercise benchPress = new Exercise("Bench Press", "Chest exercise", ExerciseType.CHEST);
        Exercise squats = new Exercise("Squats", "Leg exercise", ExerciseType.LEGS);
        Exercise running = new Exercise("Running", "Cardio exercise", ExerciseType.CARDIO);

        // Create exercise instances
        ExerciseInstance ex1 = new ExerciseInstance(benchPress, 3, 10, 135.0);
        ExerciseInstance ex2 = new ExerciseInstance(squats, 4, 8, 185.0);
        ExerciseInstance ex3 = new ExerciseInstance(running, 30);

        // Create workouts
        workout1 = new Workout();
        workout1.addExercise(ex1);

        workout2 = new Workout();
        workout2.addExercise(ex2);

        workout3 = new Workout();
        workout3.addExercise(ex3);

        // Create calendar events
        event1 = new CalendarEvent(LocalDateTime.of(2024, 3, 11, 10, 0), workout1, "Morning workout");
        event2 = new CalendarEvent(LocalDateTime.of(2024, 3, 11, 18, 30), workout2, "Evening workout");
        event3 = new CalendarEvent(LocalDateTime.of(2024, 3, 12, 7, 0), workout3, "Early morning cardio");
    }

    @AfterEach
    public void tearDown() {
        calendarService = null;
        event1 = null;
        event2 = null;
        event3 = null;
        workout1 = null;
        workout2 = null;
        workout3 = null;
    }

    @Test
    public void testCalendarServiceInitialization() {
        assertNotNull(calendarService);
        assertNotNull(calendarService.getEvents());
    }

    @Test
    public void testAddEvent() {
        calendarService.addEvent(event1);
        LinkedList<CalendarEvent> events = calendarService.getEvents();
        assertTrue(events.contains(event1));
        assertEquals(1, events.size());
    }

    @Test
    public void testAddMultipleEvents() {
        calendarService.addEvent(event1);
        calendarService.addEvent(event2);
        calendarService.addEvent(event3);

        LinkedList<CalendarEvent> events = calendarService.getEvents();
        assertEquals(3, events.size());
        assertTrue(events.contains(event1));
        assertTrue(events.contains(event2));
        assertTrue(events.contains(event3));
    }

    @Test
    public void testAddDuplicateEvents() {
        // Should allow duplicates for multiple workouts per day
        calendarService.addEvent(event1);
        calendarService.addEvent(event1);

        LinkedList<CalendarEvent> events = calendarService.getEvents();
        assertEquals(2, events.size());
    }

    @Test
    public void testRemoveEvent() {
        calendarService.addEvent(event1);
        calendarService.addEvent(event2);

        calendarService.removeEvent(event1);
        LinkedList<CalendarEvent> events = calendarService.getEvents();
        assertFalse(events.contains(event1));
        assertTrue(events.contains(event2));
        assertEquals(1, events.size());
    }

    @Test
    public void testRemoveEventAt() {
        calendarService.addEvent(event1);
        calendarService.addEvent(event2);
        calendarService.addEvent(event3);

        calendarService.removeEventAt(1);
        LinkedList<CalendarEvent> events = calendarService.getEvents();
        assertEquals(2, events.size());
        assertFalse(events.contains(event2));
        assertTrue(events.contains(event1));
        assertTrue(events.contains(event3));
    }

    @Test
    public void testRemoveEventAtInvalidIndex() {
        calendarService.addEvent(event1);

        // Should not throw exception, just do nothing
        calendarService.removeEventAt(5);
        assertEquals(1, calendarService.getEvents().size());
    }

    @Test
    public void testGetEventsForDate() {
        calendarService.addEvent(event1);
        calendarService.addEvent(event2);
        calendarService.addEvent(event3);

        LinkedList<CalendarEvent> eventsFor3_11 = calendarService.getEventsForDate(LocalDate.of(2024, 3, 11));
        assertEquals(2, eventsFor3_11.size());
        assertTrue(eventsFor3_11.contains(event1));
        assertTrue(eventsFor3_11.contains(event2));

        LinkedList<CalendarEvent> eventsFor3_12 = calendarService.getEventsForDate(LocalDate.of(2024, 3, 12));
        assertEquals(1, eventsFor3_12.size());
        assertTrue(eventsFor3_12.contains(event3));
    }

    @Test
    public void testGetEventsForDateNoEvents() {
        calendarService.addEvent(event1);

        LinkedList<CalendarEvent> eventsFor3_15 = calendarService.getEventsForDate(LocalDate.of(2024, 3, 15));
        assertEquals(0, eventsFor3_15.size());
    }

    @Test
    public void testGetEventsInRange() {
        calendarService.addEvent(event1);
        calendarService.addEvent(event2);
        calendarService.addEvent(event3);

        LinkedList<CalendarEvent> eventsInRange = calendarService.getEventsInRange(
                LocalDate.of(2024, 3, 11),
                LocalDate.of(2024, 3, 12)
        );

        assertEquals(3, eventsInRange.size());
        assertTrue(eventsInRange.contains(event1));
        assertTrue(eventsInRange.contains(event2));
        assertTrue(eventsInRange.contains(event3));
    }

    @Test
    public void testGetEventsInRangePartial() {
        calendarService.addEvent(event1);
        calendarService.addEvent(event2);
        calendarService.addEvent(event3);

        LinkedList<CalendarEvent> eventsInRange = calendarService.getEventsInRange(
                LocalDate.of(2024, 3, 11),
                LocalDate.of(2024, 3, 11)
        );

        assertEquals(2, eventsInRange.size());
        assertTrue(eventsInRange.contains(event1));
        assertTrue(eventsInRange.contains(event2));
        assertFalse(eventsInRange.contains(event3));
    }

    @Test
    public void testGetEventsInRangeEmpty() {
        calendarService.addEvent(event1);

        LinkedList<CalendarEvent> eventsInRange = calendarService.getEventsInRange(
                LocalDate.of(2024, 3, 15),
                LocalDate.of(2024, 3, 20)
        );

        assertEquals(0, eventsInRange.size());
    }

    @Test
    public void testDuplicateEventsOnSameDay() {
        // Create a second event for the same day as event1 but at different time
        CalendarEvent event1Duplicate = new CalendarEvent(
                LocalDateTime.of(2024, 3, 11, 14, 0),
                workout1,
                "Another morning workout"
        );

        calendarService.addEvent(event1);
        calendarService.addEvent(event1Duplicate);

        LinkedList<CalendarEvent> eventsFor3_11 = calendarService.getEventsForDate(LocalDate.of(2024, 3, 11));
        assertEquals(2, eventsFor3_11.size());
    }
}
