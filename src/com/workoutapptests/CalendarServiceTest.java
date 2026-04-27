package com.workoutapptests;

import com.workoutapp.models.*;
import com.workoutapp.services.CalendarService;
import org.junit.jupiter.api.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Verifies that CalendarService works properly. Tests adding events, removing events, getting events for 
 * a specific date and date range. 
 */
public class CalendarServiceTest {

    private static final String PROFILE = "testprofile";
    private CalendarService cs;

    @BeforeEach
    public void clean() {
        File dir = new File("data/" + PROFILE);
        if (dir.exists()) {
            for (File f : dir.listFiles()) f.delete();
        }
    }

    //Test adding workout event (uses empty workout)
    @Test
    public void testAddEvent() {
        cs = new CalendarService(PROFILE);
        CalendarEvent e = new CalendarEvent(LocalDateTime.now(), new Workout(), "Test");
        cs.addEvent(e);

        assertEquals(1, cs.getEvents().size());
    }

    //Test deleting workout event from calendar service
    @Test
    public void testRemoveEvent() {
        cs = new CalendarService(PROFILE);
        CalendarEvent e = new CalendarEvent(LocalDateTime.now(), new Workout(), "Test");
        cs.addEvent(e);
        cs.removeEvent(e);

        assertEquals(0, cs.getEvents().size());
    }

    //Test getting events for specific date 
    @Test
    public void testGetEventsForDate() {
        cs = new CalendarService(PROFILE);
        LocalDateTime dt = LocalDateTime.of(2024, 1, 1, 10, 0);
        cs.addEvent(new CalendarEvent(dt, new Workout(), "Test"));

        assertEquals(1, cs.getEventsForDate(LocalDate.of(2024, 1, 1)).size());
    }

    //Test getting events for specific date range 
    @Test
    public void testGetEventsInRange() {
        cs = new CalendarService(PROFILE);
        cs.addEvent(new CalendarEvent(LocalDateTime.of(2024, 1, 1, 10, 0), new Workout(), "A"));
        cs.addEvent(new CalendarEvent(LocalDateTime.of(2024, 1, 3, 10, 0), new Workout(), "B"));

        assertEquals(2, cs.getEventsInRange(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5)).size());
    }
}