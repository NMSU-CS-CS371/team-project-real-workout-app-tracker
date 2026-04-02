package com.workoutapp.services;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
* LocalDateTimeAdapter is a serializer and deserializer for LocalDateTime objects, which are used by the CalendarEvent model
* to represent the date and time of completed workouts. The adapter specifies a format that LocalDateTime objects should
* be converted to when being saved to json, and provides methods to convert them to and from that format when saving and loading
* calendar events from json files.
*/


public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    // formatter to convert LocalDateTime objects to and from a string, for storage in JSON format; uses ISO_LOCAL_DATE_TIME format "yyyy-MM-dd'T'HH:mm:ss"
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME; 

	@Override
    // serialize override method to serialize LocalDateTime objects to json by converting them into a string.
	public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) { 
		return new JsonPrimitive(src.format(FORMATTER));
	}

	@Override
    // deserialize override method to deserialize LocalDateTime objects from json by parsing the string back into a LocalDateTime object.
	public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return LocalDateTime.parse(json.getAsString(), FORMATTER);
	}
}
