package com.workoutapp.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import com.workoutapp.services.*;
import com.workoutapp.models.*;

/**
 * Class to control workout process UI
 */
public class WorkoutController implements ScreenController {
    private MainController main;
    private WorkoutService workoutService;

    // Timer for updating duration
    private Timeline durationTimer;

    // FXML UI components
    @FXML private Label statusLabel;
    @FXML private Label durationLabel;
    @FXML private Label volumeLabel;
    @FXML private Label currentExerciseLabel;
    @FXML private Label topHeaderLabel;
    @FXML private ProgressBar progressBar;
    @FXML private ListView<ExerciseInstance> exerciseList;
    @FXML private Spinner<Integer> setsSpinner;
    @FXML private Spinner<Integer> repsSpinner;
    @FXML private Spinner<Double> weightSpinner;
    @FXML private Spinner<Integer> durationSpinner;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Button finishButton;
    @FXML private Button cancelButton;

    // Optional: Add exercise mid‑workout
    @FXML private Button addExerciseButton;

    @Override
    public void setMainController(MainController mainController) {
        this.main = mainController;
    }

    @FXML
    public void initialize() {
        // Prepare spinners with default factories
        setsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 3));
        repsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10));
        weightSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1000, 0, 5));

        // Cardio duration spinner (hidden by default)
        durationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 300, 30));
        durationSpinner.setVisible(false);
        cancelButton.setOnAction(e -> cancelWorkout());
    }

    // Initialize workout
    public void initializeWorkout(String profileName, String routineName) {
        //Initialize services
        CalendarService calendarService = new CalendarService(profileName);
        RoutineService routineService = new RoutineService(profileName);
        ExerciseService exerciseService = new ExerciseService(profileName);
        workoutService = new WorkoutService(profileName, calendarService, routineService, exerciseService);

        if (routineName == null) {
            workoutService.startWorkoutFromScratch();
                //Force user to add exercise
                if (workoutService.getCurrentWorkout().getNumExercises() == 0) {
                ExerciseSelectorController.showSelectorForWorkout(
                    main,
                    workoutService.getExerciseService(),
                    workoutService,
                    workoutService.getRoutineService()
                );
            }
        } else {
            workoutService.startWorkoutFromRoutine(routineName);
        }

        bindUIToService();
        loadExerciseList();
        setupExerciseListSelection();
        setupSpinnerListeners();
        onNavigationButtons();
        finishWorkout();
        onAddExercise();
        startDurationTimer();
        updateExerciseControls();
    }

    //Load exercise list
    private void loadExerciseList() {
        exerciseList.setItems(workoutService.getCurrentWorkoutExercisesObservable());
    }

    //Bind UI to service properties
    private void bindUIToService() {
        statusLabel.textProperty().bind(workoutService.statusMessageProperty());
        durationLabel.textProperty().bind(workoutService.durationProperty());
        volumeLabel.textProperty().bind(workoutService.volumeProperty());
        progressBar.progressProperty().bind(workoutService.progressProperty());
        currentExerciseLabel.textProperty().bind(workoutService.currentExerciseNameProperty());
    }

    //Make exercise list selectable
    private void setupExerciseListSelection() {
        exerciseList.getSelectionModel().selectedIndexProperty().addListener((obs, oldV, newV) -> {
            if (newV.intValue() >= 0) {
                workoutService.setCurrentExerciseIndex(newV.intValue());
                updateExerciseControls();
            }
        });
    }

    //Update exercise controls, check if cardio
    private void updateExerciseControls(){
        ExerciseInstance ex = workoutService.getCurrentExercise();
        if(ex == null) return;
        boolean isCardio = ex.getExerciseType() == ExerciseType.CARDIO;

        //Show/hide spinners based on type
        setsSpinner.setVisible(!isCardio);
        repsSpinner.setVisible(!isCardio);
        weightSpinner.setVisible(!isCardio);
        durationSpinner.setVisible(isCardio);
    
        if(isCardio) {
            durationSpinner.getValueFactory().setValue(ex.getDurationMinutes());
        } else {
            setsSpinner.getValueFactory().setValue(ex.getSets());
            repsSpinner.getValueFactory().setValue(ex.getReps());
            weightSpinner.getValueFactory().setValue(ex.getWeight());
        }
    }

    //Set up listeners for spinners
    private void setupSpinnerListeners() {
        setsSpinner.valueProperty().addListener((obs, oldV, newV) -> {
            workoutService.updateExerciseDataSafe(
                newV,
                repsSpinner.getValue(),
                weightSpinner.getValue());
        });
        repsSpinner.valueProperty().addListener((obs, oldV, newV) -> {
            workoutService.updateExerciseDataSafe(
                setsSpinner.getValue(),
                newV,
                weightSpinner.getValue());
        });
        weightSpinner.valueProperty().addListener((obs, oldV, newV) -> {
            workoutService.updateExerciseDataSafe(
                setsSpinner.getValue(),
                repsSpinner.getValue(),
                newV);
        });
        durationSpinner.valueProperty().addListener((obs, oldV, newV) -> {
            workoutService.updateCardioDuration(
                workoutService.getCurrentExerciseIndex(),
                newV);
        });
    }

    //Start duration for cardio exercise
    private void startDurationTimer() {
        durationTimer = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                try {
                    String formatted = workoutService.getFormattedDuration();
                    workoutService.durationProperty().set(formatted);
                } catch (Exception ex) {
                    System.err.println("Duration timer error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            })
        );
        durationTimer.setCycleCount(Timeline.INDEFINITE);
        durationTimer.play();
    }

    //Start Rest Timer
    public void startRestPeriod(int seconds){
        //Disable bindings
        nextButton.setDisable(true);
        prevButton.setDisable(true);

        final int[] secondsRemaining = {seconds};
        Timeline restTimeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                secondsRemaining[0]--;
                topHeaderLabel.setText("Resting - " + secondsRemaining[0] + "s remaining");
            })
        );

        restTimeline.setCycleCount(seconds);
        restTimeline.setOnFinished(e ->{
            //Restore bindings
            nextButton.setDisable(false);
            prevButton.setDisable(false);

            //Move to next exercise
            workoutService.moveToNextExercise();
            exerciseList.getSelectionModel().select(workoutService.getCurrentExerciseIndex());
            updateExerciseControls();
            topHeaderLabel.setText("Workout in Progress");
        });
        restTimeline.play();
    }

    //Set up exercise navigation buttons
    private void onNavigationButtons(){
        nextButton.setOnAction(e -> {
            if(workoutService.moveToNextExercise()) {
                int restSeconds = workoutService.getDefaultRestTime(); // or fixed 30
                startRestPeriod(restSeconds);
            } 
        });
        prevButton.setOnAction(e -> {
            if(workoutService.moveToPreviousExercise()) {
                exerciseList.getSelectionModel().select(workoutService.getCurrentExerciseIndex());
                updateExerciseControls();
            } 
        });
    }

    //Open ExerciseSelector to add exercise mid-workout
    private void onAddExercise(){
        if(addExerciseButton == null) return;
        addExerciseButton.setOnAction(e -> {
            ExerciseSelectorController.showSelectorForWorkout(
                main,
                workoutService.getExerciseService(),
                workoutService,
                workoutService.getRoutineService()
            );
            loadExerciseList();
        });
        Platform.runLater(() -> loadExerciseList());
    }

    //Finish and save workout to log
    private void finishWorkout() {
        finishButton.setOnAction(e -> {
            WorkoutService.WorkoutSummary summary = workoutService.endWorkout("No notes");
            durationTimer.stop();
            main.loadView("HomeView.fxml");
        });
    }

    private void cancelWorkout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Unsaved workout will be lost.",
                ButtonType.YES, ButtonType.NO);

        confirm.setHeaderText("Cancel Workout?");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            workoutService.cancelWorkout();
            main.loadView("HomeView.fxml");
        }
    }
}
