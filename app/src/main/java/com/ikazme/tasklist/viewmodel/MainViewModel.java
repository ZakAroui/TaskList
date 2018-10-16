package com.ikazme.tasklist.viewmodel;

import android.app.Application;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.SearchRecentSuggestions;
import android.util.Log;

import com.ikazme.tasklist.database.AppRepository;
import com.ikazme.tasklist.database.DBOpenHelper;
import com.ikazme.tasklist.database.NotesProvider;
import com.ikazme.tasklist.database.SearchSuggestionsProvider;

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

//    private void clearSearchHistory(){
//        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
//                SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
//        suggestions.clearHistory();
//    }

//    private void insertSampleData() {
//        //todo - media player https://www.tutorialspoint.com/android/android_mediaplayer.htm
//        insertNote("simple note");
//        insertNote("multiple \n line");
//        insertNote("this note is a very long one that even the writer of this note cannot" +
//                "keep up with the speed of the rabbit who is running on the TV in front of him :P");
//
//        restartLoader();
//    }

//    private void insertNote(String noteText) {
//        ContentValues values = new ContentValues();
//        values.put(DBOpenHelper.NOTE_TEXT, noteText);
//        Uri noteUri = getContentResolver().insert(NotesProvider.CONTENT_URI,
//                values);
//        Log.d("MainActivity", "Inserted note " + noteUri.getLastPathSegment());
//    }

    public void addSampleData() {
        mRepository.addSampleData();
    }
}
