package com.workoutapp.services;

import com.workoutapp.models.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * WorkoutService manages live workout sessions, including timing, rest periods,
 * exercise tracking, and integration with calendar and recovery suggestions.
 */
public class WorkoutService {

    private Workout currentWorkout;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean workoutActive;
    private Timer restTimer;
    private int defaultRestTimeSeconds;
    private CalendarService calendarService;
    private RoutineService routineService;
    private ExerciseService exerciseService;
    private String profileName;
    private int currentExerciseIndex; // Track current exercise being worked on

    // JavaFX Properties for UI binding
    private final SimpleBooleanProperty workoutActiveProperty = new SimpleBooleanProperty(false);
    private final SimpleStringProperty statusMessageProperty = new SimpleStringProperty("No active workout");
    private final SimpleStringProperty durationProperty = new SimpleStringProperty("00:00");
    private final SimpleStringProperty volumeProperty = new SimpleStringProperty("0 lbs");
    private final SimpleDoubleProperty progressProperty = new SimpleDoubleProperty(0.0);
    private final SimpleStringProperty currentExerciseNameProperty = new SimpleStringProperty("");
    private final SimpleIntegerProperty currentExerciseIndexProperty = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty totalExercisesProperty = new SimpleIntegerProperty(0);

    // Store last workout summary for UI display
    private WorkoutSummary lastWorkoutSummary;

    // Recovery tracking
    private static final int RECOVERY_CHECK_DAYS = 3;
    private List<String> recentMuscleGroups;

    public WorkoutService(String profileName, CalendarService calendarService,
                         RoutineService routineService, ExerciseService exerciseService) {
        this.profileName = profileName;
        this.calendarService = calendarService;
        this.routineService = routineService;
        this.exerciseService = exerciseService;
        this.defaultRestTimeSeconds = 30; // Default 30 seconds rest
        this.workoutActive = false;
        this.recentMuscleGroups = new ArrayList<>();
        this.restTimer = new Timer();
        this.currentExerciseIndex = 0;
    }

    /**
     * Starts a new workout session from a routine
     */
    public void startWorkoutFromRoutine(String routineName) {
        Routine routine = routineService.findRoutine(routineName);
        if (routine == null) {
            throw new IllegalArgumentException("Routine not found: " + routineName);
        }

        currentWorkout = new Workout(routine);
        // Set baseline of 3 sets for strength exercises
        for (ExerciseInstance instance : currentWorkout.getExercises()) {
            if (instance.getExerciseType() != ExerciseType.CARDIO) {
                instance.setSets(3);
                instance.setReps(10); // Default 10 reps
                instance.setWeight(0.0); // User will set this
            }
        }

        startTime = LocalDateTime.now();
        workoutActive = true;
        currentExerciseIndex = 0;
        checkRecoverySuggestions();

        // Update JavaFX properties
        updateUIProperties();
    }

    /**
     * Starts a new workout session from scratch
     */
    public void startWorkoutFromScratch() {
        currentWorkout = new Workout();
        startTime = LocalDateTime.now();
        workoutActive = true;
        currentExerciseIndex = 0;
        checkRecoverySuggestions();

        // Update JavaFX properties
        updateUIProperties();
    }

    /**
     * Adds an exercise to the current workout
     */
    public void addExerciseToWorkout(String exerciseName) {
        if (!workoutActive) {
            throw new IllegalStateException("No active workout session");
        }

        Exercise exercise = null;
        for (Exercise e : exerciseService.getExercises()) {
            if (e.getName().equalsIgnoreCase(exerciseName)) {
                exercise = e;
                break;
            }
        }

        if (exercise == null) {
            throw new IllegalArgumentException("Exercise not found: " + exerciseName);
        }

        ExerciseInstance instance;
        if (exercise.getType() == ExerciseType.CARDIO) {
            instance = new ExerciseInstance(exercise, 30); // Default 30 minutes
        } else {
            instance = new ExerciseInstance(exercise, 3, 10, 0.0); // 3 sets, 10 reps, 0 weight
        }

        currentWorkout.addExercise(instance);
        updateUIProperties();
    }

