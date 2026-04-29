package com.workoutapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import com.workoutapp.models.*;
import com.workoutapp.services.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

/**
 * Manages workout history screen where you can view details about all your
 * logged workouts, including exercise data, volume summary and exercise breakdown.
 * Also allows you to adjust date/time, edit Notes, and delete logged workouts.
 */

public class WorkoutHistoryController implements ScreenController{
    private MainController main;
    private CalendarService calendarService;

    //Initialize fxml components
    @FXML private ListView<CalendarEvent> eventListView;
    @FXML private ListView<String> exerciseListView;

    @FXML private TextField dateField;
    @FXML private TextField timeField;
    @FXML private TextField notesField;

    @FXML private Button deleteEventButton;
    @FXML private Button adjustDateTimeButton;
    @FXML private Button saveNotesButton;
    @FXML private Button exitButton;

    @FXML private Label workoutTitleLabel;

    //Date formats for workout
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter listFmt = DateTimeFormatter.ofPattern("dd MMM yyyy\tHH:mm");

    //Set main controller from interface
    @Override
    public void setMainController(MainController mainController) {
        this.main = mainController;
    }

    //Initialize workoutHistoryController
    public void initialize(){
        exitButton.setOnAction(e -> main.loadView("HomeView.fxml"));
        deleteEventButton.setOnAction(e -> deleteSelectedEvent());
        adjustDateTimeButton.setOnAction(e -> adjustSelectedEventDateTime());
        saveNotesButton.setOnAction(e -> saveNotes());        
    }

    //Refresh when profile calendar service
    public void onProfileChanged(String profileName) {
        if (profileName == null) return;

        //Set up calendar service
        calendarService = new CalendarService(profileName);
        loadEventsIntoList();
        eventListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(CalendarEvent event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                    return;
                }
                setText("Workout\t" + event.getDateTime().format(listFmt));
            }
        });
        eventListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> displayWorkoutDetails(newVal)
        );
    }

    //Load events into list
    private void loadEventsIntoList() {
        LinkedList<CalendarEvent> events = calendarService.getEvents();
        events.sort((a,b) -> b.getDateTime().compareTo(a.getDateTime()));
        eventListView.getItems().setAll(events);
    }

    //Display workout labels
    private void displayWorkoutDetails(CalendarEvent event) {
        if (event == null) return;

        // Update title
        workoutTitleLabel.setText("Workout Details — " + event.getDateTime().format(listFmt));

        // Update fields
        dateField.setText(event.getDateTime().toLocalDate().format(dateFmt));
        timeField.setText(event.getDateTime().toLocalTime().format(timeFmt));
        notesField.setText(event.getNotes());

        // Exercises
        exerciseListView.getItems().clear();
        Workout workout = event.getWorkout();
        if (workout == null) return;

        // Add analytics summary
        WorkoutService.WorkoutSummary summary =
            new WorkoutService.WorkoutSummary(workout,
                event.getDateTime(),
                event.getDateTime(),
                0,
            "");
        exerciseListView.getItems().add(summary.toString());
    }

    //Delete logged workout
    private void deleteSelectedEvent(){
        CalendarEvent selected = eventListView.getSelectionModel().getSelectedItem();
        if(selected == null) return;

        calendarService.removeEvent(selected);
        //Refresh
        loadEventsIntoList();
        exerciseListView.getItems().clear();
        notesField.clear();
        dateField.clear();
        timeField.clear();
    }

    //Adjust selected workout date and time
    private void adjustSelectedEventDateTime() {
        CalendarEvent selected = eventListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            LocalDate newDate = LocalDate.parse(dateField.getText(), dateFmt);
            LocalTime newTime = LocalTime.parse(timeField.getText(), timeFmt);

            LocalDateTime newDateTime = LocalDateTime.of(newDate, newTime);
            selected.setDateTime(newDateTime);

            calendarService.saveEvents(); // persist updated list
            loadEventsIntoList();
            eventListView.getSelectionModel().select(selected);
        } catch (Exception e) {
            System.out.println("Invalid date/time format");
        }
    }

    //Update notes for selected workout
    private void saveNotes(){
        CalendarEvent selected = eventListView.getSelectionModel().getSelectedItem();
        if(selected == null) return;

        selected.setNotes(notesField.getText());
        calendarService.saveEvents();
    }
}
