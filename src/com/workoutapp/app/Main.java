package com.workoutapp.app;

import com.workoutapp.services.CalendarCLI;
import com.workoutapp.models.*;
import java.util.*;

public class Main{
    public static void main(String[] args){
        CalendarCLI cli = new CalendarCLI();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Workout Calendar CLI");
        while (true) {
            System.out.println("Commands: add, list, date, quit");
            System.out.print("> ");
            String cmd = scanner.nextLine().trim().toLowerCase();
            
            switch (cmd) {
                case "add": 
                    ExerciseInstance e = inputExercise(scanner);
                    cli.addEvent(e); break;
                case "list": cli.listAll(); break;
                case "date": cli.listByDate(); break;
                case "quit":
                    cli.persistence.saveEvents(cli.getEvents());
                    System.out.println("Saved. Bye.");
                    return;
                default:
                    System.out.println("Unknown command");
            }
        }
    }    

    public static ExerciseInstance inputExercise(Scanner scanner){
        System.out.println("Enter exercise name: ");
        String name = scanner.nextLine();
        System.out.println("Enter exercise description: ");
        String description = scanner.nextLine();
        System.out.print("Enter 'C' for cardio exercise (Enter to skip): ");
        String type = scanner.nextLine();
        if(type.equals("") || type == null) {
            type = "S";}
        if(type.charAt(0) == 'C'){
            Exercise cardio = new Exercise(name, description, ExerciseType.CARDIO);
            System.out.print("Enter duration of Exercise (in minutes): ");
            String duration = scanner.nextLine();
            int dur = Integer.parseInt(duration);
            ExerciseInstance e = new ExerciseInstance(cardio, dur);
            return e;
        } else{
            Exercise strength = new Exercise(name, description, ExerciseType.CORE);
            System.out.print("Enter <sets> <reps> <weight>: ");
            String input = scanner.nextLine();
            String[] parts = input.split("\\s+", 3);
            ExerciseInstance e = new ExerciseInstance(strength, Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            return e;
        }
    }
}