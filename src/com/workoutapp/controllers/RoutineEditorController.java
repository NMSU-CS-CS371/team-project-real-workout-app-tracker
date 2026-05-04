package com.workoutapp.controllers;

import com.workoutapp.models.*;
import com.workoutapp.services.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import java.util.LinkedList;

/**
 * Controls the Routine Editor, enabling users to create, rename, delete, and modify
 * workout routines. Manages exercise ordering, addition/removal of exercises,
 * and persistence of routine data through RoutineService and ExerciseService.
 */

public class RoutineEditorController implements ScreenController {
    //Import FXML parameters
    @FXML private ListView<Routine> routineListView;
    @FXML private ListView<Exercise> exerciseListView;
    @FXML private Label routineTitleLabel;
    @FXML private TextField routineNameField;

    @FXML private Button createRoutineButton;
    @FXML private Button deleteRoutineButton;
    @FXML private Button exitButton;

    @FXML private Button addExerciseButton;
    @FXML private Button deleteExerciseButton;
    @FXML private Button moveUpButton;
    @FXML private Button moveDownButton;
    @FXML private Button saveRoutineButton;

    private MainController main;
    private RoutineService routineService;
    private ExerciseService exerciseService;
    private ObservableList<Routine> routineItems = FXCollections.observableArrayList();
    private ObservableList<Exercise> exerciseItems = FXCollections.observableArrayList();

    //Set main controller from interface
    @Override
    public void setMainController(MainController mainController) {
        this.main = mainController;
    }

    //Set up services when changing profiles or opening window
    @Override
    public void onProfileChanged(String profileName) {
        if (profileName == null) return;

        // Set up per-profile services
        this.routineService = new RoutineService(profileName);
        this.exerciseService = new ExerciseService(profileName);
        initRoutineList();
        initHandlers();
    }

    //Initialize routine list to display profile's routines
    private void initRoutineList() {
        LinkedList<Routine> routines = routineService.getRoutines();
        routineItems.setAll(routines);
        routineListView.setItems(routineItems);

        // Show "name (# exercises)" in the list
        routineListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Routine item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if(item.getNumExercises() == 0){
                    setText(item.getRoutineName() + " (No exercises)");
                } else if(item.getNumExercises() == 1){
                    setText(item.getRoutineName() + " (1 exercise)");
                } else{
                    int count = item.getNumExercises();
                    setText(item.getRoutineName() + " (" + count + " exercises)");
                }
            }
        });

        // When a routine is selected, show its exercises
        routineListView.getSelectionModel().selectedItemProperty().addListener((obs, oldR, newR) -> {
            onRoutineSelected(newR);
        });
    }

    //Initialize buttons
    private void initHandlers() {
        createRoutineButton.setOnAction(e -> onCreateRoutine());
        deleteRoutineButton.setOnAction(e -> onDeleteRoutine());
        exitButton.setOnAction(e -> main.loadView("HomeView.fxml"));

        addExerciseButton.setOnAction(e -> onAddExercise());
        deleteExerciseButton.setOnAction(e -> onDeleteExercise());
        moveUpButton.setOnAction(e -> onMoveExercise(-1));
        moveDownButton.setOnAction(e -> onMoveExercise(1));
        saveRoutineButton.setOnAction(e -> onSaveRoutine());

        // Simple text binding for rename field
        routineListView.getSelectionModel().selectedItemProperty().addListener((obs, oldR, newR) -> {
            if (newR != null) {
                routineNameField.setText(newR.getRoutineName());
            } else {
                routineNameField.clear();
            }
        });

        // Show exercises neatly
        exerciseListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Exercise item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if (item.getDesc() == null || item.getDesc().equals("")){
                    setText(item.getName() + " (" + item.getType() + ")");
                } else {
                    setText(item.getName() + " (" + item.getType() + ") - " + item.getDesc());
                }
            }
        });
    }

    //Update exercise list view for currently selected routine
    private void onRoutineSelected(Routine routine) {
        if (routine == null) {
            routineTitleLabel.setText("Select a routine...");
            exerciseItems.clear();
            exerciseListView.setItems(exerciseItems);
            return;
        }
        routineTitleLabel.setText("Editing \"" + routine.getRoutineName() + "\"");
        exerciseItems.setAll(routine.getExercises());
        exerciseListView.setItems(exerciseItems);
    }

    //Create new routine
    private void onCreateRoutine() {
        //Prevent empty routine name or duplicate
        String inputName = routineNameField.getText();
        String finalName;

        if(inputName == null || inputName.trim().isEmpty()){
            inputName = "New Routine";
        } 
        if(routineService.getRoutineNames().contains(inputName.trim())){
            System.out.println("Duplicate routine name");
            inputName = inputName + "*";
        }
        finalName = inputName.trim();
        Routine newRoutine = new Routine(finalName);
        routineService.addRoutine(newRoutine);
        routineItems.add(newRoutine);
        routineListView.getSelectionModel().select(newRoutine);
        routineNameField.requestFocus();
        routineNameField.selectAll();
    }

    //Remove routine
    private void onDeleteRoutine() {
        Routine selected = routineListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        routineService.removeRoutine(selected);
        routineItems.remove(selected);
        exerciseItems.clear();
    }

    private void onAddExercise() {
        Routine selectedRoutine = routineListView.getSelectionModel().getSelectedItem();
        if (selectedRoutine == null) return;

        ExerciseSelectorController.showSelector(main, exerciseService, selectedRoutine, exerciseItems, routineService);
    }

    //Remove exercise from routine
    private void onDeleteExercise() {
        Routine selectedRoutine = routineListView.getSelectionModel().getSelectedItem();
        Exercise selectedExercise = exerciseListView.getSelectionModel().getSelectedItem();
        if (selectedRoutine == null || selectedExercise == null) return;

        selectedRoutine.removeExercise(selectedExercise);
        exerciseItems.remove(selectedExercise);
        routineService.saveAll();
        
    }

    //Move exercises up or down in routine
    private void onMoveExercise(int direction) {
        int index = exerciseListView.getSelectionModel().getSelectedIndex();
        if (index < 0) return;

        int newIndex = index + direction;
        if (newIndex < 0 || newIndex >= exerciseItems.size()) return;

        Exercise ex = exerciseItems.remove(index);
        exerciseItems.add(newIndex, ex);
        exerciseListView.getSelectionModel().select(newIndex);

        // Update underlying routine list
        Routine selectedRoutine = routineListView.getSelectionModel().getSelectedItem();
        if (selectedRoutine != null) {
            selectedRoutine.getExercises().clear();
            selectedRoutine.getExercises().addAll(exerciseItems);
            routineService.saveAll();
        }
    }

    //Save changes to routines
    private void onSaveRoutine() {
        Routine selected = routineListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String newName = routineNameField.getText();
            if (newName != null && !newName.isBlank()) {
                selected.setRoutineName(newName.trim());
                routineListView.refresh();
                routineTitleLabel.setText("Editing \"" + selected.getRoutineName() + "\"");
            }
        }
        routineService.saveAll();
    }
}