    /**
     * Updates exercise instance data during workout
     */
    public void updateExerciseData(int exerciseIndex, int sets, int reps, double weight) {
        if (!workoutActive) {
            throw new IllegalStateException("No active workout session");
        }

        if (exerciseIndex < 0 || exerciseIndex >= currentWorkout.getNumExercises()) {
            throw new IllegalArgumentException("Invalid exercise index");
        }

        ExerciseInstance instance = currentWorkout.getExercises().get(exerciseIndex);
        if (instance.getExerciseType() == ExerciseType.CARDIO) {
            throw new IllegalArgumentException("Cannot update sets/reps/weight for cardio exercise");
        }

        instance.setSets(sets);
        instance.setReps(reps);
        instance.setWeight(weight);
        updateUIProperties();
    }

    /**
     * Updates cardio exercise duration
     */
    public void updateCardioDuration(int exerciseIndex, int durationMinutes) {
        if (!workoutActive) {
            throw new IllegalStateException("No active workout session");
        }

        if (exerciseIndex < 0 || exerciseIndex >= currentWorkout.getNumExercises()) {
            throw new IllegalArgumentException("Invalid exercise index");
        }

        ExerciseInstance instance = currentWorkout.getExercises().get(exerciseIndex);
        if (instance.getExerciseType() != ExerciseType.CARDIO) {
            throw new IllegalArgumentException("Exercise is not cardio type");
        }

        instance.setDurationMinutes(durationMinutes);
        updateUIProperties();
    }

