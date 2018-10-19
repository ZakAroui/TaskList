package com.ikazme.tasklist.database;

import android.content.Context;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppRepository {
    private static AppRepository ourInstance;
    //todo - clean this class
//    public List<NoteEntity> mNotes;
//    private TasklistDatabase mDb;

    private Executor executor = Executors.newSingleThreadExecutor();

    public static AppRepository getInstance(Context context) {
        if(ourInstance == null){
            ourInstance = new AppRepository(context);
        }
        return ourInstance;
    }

    private AppRepository(Context context) {
//        mDb = TasklistDatabase.getInstance(context);
    }

    public void addSampleData() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
//                mDb.noteDao().insertAll(SampleData.getNotes());
            }
        });
    }
}
