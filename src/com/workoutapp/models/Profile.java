package com.workoutapp.models;

import java.util.LinkedList;
import java.lang.StringBuilder;

public class Profile {
    private String profileName;
    private LinkedList<Routine> routines;
    private int userHeight;                 //User height in inches
    private double userWeight;              //User weight in pounds
    private int userAge;                    //User age in years 

    public Profile(String name) throws NullPointerException{
        if(name == null)        throw new NullPointerException("Name cannot be null");
        this.profileName = name;
        this.routines = new LinkedList<Routine>();
        userHeight = 68;
        userWeight = 150;
        userAge = 30;
    }

    public Profile(String name, int height, double weight, int age) throws Exception{
        if(name == null)        throw new NullPointerException("Name cannot be null");
        if(height < 30)         throw new IllegalArgumentException("Height must be at least 30 inches");
        if(weight < 50.0)       throw new IllegalArgumentException("Weight must be at least 50 pounds");
        if(age < 13)            throw new IllegalArgumentException("Age must be at least 13 years");
        this.profileName = name;
        this.routines = new LinkedList<Routine>();
        userHeight = height;
        userWeight = weight;
        userAge = age;
    }

    //Get name
    public String getName(){
        return this.profileName;
    }

    //Get user height
    public int getUserHeight(){
        return this.userHeight;
    }

    //Get user weight
    public double getUserWeight(){
        return this.userWeight;
    }

    //Get user age
    public int getUserAge(){
        return this.userAge;
    }

    //Set name
    public void setName(String name){
        this.profileName = name;
    }

    //Get user height
    public void setUserHeight(int height){
        this.userHeight = height;
    }

    //Get user weight
    public void setUserWeight(double weight){
        this.userWeight = weight;
    }

    //Get user age
    public void setUserAge(int age){
        this.userAge = age;
    }


    //Get Routines
    public LinkedList<Routine> getRoutines(){
        return routines;
    }

    //Get Routine at index
    public Routine getRoutineAt(int index){
        return routines.get(index);
    }

    //Add routine
    public void addRoutine(Routine r){
        routines.add(r);
    }
    
    //Remove routine
    public void removeRoutine(Routine r){
        routines.remove(r);
    }

    //Remove routine at index
    public void removeRoutineAt(int index){
        routines.remove(index);
    }

    //Get number of routines
    public int getNumRoutines(){
        return routines.size();
    }

    public String toString(){
        StringBuilder out = new StringBuilder();
        out.append(profileName).append("\n");
        for(int i = 0; i < routines.size(); i++){
            out.append("Routine #" + (i + 1) + ": ");
            out.append(routines.get(i).toString());
        }
        return out.toString();
    }
}
