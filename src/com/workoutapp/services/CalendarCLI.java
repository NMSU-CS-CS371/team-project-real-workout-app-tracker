package com.workoutapp.services;

import com.workoutapp.models.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class CalendarCLI {
    private PersistenceService persistence = new PersistenceService();
    private List<CalendarEvent> events;
    private Scanner scanner = new Scanner(System.in);

    public CalendarCLI() {
        events = persistence.loadEvents();
    }

    public void run() {
        System.out.println("Workout Calendar CLI");
        while (true) {
            System.out.println("Commands: add, list, date, quit");
            System.out.print("> ");
            String cmd = scanner.nextLine().trim().toLowerCase();
            switch (cmd) {
                case "add": addEvent(); break;
                case "list": listAll(); break;
                case "date": listByDate(); break;
                case "quit":
                    persistence.saveEvents(events);
                    System.out.println("Saved. Bye.");
                    return;
                default:
                    System.out.println("Unknown command");
            }
        }
    }

    private void addEvent() {
        try {
            System.out.print("Title for workout: ");
            String title = scanner.nextLine().trim();

            System.out.print("Date (YYYY-MM-DD): ");
            String date = scanner.nextLine().trim();
            System.out.print("Time (HH:MM, 24h): ");
            String time = scanner.nextLine().trim();

            LocalDate d = LocalDate.parse(date);
            LocalTime t = LocalTime.parse(time);
            LocalDateTime dt = LocalDateTime.of(d, t);

            ExerciseInstance w = new ExerciseInstance(null, 0);

            CalendarEvent ev = new CalendarEvent(dt, w, "notes");
            events.add(ev);
            System.out.println("Event added: " + dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } catch (Exception ex) {
            System.out.println("Failed to add event: " + ex.getMessage());
        }
    }

    private void listAll() {
        if (events.isEmpty()) { System.out.println("No events"); return; }
        events.sort((a,b) -> a.getDateTime().compareTo(b.getDateTime()));
        for (CalendarEvent e : events) {
            System.out.println(e);
            System.out.println("---");
        }
    }

    private void listByDate() {
        try {
            System.out.print("Date (YYYY-MM-DD): ");
            String date = scanner.nextLine().trim();
            LocalDate d = LocalDate.parse(date);
            boolean found = false;
            for (CalendarEvent e : events) {
                if (e.getDateTime().toLocalDate().equals(d)) {
                    System.out.println(e);
                    System.out.println("---");
                    found = true;
                }
            }
            if (!found) System.out.println("No events on that date");
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
