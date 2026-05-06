package com.workoutapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.util.Duration;
import javafx.application.Platform;
import com.workoutapp.services.*;
import com.workoutapp.models.*;

/**
 * Manages the workout execution screen with progression-based UI.
 * Guides user through per-exercise, per-set data entry.
 */

public class WorkoutController implements ScreenController {
    private MainController main;
    private WorkoutService workoutService;
    private Timeline durationTimer;
    private Timeline cardioTimer;
    private int cardioSecondsRemaining;

    // Root VBox injected from FXML - auto-wired by fx:root
    @FXML private VBox vboxRoot;
    @FXML private HBox bottomNav;

    @Override
    public void setMainController(MainController mainController) {
        this.main = mainController;
    }

    @FXML
    public void initialize() {
        // Setup will happen in initializeWorkout
    }

    public void initializeWorkout(String profileName, String routineName) {
        // Initialize services
        CalendarService calendarService = new CalendarService(profileName);
        RoutineService routineService = new RoutineService(profileName);
        ExerciseService exerciseService = new ExerciseService(profileName);
        workoutService = new WorkoutService(profileName, calendarService, routineService, exerciseService);

        if (routineName == null) {
            workoutService.startWorkoutFromScratch();
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

        startDurationTimer();
        showCurrentExercise();
    }

    // Build UI for current exercise
    private void showCurrentExercise() {
        stopCardioTimer();

        ExerciseInstance ex = workoutService.getCurrentExercise();
        if (ex == null) {
            main.loadView("HomeView.fxml");
            return;
        }

        vboxRoot.getChildren().clear();

        // Header: exercise name + progress
        Label header = new Label(ex.getExerciseName() + " (Exercise " 
            + (workoutService.getCurrentExerciseIndex() + 1) + " of " 
            + workoutService.getCurrentWorkout().getNumExercises() + ")");
        header.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 20;");
        content.getChildren().add(header);

        if (ex.getExerciseType() == ExerciseType.CARDIO) {
            buildCardioUI(content, ex);
        } else {
            buildStrengthUI(content, ex);
        }

        // Navigation buttons (placed in bottom nav area)
        HBox navButtons = new HBox(15);
        Button prevBtn = new Button("Previous Exercise");
        Button nextBtn = new Button("Next Exercise");
        Button finishBtn = new Button("Finish Workout");
        Button cancelBtn = new Button("Cancel");

        prevBtn.setOnAction(e -> onPreviousExercise());
        nextBtn.setOnAction(e -> onNextExercise());
        finishBtn.setOnAction(e -> onFinishWorkout());
        cancelBtn.setOnAction(e -> onCancelWorkout());

        // Only show "Next" while there are remaining exercises; otherwise show "Finish"
        boolean hasNext = workoutService.getCurrentWorkout() != null
                && workoutService.getCurrentExerciseIndex() < workoutService.getCurrentWorkout().getNumExercises() - 1;

        navButtons.getChildren().add(prevBtn);
        if (hasNext) {
            navButtons.getChildren().add(nextBtn);
        } else {
            navButtons.getChildren().add(finishBtn);
        }
        navButtons.getChildren().add(cancelBtn);

        navButtons.setAlignment(Pos.CENTER);

        if (bottomNav != null) {
            bottomNav.getChildren().clear();
            bottomNav.setAlignment(Pos.CENTER);
            bottomNav.getChildren().add(navButtons);
        } else {
            // Fallback: add into content if bottomNav not available
            content.getChildren().add(navButtons);
        }

        vboxRoot.getChildren().add(content);
    }

    private void buildStrengthUI(VBox parent, ExerciseInstance ex) {
        // Per-set entry
        Spinner<Integer> repsSpinner = new Spinner<>(1, 100, 10);
        repsSpinner.setEditable(true);
        Spinner<Double> weightSpinner = new Spinner<>(0.0, 1000.0, 0.0, 5.0);
        weightSpinner.setEditable(true);

        Button addSetBtn = new Button("Add Set");
        addSetBtn.setStyle("-fx-font-size: 14;");

        HBox entryBox = new HBox(10);
        entryBox.getChildren().addAll(
            new Label("Reps:"), repsSpinner,
            new Label("Weight (lbs):"), weightSpinner,
            addSetBtn
        );

        ListView<String> setsList = new ListView<>();
        refreshSetsList(setsList, ex);

        addSetBtn.setOnAction(e -> {
            int reps = repsSpinner.getValue();
            double weight = weightSpinner.getValue();
            if (reps > 0 && weight >= 0) {
                ex.addSet(new WorkoutSet(reps, weight));
                refreshSetsList(setsList, ex);
                repsSpinner.getValueFactory().setValue(10);
                weightSpinner.getValueFactory().setValue(0.0);
            }
        });

        parent.getChildren().addAll(
            new Label("Add sets for this exercise:"),
            entryBox,
            new Label("Sets completed:"),
            setsList
        );
    }

    private void buildCardioUI(VBox parent, ExerciseInstance ex) {
        int initialMinutes = ex.getDurationMinutes() > 0 ? ex.getDurationMinutes() : 30;
        double initialDistance = ex.getDistance() > 0 ? ex.getDistance() : 0.0;
        
        Spinner<Integer> durationSpinner = new Spinner<>(1, 300, initialMinutes);
        durationSpinner.setEditable(true);
        Spinner<Double> distanceSpinner = new Spinner<>(0.0, 100.0, initialDistance, 0.1);
        distanceSpinner.setEditable(true);
        
        Label timerTitle = new Label("Cardio Timer");
        timerTitle.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Label timerLabel = new Label("Ready to start");
        timerLabel.setStyle("-fx-font-size: 48; -fx-font-weight: bold; -fx-text-fill: #1d4ed8;");

        VBox timerDisplayBox = new VBox(8, timerTitle, timerLabel);
        timerDisplayBox.setAlignment(Pos.CENTER);
        timerDisplayBox.setStyle("-fx-padding: 24; -fx-border-color: #cbd5e1; -fx-border-radius: 12; -fx-background-radius: 12; -fx-background-color: #f8fafc;");

        Button startTimerBtn = new Button("Start Timer");
        Button stopTimerBtn = new Button("Stop Timer");
        Button resetTimerBtn = new Button("Reset Timer");

        startTimerBtn.setOnAction(e -> startCardioTimer(durationSpinner.getValue(), timerLabel));
        stopTimerBtn.setOnAction(e -> stopCardioTimer());
        resetTimerBtn.setOnAction(e -> {
            stopCardioTimer();
            timerLabel.setText("Ready to start");
            timerLabel.setStyle("-fx-font-size: 48; -fx-font-weight: bold; -fx-text-fill: #1d4ed8;");
        });

        durationSpinner.valueProperty().addListener((obs, oldV, newV) -> {
            workoutService.updateCardioDuration(workoutService.getCurrentExerciseIndex(), newV);
        });

        distanceSpinner.valueProperty().addListener((obs, oldV, newV) -> {
            workoutService.updateCardioDistance(workoutService.getCurrentExerciseIndex(), newV);
        });

        HBox timerButtons = new HBox(10, startTimerBtn, stopTimerBtn, resetTimerBtn);
        timerButtons.setAlignment(Pos.CENTER);

        Label durationLabel = new Label("Duration for this cardio exercise (minutes):");
        VBox durationBox = new VBox(6, durationLabel, durationSpinner);
        durationBox.setAlignment(Pos.CENTER);

        Label distanceLabel = new Label("Distance (miles):");
        VBox distanceBox = new VBox(6, distanceLabel, distanceSpinner);
        distanceBox.setAlignment(Pos.CENTER);

        parent.getChildren().addAll(
            timerDisplayBox,
            durationBox,
            distanceBox,
            timerButtons
        );
    }

    private void refreshSetsList(ListView<String> list, ExerciseInstance ex) {
        list.getItems().clear();
        for (WorkoutSet set : ex.getWorkoutSets()) {
            list.getItems().add(set.getReps() + " reps @ " + set.getWeight() + " lbs");
        }
    }

    private void startDurationTimer() {
        durationTimer = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                // Timer tick
            })
        );
        durationTimer.setCycleCount(Timeline.INDEFINITE);
        durationTimer.play();
    }

    private void onPreviousExercise() {
        if (workoutService.moveToPreviousExercise()) {
            showCurrentExercise();
        }
    }

    private void onNextExercise() {
        ExerciseInstance current = workoutService.getCurrentExercise();
        if (current != null && current.getExerciseType() != ExerciseType.CARDIO) {
            if (current.getSetCount() == 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Add at least one set before progressing.",
                    ButtonType.OK);
                alert.showAndWait();
                return;
            }
        }

        if (workoutService.moveToNextExercise()) {
            showCurrentExercise();
        }
    }

    private void onFinishWorkout() {
        workoutService.endWorkout("");
        durationTimer.stop();
        main.loadView("HomeView.fxml");
    }

    private void onCancelWorkout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Unsaved workout will be lost.",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Cancel Workout?");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            workoutService.cancelWorkout();
            durationTimer.stop();
            stopCardioTimer();
            main.loadView("HomeView.fxml");
        }
    }

    private void startCardioTimer(int minutes, Label timerLabel) {
        stopCardioTimer();

        cardioSecondsRemaining = minutes * 60;
        timerLabel.setStyle("-fx-font-size: 48; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
        timerLabel.setText(formatSeconds(cardioSecondsRemaining));

        cardioTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (cardioSecondsRemaining > 0) {
                cardioSecondsRemaining--;
                timerLabel.setText(formatSeconds(cardioSecondsRemaining));
            } else {
                stopCardioTimer();
                timerLabel.setText("Done");
                timerLabel.setStyle("-fx-font-size: 48; -fx-font-weight: bold; -fx-text-fill: #16a34a;");
            }
        }));
        cardioTimer.setCycleCount(Timeline.INDEFINITE);
        cardioTimer.play();
    }

    private void stopCardioTimer() {
        if (cardioTimer != null) {
            cardioTimer.stop();
            cardioTimer = null;
        }
    }

    private String formatSeconds(int totalSeconds) {
        int mins = Math.max(0, totalSeconds) / 60;
        int secs = Math.max(0, totalSeconds) % 60;
        return String.format("%02d:%02d remaining", mins, secs);
    }
}
