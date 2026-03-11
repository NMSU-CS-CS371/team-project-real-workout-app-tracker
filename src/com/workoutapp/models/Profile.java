package com.workoutapp.models;

import java.util.LinkedList;
import java.lang.StringBuilder;

public class Profile {
    private String profileName;
    private LinkedList<Routine> routines;

    public Profile(String name){
        this.profileName = name;
        this.routines = new LinkedList<Routine>();
    }

    //Get name
    public String getName(){
        return profileName;
    }

    //Set name
    public void setName(String name){
        this.profileName = name;
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
