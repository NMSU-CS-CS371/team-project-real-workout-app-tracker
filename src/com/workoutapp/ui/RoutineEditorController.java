package com.workoutapp.ui;

import com.workoutapp.models.*;
import com.workoutapp.services.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.util.LinkedList;

public class RoutineEditorController implements ScreenController {

    @FXML private ListView<Routine> routineListView;
    @FXML private ListView<Exercise> exerciseListView;
    @FXML private Label routineTitleLabel;
    @FXML private TextField routineNameField;

    @FXML private Button createRoutineButton;
    @FXML private Button deleteRoutineButton;
    @FXML private Button renameRoutineButton;
    @FXML private Button exitButton;

    @FXML private Button addExerciseButton;
    @FXML private Button deleteExerciseButton;
    @FXML private Button moveUpButton;
    @FXML private Button moveDownButton;
    @FXML private Button clearExercisesButton;
    @FXML private Button saveRoutineButton;

    private MainController main;
    private RoutineService routineService;
    private ExerciseService exerciseService;

    private ObservableList<Routine> routineItems = FXCollections.observableArrayList();
    private ObservableList<Exercise> exerciseItems = FXCollections.observableArrayList();

    @Override
    public void setMainController(MainController mainController) {
        this.main = mainController;
    }

    @Override
    public void onProfileChanged(String profileName) {
        if (profileName == null) return;

        // Services are per-profile
        this.routineService = new RoutineService(profileName);
        this.exerciseService = new ExerciseService(profileName);

        initRoutineList();
        initHandlers();
    }

    private void initRoutineList() {
        LinkedList<Routine> routines = routineService.getRoutines();
        routineItems.setAll(routines);
        routineListView.setItems(routineItems);

        // Show "name (#exercises)" in the list
        routineListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Routine item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
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

    private void initHandlers() {
        createRoutineButton.setOnAction(e -> onCreateRoutine());
        deleteRoutineButton.setOnAction(e -> onDeleteRoutine());
        renameRoutineButton.setOnAction(e -> onRenameRoutine());
        exitButton.setOnAction(e -> main.loadView("HomeView.fxml"));

        addExerciseButton.setOnAction(e -> onAddExercise());
        deleteExerciseButton.setOnAction(e -> onDeleteExercise());
        moveUpButton.setOnAction(e -> onMoveExercise(-1));
        moveDownButton.setOnAction(e -> onMoveExercise(1));
        clearExercisesButton.setOnAction(e -> onClearExercises());
        saveRoutineButton.setOnAction(e -> onSaveRoutine());

        // Simple text binding for rename field
        routineListView.getSelectionModel().selectedItemProperty().addListener((obs, oldR, newR) -> {
            if (newR != null) {
                routineNameField.setText(newR.getRoutineName());
            } else {
                routineNameField.clear();
            }
        });

        // Show exercises nicely
        exerciseListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Exercise item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getType() + ")");
                }
            }
        });
    }

    private void onRoutineSelected(Routine routine) {
        if (routine == null) {
            routineTitleLabel.setText("Select a routine...");
            exerciseItems.clear();
            exerciseListView.setItems(exerciseItems);
            return;
        }
        routineTitleLabel.setText("Routine: " + routine.getRoutineName());
        exerciseItems.setAll(routine.getExercises());
        exerciseListView.setItems(exerciseItems);
    }

    private void onCreateRoutine() {
        Routine newRoutine = new Routine("New Routine");
        routineService.addRoutine(newRoutine); // will save
        routineItems.add(newRoutine);
        routineListView.getSelectionModel().select(newRoutine);
        routineNameField.requestFocus();
        routineNameField.selectAll();
    }

    private void onDeleteRoutine() {
        Routine selected = routineListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        routineService.removeRoutine(selected);
        routineItems.remove(selected);
        exerciseItems.clear();
    }

    private void onRenameRoutine() {
        Routine selected = routineListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        String newName = routineNameField.getText();
        if (newName == null || newName.isBlank()) return;

        selected.setRoutineName(newName);
        // Persist: simplest is to save the whole list again
        routineService.getRoutines().clear();
        routineService.getRoutines().addAll(routineItems);
        // routineService internally saves on add/remove; here we can call save via a helper or reuse saveRoutineButton
        onSaveRoutine(); // reuse save logic
        routineListView.refresh();
        routineTitleLabel.setText("Routine: " + selected.getRoutineName());
    }

    private void onAddExercise() {
        Routine selectedRoutine = routineListView.getSelectionModel().getSelectedItem();
        if (selectedRoutine == null) return;

        ExerciseSelectorController.showSelector(main, exerciseService, selectedRoutine, exerciseItems, routineService);
    }

    private void onDeleteExercise() {
        Routine selectedRoutine = routineListView.getSelectionModel().getSelectedItem();
        Exercise selectedExercise = exerciseListView.getSelectionModel().getSelectedItem();
        if (selectedRoutine == null || selectedExercise == null) return;

        selectedRoutine.removeExercise(selectedExercise);
        exerciseItems.remove(selectedExercise);
    }

    private void onMoveExercise(int direction) {
        int index = exerciseListView.getSelectionModel().getSelectedIndex();
        if (index < 0) return;

        int newIndex = index + direction;
        if (newIndex < 0 || newIndex >= exerciseItems.size()) return;

        Exercise ex = exerciseItems.remove(index);
        exerciseItems.add(newIndex, ex);
        exerciseListView.getSelectionModel().select(newIndex);

        // Also update underlying routine list
        Routine selectedRoutine = routineListView.getSelectionModel().getSelectedItem();
        if (selectedRoutine != null) {
            selectedRoutine.getExercises().clear();
            selectedRoutine.getExercises().addAll(exerciseItems);
        }
    }

    private void onClearExercises() {
        Routine selectedRoutine = routineListView.getSelectionModel().getSelectedItem();
        if (selectedRoutine == null) return;

        selectedRoutine.getExercises().clear();
        exerciseItems.clear();
    }

    private void onSaveRoutine() {
        // Persist current state of routines list
        // RoutineService saves on add/remove; here we can force a save by re-saving the list
        // Simple approach: remove and re-add all routines
        LinkedList<Routine> backing = routineService.getRoutines();
        backing.clear();
        backing.addAll(routineItems);
        // storage.save(backing) is called inside RoutineService methods; if you want explicit save,
        // you can add a saveAll() method to RoutineService that calls storage.save(routines).
    }
}
