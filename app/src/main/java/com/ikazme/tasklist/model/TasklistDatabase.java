package com.ikazme.tasklist.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {NoteEntity.class}, version = 1)
@TypeConverters(DateTypeConverter.class)
public abstract class TasklistDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "TasklistDatabase.db";

    private static volatile TasklistDatabase instance;
    private static final Object LOCK = new Object();

    public abstract NoteDao noteDao();

    public static TasklistDatabase getInstance(Context context){

        if(instance == null){
            synchronized (LOCK){
                if(instance == null){
                    instance = Room.databaseBuilder(context, TasklistDatabase.class, DATABASE_NAME).build();
                }
            }
        }

        return instance;
    }

}
