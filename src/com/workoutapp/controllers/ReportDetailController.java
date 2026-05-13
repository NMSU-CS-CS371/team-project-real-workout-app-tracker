package com.workoutapp.controllers;

import com.workoutapp.models.CalendarEvent;
import com.workoutapp.models.ExerciseInstance;
import com.workoutapp.models.ExerciseType;
import com.workoutapp.models.Workout;
import com.workoutapp.models.WorkoutSet;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;

/* Controller for the workout detail report screen, which displays information about a specific workout
* Including exercises completed, sets/reps/weight, cardio details, and provides navigation to exercise-specific progress charts
*/

public class ReportDetailController implements ScreenController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    @FXML private Button backButton;
    @FXML private Label titleLabel;
    @FXML private ScrollPane reportScrollPane;
    @FXML private VBox reportContentBox;

    private MainController main;
    private String profileName;
    private CalendarEvent selectedEvent;

    // Connects the controller to the main app controller and wires navigation.
    @Override
    public void setMainController(MainController mainController) {
        this.main = mainController;

        backButton.setOnAction(e -> main.loadView("ReportView.fxml"));
    }

    // Stores the currently active profile when the profile context changes.
    @Override
    public void onProfileChanged(String profileName) {
        this.profileName = profileName;
    }

    // Loads the selected workout event and updates the header text.
    public void loadEvent(String profileName, CalendarEvent event) {
        this.profileName = profileName;
        this.selectedEvent = event;

        // Fallback header text when no workout is selected.
        if (selectedEvent == null || selectedEvent.getDateTime() == null) {
            titleLabel.setText("Workout Detail Report - " + (profileName == null ? "profile" : profileName));
            if (reportContentBox != null) {
                reportContentBox.getChildren().clear();
            }
            return;
        }

        // Header/title text format for selected workout date.
        String dateText = selectedEvent.getDateTime().format(DATE_FORMAT);
        titleLabel.setText("Workout Detail Report - " + dateText);

        if (reportContentBox == null) {
            return;
        }

        reportContentBox.getChildren().clear();

        Workout workout = selectedEvent.getWorkout();
        if (workout == null) {
            return;
        }

        Label exercisesHeader = new Label("Exercises");
        exercisesHeader.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #e2e8f0;");
        reportContentBox.getChildren().add(exercisesHeader);

        for (ExerciseInstance exercise : workout.getExercises()) {
            VBox exerciseBox = new VBox(6);
            exerciseBox.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12;");

            Label exerciseName = new Label(exercise.getExerciseName());
            exerciseName.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
            Button progressButton = new Button("View Progress");
            progressButton.setCursor(Cursor.HAND);
            progressButton.setOnAction(e -> main.loadProgressReportView(profileName, exercise.getExerciseName()));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox headerRow = new HBox(10, exerciseName, spacer, progressButton);
            exerciseBox.getChildren().add(headerRow);

            if (exercise.getExerciseType() == ExerciseType.CARDIO) {
                VBox cardioDetails = new VBox(4);
                cardioDetails.getChildren().add(new Label(exercise.getDurationMinutes() + " minutes"));
                if (exercise.getDistance() > 0) {
                    cardioDetails.getChildren().add(new Label(String.format("%.2f miles", exercise.getDistance())));
                }
                exerciseBox.getChildren().add(cardioDetails);
                reportContentBox.getChildren().add(exerciseBox);
                continue;
            }

            int setNumber = 1;
            for (WorkoutSet set : exercise.getWorkoutSets()) {
                Label setLabel = new Label("Set " + setNumber + ": " + set.getReps() + " reps @ " + set.getWeight() + " lbs");
                exerciseBox.getChildren().add(setLabel);
                setNumber++;
            }

            reportContentBox.getChildren().add(exerciseBox);
        }
    }
}
