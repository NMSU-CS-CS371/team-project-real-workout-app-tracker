package com.workoutapp.controllers;

import com.workoutapp.models.CalendarEvent;
import com.workoutapp.models.ExerciseInstance;
import com.workoutapp.models.Workout;
import com.workoutapp.services.CalendarService;

import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.print.PageLayout;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.Cursor;
import javafx.scene.transform.Scale;
import javafx.stage.Window;

public class ReportController implements ScreenController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    @FXML private Button backButton;
    @FXML private Button exportPdfButton;
    @FXML private Label titleLabel;
    @FXML private ScrollPane reportScrollPane;
    @FXML private VBox reportContentBox;

    private MainController main;
    private CalendarService calendarService;

    // Connects controller to the main app controller 
    public void setMainController(MainController mainController) {
        this.main = mainController;

        backButton.setOnAction(e -> main.loadView("HomeView.fxml"));
        exportPdfButton.setOnAction(e -> exportPdf());
    }

    // Refreshes report data whenever the active profile changes.
    @Override
    public void onProfileChanged(String profileName) {
        if (profileName == null) return;

        titleLabel.setText("Workout Report - " + profileName);
        calendarService = new CalendarService(profileName);
        renderEvents(calendarService.getEvents());
    }

    // Builds the report screen content: summary metrics and clickable workout cards.
    private void renderEvents(LinkedList<CalendarEvent> events) {
        reportContentBox.getChildren().clear();
        reportContentBox.setStyle("-fx-background-color: #f5f7fb;");

        int workoutCount = events.size();
        int exerciseCount = 0;
        int cardioMinutes = 0;

        for (CalendarEvent event : events) {
            Workout workout = event.getWorkout();
            if (workout == null) continue;
            for (ExerciseInstance ex : workout.getExercises()) {
                exerciseCount++;
                if (ex.getDurationMinutes() > 0) {
                    cardioMinutes += ex.getDurationMinutes();
                }
            }
        }

        Label sectionHeader = new Label("Summary");
        sectionHeader.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        HBox summaryRow = new HBox(10,
            metricCard("Workouts", String.valueOf(workoutCount)),
            metricCard("Exercises", String.valueOf(exerciseCount)),
            metricCard("Cardio Minutes", String.valueOf(cardioMinutes))
        );

        Label detailsHeader = new Label("Workout Sessions");
        detailsHeader.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        reportContentBox.getChildren().addAll(sectionHeader, summaryRow, detailsHeader);

        if (events.isEmpty()) {
            Label empty = new Label("No workouts recorded yet.");
            empty.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 16; -fx-text-fill: #6b7280;");
            reportContentBox.getChildren().add(empty);
            return;
        }

        for (int i = events.size() - 1; i >= 0; i--) {
            CalendarEvent event = events.get(i);
            VBox card = new VBox(8);
            card.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 12;");
            card.setCursor(Cursor.HAND);
            card.setOnMouseClicked(e -> main.loadReportDetailView(event));

            String when = event.getDateTime() == null ? "Unknown date" : event.getDateTime().format(DATE_FORMAT);
            Label header = new Label(when);
            header.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #111827;");

            Workout workout = event.getWorkout();
            int eventExerciseCount = (workout == null) ? 0 : workout.getExercises().size();
            Label badge = new Label(eventExerciseCount + " exercises");
            badge.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-padding: 4 8 4 8; -fx-background-radius: 999;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox headerRow = new HBox(8, header, spacer, badge);
            card.getChildren().add(headerRow);

            if (workout == null || workout.getExercises().isEmpty()) {
                Label none = new Label("No exercises logged.");
                none.setStyle("-fx-text-fill: #6b7280;");
                card.getChildren().add(none);
            } else {
                for (ExerciseInstance ex : workout.getExercises()) {
                    String line;
                    if (ex.getDurationMinutes() > 0) {
                        line = ex.getExerciseName() + "  •  " + ex.getDurationMinutes() + " min cardio";
                    } else {
                        line = ex.getExerciseName() + "  •  " + ex.getSets() + " sets x " + ex.getReps() + " reps";
                    }
                    Label exerciseLine = new Label(line);
                    exerciseLine.setStyle("-fx-text-fill: #374151;");
                    card.getChildren().add(exerciseLine);
                }
            }

            String notes = (event.getNotes() == null || event.getNotes().isBlank()) ? "No notes." : event.getNotes();
            Label notesLabel = new Label("Notes: " + notes);
            notesLabel.setWrapText(true);
            notesLabel.setStyle("-fx-text-fill: #4b5563; -fx-background-color: #f9fafb; -fx-border-color: #f3f4f6; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8;");
            card.getChildren().add(notesLabel);

            reportContentBox.getChildren().add(card);
        }
    }

    // Creates a summary metric card used at the top of the report.
    private VBox metricCard(String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12;");

        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-text-fill: #111827; -fx-font-size: 22; -fx-font-weight: bold;");

        VBox card = new VBox(4, labelNode, valueNode);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 12;");
        card.setPrefWidth(180);
        return card;
    }

    // Opens the print dialog and prints the report content as a PDF/printed page.
    private void exportPdf() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            showMessage("Export PDF", "printing is not available.");
            return;
        }

        Window owner = reportContentBox.getScene() == null ? null : reportContentBox.getScene().getWindow();
        boolean accepted = job.showPrintDialog(owner);
        if (!accepted) {
            return;
        }

        PageLayout pageLayout = job.getJobSettings().getPageLayout();

        double contentWidth = reportContentBox.getBoundsInParent().getWidth();
        double contentHeight = reportContentBox.getBoundsInParent().getHeight();
        if (contentWidth <= 0 || contentHeight <= 0) {
            showMessage("Export PDF", "Empty report.");
            return;
        }

        double scaleX = pageLayout.getPrintableWidth() / contentWidth;
        double scaleY = pageLayout.getPrintableHeight() / contentHeight;
        double scaleValue = Math.min(scaleX, scaleY);

        Scale scale = new Scale(scaleValue, scaleValue);
        reportContentBox.getTransforms().add(scale);

        boolean success;
        try {
            success = job.printPage(pageLayout, reportContentBox);
        } finally {
            reportContentBox.getTransforms().remove(scale);
        }

        if (success) {
            job.endJob();
            showMessage("Export PDF", "Successfully printed to pdf.");
        } else {
            showMessage("Export PDF", "Failed to print report.");
        }
    }

    // Shows a simple informational popup for export and error messages.
    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package com.workoutapp.controllers;

