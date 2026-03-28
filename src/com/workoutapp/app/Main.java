package com.workoutapp.app;

import com.workoutapp.models.*;
import com.workoutapp.services.*;
import java.time.LocalDateTime;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/workoutapp/ui/MainView.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Workout Routine Builder");
        stage.show();
    }
    public static void main(String[] args) {

        // initialize services
        ExerciseService exerciseService = new ExerciseService();
        RoutineService routineService = new RoutineService();
        CalendarService calendarService = new CalendarService();

        // create some exercises
        Exercise pushUp = new Exercise("Push-Up", "description", ExerciseType.CHEST);
        exerciseService.addExercise(pushUp);
        Exercise squat = new Exercise("Squat", "description", ExerciseType.LEGS);
        exerciseService.addExercise(squat);
        Exercise plank = new Exercise("Plank", "description", ExerciseType.CORE);
        exerciseService.addExercise(plank);

        // create a routine and add exercises to it
        Routine test = new Routine("test");
        routineService.addRoutine(test);

        routineService.addExercise("test", pushUp);
        routineService.addExercise("test", squat);
        routineService.addExercise("test", plank);

        // refetch the routine to ensure it has all exercises
        test = routineService.findRoutine("test");

        test.toString();

        Workout workout = new Workout(test);
        System.out.println(workout.toString());

        // create calendar event with the workout
        CalendarEvent event = new CalendarEvent(LocalDateTime.now(), workout, "Demo workout session");
        event.toString();
        calendarService.addEvent(event);

        launch(args);
    }    
}