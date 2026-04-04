package com.workoutapp.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.*;
import javafx.stage.*;
import com.workoutapp.services.*;
import com.workoutapp.models.*;

public class MainController {
    //Top
    @FXML private ComboBox<String> profileCombo;
    @FXML private Button newProfileButton;
    @FXML private Button deleteProfileButton;

    //List views
    @FXML private ListView<Routine> routineListView;
    @FXML private ListView<Exercise> routineExercisesListView;

    //Bottom buttons
    @FXML private Button newRoutineButton;
    @FXML private Button editRoutineButton;
    @FXML private Button deleteRoutineButton;

    private ProfileService profileService;
    private ExerciseService exerciseService;
    private RoutineService routineService;
    private String currentProfile;    

    @FXML
    public void initialize(){
        //Set up profile service
        profileService = new ProfileService();
        profileCombo.getItems().addAll(profileService.getProfiles());
        profileCombo.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> onProfileSelected(newVal)
        );
        
        profileCombo.getSelectionModel().selectFirst();

        //Initialize buttons
        newProfileButton.setOnAction(e -> createProfile());
        deleteProfileButton.setOnAction(e -> deleteProfile());

        routineListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> showRoutineExercises(newVal)
        );

        newRoutineButton.setOnAction(e -> openRoutineEditor(null));
        editRoutineButton.setOnAction(e -> {
            Routine selected = routineListView.getSelectionModel().getSelectedItem();
            if (selected != null) openRoutineEditor(selected);
        });
        deleteRoutineButton.setOnAction(e -> deleteRoutine());
    }
    
    //Refresh UI when changing profiles
    private void onProfileSelected(String profileName){
        if(profileName == null) return;
        currentProfile = profileName;

        //Create services for current profile
        exerciseService = new ExerciseService(currentProfile);
        routineService = new RoutineService(currentProfile);
        
        //Update routine list
        refreshRoutineList();
        routineExercisesListView.getItems().clear();
    }

    private void refreshRoutineList(){
        routineExercisesListView.getItems().clear();
        routineService = new RoutineService(currentProfile);
        routineListView.getItems().setAll(routineService.getRoutines());
    }

    private void showRoutineExercises(Routine routine) {
        if (routine == null) {
            routineExercisesListView.getItems().clear();
        } else {
            routineExercisesListView.getItems().setAll(routine.getExercises());
        }
    }

    //Create new user profile
    private void createProfile() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Profile");
        dialog.setHeaderText("Create new profile");
        dialog.setContentText("Profile name:");

        String name = dialog.showAndWait().orElse(null);
        if (name == null || name.isBlank()) return;

        //Add profile to service
        profileService.addProfile(name);
        if (!profileCombo.getItems().contains(name)) {
            profileCombo.getItems().add(name);
        }
        profileCombo.getSelectionModel().select(name);
    }    

    //Remove user profile
    private void deleteProfile(){
        String selected = profileCombo.getSelectionModel().getSelectedItem();
        if(selected == null) return;

        profileService.removeProfile(selected);
        int removedIndex = profileCombo.getSelectionModel().getSelectedIndex();
        profileCombo.getItems().remove(selected);
        currentProfile = null;

        //Clear UI
        routineListView.getItems().clear();
        routineExercisesListView.getItems().clear();

        // Delete profile directory
        Path profileDir = Path.of("Data", selected);
        try {
            if (Files.exists(profileDir)) {
                Files.walk(profileDir)
                    .sorted(Comparator.reverseOrder()) // delete children first
                    .forEach(path -> {
                        try { Files.delete(path); }
                        catch (IOException e) { e.printStackTrace(); }
                    });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Select previous profile if possible
        if (!profileCombo.getItems().isEmpty()) {
            int newIndex = Math.max(0, removedIndex - 1);
            profileCombo.getSelectionModel().select(newIndex);
            onProfileSelected(profileCombo.getSelectionModel().getSelectedItem());
        } else {
            // No profiles left
            currentProfile = null;
            routineListView.getItems().clear();
            routineExercisesListView.getItems().clear();
        }
    }

    //Delete routine
    private void deleteRoutine() {
        Routine selected = routineListView.getSelectionModel().getSelectedItem();
        if (selected == null || routineService == null) return;

        routineService.removeRoutine(selected);
        refreshRoutineList();
        routineExercisesListView.getItems().clear();
    }    

    //Open routine editor when user clicks edit routine
    private void openRoutineEditor(Routine routineToEdit) {
        if (currentProfile == null) {
            System.out.println("Select a profile first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RoutineEditorView.fxml"));
            Parent root = loader.load();

            RoutineEditorController controller = loader.getController();
            controller.initialize(currentProfile, routineToEdit);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(routineToEdit == null ? "New Routine" : "Edit Routine");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // After closing editor, refresh routines
            refreshRoutineList();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }    
}
