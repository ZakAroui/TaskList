package com.ikazme.tasklist.utils;

import android.content.Context;
import android.widget.Toast;

public class Utils {



    public static void showShortToast(String message, Context appContext){
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(String message, Context appContext){
        Toast.makeText(appContext, message, Toast.LENGTH_LONG).show();
    }

}
