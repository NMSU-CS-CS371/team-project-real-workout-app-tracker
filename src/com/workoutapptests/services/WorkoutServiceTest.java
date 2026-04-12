package com.workoutapptests.services;

import com.workoutapp.models.*;
import com.workoutapp.services.*;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class WorkoutServiceTest {

    private WorkoutService workoutService;
    private CalendarService calendarService;
    private RoutineService routineService;
    private ExerciseService exerciseService;
    private String testProfile = "testProfile";

    @BeforeEach
    public void setUp() {
        // Create services
        calendarService = new CalendarService(testProfile);
        routineService = new RoutineService(testProfile);
        exerciseService = new ExerciseService(testProfile);
        workoutService = new WorkoutService(testProfile, calendarService, routineService, exerciseService);

        // Clear any existing data
        calendarService.getEvents().clear();
        routineService.getRoutines().clear();
        exerciseService.getExercises().clear();

        // Add test exercises
        Exercise benchPress = new Exercise("Bench Press", "Chest exercise", ExerciseType.CHEST);
        Exercise squats = new Exercise("Squats", "Leg exercise", ExerciseType.LEGS);
        exerciseService.addExercise(benchPress);
        exerciseService.addExercise(squats);

        // Add test routine
        Routine testRoutine = new Routine("Test Routine");
        testRoutine.addExercise(benchPress);
        testRoutine.addExercise(squats);
        routineService.addRoutine(testRoutine);
    }

    @AfterEach
    public void tearDown() {
        workoutService.cancelWorkout();
    }

    @Test
    public void testStartWorkoutFromRoutine() {
        workoutService.startWorkoutFromRoutine("Test Routine");

        assertTrue(workoutService.isWorkoutActive());
        assertNotNull(workoutService.getCurrentWorkout());
        assertEquals(2, workoutService.getCurrentWorkout().getNumExercises());

        // Check that exercises have baseline sets
        ExerciseInstance benchInstance = workoutService.getCurrentWorkout().getExercises().get(0);
        assertEquals(3, benchInstance.getSets());
        assertEquals(10, benchInstance.getReps());
    }

    @Test
    public void testStartWorkoutFromScratch() {
        workoutService.startWorkoutFromScratch();

        assertTrue(workoutService.isWorkoutActive());
        assertNotNull(workoutService.getCurrentWorkout());
        assertEquals(0, workoutService.getCurrentWorkout().getNumExercises());
    }

    @Test
    public void testAddExerciseToWorkout() {
        workoutService.startWorkoutFromScratch();
        workoutService.addExerciseToWorkout("Bench Press");

        assertEquals(1, workoutService.getCurrentWorkout().getNumExercises());
        ExerciseInstance instance = workoutService.getCurrentWorkout().getExercises().get(0);
        assertEquals("Bench Press", instance.getExerciseName());
        assertEquals(3, instance.getSets());
    }

    @Test
    public void testUpdateExerciseData() {
        workoutService.startWorkoutFromRoutine("Test Routine");

        workoutService.updateExerciseData(0, 4, 12, 150.0);

        ExerciseInstance instance = workoutService.getCurrentWorkout().getExercises().get(0);
        assertEquals(4, instance.getSets());
        assertEquals(12, instance.getReps());
        assertEquals(150.0, instance.getWeight());
    }

    @Test
    public void testEndWorkout() {
        workoutService.startWorkoutFromRoutine("Test Routine");

        // Simulate some time passing
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // Ignore
        }

        WorkoutService.WorkoutSummary summary = workoutService.endWorkout("Test workout notes");

        assertFalse(workoutService.isWorkoutActive());
        assertNotNull(summary);
        assertTrue(summary.getDurationMinutes() >= 0);
        assertNotNull(summary.getRecoverySuggestion());

        // Check that workout was saved to calendar
        assertEquals(1, calendarService.getEvents().size());
        CalendarEvent event = calendarService.getEvents().get(0);
        assertEquals("Test workout notes", event.getNotes());
        assertNotNull(event.getWorkout());
    }

    @Test
    public void testRestTimer() throws Exception {
        CompletableFuture<Void> future = workoutService.startRestTimer(1); // 1 second timer

        // Wait for timer to complete
        future.get(2, TimeUnit.SECONDS);

        assertTrue(future.isDone());
    }

    @Test
    public void testCancelWorkout() {
        workoutService.startWorkoutFromRoutine("Test Routine");
        assertTrue(workoutService.isWorkoutActive());

        workoutService.cancelWorkout();
        assertFalse(workoutService.isWorkoutActive());
        assertNull(workoutService.getCurrentWorkout());
    }

    @Test
    public void testInvalidOperationsWhenNoWorkoutActive() {
        assertThrows(IllegalStateException.class, () -> {
            workoutService.addExerciseToWorkout("Bench Press");
        });

        assertThrows(IllegalStateException.class, () -> {
            workoutService.updateExerciseData(0, 3, 10, 100.0);
        });

        assertThrows(IllegalStateException.class, () -> {
            workoutService.setCurrentExerciseIndex(0);
        });
    }

    @Test
    public void testExerciseNavigation() {
        workoutService.startWorkoutFromRoutine("Test Routine");

        // Initial position should be 0
        assertEquals(0, workoutService.getCurrentExerciseIndex());
        assertNotNull(workoutService.getCurrentExercise());
        assertEquals("Bench Press", workoutService.getCurrentExercise().getExerciseName());

        // Move to next exercise
        assertTrue(workoutService.moveToNextExercise());
        assertEquals(1, workoutService.getCurrentExerciseIndex());
        assertEquals("Squats", workoutService.getCurrentExercise().getExerciseName());

        // Try to move past last exercise
        assertFalse(workoutService.moveToNextExercise());
        assertEquals(1, workoutService.getCurrentExerciseIndex());

        // Move back to previous exercise
        assertTrue(workoutService.moveToPreviousExercise());
        assertEquals(0, workoutService.getCurrentExerciseIndex());
        assertEquals("Bench Press", workoutService.getCurrentExercise().getExerciseName());

        // Try to move before first exercise
        assertFalse(workoutService.moveToPreviousExercise());
        assertEquals(0, workoutService.getCurrentExerciseIndex());
    }

    @Test
    public void testSetCurrentExerciseIndex() {
        workoutService.startWorkoutFromRoutine("Test Routine");

        workoutService.setCurrentExerciseIndex(1);
        assertEquals(1, workoutService.getCurrentExerciseIndex());
        assertEquals("Squats", workoutService.getCurrentExercise().getExerciseName());

        workoutService.setCurrentExerciseIndex(0);
        assertEquals(0, workoutService.getCurrentExerciseIndex());
        assertEquals("Bench Press", workoutService.getCurrentExercise().getExerciseName());
    }

    @Test
    public void testVolumeCalculations() {
        workoutService.startWorkoutFromRoutine("Test Routine");

        // Update exercise data
        workoutService.updateExerciseData(0, 3, 10, 135.0); // Bench Press: 3 sets, 10 reps, 135 lbs
        workoutService.updateExerciseData(1, 4, 8, 185.0);  // Squats: 4 sets, 8 reps, 185 lbs

        // Calculate expected values
        double expectedVolume = (3 * 10 * 135.0) + (4 * 8 * 185.0); // 4050 + 5920 = 9970
        int expectedReps = (3 * 10) + (4 * 8); // 30 + 32 = 62
        int expectedSets = 3 + 4; // 7

        assertEquals(expectedVolume, workoutService.calculateTotalVolume(), 0.1);
        assertEquals(expectedReps, workoutService.calculateTotalReps());
        assertEquals(expectedSets, workoutService.calculateTotalSets());
    }

    @Test
    public void testVolumeSummary() {
        workoutService.startWorkoutFromRoutine("Test Routine");

        workoutService.updateExerciseData(0, 3, 10, 135.0);
        workoutService.updateExerciseData(1, 4, 8, 185.0);

        String summary = workoutService.getVolumeSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("9970.0 lbs"));
        assertTrue(summary.contains("62"));
        assertTrue(summary.contains("7"));
        assertTrue(summary.contains("Bench Press"));
        assertTrue(summary.contains("Squats"));
    }

    @Test
    public void testWorkoutSummaryWithVolume() {
        workoutService.startWorkoutFromRoutine("Test Routine");

        workoutService.updateExerciseData(0, 3, 10, 135.0);
        workoutService.updateExerciseData(1, 4, 8, 185.0);

        WorkoutService.WorkoutSummary summary = workoutService.endWorkout("Test workout");

        assertEquals(9970.0, summary.getTotalVolume(), 0.1);
        assertEquals(62, summary.getTotalReps());
        assertEquals(7, summary.getTotalSets());
        assertNotNull(summary.getVolumeSummary());
        assertTrue(summary.getVolumeSummary().contains("9970.0 lbs"));
    }

    @Test
    public void testInvalidExerciseIndex() {
        workoutService.startWorkoutFromRoutine("Test Routine");

        assertThrows(IllegalArgumentException.class, () -> {
            workoutService.setCurrentExerciseIndex(-1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            workoutService.setCurrentExerciseIndex(5); // Only 2 exercises
        });
    }
}