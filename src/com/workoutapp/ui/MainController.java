package com.workoutapp.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.workoutapp.services.*;
import com.workoutapp.models.*;

public class MainController {
    @FXML private ListView<Exercise> exerciseList;
    @FXML private ListView<Exercise> routineList;

    @FXML private Button moveUpButton;
    @FXML private Button moveDownButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;
    @FXML private Button saveButton; 

    private ExerciseService exerciseService;
    private RoutineService routineService;
    private Routine currentRoutine;    

    @FXML
    public void initialize(){
        exerciseService = new ExerciseService();
        routineService = new RoutineService();

        // Load exercises into left list
        exerciseList.getItems().addAll(exerciseService.getExercises());

         // Create or load working routine
        currentRoutine = new Routine("UI Routine");

        // When user clicks an exercise on left, add to routine
        exerciseList.setOnMouseClicked(e -> {
            Exercise selected = exerciseList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                currentRoutine.addExercise(selected);
                refreshRoutineList();
            }
        });

 
        moveUpButton.setOnAction(e -> moveExerciseUp());
        moveDownButton.setOnAction(e -> moveExerciseDown());
        deleteButton.setOnAction(e -> deleteExercise());
        clearButton.setOnAction(e -> clearRoutine());
        saveButton.setOnAction(e -> saveRoutine());       
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

    private void saveRoutine(){
        //Ask user for routine name
        TextInputDialog dialog = new TextInputDialog(currentRoutine.getRoutineName());
        dialog.setTitle("Save Routine");
        dialog.setHeaderText("Enter a name for this routine");
        dialog.setContentText("Routine name:");

        String name = dialog.showAndWait().orElse(null);
        if(name == null || name.isBlank()){
            System.out.println("Failed to save routine - no name");
            return;
        }

        //Update routine name
        currentRoutine.setRoutineName(name);

        // Remove old routine if it exists
        if (routineService.checkRoutineExists(name)) {
            Routine old = routineService.findRoutine(name);
            routineService.removeRoutine(old);
        }

        // Create fresh copy to ensure saving works
        Routine copy = new Routine(name);
        copy.getExercises().addAll(currentRoutine.getExercises());
        routineService.addRoutine(copy);
        System.out.println("Routine saved as: " + name);
    }
}
