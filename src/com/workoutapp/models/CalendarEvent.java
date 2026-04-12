package com.workoutapp.models;

import java.io.Serializable;
import java.time.LocalDateTime;

// CalendarEvent represents a scheduled workout event, containing the date/time, associated workout, and any notes.
public class CalendarEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDateTime dateTime;
    private Workout workout;
    private String notes;
    
    public CalendarEvent() {}

    public CalendarEvent(LocalDateTime dateTime, Workout workout, String notes) {
        this.dateTime = dateTime;
        this.workout = workout;
        this.notes = notes;
    }
    // Getters and setters
    public LocalDateTime getDateTime() { 
        return dateTime; 
    }

    public Workout getWorkout() { 
        return workout; 
    }

    public String getNotes() { 
        return notes; 
    }

    public void setDateTime(LocalDateTime dateTime) { 
        this.dateTime = dateTime; 
    }

    public void setWorkout(Workout workout) { 
        this.workout = workout; 
    }
    public void setNotes(String notes) { 
        this.notes = notes; 
    }

    public String getRecoverySuggestion() {
        return RecoverySuggestion.suggestRecovery(this.workout);
    }
    
    // Simple text representation for debugging
    @Override
    public String toString() {
        return "Event[" + dateTime + "]\n" + (workout != null ? workout.toString() : "(no workout)") + "\nNotes: " + notes;
    }
    // Calendar event gets name of workout which references to the workout object, which is a collection of exerciseInstances. Should also include the date that should pull from system , and then also include notes for the workout. This will be used to populate the calendar view, which will show the name of the workout and the date, and then when clicked will show the details of the workout and any notes.
    // should be inputed into a CalendarService that will manage collection of CalendarEvents. Its code should be close to ExerciseService with a linked list of CalendarEvents and methods to remove and get events. It should also have a method to get events for a specific date, which will be used to populate the calendar view.
}
