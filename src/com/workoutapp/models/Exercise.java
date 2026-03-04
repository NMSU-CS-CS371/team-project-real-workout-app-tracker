package com.workoutapp.models;

import java.util.*;

/**
 * Represents an exercise that can be added to workout routines. Contains basic information about 
 * the exercise such as its name, type (cardio or strength), and optional tags for categorization.
 */
public class Exercise {
    final private String name;
    final private ExerciseType type;
    private List<String> tags;          //Muscle groups, equipment, etc. for searching and filtering

    /**
     * Constructor for Exercise. Name and type are required, while tags can be added later.
     * @param name
     * @param type
     * @throws IllegalArgumentException if name is null or empty, or if type is not valid
     */

    public Exercise(String name, ExerciseType type) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Exercise name cannot be null or empty.");
        else if(type != ExerciseType.CARDIO && type != ExerciseType.STRENGTH)
            throw new IllegalArgumentException("Exercise type must be either CARDIO or STRENGTH.");
        this.name = name;
        this.type = type;
        this.tags = new ArrayList<String>();
    }

    // Getters and setters for exercise name, type, and tags. Also includes methods for adding, removing, and checking tags.
    public String getName() {
        return name;
    }

    public ExerciseType getType() {
        return type;
    }

    public List<String> getTags() {
        return tags == null ? null : List.copyOf(tags);
    }  

    //Add new tag to exercise
    public void addTag(String tag) throws IllegalArgumentException {
        if (tag == null || tag.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag cannot be null or empty.");
        } else if (tags == null) {
            tags = new java.util.ArrayList<>();
        }
        tags.add(tag);
    }

    //Remove tag from exercise
    public boolean removeTag(String tag) throws IllegalArgumentException {
        if (tag == null || tag.trim().isEmpty())
            throw new IllegalArgumentException("Tag cannot be null or empty.");
        return tags != null && tags.remove(tag);
    }

    public int numTags(){
        return tags.size();
    }

    //Check if exercise has specific tag
    public boolean hasTag(String tag) {
        return tags != null && tags.contains(tag);
    }

    //Clear tags from exercise
    public void clearTags() {
        if (tags != null) {
            tags.clear();
        }
    }

    @Override
    /**
     * Equality is based on name and type, as these should uniquely identify an exercise. Tags are 
     * not considered in equality checks since they are mutable and optional.
      * @param o the object to compare with
      * @return true if the exercises are equal, false otherwise
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exercise exercise = (Exercise) o;
        return name.equals(exercise.name) && type == exercise.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    //String representation of exercise
    public String toString() {
        return "Exercise{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", tags=" + tags +
                '}';
    }
}
