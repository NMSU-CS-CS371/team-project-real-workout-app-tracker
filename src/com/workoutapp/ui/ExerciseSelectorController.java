package com.workoutapp.ui;

import com.workoutapp.models.*;
import com.workoutapp.services.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.LinkedList;

public class ExerciseSelectorController {
    //Components from FXML file
    @FXML private ListView<Exercise> exerciseListView;
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private Button createExerciseButton;
    @FXML private Button editExerciseButton;
    @FXML private Button addToRoutineButton;
    @FXML private Button closeButton;
    @FXML private ComboBox<ExerciseType> typeDropDown;

    private ExerciseService exerciseService;
    private RoutineService routineService;
    private Routine targetRoutine;
    private WorkoutService workoutService;
    private boolean workoutMode = false;
    private ObservableList<Exercise> exerciseItems;
    private ObservableList<Exercise> routineExerciseItems; // reference from RoutineEditor
    private Stage stage;
 
    // Open selector window for routine editor
    public static void showSelector(MainController main, ExerciseService exerciseService, Routine targetRoutine,
                ObservableList<Exercise> routineExerciseItems, RoutineService routineService) {
        try {
            FXMLLoader loader = new FXMLLoader(ExerciseSelectorController.class.getResource("ExerciseSelectorView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage dialog = new Stage();
            dialog.setTitle("Select Exercise");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(main == null ? null : main.getProfileDropDown().getScene().getWindow()); // or any window
            dialog.setScene(scene);

            ExerciseSelectorController controller = loader.getController();
            controller.init(exerciseService, routineService, targetRoutine, routineExerciseItems, dialog);

            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Open selector for mid-workout
    public static void showSelectorForWorkout(MainController main, ExerciseService exerciseService, WorkoutService workoutService,  RoutineService routineService) {
        try {
            FXMLLoader loader = new FXMLLoader(ExerciseSelectorController.class.getResource("ExerciseSelectorView.fxml"));
            Scene scene = new Scene(loader.load());
            Stage dialog = new Stage();
            dialog.setTitle("Select Exercise");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(main.getProfileDropDown().getScene().getWindow());
            dialog.setScene(scene);

            ExerciseSelectorController controller = loader.getController();
            controller.initForWorkout(exerciseService, routineService, workoutService, dialog);

            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Initialize selector view
    public void init(ExerciseService exerciseService, RoutineService routineService, Routine targetRoutine,
                     ObservableList<Exercise> routineExerciseItems, Stage stage) {
        //Initialize services
        this.exerciseService = exerciseService;
        this.routineService = routineService;
        this.targetRoutine = targetRoutine;
        this.routineExerciseItems = routineExerciseItems;
        this.stage = stage;
        this.workoutMode = false;

        setupUI();
    }

    //Initialize selector view mid workout
    public void initForWorkout(
            ExerciseService exerciseService,
            RoutineService routineService,
            WorkoutService workoutService,
            Stage stage) {

        this.exerciseService = exerciseService;
        this.routineService = routineService;
        this.workoutService = workoutService;
        this.stage = stage;
        this.workoutMode = true;

        setupUI();
    }

    //Create new exercise and update
    private void onCreateExercise() {
        if(nameField.getText().equals("") || nameField.getText() == null) return;
        Exercise ex = new Exercise(nameField.getText(), descriptionField.getText(), typeDropDown.getValue());
        exerciseService.addExercise(ex); // persists
        exerciseItems.add(ex);
        exerciseListView.getSelectionModel().select(ex);
        nameField.requestFocus();
        nameField.selectAll();
    }

    //Set up UI for initialziation
    public void setupUI(){
        typeDropDown.getItems().addAll(ExerciseType.values());
        typeDropDown.getSelectionModel().selectFirst();

        LinkedList<Exercise> all = exerciseService.getExercises();
        exerciseItems = FXCollections.observableArrayList(all);
        exerciseListView.setItems(exerciseItems);

        exerciseListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Exercise item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getType() + ")\t" + item.getDesc());
                }
            }
        });

        exerciseListView.getSelectionModel().selectedItemProperty().addListener((obs, oldE, newE) -> {
            if (newE != null) {
                nameField.setText(newE.getName());
                descriptionField.setText(newE.getDesc());
                typeDropDown.getSelectionModel().select(newE.getType());
            }
        });

        // Double-click to add to routine
        exerciseListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                onAdd();
            }
        });

        //Define actions of buttons
        createExerciseButton.setOnAction(e -> onCreateExercise());
        editExerciseButton.setOnAction(e -> onEditExercise());
        addToRoutineButton.setOnAction(e -> onAdd());
        closeButton.setOnAction(e -> stage.close());
        if (workoutMode) {
            addToRoutineButton.setText("Add to Workout");
        }
    }

    //Edit exercise name, description or type
    private void onEditExercise() {
        Exercise selected = exerciseListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        selected.setName(nameField.getText());
        selected.setDesc(descriptionField.getText());
        exerciseListView.refresh();

        // Re-save all exercises
        exerciseService.getExercises().clear();
        exerciseService.getExercises().addAll(exerciseItems);
        exerciseService.saveExercises(); //Persist
    }

    //Add to routine button clicked
    private void onAdd() {
        Exercise selected = exerciseListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if(workoutMode){
            //Add to workout
            workoutService.addExerciseToWorkout(selected.getName());
            stage.close();
            return;
        }

        if(targetRoutine != null){
            targetRoutine.addExercise(selected);
            routineExerciseItems.add(selected);
            routineService.addExercise(targetRoutine.getRoutineName(), selected);
            stage.close();  //Close view and return to RoutineEditor
        }
    }
}
