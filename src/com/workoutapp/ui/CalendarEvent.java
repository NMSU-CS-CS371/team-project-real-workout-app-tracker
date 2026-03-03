package models;

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
    public LocalDateTime getDateTime() { return dateTime; }
    public Workout getWorkout() { return workout; }
    public String getNotes() { return notes; }

    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public void setWorkout(Workout workout) { this.workout = workout; }
    public void setNotes(String notes) { this.notes = notes; }
    // Simple text representation for debugging
    @Override
    public String toString() {
        return "Event[" + dateTime + "]\n" + (workout != null ? workout.toString() : "(no workout)") + "\nNotes: " + notes;
    }
}
