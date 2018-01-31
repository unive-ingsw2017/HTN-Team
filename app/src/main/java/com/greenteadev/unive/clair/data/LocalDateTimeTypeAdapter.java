package com.greenteadev.unive.clair.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.greenteadev.unive.clair.reference.Endpoint;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import java.lang.reflect.Type;

/**
 * Created by Hitech95 on 11/01/2018.
 */

public class LocalDateTimeTypeAdapter implements
        JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
        return DateTimeFormat.forPattern(Endpoint.DATETIMEFORMAT).parseLocalDateTime(
                json.getAsString());
    }

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        return new JsonPrimitive(DateTimeFormat.forPattern(Endpoint.DATETIMEFORMAT).print(src));
    }
}