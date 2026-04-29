package com.workoutapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import com.workoutapp.services.*;

/**
 * Manages the Profile Settings screen, allowing users to create, rename, and delete
 * workout profiles. Updates the main controller’s active profile and ensures UI
 * elements reflect current profile data.
 */

public class ProfileSettingsController implements ScreenController {
    @FXML private ListView<String> profilesList;
    @FXML private TextField nameField;
    @FXML private Button createButton;
    @FXML private Button renameButton;
    @FXML private Button deleteButton;
    @FXML private Button closeButton;

    private MainController main;
    private ProfileService profileService;

    @Override
    public void setMainController(MainController mainController) {
        this.main = mainController;
        this.profileService = main.getProfileService();
        initialize();
    }

    //Refresh when profile changed
    public void onProfileChanged(String profileName){
        if(profileName != null){
            profilesList.getSelectionModel().select(profileName);
            nameField.setText(profileName);
        }
    }

    //Initialize profile settings UI
    private void initialize(){
        profilesList.setItems(FXCollections.observableArrayList(profileService.getProfiles()));
        profilesList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                nameField.setText(newV);
            }
        });

        createButton.setOnAction(e -> onCreate());
        renameButton.setOnAction(e -> onRename());
        deleteButton.setOnAction(e -> onDelete());
        closeButton.setOnAction(e -> main.loadView("HomeView.fxml"));
    }

    //Add new profile when create button clicked
    private void onCreate() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) name = "New User";

        profileService.addProfile(name);
        refresh(name);
    }

    //When rename button clicked
    private void onRename() {
        String selected = profilesList.getSelectionModel().getSelectedItem();
        if(selected == null) return;

        String newName = nameField.getText().trim();
        if (newName.isEmpty()) return;

        profileService.renameProfile(selected, newName);
        refresh(newName);
    }

    //When delete button clicked - remove profle
    private void onDelete() {
        String selected = profilesList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        //Confirm deleting profile
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete profile '" + selected + "' and all its data?",
                ButtonType.OK, ButtonType.CANCEL);

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                profileService.removeProfile(selected);
                main.refreshProfileDropdown();
                refresh(null);
            }
        });
    }

    //Refresh ui and drop down
    private void refresh(String preferredSelection) {
        profilesList.setItems(FXCollections.observableArrayList(profileService.getProfiles()));
        main.refreshProfileDropdown();

        if (preferredSelection != null && profileService.profileExists(preferredSelection)) {
            profilesList.getSelectionModel().select(preferredSelection);
            main.onProfileSelected(preferredSelection);
        }
    }
}
