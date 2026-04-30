package com.workoutapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import com.workoutapp.models.*;
import com.workoutapp.services.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
    @FXML private ListView<CalendarEvent> selectedDateEventList;
    @FXML private ListView<String> exerciseListView;

    @FXML private TextField dateField;
    @FXML private TextField timeField;
    @FXML private TextField notesField;

    @FXML private Button deleteEventButton;
    @FXML private Button adjustDateTimeButton;
    @FXML private Button saveNotesButton;
    @FXML private Button exitButton;
    @FXML private Button prevMonthButton;
    @FXML private Button nextMonthButton;

    @FXML private Label workoutTitleLabel;
    @FXML private Label monthLabel;
    @FXML private Label selectedDateLabel;
    @FXML private GridPane calendarGrid;

    private YearMonth displayedMonth = YearMonth.now();
    private LocalDate selectedDate = LocalDate.now();

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
        prevMonthButton.setOnAction(e -> changeMonth(-1));
        nextMonthButton.setOnAction(e -> changeMonth(1));

        selectedDateEventList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(CalendarEvent event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                    return;
                }
                setText(event.getDateTime().format(listFmt) + " - "
                    + (event.getWorkout() != null ? event.getWorkout().getNumExercises() + " exercises" : "No workout"));
            }
        });
        selectedDateEventList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> displayWorkoutDetails(newVal)
        );
    }

    //Refresh when profile calendar service
    public void onProfileChanged(String profileName) {
        if (profileName == null) return;

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
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    selectDate(newVal.getDateTime().toLocalDate());
                }
            }
        );

        buildCalendar();
        selectDate(selectedDate);
    }

    //Load events into list
    private void loadEventsIntoList() {
        LinkedList<CalendarEvent> events = calendarService.getEvents();
        events.sort((a,b) -> b.getDateTime().compareTo(a.getDateTime()));
        eventListView.getItems().setAll(events);
    }

    private void buildCalendar() {
        calendarGrid.getChildren().clear();
        monthLabel.setText(displayedMonth.getMonth().toString() + " " + displayedMonth.getYear());

        String[] headers = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        for (int col = 0; col < headers.length; col++) {
            Label header = new Label(headers[col]);
            header.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155;");
            calendarGrid.add(header, col, 0);
        }

        LocalDate firstOfMonth = displayedMonth.atDay(1);
        int weekday = firstOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = displayedMonth.lengthOfMonth();
        int row = 1;
        int col = weekday;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = displayedMonth.atDay(day);
            Button dayButton = new Button(String.valueOf(day));
            dayButton.setMaxWidth(Double.MAX_VALUE);
            dayButton.setMaxHeight(Double.MAX_VALUE);
            dayButton.setOnAction(e -> selectDate(date));

            if (date.equals(selectedDate)) {
                dayButton.setStyle("-fx-border-color: #2563eb; -fx-border-width: 2; -fx-background-color: #e0f2fe;");
            }

            if (!calendarService.getEventsForDate(date).isEmpty()) {
                dayButton.setStyle(dayButton.getStyle() + "-fx-background-color: #d1fae5;");
            }

            calendarGrid.add(dayButton, col, row);
            GridPane.setHgrow(dayButton, Priority.ALWAYS);
            GridPane.setVgrow(dayButton, Priority.ALWAYS);
            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }

    private void selectDate(LocalDate date) {
        selectedDate = date;
        buildCalendar();
        selectedDateLabel.setText("Selected: " + date.format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")));

        LinkedList<CalendarEvent> dateEvents = calendarService.getEventsForDate(date);
        selectedDateEventList.getItems().setAll(dateEvents);
        if (!dateEvents.isEmpty()) {
            selectedDateEventList.getSelectionModel().selectFirst();
            displayWorkoutDetails(dateEvents.getFirst());
        } else {
            exerciseListView.getItems().clear();
            workoutTitleLabel.setText("Workout Details");
            dateField.clear();
            timeField.clear();
            notesField.clear();
        }
    }

    private void changeMonth(int months) {
        displayedMonth = displayedMonth.plusMonths(months);
        buildCalendar();
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
