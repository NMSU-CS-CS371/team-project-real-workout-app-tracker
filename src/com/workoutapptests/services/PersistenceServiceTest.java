package com.workoutapptests.services;

import com.workoutapp.services.PersistenceService;
import com.workoutapp.models.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceServiceTest {
    private PersistenceService service;
    private static final File EVENTS_FILE = new File("data/events.dat");

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {

    }
    
    @Test 
    public void test() {

    }
}
