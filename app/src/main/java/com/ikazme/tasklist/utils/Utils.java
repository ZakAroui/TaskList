package com.ikazme.tasklist.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class Utils {


    public static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 100;

    private static Gson gson = new Gson();

    public static void showShortToast(String message, Context appContext){
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(String message, Context appContext){
        Toast.makeText(appContext, message, Toast.LENGTH_LONG).show();
    }

    public static List<String> toList(String json) {
        if (json == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(json, listType);
    }

    public static String toJson(List<String> list) {
        return gson.toJson(list);
    }

}
