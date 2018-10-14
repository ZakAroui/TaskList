package com.ikazme.tasklist.viewmodel;

import android.app.Application;

import com.ikazme.tasklist.database.AppRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MainViewModel extends AndroidViewModel {

//    public List<NoteEntity> mNotes;
    private AppRepository mRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);

        mRepository = AppRepository.getInstance(application.getApplicationContext());
//        mNotes = mRepository.mNotes;
    }

    //todo - move some activity code here

    public void addSampleData() {
        mRepository.addSampleData();
    }
}
