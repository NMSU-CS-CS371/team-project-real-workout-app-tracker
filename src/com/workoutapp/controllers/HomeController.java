package com.workoutapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import com.workoutapp.services.*;
import java.util.LinkedList;
import com.workoutapp.models.*;
import java.time.format.DateTimeFormatter;

public class HomeController implements ScreenController {
    private MainController main;
    private CalendarService calendarService;

    @FXML private Label totalWorkoutsLabel;
    @FXML private Label totalExercisesLabel;
    @FXML private Label totalCardioMinutesLabel;
    @FXML private Label latestWorkoutLabel;
    @FXML private Label latestWorkoutDetailsLabel;
    @FXML private Button startWorkoutButton;
    @FXML private Button historyButton;
    @FXML private Button routineEditorButton;
    @FXML private VBox recoveryBox;

    @Override
    public void setMainController(MainController mainController){
        this.main = mainController;
        //Navigation buttons
        startWorkoutButton.setOnAction(e -> main.loadView("WorkoutStartView.fxml"));
        historyButton.setOnAction(e -> main.loadView("ReportView.fxml"));
        routineEditorButton.setOnAction(e -> main.loadView("RoutineEditorView.fxml"));
    }

    @Override
    public void onProfileChanged(String profileName) {
        if (profileName == null) return;

        calendarService = new CalendarService(profileName);
        loadDashboardSummary();

        // TODO: Load recovery suggestions here
    }

    private void loadDashboardSummary() {
        LinkedList<CalendarEvent> events = calendarService.getEvents();
        totalWorkoutsLabel.setText(String.valueOf(events.size()));

        int totalExercises = 0;
        int cardioMinutes = 0;
        for (CalendarEvent event : events) {
            Workout workout = event.getWorkout();
            if (workout == null) {
                continue;
            }
            for (ExerciseInstance instance : workout.getExercises()) {
                totalExercises++;
                if (instance.getDurationMinutes() > 0) {
                    cardioMinutes += instance.getDurationMinutes();
                }
            }
        }

        totalExercisesLabel.setText(String.valueOf(totalExercises));
        totalCardioMinutesLabel.setText(String.valueOf(cardioMinutes));

        if (events.isEmpty()) {
            latestWorkoutLabel.setText("No sessions yet");
            latestWorkoutDetailsLabel.setText("Complete a workout to see details here.");
            return;
        }

        CalendarEvent latest = events.getLast();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        String dateText = latest.getDateTime() == null ? "Unknown date" : latest.getDateTime().format(fmt);
        latestWorkoutLabel.setText(dateText);

        Workout latestWorkout = latest.getWorkout();
        int latestCount = latestWorkout == null ? 0 : latestWorkout.getExercises().size();
        String notes = (latest.getNotes() == null || latest.getNotes().isBlank()) ? "No notes." : latest.getNotes();
        latestWorkoutDetailsLabel.setText("Exercises: " + latestCount + "\nNotes: " + notes);
    }
}
