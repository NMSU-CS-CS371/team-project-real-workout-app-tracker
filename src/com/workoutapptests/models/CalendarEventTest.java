package com.workoutapptests.models;

import com.workoutapp.models.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CalendarEventTest {
    private CalendarEvent event;
    private WorkoutRoutine workout;

    @BeforeEach
    public void setUp() {
        workout = new WorkoutRoutine("Morning", "");
        Exercise e = new Exercise("Pushups", ExerciseType.STRENGTH);
        ExerciseEvent r = new ExerciseEvent(e, 10, 5, 60, 600);
        r.setNotes("good");
        workout.addEntry(r);
        event = new CalendarEvent(LocalDateTime.of(2025,1,1,9,0), workout, "note1");
    }

    @AfterEach
    public void tearDown() {
        event = null;
        workout = null;
    }
    
    @Test // also tests constructor and null handling for workout parameter
    public void testGettersSetters() {
        assertEquals(LocalDateTime.of(2025,1,1,9,0), event.getDateTime());
        assertEquals(workout, event.getWorkout());
        assertEquals("note1", event.getNotes());

        LocalDateTime newTime = LocalDateTime.of(2025,1,2,10,0);
        event.setDateTime(newTime);
        event.setNotes("other");
        WorkoutRoutine w2 = new WorkoutRoutine("Evening", "");
        event.setWorkout(w2);

        assertEquals(newTime, event.getDateTime());
        assertEquals("other", event.getNotes());
        assertEquals(w2, event.getWorkout());
    }

    @Test // also tests toString includes key info and handles null workout 
    public void testToStringContainsInfo() {
        String s = event.toString();
        assertTrue(s.contains("2025-01-01"));
        assertTrue(s.contains("Workout: Morning"));
        assertTrue(s.contains("note1"));
    }

    @Test // also tests that workout can be null and toString handles it
    public void testNullWorkout() {
        CalendarEvent e2 = new CalendarEvent(LocalDateTime.now(), null, "no workout");
        assertNull(e2.getWorkout());
        String s = e2.toString();
        assertTrue(s.contains("(no workout)"));
    }
    public void testSerialization() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(event);
        oos.close();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object obj = ois.readObject();
        assertTrue(obj instanceof CalendarEvent);
        CalendarEvent e2 = (CalendarEvent) obj;
        assertEquals(event.getDateTime(), e2.getDateTime());
        assertEquals(event.getNotes(), e2.getNotes());
        assertEquals(event.getWorkout().getName(), e2.getWorkout().getName());
    }
}
