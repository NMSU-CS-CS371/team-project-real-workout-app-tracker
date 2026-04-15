package com.workoutapp.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import com.workoutapp.services.*;
import java.util.LinkedList;
import java.util.List;
import com.workoutapp.models.*;
import java.time.format.DateTimeFormatter;

public class HomeController implements ScreenController {
    private MainController main;
    private CalendarService calendarService;

    @FXML private ListView<CalendarEvent> recentWorkoutsList;
    @FXML private Button startWorkoutButton;
    @FXML private Button historyButton;
    @FXML private Button routineEditorButton;
    @FXML private VBox recoveryBox;

    @Override
    public void setMainController(MainController mainController){
        this.main = mainController;
        //Navigation buttons
        startWorkoutButton.setOnAction(e -> main.loadView("WorkoutStartView.fxml"));
        historyButton.setOnAction(e -> main.loadView("WorkoutHistoryView.xml"));
        routineEditorButton.setOnAction(e -> main.loadView("RoutineEditorView.fxml"));
    }

    @Override
    public void onProfileChanged(String profileName) {
        if (profileName == null) return;

        // Load workouts for this profile
        calendarService = new CalendarService(profileName);
        recentWorkoutsList.getItems().setAll(
            getRecentWorkouts(calendarService, 5) // last 5 workouts
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
                setText("Workout\t" + formatted + "\n" + event.getWorkout().toString() + "\n");
            }
}       );

        // TODO: Load recovery suggestions here
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
}
