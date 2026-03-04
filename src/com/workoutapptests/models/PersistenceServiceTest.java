package com.workoutapptests.models;

import com.workoutapp.services.PersistenceService;
import com.workoutapp.models.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceServiceTest {
    private PersistenceService service;
    private static final File EVENTS_FILE = new File("data/events.dat");

    @BeforeEach
    public void setUp() {
        service = new PersistenceService();
        // ensure clean state
        if (EVENTS_FILE.exists()) {
            EVENTS_FILE.delete();
        }
    }

    @AfterEach
    public void tearDown() {
        if (EVENTS_FILE.exists()) {
            EVENTS_FILE.delete();
        }
    }

    @Test
    public void testLoadEmptyWhenMissing() {
        List<CalendarEvent> loaded = service.loadEvents();
        assertNotNull(loaded);
        assertTrue(loaded.isEmpty());
    }

    @Test
    public void testSaveAndLoad() {
        Workout w = new Workout("Test");
        CalendarEvent ev = new CalendarEvent(LocalDateTime.now(), w, "note");
        List<CalendarEvent> events = new ArrayList<>();
        events.add(ev);

        service.saveEvents(events);
        assertTrue(EVENTS_FILE.exists());

        List<CalendarEvent> loaded = service.loadEvents();
        assertEquals(1, loaded.size());
        assertEquals("note", loaded.get(0).getNotes());
        assertEquals("Test", loaded.get(0).getWorkout().getTitle());
    }
}
