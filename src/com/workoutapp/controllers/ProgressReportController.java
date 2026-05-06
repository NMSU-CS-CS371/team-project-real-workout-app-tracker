package com.workoutapp.controllers;

import com.workoutapp.models.CalendarEvent;
import com.workoutapp.models.ExerciseInstance;
import com.workoutapp.models.ExerciseType;
import com.workoutapp.models.Workout;
import com.workoutapp.models.WorkoutSet;
import com.workoutapp.services.CalendarService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.SnapshotParameters;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ProgressReportController implements ScreenController {

    @FXML private Button backButton;
    @FXML private Button exportButton;
    @FXML private Label titleLabel;
    @FXML private VBox chartContainer;
    @FXML private ComboBox<String> metricSelector;

    private MainController main;
    private String profileName;
    private String exerciseName;
    private ExerciseType exerciseType;
    private TreeMap<LocalDate, Double> maxWeightData;
    private TreeMap<LocalDate, Double> avgWeightData;
    private TreeMap<LocalDate, Double> timeData;
    private TreeMap<LocalDate, Double> distanceData;
    private TreeMap<LocalDate, Double> paceData;
    private TreeMap<LocalDate, Double> paceSumData;
    private TreeMap<LocalDate, Integer> paceCountData;

    @Override
    public void setMainController(MainController mainController) {
        this.main = mainController;

        backButton.setOnAction(e -> {
            if (main.getLastReportEvent() != null) {
                main.loadReportDetailView(main.getLastReportEvent());
            } else {
                main.loadView("ReportView.fxml");
            }
        });

        exportButton.setOnAction(e -> handleExportChart());
    }

    @Override
    public void onProfileChanged(String profileName) {
        titleLabel.setText("Progress Report");
    }

    public void loadExerciseProgress(String profileName, String exerciseName) {
        this.profileName = profileName;
        this.exerciseName = exerciseName;
        titleLabel.setText(exerciseName + " Progress");

        // Fetch exercise progression data
        CalendarService calendarService = new CalendarService(profileName);
        LinkedList<CalendarEvent> allEvents = calendarService.getEvents();

        // Initialize data maps
        maxWeightData = new TreeMap<>();
        avgWeightData = new TreeMap<>();
        timeData = new TreeMap<>();
        distanceData = new TreeMap<>();
        paceData = new TreeMap<>();
        paceSumData = new TreeMap<>();
        paceCountData = new TreeMap<>();

        // Determine exercise type from first matching exercise
        exerciseType = null;

        for (CalendarEvent event : allEvents) {
            Workout workout = event.getWorkout();
            if (workout == null) continue;

            for (ExerciseInstance exercise : workout.getExercises()) {
                if (!exercise.getExerciseName().equals(exerciseName)) continue;

                if (exerciseType == null) {
                    exerciseType = exercise.getExerciseType();
                }

                LocalDate date = event.getDateTime().toLocalDate();

                if (exercise.getExerciseType() == ExerciseType.CARDIO) {
                    // For cardio: track time and distance
                    double cardioTime = exercise.getDurationMinutes();
                    if (!timeData.containsKey(date) || timeData.get(date) < cardioTime) {
                        timeData.put(date, cardioTime);
                    }

                    double cardioDistance = exercise.getDistance();
                    if (!distanceData.containsKey(date) || distanceData.get(date) < cardioDistance) {
                        distanceData.put(date, cardioDistance);
                    }

                    if (cardioDistance > 0) {
                        double cardioPace = cardioTime / cardioDistance;
                        paceSumData.put(date, paceSumData.getOrDefault(date, 0.0) + cardioPace);
                        paceCountData.put(date, paceCountData.getOrDefault(date, 0) + 1);
                    }
                } else {
                    // For strength: calculate both metrics
                    double maxWeight = 0;
                    double totalWeight = 0;
                    int setCount = 0;

                    for (WorkoutSet set : exercise.getWorkoutSets()) {
                        if (set.getWeight() > maxWeight) {
                            maxWeight = set.getWeight();
                        }
                        totalWeight += set.getWeight();
                        setCount++;
                    }

                    // Track max weight
                    if (!maxWeightData.containsKey(date) || maxWeightData.get(date) < maxWeight) {
                        maxWeightData.put(date, maxWeight);
                    }

                    // Track average weight
                    double avgWeight = setCount > 0 ? totalWeight / setCount : 0;
                    if (!avgWeightData.containsKey(date) || avgWeightData.get(date) < avgWeight) {
                        avgWeightData.put(date, avgWeight);
                    }
                }
            }
        }

        for (Map.Entry<LocalDate, Double> entry : paceSumData.entrySet()) {
            LocalDate date = entry.getKey();
            int count = paceCountData.getOrDefault(date, 0);
            if (count > 0) {
                paceData.put(date, entry.getValue() / count);
            }
        }

        // Setup metric selector dropdown based on exercise type
        if (exerciseType == ExerciseType.CARDIO) {
            metricSelector.setItems(FXCollections.observableArrayList("Time (min)", "Distance (mi)", "Average Pace (min/mi)"));
            metricSelector.setValue("Time (min)");
            metricSelector.setOnAction(e -> {
                chartContainer.getChildren().clear();
                String selected = metricSelector.getValue();
                TreeMap<LocalDate, Double> dataToDisplay;
                if (selected.contains("Time")) {
                    dataToDisplay = timeData;
                } else if (selected.contains("Distance")) {
                    dataToDisplay = distanceData;
                } else {
                    dataToDisplay = paceData;
                }
                buildChart(dataToDisplay, selected);
            });
            buildChart(timeData, "Time (min)");
        } else {
            metricSelector.setItems(FXCollections.observableArrayList("Max Weight", "Average Weight"));
            metricSelector.setValue("Max Weight");
            metricSelector.setOnAction(e -> {
                chartContainer.getChildren().clear();
                String selected = metricSelector.getValue();
                TreeMap<LocalDate, Double> dataToDisplay = selected.equals("Max Weight") ? maxWeightData : avgWeightData;
                buildChart(dataToDisplay, selected);
            });
            buildChart(maxWeightData, "Max Weight");
        }
    }

    private void buildChart(TreeMap<LocalDate, Double> progressionData, String metricLabel) {
        if (progressionData.isEmpty()) {
            Label noDataLabel = new Label("No progression data available for this exercise.");
            noDataLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14;");
            chartContainer.getChildren().add(noDataLabel);
            return;
        }

        // Create axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");

        NumberAxis yAxis = new NumberAxis();
        
        // Set Y-axis label based on metric
        if (metricLabel.contains("Time")) {
            yAxis.setLabel("Minutes");
        } else if (metricLabel.contains("Distance")) {
            yAxis.setLabel("Miles");
        } else if (metricLabel.contains("Pace")) {
            yAxis.setLabel("Minutes / Mile");
        } else if (metricLabel.contains("Weight")) {
            yAxis.setLabel("Weight (lbs)");
        } else {
            yAxis.setLabel("Value");
        }

        // Create line chart
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(exerciseName + " Progression - " + metricLabel);
        chart.setStyle("-fx-font-size: 12;");
        chart.setPrefHeight(400);

        // Add data series with actual dates
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(exerciseName);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd");
        List<String> dateLabels = new ArrayList<>();
        
        for (Map.Entry<LocalDate, Double> entry : progressionData.entrySet()) {
            String dateLabel = entry.getKey().format(dateFormatter);
            dateLabels.add(dateLabel);
            series.getData().add(new XYChart.Data<>(dateLabel, entry.getValue()));
        }

        xAxis.setCategories(FXCollections.observableArrayList(dateLabels));
        chart.getData().add(series);
        chartContainer.getChildren().add(chart);
    }

    private void handleExportChart() {
        if (chartContainer.getChildren().isEmpty()) {
            return;
        }

        // Get the chart from the container
        Object chartNode = chartContainer.getChildren().get(0);
        if (!(chartNode instanceof LineChart)) {
            return;
        }

        LineChart<String, Number> chart = (LineChart<String, Number>) chartNode;

        // Create file chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Chart as Image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PNG Images", "*.png")
        );
        
        // Set initial filename
        String filename = exerciseName.replaceAll("[^a-zA-Z0-9_-]", "_") + "_progress.png";
        fileChooser.setInitialFileName(filename);

        // Show save dialog
        File file = fileChooser.showSaveDialog(chart.getScene().getWindow());
        if (file != null) {
            try {
                // Take snapshot of chart
                WritableImage image = new WritableImage((int)chart.getWidth(), (int)chart.getHeight());
                chart.snapshot(new SnapshotParameters(), image);

                // Convert WritableImage to BufferedImage
                BufferedImage bufferedImage = new BufferedImage(
                    (int)image.getWidth(),
                    (int)image.getHeight(),
                    BufferedImage.TYPE_INT_RGB
                );

                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        bufferedImage.setRGB(x, y, image.getPixelReader().getArgb(x, y));
                    }
                }

                // Write to file
                ImageIO.write(bufferedImage, "png", file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