    /**
     * Starts a rest timer between sets/exercises
     */
    public CompletableFuture<Void> startRestTimer(int seconds) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        restTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                future.complete(null);
            }
        }, seconds * 1000);

        return future;
    }

    /**
     * Starts default rest timer
     */
    public CompletableFuture<Void> startRestTimer() {
        return startRestTimer(defaultRestTimeSeconds);
    }

    /**
     * Sets the default rest time
     */
    public void setDefaultRestTime(int seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("Rest time must be non-negative");
        }
        this.defaultRestTimeSeconds = seconds;
    }

    /**
     * Gets the current workout duration in minutes
     */
    public long getCurrentWorkoutDurationMinutes() {
        if (!workoutActive || startTime == null) {
            return 0;
        }
        return Duration.between(startTime, LocalDateTime.now()).toMinutes();
    }

    /**
     * Ends the current workout and saves it to calendar
     */
    public WorkoutSummary endWorkout(String notes) {
        if (!workoutActive) {
            throw new IllegalStateException("No active workout session");
        }

        endTime = LocalDateTime.now();
        workoutActive = false;

        long durationMinutes = Duration.between(startTime, endTime).toMinutes();

        // Create calendar event
        CalendarEvent event = new CalendarEvent(startTime, currentWorkout, notes);
        calendarService.addEvent(event);

        // Update recent muscle groups for recovery tracking
        updateRecentMuscleGroups();

        WorkoutSummary summary = new WorkoutSummary(currentWorkout, startTime, endTime,
                                                  durationMinutes, getRecoverySuggestions());

        // Store for UI access
        lastWorkoutSummary = summary;

        // Reset for next workout
        currentWorkout = null;
        startTime = null;
        endTime = null;
        currentExerciseIndex = 0;

        // Update JavaFX properties
        updateUIProperties();

        return summary;
    }

    /**
     * Cancels the current workout without saving
     */
    public void cancelWorkout() {
        if (!workoutActive) {
            return;
        }

        workoutActive = false;
        currentWorkout = null;
        startTime = null;
        endTime = null;
        currentExerciseIndex = 0;
        restTimer.cancel();
        restTimer = new Timer();

        // Update JavaFX properties
        updateUIProperties();
    }

    /**
     * Gets the current workout
     */
    public Workout getCurrentWorkout() {
        return currentWorkout;
    }

    /**
     * Gets a user-friendly status message for the current workout state
     */
    public String getWorkoutStatusMessage() {
        if (!workoutActive) {
            return "No active workout";
        }

        long duration = getCurrentWorkoutDurationMinutes();
        int currentIndex = getCurrentExerciseIndex();
        int totalExercises = currentWorkout.getNumExercises();

        return String.format("Workout in progress - %d min elapsed, Exercise %d of %d",
                           duration, currentIndex + 1, totalExercises);
    }

    /**
     * Gets formatted duration string (MM:SS or HH:MM:SS)
     */
    public String getFormattedDuration() {
        if (!workoutActive || startTime == null) {
            return "00:00";
        }

        long seconds = Duration.between(startTime, LocalDateTime.now()).getSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%02d:%02d", minutes, secs);
        }
    }

    /**
     * Gets formatted volume string for display
     */
    public String getFormattedVolume() {
        double volume = calculateTotalVolume();
        if (volume >= 1000) {
            return String.format("%.1fK lbs", volume / 1000);
        } else {
            return String.format("%.0f lbs", volume);
        }
    }

    /**
     * Gets progress percentage (0.0 to 1.0) for UI progress bars
     */
    public double getWorkoutProgress() {
        if (!workoutActive || currentWorkout == null || currentWorkout.getNumExercises() == 0) {
            return 0.0;
        }

        // Simple progress based on current exercise index
        return (double) (currentExerciseIndex + 1) / currentWorkout.getNumExercises();
    }

    /**
     * Gets a list of exercise names for UI dropdowns/combo boxes
     */
    public List<String> getAvailableExerciseNames() {
        List<String> names = new ArrayList<>();
        for (Exercise exercise : exerciseService.getExercises()) {
            names.add(exercise.getName());
        }
        return names;
    }

    public List<String> getAvailableRoutineNames() {
        List<String> names = new ArrayList<>();
        for (Routine routine : routineService.getRoutines()) {
            names.add(routine.getName());
        }
        return names;
    }

    /**
     * Gets an observable list of exercise names for UI combo boxes
     */
    public ObservableList<String> getAvailableExerciseNamesObservable() {
        return FXCollections.observableArrayList(getAvailableExerciseNames());
    }

    /**
     * Gets an observable list of routine names for UI combo boxes
     */
    public ObservableList<String> getAvailableRoutineNamesObservable() {
        return FXCollections.observableArrayList(getAvailableRoutineNames());
    }

    /**
     * Gets an observable list of current workout exercises for UI lists
     */
    public ObservableList<ExerciseInstance> getCurrentWorkoutExercisesObservable() {
        if (currentWorkout == null) {
            return FXCollections.emptyObservableList();
        }
        return FXCollections.observableArrayList(currentWorkout.getExercises());
    }

    /**
     * Safely starts a workout from scratch with user-friendly error handling
     */
    public String startWorkoutFromScratchSafe() {
        try {
            startWorkoutFromScratch();
            return "Workout started successfully";
        } catch (Exception e) {
            return "Unexpected error starting workout: " + e.getMessage();
        }
    }

    /**
     * Safely starts a workout from routine with user-friendly error handling
     */
    public String startWorkoutFromRoutineSafe(String routineName) {
        try {
            startWorkoutFromRoutine(routineName);
            return "Workout started successfully from routine: " + routineName;
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected error starting workout: " + e.getMessage();
        }
    }

    /**
     * Safely adds exercise to workout with user-friendly error handling
     */
    public String addExerciseToWorkoutSafe(String exerciseName) {
        try {
            addExerciseToWorkout(exerciseName);
            return "Exercise added: " + exerciseName;
        } catch (IllegalStateException e) {
            return "Error: " + e.getMessage();
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected error adding exercise: " + e.getMessage();
        }
    }

    /**
     * Safely updates exercise data with user-friendly error handling
     */
    public String updateExerciseDataSafe(int sets, int reps, double weight) {
        try {
            updateExerciseData(getCurrentExerciseIndex(), sets, reps, weight);
            return "Exercise updated successfully";
        } catch (IllegalStateException e) {
            return "Error: " + e.getMessage();
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected error updating exercise: " + e.getMessage();
        }
    }

    /**
     * Safely navigates to exercise with user-friendly error handling
     */
    public String navigateToExerciseSafe(int index) {
        try {
            setCurrentExerciseIndex(index);
            ExerciseInstance current = getCurrentExercise();
            return "Now working on: " + current.getExerciseName();
        } catch (IllegalStateException e) {
            return "Error: " + e.getMessage();
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected error navigating: " + e.getMessage();
        }
    }

    /**
     * Safely ends workout with user-friendly error handling
     */
    public String endWorkoutSafe(String notes) {
        try {
            WorkoutSummary summary = endWorkout(notes);
            return String.format("Workout completed! Duration: %d minutes, Volume: %.0f lbs",
                               summary.getDurationMinutes(), summary.getTotalVolume());
        } catch (IllegalStateException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected error ending workout: " + e.getMessage();
        }
    }

    /**
     * Updates all UI-related cached values (for non-JavaFX implementations)
     * Call this after state changes to ensure UI getters return current values
     */
    private void updateUIProperties() {
        workoutActiveProperty.set(workoutActive);
        statusMessageProperty.set(getWorkoutStatusMessage());
        durationProperty.set(getFormattedDuration());
        volumeProperty.set(getFormattedVolume());
        progressProperty.set(getWorkoutProgress());
        currentExerciseIndexProperty.set(currentExerciseIndex);
        totalExercisesProperty.set(currentWorkout != null ? currentWorkout.getNumExercises() : 0);
        currentExerciseNameProperty.set((currentWorkout != null && currentWorkout.getNumExercises() > 0)
            ? getCurrentExercise().getExerciseName()
            : "");
    }

    public SimpleBooleanProperty workoutActiveProperty() {
        return workoutActiveProperty;
    }

    public SimpleStringProperty statusMessageProperty() {
        return statusMessageProperty;
    }

    public SimpleStringProperty durationProperty() {
        return durationProperty;
    }

    public SimpleStringProperty volumeProperty() {
        return volumeProperty;
    }

    public SimpleDoubleProperty progressProperty() {
        return progressProperty;
    }

    public SimpleStringProperty currentExerciseNameProperty() {
        return currentExerciseNameProperty;
    }

    public SimpleIntegerProperty currentExerciseIndexProperty() {
        return currentExerciseIndexProperty;
    }

    public SimpleIntegerProperty totalExercisesProperty() {
        return totalExercisesProperty;
    }

    /**
     * Gets the last completed workout summary
     */
    public WorkoutSummary getLastWorkoutSummary() {
        return lastWorkoutSummary;
    }

    /**
     * Checks if a workout is currently active
     */
    public boolean isWorkoutActive() {
        return workoutActive;
    }

    /**
     * Gets the current exercise index being worked on
     */
    public int getCurrentExerciseIndex() {
        return currentExerciseIndex;
    }

    /**
     * Sets the current exercise index (for navigating backwards/forwards)
     */
    public void setCurrentExerciseIndex(int index) {
        if (!workoutActive) {
            throw new IllegalStateException("No active workout session");
        }
        if (index < 0 || index >= currentWorkout.getNumExercises()) {
            throw new IllegalArgumentException("Invalid exercise index: " + index);
        }
        this.currentExerciseIndex = index;
        updateUIProperties();
    }

    /**
     * Moves to the next exercise in the workout
     */
    public boolean moveToNextExercise() {
        if (!workoutActive || currentExerciseIndex >= currentWorkout.getNumExercises() - 1) {
            return false;
        }
        currentExerciseIndex++;
        updateUIProperties();
        return true;
    }

    /**
     * Moves to the previous exercise in the workout
     */
    public boolean moveToPreviousExercise() {
        if (!workoutActive || currentExerciseIndex <= 0) {
            return false;
        }
        currentExerciseIndex--;
        updateUIProperties();
        return true;
    }

    /**
     * Gets the current exercise being worked on
     */
    public ExerciseInstance getCurrentExercise() {
        if (!workoutActive || currentExerciseIndex >= currentWorkout.getNumExercises()) {
            return null;
        }
        return currentWorkout.getExercises().get(currentExerciseIndex);
    }

    /**
     * Calculates the total volume (weight * reps * sets) for the entire workout
     */
    public double calculateTotalVolume() {
        if (currentWorkout == null) {
            return 0.0;
        }

        double totalVolume = 0.0;
        for (ExerciseInstance instance : currentWorkout.getExercises()) {
            if (instance.getExerciseType() != ExerciseType.CARDIO) {
                totalVolume += instance.getSets() * instance.getReps() * instance.getWeight();
            }
        }
        return totalVolume;
    }

    /**
     * Calculates the total reps performed in the workout
     */
    public int calculateTotalReps() {
        if (currentWorkout == null) {
            return 0;
        }

        int totalReps = 0;
        for (ExerciseInstance instance : currentWorkout.getExercises()) {
            if (instance.getExerciseType() != ExerciseType.CARDIO) {
                totalReps += instance.getSets() * instance.getReps();
            }
        }
        return totalReps;
    }

    /**
     * Calculates the total sets performed in the workout
     */
    public int calculateTotalSets() {
        if (currentWorkout == null) {
            return 0;
        }

        int totalSets = 0;
        for (ExerciseInstance instance : currentWorkout.getExercises()) {
            if (instance.getExerciseType() != ExerciseType.CARDIO) {
                totalSets += instance.getSets();
            }
        }
        return totalSets;
    }

    /**
     * Gets a summary of workout volume by exercise type
     */
    public String getVolumeSummary() {
        if (currentWorkout == null) {
            return "No workout data available";
        }

        StringBuilder summary = new StringBuilder();
        summary.append("Workout Volume Summary:\n");
        summary.append(String.format("Total Volume: %.1f lbs\n", calculateTotalVolume()));
        summary.append(String.format("Total Reps: %d\n", calculateTotalReps()));
        summary.append(String.format("Total Sets: %d\n\n", calculateTotalSets()));

        summary.append("By Exercise:\n");
        for (int i = 0; i < currentWorkout.getExercises().size(); i++) {
            ExerciseInstance instance = currentWorkout.getExercises().get(i);
            if (instance.getExerciseType() != ExerciseType.CARDIO) {
                double volume = instance.getSets() * instance.getReps() * instance.getWeight();
                summary.append(String.format("%d. %s: %.1f lbs (%dx%dx%.1f)\n",
                    i + 1, instance.getExerciseName(), volume,
                    instance.getSets(), instance.getReps(), instance.getWeight()));
            } else {
                summary.append(String.format("%d. %s: %d minutes (Cardio)\n",
                    i + 1, instance.getExerciseName(), instance.getDurationMinutes()));
            }
        }

        return summary.toString();
    }

    /**
     * Updates recent muscle groups based on completed workout
     */
    private void updateRecentMuscleGroups() {
        if (currentWorkout == null) return;

        recentMuscleGroups.clear();
        for (ExerciseInstance instance : currentWorkout.getExercises()) {
            ExerciseType type = instance.getExerciseType();
            if (type != ExerciseType.CARDIO) {
                recentMuscleGroups.add(type.toString());
            }
        }
    }

    /**
     * Checks for recovery suggestions based on recent workouts
     */
    private void checkRecoverySuggestions() {
        // Check recent workouts in the last few days
        LocalDateTime checkDate = LocalDateTime.now().minusDays(RECOVERY_CHECK_DAYS);
        LinkedList<CalendarEvent> recentEvents = calendarService.getEventsInRange(
            checkDate.toLocalDate(), LocalDateTime.now().toLocalDate());

        List<String> overlappingGroups = new ArrayList<>();

        for (CalendarEvent event : recentEvents) {
            if (event.getWorkout() != null) {
                for (ExerciseInstance instance : event.getWorkout().getExercises()) {
                    ExerciseType type = instance.getExerciseType();
                    if (type != ExerciseType.CARDIO && recentMuscleGroups.contains(type.toString())) {
                        if (!overlappingGroups.contains(type.toString())) {
                            overlappingGroups.add(type.toString());
                        }
                    }
                }
            }
        }

        if (!overlappingGroups.isEmpty()) {
            System.out.println("WARNING: Recent workouts have targeted these muscle groups: " +
                             String.join(", ", overlappingGroups) +
                             ". Consider allowing more recovery time.");
        }
    }

    /**
     * Gets recovery suggestions for the current workout
     */
    public String getRecoverySuggestions() {
        if (currentWorkout == null) {
            return "No workout data available";
        }

        String basicSuggestion = RecoverySuggestion.suggestRecovery(currentWorkout);

        // Add additional logic for multiple workouts targeting same groups
        StringBuilder enhancedSuggestion = new StringBuilder(basicSuggestion);

        if (!recentMuscleGroups.isEmpty()) {
            enhancedSuggestion.append("\n\nNote: Recent workouts have targeted muscle groups. " +
                                    "Monitor for fatigue and adjust intensity as needed.");
        }

        return enhancedSuggestion.toString();
    }

    /**
     * Inner class to represent workout summary
     */
    public static class WorkoutSummary {
        private final Workout workout;
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;
        private final long durationMinutes;
        private final String recoverySuggestion;
        private final double totalVolume;
        private final int totalReps;
        private final int totalSets;
        private final String volumeSummary;

        public WorkoutSummary(Workout workout, LocalDateTime startTime, LocalDateTime endTime,
                            long durationMinutes, String recoverySuggestion) {
            this.workout = workout;
            this.startTime = startTime;
            this.endTime = endTime;
            this.durationMinutes = durationMinutes;
            this.recoverySuggestion = recoverySuggestion;

            // Calculate volume metrics
            this.totalVolume = calculateWorkoutVolume(workout);
            this.totalReps = calculateWorkoutReps(workout);
            this.totalSets = calculateWorkoutSets(workout);
            this.volumeSummary = generateVolumeSummary(workout);
        }

        public Workout getWorkout() { return workout; }
        public LocalDateTime getStartTime() { return startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public long getDurationMinutes() { return durationMinutes; }
        public String getRecoverySuggestion() { return recoverySuggestion; }
        public double getTotalVolume() { return totalVolume; }
        public int getTotalReps() { return totalReps; }
        public int getTotalSets() { return totalSets; }
        public String getVolumeSummary() { return volumeSummary; }

        private static double calculateWorkoutVolume(Workout workout) {
            if (workout == null) return 0.0;
            double total = 0.0;
            for (ExerciseInstance instance : workout.getExercises()) {
                if (instance.getExerciseType() != ExerciseType.CARDIO) {
                    total += instance.getSets() * instance.getReps() * instance.getWeight();
                }
            }
            return total;
        }

        private static int calculateWorkoutReps(Workout workout) {
            if (workout == null) return 0;
            int total = 0;
            for (ExerciseInstance instance : workout.getExercises()) {
                if (instance.getExerciseType() != ExerciseType.CARDIO) {
                    total += instance.getSets() * instance.getReps();
                }
            }
            return total;
        }

        private static int calculateWorkoutSets(Workout workout) {
            if (workout == null) return 0;
            int total = 0;
            for (ExerciseInstance instance : workout.getExercises()) {
                if (instance.getExerciseType() != ExerciseType.CARDIO) {
                    total += instance.getSets();
                }
            }
            return total;
        }

        private static String generateVolumeSummary(Workout workout) {
            if (workout == null) return "No workout data available";

            StringBuilder summary = new StringBuilder();
            double totalVolume = calculateWorkoutVolume(workout);
            int totalReps = calculateWorkoutReps(workout);
            int totalSets = calculateWorkoutSets(workout);

            summary.append("💪 Workout Volume Summary:\n");
            summary.append(String.format("Total Volume: %.1f lbs lifted\n", totalVolume));
            summary.append(String.format("Total Reps: %d\n", totalReps));
            summary.append(String.format("Total Sets: %d\n\n", totalSets));

            summary.append("Exercise Breakdown:\n");
            for (int i = 0; i < workout.getExercises().size(); i++) {
                ExerciseInstance instance = workout.getExercises().get(i);
                if (instance.getExerciseType() != ExerciseType.CARDIO) {
                    double volume = instance.getSets() * instance.getReps() * instance.getWeight();
                    summary.append(String.format("%d. %s: %.1f lbs (%dx%dx%.1f)\n",
                        i + 1, instance.getExerciseName(), volume,
                        instance.getSets(), instance.getReps(), instance.getWeight()));
                } else {
                    summary.append(String.format("%d. %s: %d minutes (Cardio)\n",
                        i + 1, instance.getExerciseName(), instance.getDurationMinutes()));
                }
            }

            return summary.toString();
        }

        @Override
        public String toString() {
            return String.format("Workout completed in %d minutes\n%s\nRecovery: %s\n\n%s",
                               durationMinutes, workout.toString(), recoverySuggestion, volumeSummary);
        }
    }
}