package com.workoutapp.services;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedList;

/*
 * ProfileService manages a collection of profile names that are persisted to data/profiles.json.
 * Each profile serves as a reference to a directory used for storing that user's exercises, routines, calendar events, etc.
 * The service provides methods to create, retrieve, and delete profiles.
 */
public class ProfileService {

    private LinkedList<String> profileNames; // list of profile names managed during runtime
    private DataStorage<String> storage; // instance of DataStorage to handle saving and loading profile names
    private Type profileListType; // type object used to specify the type of data being loaded/saved
    private static final String BASE_DIR = "data";

    public ProfileService() {
        this.storage = new DataStorage<>("data/profiles.json"); // Initializes DataStorage with the file path for profiles
        this.profileListType = new TypeToken<LinkedList<String>>(){}.getType(); // Initializes the type object for a LinkedList of String objects
        this.profileNames = storage.load(profileListType); // Loads the list of profile names from the specified file path
    }

    // Returns the list of all profile names
    public LinkedList<String> getProfiles() {
        return profileNames;
    }

    // Adds a new profile name to the list and saves the updated list
    public void addProfile(String profileName) {
        if (profileName == null) {
            System.out.println("Profile name cannot be empty.");
            return;
        }

        if (profileExists(profileName)) {
            System.out.println("A profile with the name " + profileName + " already exists.");
            return;
        }

        profileNames.add(profileName);
        storage.save(profileNames);
        createProfileDirectory(profileName);
    }

    // Removes a profile name from the list and saves the updated list
    public void removeProfile(String profileName) {
        if (profileNames.remove(profileName)) {
            deleteProfileDirectory(profileName);
            storage.save(profileNames);
        }
    }

    // Checks if a profile with the given name exists
    public boolean profileExists(String profileName) {
        for (String name : profileNames) {
            if (name.equalsIgnoreCase(profileName)) {
                return true;
            }
        }
        return false;
    }

    //Rename profile
    public void renameProfile(String oldName, String newName){
        if(newName == null || newName.isBlank()) return;
        if(!profileExists(oldName)) return;
        if(profileExists(newName)) {        //Prevent duplicate profile names 
            System.out.println("Profile with name '" + newName + "' already exists.");
            return;
        }   

        for(int i = 0; i < profileNames.size(); i++){
            if(profileNames.get(i).equalsIgnoreCase(oldName)){
                profileNames.set(i, newName);
                break;
            }
        }
        storage.save(profileNames);
        
        //Rename directory ../data/<profileName>
        renameProfileDirectory(oldName, newName);
    }

    //Create profile directory of given name
    private void createProfileDirectory(String profileName) {
        try {
            Path dir = Path.of(BASE_DIR, profileName);
            Files.createDirectories(dir);

            // Copy defaultExercises.json → exercises.json
            Path source = Path.of(BASE_DIR, "defaultExercises.json");
            Path target = dir.resolve("exercises.json");

            if (Files.exists(source)) {
                Files.copy(source, target);
            } else {
                System.out.println("Warning: defaultExercises.json not found.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Delete profile directory
    private void deleteProfileDirectory(String profileName) {
        try {
            Path dir = Path.of(BASE_DIR, profileName);
            if (!Files.exists(dir)) return;
            //Iterate through files
            Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try { Files.delete(path); } 
                    catch (IOException e) { e.printStackTrace(); }
                });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Rename directory
    private void renameProfileDirectory(String oldName, String newName) {
        try {
            Path oldDir = Path.of(BASE_DIR, oldName);
            Path newDir = Path.of(BASE_DIR, newName);

            if (Files.exists(oldDir)) {
                Files.move(oldDir, newDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Get profile class
    public Path getProfileDirectory(String profileName) {
        return Path.of(BASE_DIR, profileName);
    }
} // end of ProfileService class
