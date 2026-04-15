package com.workoutapp.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import com.workoutapp.services.*;
import com.workoutapp.models.*;

public class MainController {
    @FXML private Button profileSettingsButton;
    @FXML private ComboBox<String> profileDropDown;
    @FXML private StackPane contentPane;

    private ProfileService profileService;
    private String currentProfile;    

    @FXML
    public void initialize(){
        //Set up profile service
        System.out.println(getClass().getResource("/com/workoutapp/ui/WorkoutView.fxml"));
        profileService = new ProfileService();
        profileDropDown.getItems().setAll(profileService.getProfiles());
        profileDropDown.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> onProfileSelected(newVal)
        );
        
        if (!profileDropDown.getItems().isEmpty()) {
            profileDropDown.getSelectionModel().selectFirst();
        } //Future: If empty, redirect to profile creation
        
        // Load home screen by default
        loadView("HomeView.fxml");

        profileSettingsButton.setOnAction(e -> loadView("ProfileSettingsView.fxml"));
    }

    private void onProfileSelected(String profileName) {
        if(profileName == null) return;
        currentProfile = profileName;

        //TODO: Modify screens as needed
    }

    //Load views within UI
    public void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/workoutapp/ui/" + fxml));
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof ScreenController sc) {
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/workoutapp/ui/WorkoutView.fxml"));
            Parent view = loader.load();

            WorkoutController wc = loader.getController();
            wc.setMainController(this);
            wc.initializeWorkout(currentProfile, routineName);

            contentPane.getChildren().setAll(view);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCurrentProfile() {
        return currentProfile;
    }

    public ComboBox<String> getProfileDropDown() {
        return profileDropDown;
    }

}
