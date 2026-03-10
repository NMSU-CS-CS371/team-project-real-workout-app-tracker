package com.workoutapp.models;

public class Exercise {

    private String name;
    private String description;
    private ExerciseType type;

    public Exercise() {} // Default/null constructor for GSON serialization/deserialization

    // Constructor that accepts arguments for all fields
    public Exercise(String name, String description, ExerciseType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return description;
    }

    public ExerciseType getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Exercise: " + name
            + "\n Description: " + description
            + "\nType: " + type;
    }
}
