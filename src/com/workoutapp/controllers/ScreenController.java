package com.workoutapp.controllers;

// Controls the main application shell and navigation.

public interface ScreenController {
    void setMainController(MainController mainController);  // Connect the controller to the main app controller
    default void onProfileChanged(String profileName) {}    // Handle profile changes 
}
