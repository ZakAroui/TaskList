package com.ikazme.tasklist.model;

import java.util.Date;

import androidx.room.TypeConverter;

public class DateTypeConverter {

    @TypeConverter
    public static Date toDate(Long timestamp){
        return timestamp != null ? new Date(timestamp):null;
    }

    @TypeConverter
    public static Long toTimestamp(Date date){
        return date != null ? date.getTime():null;
    }
}
