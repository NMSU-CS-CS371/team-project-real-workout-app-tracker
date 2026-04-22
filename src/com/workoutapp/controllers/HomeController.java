package com.workoutapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import com.workoutapp.services.*;
import java.util.LinkedList;
import java.util.List;
import com.workoutapp.models.*;
import java.time.format.DateTimeFormatter;

/**
 * Controls the Home screen, displaying recent workouts and recovery suggestions.
 * Responds to profile changes by loading profile‑specific workout history and
 * generating recovery analysis. Provides navigation to workout start, history,
 * and routine editor views.
 */

public class HomeController implements ScreenController {
    private MainController main;
    private CalendarService calendarService;

    @FXML private ListView<CalendarEvent> recentWorkoutsList;
    @FXML private Button startWorkoutButton;
    @FXML private Button historyButton;
    @FXML private Button routineEditorButton;
    @FXML private Button reportButton;
    @FXML private VBox recoveryBox;

    @Override
    public void setMainController(MainController mainController){
        this.main = mainController;
        //Navigation buttons
        startWorkoutButton.setOnAction(e -> main.loadView("WorkoutStartView.fxml"));
        historyButton.setOnAction(e -> main.loadView("WorkoutHistoryView.fxml"));
        routineEditorButton.setOnAction(e -> main.loadView("RoutineEditorView.fxml"));
        reportButton.setOnAction(e -> main.loadView("ReportView.fxml"));
    }

    @Override
    public void onProfileChanged(String profileName) {
        if (profileName == null) return;

        // Load workouts for this profile
        calendarService = new CalendarService(profileName);
        recentWorkoutsList.getItems().setAll(
            getRecentWorkouts(calendarService, 3) // last 5 workouts
        );
        recentWorkoutsList.setCellFactory(list -> new ListCell<>() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy,\tHH:mm");
            
            @Override
            protected void updateItem(CalendarEvent event, boolean empty) {
                super.updateItem(event, empty);

                if (empty || event == null) {
                    setText(null);
                    return;
                }
                String formatted = event.getDateTime().format(fmt);
                setText("Workout\t" + formatted + "\n" + event.getWorkout().toString() + "Notes: " + event.getNotes() + "\n\n");
            }
}       );

        loadRecoverySuggestions(profileName);
    }

    //Get recent calendarEvents
    private LinkedList<CalendarEvent> getRecentWorkouts(CalendarService calendarService, int num) {
        LinkedList<CalendarEvent> all = calendarService.getEvents();
        int size = all.size();
        int start = Math.max(0, size - num);
        LinkedList<CalendarEvent> result = new LinkedList<CalendarEvent>();
        for(int i = size - 1; i >= start; i--){
            result.add(all.get(i));
        }
        return result;
    }

    //Load recovery suggestions
    private void loadRecoverySuggestions(String profileName){
        recoveryBox.getChildren().clear();

        //Set up services for workoutService
        RoutineService routineService = new RoutineService(profileName);
        ExerciseService exerciseService = new ExerciseService(profileName);
        WorkoutService ws = new WorkoutService(profileName, calendarService, routineService, exerciseService);
    
        //Get and display recovery analysis
        String analysis = ws.getFullRecoverySuggestions();
        Label label = new Label(analysis);
        label.setWrapText(true);
        recoveryBox.getChildren().add(label);
    }
}
