package com.workoutapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import com.workoutapp.services.*;
import java.util.LinkedList;
import com.workoutapp.models.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HomeController implements ScreenController {
    private MainController main;
    private CalendarService calendarService;

    @FXML private Label totalWorkoutsLabel;
    @FXML private Label totalExercisesLabel;
    @FXML private Label totalCardioMinutesLabel;
    @FXML private Label latestWorkoutLabel;
    @FXML private Label latestWorkoutDetailsLabel;
    @FXML private Button startWorkoutButton;
    @FXML private Button historyButton;
    @FXML private Button routineEditorButton;
    @FXML private VBox recoveryBox;
    @FXML private PieChart recoveryChart;
    @FXML private TextArea recoveryDetailsArea;

    private static final int RECOVERY_PERIOD_DAYS = 7;

    @Override
    public void setMainController(MainController mainController){
        this.main = mainController;
        //Navigation buttons
        startWorkoutButton.setOnAction(e -> main.loadView("WorkoutStartView.fxml"));
        historyButton.setOnAction(e -> main.loadView("ReportView.fxml"));
        routineEditorButton.setOnAction(e -> main.loadView("RoutineEditorView.fxml"));
    }

    @Override
    public void onProfileChanged(String profileName) {
        if (profileName == null) return;

        calendarService = new CalendarService(profileName);
        loadDashboardSummary();
        loadRecoverySuggestions();
    }

    private void loadRecoverySuggestions() {
        Map<ExerciseType, Double> volumeByGroup = getRecentMuscleVolume(RECOVERY_PERIOD_DAYS);
        recoveryChart.getData().clear();

        if (volumeByGroup.isEmpty()) {
            recoveryDetailsArea.setText("No strength workouts recorded in the last "
                + RECOVERY_PERIOD_DAYS + " days. Complete workouts to build a muscle volume map and get recovery guidance.");
            return;
        }

        double totalVolume = volumeByGroup.values().stream().mapToDouble(Double::doubleValue).sum();

        for (Map.Entry<ExerciseType, Double> entry : volumeByGroup.entrySet()) {
            double value = entry.getValue();
            PieChart.Data slice = new PieChart.Data(entry.getKey().toString(), value);
            recoveryChart.getData().add(slice);
        }

        StringBuilder message = new StringBuilder();
        message.append(String.format("Volume in the last %d days:\n", RECOVERY_PERIOD_DAYS));
        for (Map.Entry<ExerciseType, Double> entry : volumeByGroup.entrySet()) {
            double value = entry.getValue();
            double percent = totalVolume > 0 ? value / totalVolume * 100.0 : 0.0;
            message.append(String.format("• %s: %.1f lbs (%.0f%%)\n",
                entry.getKey(), value, percent));
        }
        message.append("\n");

        String recoveryText = buildRecoveryDetailsText(volumeByGroup, totalVolume);
        message.append(recoveryText);
        recoveryDetailsArea.setText(message.toString());
    }

    private Map<ExerciseType, Double> getRecentMuscleVolume(int days) {
        Map<ExerciseType, Double> volumeByGroup = new EnumMap<>(ExerciseType.class);
        LocalDate cutoff = LocalDate.now().minusDays(days);

        for (CalendarEvent event : calendarService.getEventsInRange(cutoff, LocalDate.now())) {
            if (event.getWorkout() == null) continue;
            for (ExerciseInstance instance : event.getWorkout().getExercises()) {
                if (instance.getExerciseType() == ExerciseType.CARDIO) continue;
                double volume = 0.0;
                for (WorkoutSet set : instance.getWorkoutSets()) {
                    volume += set.getReps() * set.getWeight();
                }
                volumeByGroup.merge(instance.getExerciseType(), volume, Double::sum);
            }
        }
        return volumeByGroup;
    }

    private String buildRecoveryDetailsText(Map<ExerciseType, Double> volumeByGroup, double totalVolume) {
        Map<ExerciseType, Integer> sessionCount = new EnumMap<>(ExerciseType.class);
        LocalDate cutoff = LocalDate.now().minusDays(RECOVERY_PERIOD_DAYS);

        for (CalendarEvent event : calendarService.getEventsInRange(cutoff, LocalDate.now())) {
            if (event.getWorkout() == null) continue;
            Set<ExerciseType> usedGroups = new HashSet<>();
            for (ExerciseInstance instance : event.getWorkout().getExercises()) {
                ExerciseType type = instance.getExerciseType();
                if (type == ExerciseType.CARDIO) continue;
                usedGroups.add(type);
            }
            for (ExerciseType type : usedGroups) {
                sessionCount.merge(type, 1, Integer::sum);
            }
        }

        StringBuilder text = new StringBuilder();
        List<ExerciseType> overloaded = new ArrayList<>();
        for (Map.Entry<ExerciseType, Double> entry : volumeByGroup.entrySet()) {
            double ratio = totalVolume > 0 ? entry.getValue() / totalVolume : 0.0;
            if (ratio >= 0.40) {
                overloaded.add(entry.getKey());
            }
        }

        if (!overloaded.isEmpty()) {
            text.append("⚠ Overuse warning: ");
            text.append("Your workouts are heavily focused on ");
            text.append(String.join(", ", overloaded.stream().map(Enum::toString).toList()));
            text.append(". Consider resting or rotating these muscle groups.\n");
        }

        List<String> repeated = new ArrayList<>();
        for (Map.Entry<ExerciseType, Integer> entry : sessionCount.entrySet()) {
            if (entry.getValue() >= 2) {
                repeated.add(entry.getKey().toString());
            }
        }
        if (!repeated.isEmpty()) {
            text.append("⚠ Recovery alert: ");
            text.append(String.join(", ", repeated));
            text.append(" were trained on multiple recent sessions. Prioritize recovery.\n");
        }

        if (text.length() == 0) {
            text.append("Recovery is looking balanced. Keep alternating muscle groups and listen to your energy levels.");
        }
        return text.toString();
    }

    private void loadDashboardSummary() {
        LinkedList<CalendarEvent> events = calendarService.getEvents();
        totalWorkoutsLabel.setText(String.valueOf(events.size()));

        int totalExercises = 0;
        int cardioMinutes = 0;
        for (CalendarEvent event : events) {
            Workout workout = event.getWorkout();
            if (workout == null) {
                continue;
            }
            for (ExerciseInstance instance : workout.getExercises()) {
                totalExercises++;
                if (instance.getDurationMinutes() > 0) {
                    cardioMinutes += instance.getDurationMinutes();
                }
            }
        }

        totalExercisesLabel.setText(String.valueOf(totalExercises));
        totalCardioMinutesLabel.setText(String.valueOf(cardioMinutes));

        if (events.isEmpty()) {
            latestWorkoutLabel.setText("No sessions yet");
            latestWorkoutDetailsLabel.setText("Complete a workout to see details here.");
            return;
        }

        CalendarEvent latest = events.getLast();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        String dateText = latest.getDateTime() == null ? "Unknown date" : latest.getDateTime().format(fmt);
        latestWorkoutLabel.setText(dateText);

        Workout latestWorkout = latest.getWorkout();
        String exerciseNames = "";
        if (latestWorkout != null && !latestWorkout.getExercises().isEmpty()) {
            List<String> names = new ArrayList<>();
            for (ExerciseInstance ex : latestWorkout.getExercises()) {
                names.add(ex.getExerciseName());
            }
            exerciseNames = String.join(", ", names);
        } else {
            exerciseNames = "No exercises";
        }
        String notes = (latest.getNotes() == null || latest.getNotes().isBlank()) ? "No notes." : latest.getNotes();
        latestWorkoutDetailsLabel.setText("Exercises: " + exerciseNames + "\nNotes: " + notes);
    }
}
