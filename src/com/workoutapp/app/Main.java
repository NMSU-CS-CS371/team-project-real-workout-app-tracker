package com.workoutapp.app;

import com.workoutapp.models.*;

public class Main{
    public static void main(String[] args){
        Routine r = new Routine("Workout");
        Exercise e1 = new Exercise("Treadmill", "Warm up", ExerciseType.CARDIO);
        Exercise e2 = new Exercise("Bench Press", "", ExerciseType.CHEST);
        Exercise e3 = new Exercise("Curls", "", ExerciseType.ARMS);
        r.addExercise(e1);
        r.addExercise(e2);
        r.addExercise(e3);
        Profile user = new Profile("User");
        user.addRoutine(r);
        System.out.println(user);
    }    
}