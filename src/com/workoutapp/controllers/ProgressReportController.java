package com.workoutapp.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ProgressReportController implements ScreenController {

    @FXML private Button backButton;
    @FXML private Label titleLabel;

    private MainController main;

    @Override
    public void setMainController(MainController mainController) {
        this.main = mainController;

        backButton.setOnAction(e -> {
            if (main.getLastReportEvent() != null) {
                main.loadReportDetailView(main.getLastReportEvent());
            } else {
                main.loadView("ReportView.fxml");
            }
        });
    }

    @Override
    public void onProfileChanged(String profileName) {
        titleLabel.setText("Progress Report");
    }
}
