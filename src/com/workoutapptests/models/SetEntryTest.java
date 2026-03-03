package com.workoutapptests.models;

import com.workoutapp.models.SetEntry;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class SetEntryTest {
    private SetEntry entry;

    @BeforeEach
    public void setUp() {
        entry = new SetEntry(5, 100.0, "notes");
    }

    @AfterEach
    public void tearDown() {
        entry = null;
    }

    @Test
    public void testGetters() {
        assertEquals(5, entry.getReps());
        assertEquals(100.0, entry.getWeight());
        assertEquals("notes", entry.getNotes());
    }

    @Test
    public void testSetters() {
        entry.setReps(8);
        entry.setWeight(120.0);
        entry.setNotes("ok");
        assertEquals(8, entry.getReps());
        assertEquals(120.0, entry.getWeight());
        assertEquals("ok", entry.getNotes());
    }

    @Test
    public void testToStringIncludesValues() {
        String s = entry.toString();
        assertTrue(s.contains("reps=5"));
        assertTrue(s.contains("weight=100.0"));
    }
}
