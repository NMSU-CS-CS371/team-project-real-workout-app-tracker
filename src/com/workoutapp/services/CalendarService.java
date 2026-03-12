package com.workoutapp.services;

import com.workoutapp.models.CalendarEvent;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.LinkedList;

/*
 * CalendarService manages a collection of CalendarEvents, which represent completed workouts.
 * It provides methods to add, remove, and retrieve calendar events, allowing duplicates
 * for users who workout multiple times per day.
 * The service uses a DataStorage instance to persist events to a JSON file, ensuring that
 * events are saved and loaded across application sessions.
 * The service also includes methods to retrieve events for a specific date or within a date range,
 * which can be used to populate a calendar view in the user interface.
 * The implementation is similar to ExerciseService, utilizing a LinkedList to manage the collection of events and providing methods for event manipulation and retrieval.
 */

public class CalendarService {

    private LinkedList<CalendarEvent> events; // list of calendar events managed during runtime
    private DataStorage<CalendarEvent> storage; // instance of DataStorage to handle saving and loading events
    private Type eventListType; // type object used to specify the type of data being loaded/saved

    public CalendarService() {
        this.storage = new DataStorage<>("data/calendar_events.json"); // Initializes DataStorage with the file path for calendar events
        this.eventListType = new TypeToken<LinkedList<CalendarEvent>>(){}.getType(); // Initializes the type object for a LinkedList of CalendarEvent objects
        this.events = storage.load(eventListType); // Loads the list of events from the specified file path
    }

    // Returns the list of all calendar events
    public LinkedList<CalendarEvent> getEvents() {
        return events;
    }

    // Adds a new calendar event to the list and saves the updated list
    public void addEvent(CalendarEvent event) {
        events.add(event);
        storage.save(events);
    }

    // Removes a specific calendar event from the list and saves the updated list
    public void removeEvent(CalendarEvent event) {
        events.remove(event);
        storage.save(events);
    }

    // Removes a calendar event at the specified index and saves the updated list
    public void removeEventAt(int index) {
        if (index >= 0 && index < events.size()) {
            events.remove(index);
            storage.save(events);
        }
    }

    // Returns a list of events that occurred on the specified date
    public LinkedList<CalendarEvent> getEventsForDate(LocalDate date) {
        LinkedList<CalendarEvent> eventsForDate = new LinkedList<>();
        for (CalendarEvent event : events) {
            if (event.getDateTime().toLocalDate().equals(date)) {
                eventsForDate.add(event);
            }
        }
        return eventsForDate;
    }

    // Returns a list of events that occurred within the specified date range (inclusive)
    public LinkedList<CalendarEvent> getEventsInRange(LocalDate startDate, LocalDate endDate) {
        LinkedList<CalendarEvent> eventsInRange = new LinkedList<>();
        for (CalendarEvent event : events) {
            LocalDate eventDate = event.getDateTime().toLocalDate();
            if ((eventDate.isEqual(startDate) || eventDate.isAfter(startDate)) &&
                (eventDate.isEqual(endDate) || eventDate.isBefore(endDate))) {
                eventsInRange.add(event);
            }
        }
        return eventsInRange;
    }

} // end of CalendarService class