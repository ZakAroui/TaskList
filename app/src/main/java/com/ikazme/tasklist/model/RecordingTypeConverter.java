package com.ikazme.tasklist.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import androidx.room.TypeConverter;

public class RecordingTypeConverter {

    private static Gson gson = new Gson();

    @TypeConverter
    public static List<String> toRecordings(String json) {
        if (json == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(json, listType);
    }

    @TypeConverter
    public static String toJson(List<String> recordings) {
        return gson.toJson(recordings);
    }
}
