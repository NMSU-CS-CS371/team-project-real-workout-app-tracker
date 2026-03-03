package com.workoutapp.services;

import com.workoutapp.ui.CalendarEvent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// PersistenceService handles saving and loading of calendar events to/from a file using Java's serialization mechanism.
public class PersistenceService {
    private static final String DATA_DIR = "data";
    private static final String EVENTS_FILE = "data/events.dat";

    public List<CalendarEvent> loadEvents() {
        Path p = Paths.get(EVENTS_FILE);
        if (!Files.exists(p)) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(p.toFile()))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                // unchecked cast but safe if file created by saveEvents
                return (List<CalendarEvent>) obj;
            }
        } catch (Exception e) {
            System.err.println("Failed to load events: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    // Saves the list of calendar events to a file using serialization. Creates the data directory if it doesn't exist.
    public void saveEvents(List<CalendarEvent> events) {
        try {
            Path dir = Paths.get(DATA_DIR);
            if (!Files.exists(dir)) Files.createDirectories(dir);

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EVENTS_FILE))) {
                oos.writeObject(events);
            }
        } catch (IOException e) {
            System.err.println("Failed to save events: " + e.getMessage());
        }
    }
}
