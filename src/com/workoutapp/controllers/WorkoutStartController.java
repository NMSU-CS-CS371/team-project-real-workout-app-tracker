package com.workoutapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.workoutapp.services.*;

/**
 * Opens when you start a new workout. Prompts user to select a routine or start
 * a workout without a routine (from scratch), in which it prompts the user to
 * select an exercise.
 */

public class WorkoutStartController implements ScreenController {

    private MainController main;
    private String profileName;

    @FXML private ListView<String> routineList;
    @FXML private Button startScratchButton;
    @FXML private Button cancelButton;

    private RoutineService routineService;

    @Override
    public void setMainController(MainController mainController) {
        this.main = mainController;
    }

    @Override
    public void onProfileChanged(String profileName) {
        this.profileName = profileName;

        routineService = new RoutineService(profileName);
        routineList.getItems().setAll(routineService.getRoutineNames());
    }

    @FXML
    public void initialize() {

        routineList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String routine = routineList.getSelectionModel().getSelectedItem();
                if (routine != null) {
                    main.loadWorkoutView(routine);
                }
            }
        });

        startScratchButton.setOnAction(e -> main.loadWorkoutView(null));
        cancelButton.setOnAction(e -> main.loadView("HomeView.fxml"));
    }
}
