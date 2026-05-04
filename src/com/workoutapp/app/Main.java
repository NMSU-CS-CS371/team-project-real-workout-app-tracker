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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/workoutapp/views/MainView.fxml"));
        Scene scene = new Scene(loader.load());
        
        // Initialize theme manager with the scene
        ThemeManager themeManager = ThemeManager.getInstance();
        themeManager.setScene(scene);
        
        stage.setScene(scene);
        stage.setTitle("Workout Tracker");
        stage.setWidth(1000);
        stage.setHeight(700);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }    
}