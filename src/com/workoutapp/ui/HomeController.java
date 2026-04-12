package com.workoutapp.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import com.workoutapp.services.*;

import java.util.LinkedList;
import java.util.List;

import com.workoutapp.models.*;

public class HomeController implements ScreenController {
    private MainController main;
    private CalendarService calendarService;

    @FXML private ListView<Workout> recentWorkoutsList;
    @FXML private Button startWorkoutButton;
    @FXML private Button historyButton;
    @FXML private Button routineEditorButton;
    @FXML private VBox recoveryBox;

    @Override
    public void setMainController(MainController mainController){
        this.main = mainController;
        //Navigation buttons
        startWorkoutButton.setOnAction(e -> main.loadView("WorkoutView.fxml"));
        historyButton.setOnAction(e -> main.loadView("WorkoutHistoryView.xml"));
        routineEditorButton.setOnAction(e -> main.loadView("RoutineView.fxml"));
    }

    @Override
    public void onProfileChanged(String profileName) {
        if (profileName == null) return;

        // Load workouts for this profile
        calendarService = new CalendarService(profileName);

        recentWorkoutsList.getItems().setAll(
            getRecentWorkouts(calendarService, 5) // last 5 workouts
        );

        // TODO: Load recovery suggestions here
    }

    //Get recent calendarEvents
    private LinkedList<Workout> getRecentWorkouts(CalendarService calendarService, int num) {
        LinkedList<CalendarEvent> all = calendarService.getEvents();

        int size = all.size();
        int start = Math.max(0, size - num);

        List<CalendarEvent> sub = all.subList(start, size);
        LinkedList<Workout> workouts = new LinkedList<Workout>();
        for(int i = 0; i < sub.size(); i++){
            workouts.add(sub.get(i).getWorkout());
        }
        return workouts;
    }
}
