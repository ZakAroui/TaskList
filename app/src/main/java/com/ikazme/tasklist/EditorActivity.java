package com.ikazme.tasklist;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ikazme.tasklist.database.DBOpenHelper;
import com.ikazme.tasklist.database.NotesProvider;
import com.ikazme.tasklist.service.PermissionsService;
import com.ikazme.tasklist.ui.AudioNotesAdapter;
import com.ikazme.tasklist.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.drawable.ic_media_pause;
import static android.R.drawable.ic_media_play;
import static com.ikazme.tasklist.utils.Utils.PERMISSIONS_REQUEST_RECORD_AUDIO;

public class EditorActivity extends AppCompatActivity {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

    @BindView(R.id.noteRecordingsList)
    RecyclerView mRecyclerView;

    AudioNotesAdapter mAudioNotesAdapter;

    private String action;
    private EditText editor;
    private String noteFilter;
    private String oldText;
    private String mNoteShare;
    private FloatingActionButton mRecordNoteBtn;
    private FloatingActionButton mPlayNoteBtn;

    private static String mFileName = null;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer = null;
    private boolean mStartPlaying = true;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        ButterKnife.bind(this);
        initRecordingList();

        editor = findViewById(R.id.editText);
        mPlayNoteBtn = findViewById(R.id.playFloatingButton);
        mRecordNoteBtn = findViewById(R.id.recordNotefloatingButton);
        mRecordNoteBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mRecordNoteBtn.setBackgroundTintList(getResources().getColorStateList(R.color.red_tint));
                    startNoteRecording();
                    return true;
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    mRecordNoteBtn.setBackgroundTintList(getResources().getColorStateList(R.color.accent_tint));
                    stopNoteRecording();
                    if((event.getEventTime() - event.getDownTime()) < 1000) {
                        Utils.showShortToast("Very short note recording.", EditorActivity.this);
                        deleteRecording(mFileName);
                    }
                    return true;
                }

                return false;
            }
        });
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        if (uri == null){
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        } else {
            action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.edit_note));
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            mNoteShare = oldText;
            editor.setText(oldText);
            editor.requestFocus();

            cursor.close();
        }
    }

    private void initRecordingList() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayout);

        mAudioNotesAdapter = new AudioNotesAdapter(Arrays.asList("audio1", "audio2"), this);
        mRecyclerView.setAdapter(mAudioNotesAdapter);
    }

    private void finishEditing(){
        String newText = editor.getText().toString().trim();
        switch (action){
            case Intent.ACTION_INSERT:
                if (newText.length() == 0){
                    setResult(RESULT_CANCELED);
                }
                else {
                    insertNote(newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.equals(oldText)){
                    setResult(RESULT_CANCELED);
                }
                else {
                    updateNote(newText);
                }
        }
        finish();
    }

    private void updateNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        Utils.showShortToast(getString(R.string.note_updated), this);
        setResult(RESULT_OK);
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Intent.ACTION_EDIT.equals(action)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
            MenuItem menuItem = menu.findItem(R.id.action_share);
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            if (mShareActionProvider != null ) {
                mShareActionProvider.setShareIntent(createShareContactIntent());
            } else {
                Log.w(LOG_TAG, "onCreateOptionsMenu(): Share Action Provider is null!");
            }
        }
        return true;
    }

    private Intent createShareContactIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mNoteShare);
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteNote();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.showShortToast("Permission granted!", this);
            } else {
                Utils.showShortToast("Grant the permission to record an audio note.", this);
            }
        }
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI, noteFilter, null);
        Utils.showShortToast(getString(R.string.note_deleted), this);
        setResult(RESULT_OK);
        finish();
    }

    private void startNoteRecording() {
        //TODO - CONTINUE HERE - https://developer.android.com/guide/topics/media/mediarecorder#java
        //TODO - FIX FILE LOCATION
        //TODO - FIX DISTINCT FILE NAME
        if(PermissionsService.getInstance().hasOrRequestRecordAudioPerm(this, PERMISSIONS_REQUEST_RECORD_AUDIO)){
            mFileName = getExternalCacheDir().getAbsolutePath();
            mFileName += "/audiorecordtest.3gp";

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Utils.showShortToast("prepare() failed" ,this);
            }

            mRecorder.start();
        }
    }

    private void stopNoteRecording(){
        if(mRecorder != null){
            try{
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            } catch (Exception e){
                Log.e(LOG_TAG, "stopNoteRecording: ", e);
            }
        }
    }


    public void playNoteRecording(View view){
        onPlay(mStartPlaying);
        mStartPlaying = !mStartPlaying;
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {

        mPlayNoteBtn.setImageDrawable(getResources().getDrawable(ic_media_pause));
        mPlayNoteBtn.setBackgroundTintList(getResources().getColorStateList(R.color.green_tint));

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
            stopPlaying();
        }
    }
    private void stopPlaying() {
        mPlayNoteBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_audio));
        mPlayNoteBtn.setBackgroundTintList(getResources().getColorStateList(R.color.accent_tint));
        mStartPlaying = !mStartPlaying;

        mPlayer.release();
        mPlayer = null;
    }

    private void deleteRecording(String fileName){
        if(mFileName != null){
            File file = new File(fileName);
            file.delete();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
