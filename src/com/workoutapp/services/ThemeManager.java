package com.workoutapp.services;

import javafx.scene.Scene;
import javafx.geometry.Bounds;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.prefs.Preferences;

/**
 * Manages the application theme (Light/Dark mode).
 * Supports real-time theme switching with fade animations.
 * Persists theme preference across sessions.
 */
public class ThemeManager {
    private static final String PREF_THEME = "app_theme";
    private static final String THEME_LIGHT = "light";
    private static final String THEME_DARK = "dark";
    private static ThemeManager instance;
    
    private String currentTheme;
    private Preferences prefs;
    private Scene scene;
    
    private ThemeManager() {
        prefs = Preferences.userNodeForPackage(ThemeManager.class);
        currentTheme = prefs.get(PREF_THEME, THEME_DARK);
    }
    
    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    public void setScene(Scene scene) {
        this.scene = scene;
        applyTheme(currentTheme, false);
    }
    
    public void toggleTheme() {
        String newTheme = currentTheme.equals(THEME_DARK) ? THEME_LIGHT : THEME_DARK;
        applyTheme(newTheme, true);
    }
    
    public void applyTheme(String theme, boolean animate) {
        currentTheme = theme;
        prefs.put(PREF_THEME, theme);
        
        if (scene == null) return;
        
        if (animate) {
            animateThemeSwitch(theme);
        } else {
            applyThemeDirectly(theme);
        }
    }
    
    private void animateThemeSwitch(String newTheme) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), scene.getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            applyThemeDirectly(newTheme);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), scene.getRoot());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }
    
    private void applyThemeDirectly(String theme) {
        // Clear existing stylesheets
        scene.getStylesheets().clear();
        
        // Apply theme-specific stylesheet
        String stylesheet = getThemeStylesheet(theme);
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet);
        }
    }
    
    private String getThemeStylesheet(String theme) {
        if (theme.equals(THEME_DARK)) {
            return getClass().getResource("/com/workoutapp/styles/dark-theme.css").toExternalForm();
        } else {
            return getClass().getResource("/com/workoutapp/styles/light-theme.css").toExternalForm();
        }
    }
    
    public String getCurrentTheme() {
        return currentTheme;
    }
    
    public boolean isDarkMode() {
        return currentTheme.equals(THEME_DARK);
    }
}
