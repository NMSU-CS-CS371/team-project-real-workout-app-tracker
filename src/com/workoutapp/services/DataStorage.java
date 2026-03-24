package com.workoutapp.services;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.lang.reflect.Type;

/* 
 * 
 *  DataStorage is a generic class that provides methods to save and load data of type T to and from Json files. 
 *  It uses the Gson library to handle the conversion between Java objects and the Json format.
 * 
 *  The generic implementation allows this class to be used for different types of data, such as exercises, routines, profiles, or calendar events.
 *  
 *  Each object's service class will create an instance of DataStorage, specifying the filePath and type by using a TypeToken.
 *  This is a more straightforward method for saving and loading data, which keeps individual service classes focused on managing their specific models.
 * 
 */

public class DataStorage <T> {

    private String filePath;
    private Gson gson;

    // Constructor - initializes file path and Gson instancee
    public DataStorage(String filePath) {
        this.filePath = filePath;
        this.gson = new Gson(); // Initialize Gson instance, which converts Java objects to JSON and vice versa

    }

    // Saves the provided LinkedList data to the specified file path in Json format
    public void save(LinkedList<T> data) { 

        try (FileWriter writer = new FileWriter(filePath)) { // Create a FileWriter to write to the specified file path

            gson.toJson(data, writer); // Convert the data fed from the LinkedList to JSON format and write it to the file

        } catch (Exception e) {

            System.out.println("Error writing to file");

        }
    }

    public LinkedList<T> load(Type listType) { // Loads data from the specified file path and returns it as a LinkedList of type T

        try (FileReader reader = new FileReader(filePath)) { // Create a file reader to read from the specified file path
            
            LinkedList<T> data = gson.fromJson(reader, listType); // Create a linked list of type T that is populated with the file data, which is converted from JSON to java objects
            return data; // Return the loaded data

        } catch (Exception e) {

            System.out.println("Error reading from file");
            return new LinkedList<>(); // Return an empty linked list if there was an error reading the file

        }

    }

} // End of DataStorage class