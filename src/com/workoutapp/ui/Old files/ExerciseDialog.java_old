package com.workoutapp.ui;

import com.workoutapp.models.Exercise;
import com.workoutapp.models.ExerciseType;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class ExerciseDialog {
    public static Exercise showCreateExerciseDialog() {
        Dialog<Exercise> dialog = new Dialog<>();
        dialog.setTitle("New Exercise");
        dialog.setHeaderText("Create new exercise");

        //Set up buttons
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        //Fields
        TextField nameField = new TextField();
        TextField descField = new TextField();
        ComboBox<ExerciseType> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(ExerciseType.values());
        typeCombo.getSelectionModel().selectFirst();

        //Display items on grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(typeCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        //List exercisetypes
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                String name = nameField.getText();
                String desc = descField.getText();
                ExerciseType type = typeCombo.getValue();

                if (name == null || name.isBlank()) {
                    return null;
                }
                return new Exercise(name, desc, type);
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }
}
