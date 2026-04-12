package com.workoutapp.ui;

public interface ScreenController {
    void setMainController(MainController mainController);
    default void onProfileChanged(String profileName) {}    
}
