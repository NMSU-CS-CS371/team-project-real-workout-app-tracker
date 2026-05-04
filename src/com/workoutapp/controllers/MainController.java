package com.workoutapp.controllers;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import com.workoutapp.services.*;
import com.workoutapp.models.*;

/**
 * Central controller responsible for profile management, view loading,
 * and coordinating communication between screens. Handles profile selection,
 * initialization of services, and switching between major UI views including
 * workout screens and settings.
 */

public class MainController {
    @FXML private Button profileSettingsButton;
    @FXML private Button themeToggleButton;
    @FXML private ComboBox<String> profileDropDown;
    @FXML private Label activeProfileLabel;
    @FXML private StackPane contentPane;

    private ProfileService profileService;
    private String currentProfile;    
    private ScreenController currentScreenController;
    private CalendarEvent lastReportEvent;

    @FXML
    public void initialize(){
        //Set up profile service
        profileService = new ProfileService();
        profileDropDown.getItems().setAll(profileService.getProfiles());
        profileDropDown.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> onProfileSelected(newVal)
        );
        
        if (profileService.getProfiles().isEmpty()) {
            promptForNewProfile();
        }
        refreshProfileDropdown();

        // Load home screen by default
        loadView("HomeView.fxml");

        profileSettingsButton.setOnAction(e -> loadView("ProfileSettingsView.fxml"));
        themeToggleButton.setOnAction(e -> onThemeToggled());
        updateThemeButtonText();
    }

    public void onProfileSelected(String profileName) {
        if(profileName == null) return;
        currentProfile = profileName;
        activeProfileLabel.setText(profileName);
        if(currentScreenController != null){
            currentScreenController.onProfileChanged(profileName);
        }
    
    }

    //Load views within UI
    public void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/workoutapp/views/" + fxml));
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof ScreenController sc) {
                currentScreenController = sc;
                sc.setMainController(this);
                sc.onProfileChanged(currentProfile);
            }

            contentPane.getChildren().setAll(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // NEW unified loader for workout screen
    public void loadWorkoutView(String routineName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/workoutapp/views/WorkoutView.fxml"));
            Parent view = loader.load();

            WorkoutController wc = loader.getController();
            wc.setMainController(this);

            // Show the workout view first so any navigation performed during
            // initialization (e.g. user cancelling the exercise selector)
            // can replace it and won't be overwritten afterward.
            contentPane.getChildren().setAll(view);

            wc.initializeWorkout(currentProfile, routineName);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadReportDetailView(CalendarEvent selectedEvent) {
        try {
            lastReportEvent = selectedEvent;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/workoutapp/views/ReportDetailView.fxml"));
            Parent view = loader.load();

            ReportDetailController controller = loader.getController();
            controller.setMainController(this);
            controller.loadEvent(currentProfile, selectedEvent);

            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadProgressReportView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/workoutapp/views/ProgressReportView.fxml"));
            Parent view = loader.load();

            ProgressReportController controller = loader.getController();
            controller.setMainController(this);

            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CalendarEvent getLastReportEvent() {
        return lastReportEvent;
    }

    public void refreshProfileDropdown() {
        profileDropDown.getItems().setAll(profileService.getProfiles());

        if (currentProfile != null && profileDropDown.getItems().contains(currentProfile)) {
            profileDropDown.getSelectionModel().select(currentProfile);
        } else if (!profileDropDown.getItems().isEmpty()) {
            currentProfile = profileDropDown.getItems().get(0);
            profileDropDown.getSelectionModel().select(currentProfile);
            onProfileSelected(currentProfile);
        } else {
            // No profiles left — force creation
            promptForNewProfile();
        }
    }

    //Prompt creation if no profiles found
    private void promptForNewProfile() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Profile");
        dialog.setHeaderText("No profiles found");
        dialog.setContentText("Enter profile name:");

        dialog.showAndWait().ifPresent(name -> {
            String trimmed = name.trim();
            if (!trimmed.isEmpty()) {
                profileService.addProfile(trimmed);
                refreshProfileDropdown();
            }
        });
    }

    public String getCurrentProfile() {
        return currentProfile;
    }

    public ProfileService getProfileService(){
        return profileService;
    }

    public ComboBox<String> getProfileDropDown() {
        return profileDropDown;
    }

    private void onThemeToggled() {
        ThemeManager.getInstance().toggleTheme();
        updateThemeButtonText();
    }

    private void updateThemeButtonText() {
        boolean isDark = ThemeManager.getInstance().isDarkMode();
        themeToggleButton.setText(isDark ? "☀️ Light Mode" : "🌙 Dark Mode");
    }

}
