package com.workoutapp.controllers;

public interface ScreenController {
    void setMainController(MainController mainController);
    default void onProfileChanged(String profileName) {}    
}
