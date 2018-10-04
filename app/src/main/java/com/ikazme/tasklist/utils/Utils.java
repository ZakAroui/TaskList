package com.ikazme.tasklist.utils;

import android.content.Context;
import android.widget.Toast;

public class Utils {


    public static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 100;


    public static void showShortToast(String message, Context appContext){
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(String message, Context appContext){
        Toast.makeText(appContext, message, Toast.LENGTH_LONG).show();
    }

}
