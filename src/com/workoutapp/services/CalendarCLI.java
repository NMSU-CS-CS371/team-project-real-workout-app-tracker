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

            //Create exercise routine
            WorkoutRoutine r = new WorkoutRoutine();
            while (true) { 
                System.out.print("Enter exercise name (or empty to finish): ");
                String ename = scanner.nextLine().trim();
                if (ename.isEmpty()) break;

                System.out.print("Enter C for cardio or S for strength: ");
                String etype = scanner.nextLine();
                while(true){        //Strength
                    if(etype.equals("S")){
                        r.addExercise(new Exercise(ename, ExerciseType.STRENGTH));
                        break;
                    } else if(etype.equals("C")){       //Cardio
                        r.addExercise(new Exercise(ename, ExerciseType.CARDIO));
                        break;
                    }
                    System.out.print("Enter C for cardio or S for strength: ");
                    etype = scanner.nextLine();
                }
            }

            //Create workoutRoutine
            System.out.print("Enter workout description:");
            String description = scanner.nextLine().trim();
            WorkoutEvent w = new WorkoutEvent(title, description, r);

            for(int i = 0; i< r.getNumEntries(); i++){
                if(r.getExercise(i).getType() == ExerciseType.STRENGTH){
                    while (true) { 
                        System.out.print("  Log exercise " + r.getExercise(i).getName() + " as 'sets reps restTime weight notes': ");
                        String line = scanner.nextLine().trim();
                        String[] parts = line.split("\\s+", 5);

                        if (parts.length == 5) {
                            int sets = Integer.parseInt(parts[0]);
                            int reps = Integer.parseInt(parts[1]);
                            int rest = Integer.parseInt(parts[2]);
                            double weight = parts.length > 3 ? Double.parseDouble(parts[3]) : 0.0;
                            String notes = parts.length > 4 ? parts[4] : "";

                            w.addEntry(new ExerciseEvent(r.getExercise(i), sets, reps, weight, rest, notes));
                            break;
                        }
                    }
                } else if(r.getExercise(i).getType() == ExerciseType.CARDIO){
                   while (true) { 
                        System.out.print("  Log exercise " + r.getExercise(i).getName() + " as 'intesity duration restTime notes': ");
                        String line = scanner.nextLine().trim();
                        String[] parts = line.split("\\s+", 4);

                        if (parts.length == 4) {
                            int intensity = Integer.parseInt(parts[0]);
                            int duration = Integer.parseInt(parts[1]);
                            int rest = Integer.parseInt(parts[2]);
                            String notes = parts.length > 3 ? parts[3] : "";

                            w.addEntry(new ExerciseEvent(r.getExercise(i), intensity, rest, duration, notes));
                            break;
                        }
                    }
                }
            }

            System.out.print("Notes for event: ");
            String notes = scanner.nextLine().trim();

            CalendarEvent ev = new CalendarEvent(dt, w, notes);
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
