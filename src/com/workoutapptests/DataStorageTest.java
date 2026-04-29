package com.workoutapptests;

import com.workoutapp.models.*;
import com.workoutapp.services.DataStorage;
import org.junit.jupiter.api.*;
import java.io.File;
import java.lang.reflect.Type;
import java.util.LinkedList;
import com.google.gson.reflect.TypeToken;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests data persistence to ensure it saves to and loads data from files.
 * Tests saving and loading from file, and making sure an empty file returns empty list
 */

public class DataStorageTest {

    private static final String FILE = "data/teststorage/test.json";
    private DataStorage<Exercise> ds;

    @BeforeEach
    public void clean() {
        File f = new File(FILE);
        if (f.exists()) f.delete();
    }

    //Test saving exercise to file and loading it from file
    @Test
    public void testSaveAndLoad() {
        ds = new DataStorage<>(FILE);
        LinkedList<Exercise> list = new LinkedList<>();
        Exercise e = new Exercise("Treadmill", "Description", ExerciseType.CARDIO);
        list.add(e);

        ds.save(list);
        Type type = new TypeToken<LinkedList<Exercise>>(){}.getType();
        LinkedList<Exercise> loaded = ds.load(type);

        assertEquals(1, loaded.size());
        assertEquals(e.getName(), loaded.get(0).getName());
        assertEquals(e.getType(), loaded.get(0).getType());
    }

    //Test loading empty file returns empty list
    @Test
    public void testLoadEmptyFileReturnsEmptyList() {
        ds = new DataStorage<>(FILE);
        Type type = new TypeToken<LinkedList<Exercise>>(){}.getType();
        LinkedList<Exercise> loaded = ds.load(type);

        assertNotNull(loaded);
        assertEquals(0, loaded.size());
    }
}