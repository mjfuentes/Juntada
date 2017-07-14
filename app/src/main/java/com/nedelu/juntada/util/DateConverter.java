package com.nedelu.juntada.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by matiasj.fuentes@gmail.com.
 */

public class DateConverter implements JsonDeserializer<Date>
{
    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        String s = json.getAsJsonPrimitive().getAsString();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm a z").create();

        return gson.fromJson(json, Date.class);
    }
}