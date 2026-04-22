package com.workoutapp.controllers;

import com.workoutapp.models.CalendarEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.time.format.DateTimeFormatter;

public class ReportDetailController implements ScreenController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    @FXML private Button backButton;
    @FXML private Label titleLabel;

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

        if (selectedEvent == null || selectedEvent.getDateTime() == null) {
            titleLabel.setText("Workout Detail Report - " + (profileName == null ? "profile" : profileName));
            return;
        }

        String dateText = selectedEvent.getDateTime().format(DATE_FORMAT);
        titleLabel.setText("Workout Detail Report - " + dateText);
    }
}
