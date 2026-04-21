package com.workoutapp.ui;

import com.workoutapp.models.*;
import com.workoutapp.services.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class RoutineEditorController {
    //List views
    @FXML private ListView<Exercise> exerciseList;
    @FXML private ListView<Exercise> routineList;

    //Button objects
    @FXML private Button moveUpButton;
    @FXML private Button moveDownButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;
    @FXML private Button saveButton;
    @FXML private Button createExerciseButton;    

    private ExerciseService exerciseService;
    private RoutineService routineService;
    private Routine currentRoutine;
    private String profileName;

    public void initialize(String profileName, Routine routineToEdit){
        //Set up services
        this.profileName = profileName;
        this.exerciseService = new ExerciseService(profileName);
        this.routineService = new RoutineService(profileName);

        //Load exercises into left list
        exerciseList.getItems().addAll(exerciseService.getExercises());

        if (routineToEdit == null) {
            currentRoutine = new Routine("New Routine");
        } else {
            currentRoutine = new Routine(routineToEdit.getRoutineName());
            currentRoutine.getExercises().addAll(routineToEdit.getExercises());
        }

        //Add exercise when clicked
        exerciseList.setOnMouseClicked(event -> {
            Exercise selected = exerciseList.getSelectionModel().getSelectedItem();
            if(selected != null){
                currentRoutine.addExercise(selected);
                refreshRoutineList();
            }
        });

        refreshRoutineList();
        //Call appropriate method when button clicked
        moveUpButton.setOnAction(e -> moveExerciseUp());
        moveDownButton.setOnAction(e -> moveExerciseDown());
        deleteButton.setOnAction(e -> deleteExercise());
        clearButton.setOnAction(e -> clearRoutine());
        saveButton.setOnAction(e -> saveRoutine());
        createExerciseButton.setOnAction(e -> createExercise());
    }

    private void refreshRoutineList() {
        routineList.getItems().setAll(currentRoutine.getExercises());
    }

    private void moveExerciseUp(){
        int index = routineList.getSelectionModel().getSelectedIndex();
        if(index > 0){
            Exercise e = currentRoutine.getExercises().remove(index);
            currentRoutine.getExercises().add(index-1, e);
            refreshRoutineList();
            routineList.getSelectionModel().select(index-1);
        }
    }

    private void moveExerciseDown() {
        int index = routineList.getSelectionModel().getSelectedIndex();
        if (index >= 0 && index < currentRoutine.getNumExercises() - 1) {
            Exercise ex = currentRoutine.getExercises().remove(index);
            currentRoutine.getExercises().add(index + 1, ex);
            refreshRoutineList();
            routineList.getSelectionModel().select(index + 1);
        }
    }

    private void deleteExercise() {
        Exercise selected = routineList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentRoutine.removeExercise(selected);
            refreshRoutineList();
        }
    }
    
    private void clearRoutine() {
        currentRoutine.getExercises().clear();
        refreshRoutineList();
    }   

    private void saveRoutine() {
        //Routine name dialog
        TextInputDialog dialog = new TextInputDialog(currentRoutine.getRoutineName());
        dialog.setTitle("Save Routine");
        dialog.setHeaderText("Enter routine name");
        dialog.setContentText("Routine name:");

        String name = dialog.showAndWait().orElse(null);
        if (name == null || name.isBlank()) {
            System.out.println("Failed to save routine - no name");
            return;
        }

        currentRoutine.setRoutineName(name);

        //Replace existing routine if one exists with same name
        if (routineService.checkRoutineExists(name)) {
            Routine old = routineService.findRoutine(name);
            routineService.removeRoutine(old);
        }

        Routine copy = new Routine(name);
        copy.getExercises().addAll(currentRoutine.getExercises());
        routineService.addRoutine(copy);
        System.out.println("Routine saved as: " + name);

        // Close window and return to main view
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void createExercise() {
        Exercise newEx = ExerciseDialog.showCreateExerciseDialog();
        if(newEx != null) {
            exerciseService.addExercise(newEx);
            exerciseList.getItems().setAll(exerciseService.getExercises());
        }
    }
}
