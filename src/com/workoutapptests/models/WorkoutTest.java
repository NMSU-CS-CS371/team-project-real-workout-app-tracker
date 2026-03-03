package com.workoutapptests.models;

import com.workoutapp.models.Workout;
import com.workoutapp.models.Workout.WorkoutExercise;
import com.workoutapp.models.SetEntry;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class WorkoutTest {
    private Workout workout;

    @BeforeEach
    public void setUp() {
        workout = new Workout("Morning");
    }

    @AfterEach
    public void tearDown() {
        workout = null;
    }

    @Test
    public void testAddAndRemoveExercise() {
        WorkoutExercise e = new WorkoutExercise("Squat");
        workout.addExercise(e);
        List<WorkoutExercise> list = workout.getExercises();
        assertEquals(1, list.size());
        assertEquals("Squat", list.get(0).getName());

        workout.removeExercise(e);
        assertTrue(workout.getExercises().isEmpty());
    }

    @Test
    public void testToStringContainsTitle() {
        String s = workout.toString();
        assertTrue(s.contains("Workout: Morning"));
    }

    @Test
    public void testExerciseSetOperations() {
        WorkoutExercise e = new WorkoutExercise("Pushup");
        SetEntry set = new SetEntry(10, 0, "");
        e.addSet(set);
        assertEquals(1, e.getSets().size());
        e.removeSet(set);
        assertTrue(e.getSets().isEmpty());
    }
}
