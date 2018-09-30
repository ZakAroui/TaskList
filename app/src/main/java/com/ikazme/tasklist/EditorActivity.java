package com.ikazme.tasklist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.ikazme.tasklist.database.DBOpenHelper;
import com.ikazme.tasklist.database.NotesProvider;
import com.ikazme.tasklist.utils.Utils;

public class EditorActivity extends AppCompatActivity {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();

    private String action;
    private EditText editor;
    private String noteFilter;
    private String oldText;
    private String mNoteShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editor = findViewById(R.id.editText);
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

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI, noteFilter, null);
        Utils.showShortToast(getString(R.string.note_deleted), this);
        setResult(RESULT_OK);
        finish();
    }
}
