package com.workoutapp.services;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.LinkedList;

/*
 * ProfileService manages a collection of profile names that are persisted to data/profiles.json.
 * Each profile serves as a namespace for that user's exercises, routines, calendar events, etc.
 * The service provides methods to create, retrieve, and delete profiles.
 */
public class ProfileService {

    private LinkedList<String> profileNames; // list of profile names managed during runtime
    private DataStorage<String> storage; // instance of DataStorage to handle saving and loading profile names
    private Type profileListType; // type object used to specify the type of data being loaded/saved

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
    }

    // Removes a profile name from the list and saves the updated list
    public void removeProfile(String profileName) {
        if (profileNames.remove(profileName)) {
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

} // end of ProfileService class