import com.workoutapp.models.CalendarEvent;
import com.workoutapp.models.ExerciseInstance;
import com.workoutapp.models.Workout;
import com.workoutapp.services.CalendarService;

import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.print.PageLayout;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.Cursor;
import javafx.scene.transform.Scale;
import javafx.stage.Window;

public class ReportController implements ScreenController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    @FXML private Button backButton;
    @FXML private Button exportPdfButton;
    @FXML private Label titleLabel;
    @FXML private ScrollPane reportScrollPane;
    @FXML private VBox reportContentBox;

    private MainController main;
    private CalendarService calendarService;

    // Connects this controller to the main app controller and wires top-level actions.
    @Override
    public void setMainController(MainController mainController) {
        this.main = mainController;

        backButton.setOnAction(e -> main.loadView("HomeView.fxml"));
        exportPdfButton.setOnAction(e -> exportPdf());
    }

    // Refreshes report data whenever the active profile changes.
    @Override
    public void onProfileChanged(String profileName) {
        if (profileName == null) return;

        titleLabel.setText("Workout Report - " + profileName);
        calendarService = new CalendarService(profileName);
        renderEvents(calendarService.getEvents());
    }

    // Builds the report screen content: summary metrics and clickable workout cards.
    private void renderEvents(LinkedList<CalendarEvent> events) {
        reportContentBox.getChildren().clear();
        reportContentBox.setStyle("-fx-background-color: #f5f7fb;");

        int workoutCount = events.size();
        int exerciseCount = 0;
        int cardioMinutes = 0;

        for (CalendarEvent event : events) {
            Workout workout = event.getWorkout();
            if (workout == null) continue;
            for (ExerciseInstance ex : workout.getExercises()) {
                exerciseCount++;
                if (ex.getDurationMinutes() > 0) {
                    cardioMinutes += ex.getDurationMinutes();
                }
            }
        }

        Label sectionHeader = new Label("Summary");
        sectionHeader.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        HBox summaryRow = new HBox(10,
            metricCard("Workouts", String.valueOf(workoutCount)),
            metricCard("Exercises", String.valueOf(exerciseCount)),
            metricCard("Cardio Minutes", String.valueOf(cardioMinutes))
        );

        Label detailsHeader = new Label("Workout Sessions");
        detailsHeader.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        reportContentBox.getChildren().addAll(sectionHeader, summaryRow, detailsHeader);

        if (events.isEmpty()) {
            Label empty = new Label("No workouts recorded yet.");
            empty.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 16; -fx-text-fill: #6b7280;");
            reportContentBox.getChildren().add(empty);
            return;
        }

        for (int i = events.size() - 1; i >= 0; i--) {
            CalendarEvent event = events.get(i);
            VBox card = new VBox(8);
            card.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 12;");
            card.setCursor(Cursor.HAND);
            card.setOnMouseClicked(e -> main.loadReportDetailView(event));

            String when = event.getDateTime() == null ? "Unknown date" : event.getDateTime().format(DATE_FORMAT);
            Label header = new Label(when);
            header.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #111827;");

            Workout workout = event.getWorkout();
            int eventExerciseCount = (workout == null) ? 0 : workout.getExercises().size();
            Label badge = new Label(eventExerciseCount + " exercises");
            badge.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #1d4ed8; -fx-padding: 4 8 4 8; -fx-background-radius: 999;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox headerRow = new HBox(8, header, spacer, badge);
            card.getChildren().add(headerRow);

            if (workout == null || workout.getExercises().isEmpty()) {
                Label none = new Label("No exercises logged.");
                none.setStyle("-fx-text-fill: #6b7280;");
                card.getChildren().add(none);
            } else {
                for (ExerciseInstance ex : workout.getExercises()) {
                    String line;
                    if (ex.getDurationMinutes() > 0) {
                        line = ex.getExerciseName() + "  •  " + ex.getDurationMinutes() + " min cardio";
                    } else {
                        line = ex.getExerciseName() + "  •  " + ex.getSets() + " sets x " + ex.getReps() + " reps";
                    }
                    Label exerciseLine = new Label(line);
                    exerciseLine.setStyle("-fx-text-fill: #374151;");
                    card.getChildren().add(exerciseLine);
                }
            }

            String notes = (event.getNotes() == null || event.getNotes().isBlank()) ? "No notes." : event.getNotes();
            Label notesLabel = new Label("Notes: " + notes);
            notesLabel.setWrapText(true);
            notesLabel.setStyle("-fx-text-fill: #4b5563; -fx-background-color: #f9fafb; -fx-border-color: #f3f4f6; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8;");
            card.getChildren().add(notesLabel);

            reportContentBox.getChildren().add(card);
        }
    }

    // Creates a summary metric card used at the top of the report.
    private VBox metricCard(String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12;");

        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-text-fill: #111827; -fx-font-size: 22; -fx-font-weight: bold;");

        VBox card = new VBox(4, labelNode, valueNode);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 12;");
        card.setPrefWidth(180);
        return card;
    }

    // Opens the print dialog and prints the report content as a PDF/printed page.
    private void exportPdf() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            showMessage("Export PDF", "printing is not available.");
            return;
        }

        Window owner = reportContentBox.getScene() == null ? null : reportContentBox.getScene().getWindow();
        boolean accepted = job.showPrintDialog(owner);
        if (!accepted) {
            return;
        }

        PageLayout pageLayout = job.getJobSettings().getPageLayout();

        double contentWidth = reportContentBox.getBoundsInParent().getWidth();
        double contentHeight = reportContentBox.getBoundsInParent().getHeight();
        if (contentWidth <= 0 || contentHeight <= 0) {
            showMessage("Export PDF", "Empty report.");
            return;
        }

        double scaleX = pageLayout.getPrintableWidth() / contentWidth;
        double scaleY = pageLayout.getPrintableHeight() / contentHeight;
        double scaleValue = Math.min(scaleX, scaleY);

        Scale scale = new Scale(scaleValue, scaleValue);
        reportContentBox.getTransforms().add(scale);

        boolean success;
        try {
            success = job.printPage(pageLayout, reportContentBox);
        } finally {
            reportContentBox.getTransforms().remove(scale);
        }

        if (success) {
            job.endJob();
            showMessage("Export PDF", "Successfully printed to pdf.");
        } else {
            showMessage("Export PDF", "Failed to print report.");
        }
    }

    // Shows a simple informational popup for export and error messages.
    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
