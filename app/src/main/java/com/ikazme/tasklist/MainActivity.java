package com.ikazme.tasklist;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.ikazme.tasklist.database.DBOpenHelper;
import com.ikazme.tasklist.database.SearchSuggestionsProvider;
import com.ikazme.tasklist.database.NotesProvider;
import com.ikazme.tasklist.utils.Utils;
import com.ikazme.tasklist.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int EDITOR_REQUEST_CODE = 1001;
    public static final int NOTES_QUERY_ID = 0;

    private static final String SORT_ORDER = DBOpenHelper.NOTE_CREATED + " DESC";

    private final String SEARCH_NOTE = "search";
    private CursorAdapter cursorAdapter;
    private String currentAction;
    private String mSearchQuery;

    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        setContentView(R.layout.activity_main);

        initViewModel();
        
        populateNotes();

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            currentAction = SEARCH_NOTE;
            mSearchQuery = intent.getStringExtra(SearchManager.QUERY);
            getSupportActionBar().setSubtitle("Searched: \""+ mSearchQuery +"\"");

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
            suggestions.saveRecentQuery(mSearchQuery, null);

            searchInNotes();
        }

        //TODO - OCR FEATURE
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this)
                .get(MainViewModel.class);
    }

    private void populateNotes(){
        invalidateOptionsMenu();

        String[] from = {DBOpenHelper.NOTE_TEXT};
        int[] to = {R.id.tvNote};
        cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.note_list_item, null, from, to, 0);

        ListView list = findViewById(R.id.tasksListView);
        list.setAdapter(cursorAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + id);
                intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });

        getLoaderManager().initLoader(NOTES_QUERY_ID, null, this);
    }

    private void searchInNotes() {
        restartLoader();
    }

    private void clearSearchHistory(){
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                SearchSuggestionsProvider.AUTHORITY, SearchSuggestionsProvider.MODE);
        suggestions.clearHistory();
    }

    private void confirmClearHistory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are you sure?")
                .setMessage("Do you really want to clear your app's search history?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        clearSearchHistory();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(SEARCH_NOTE.equals(currentAction)){
            return false;
        }

        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setQueryRefinementEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.app_bar_search:
                onSearchRequested();
                break;
            case R.id.action_create_sample:
                insertSampleData();
                break;
            case R.id.action_delete_all:
                deleteAllNotes();
                break;
            case R.id.action_clear_history:
                confirmClearHistory();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllNotes() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            getContentResolver().delete(NotesProvider.CONTENT_URI,null,null);
                            restartLoader();
                            Utils.showShortToast(getString(R.string.all_deleted ), MainActivity.this);
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void insertSampleData() {
        //todo - media player https://www.tutorialspoint.com/android/android_mediaplayer.htm
        insertNote("simple note");
        insertNote("multiple \n line");
        insertNote("this note is a very long one that even the writer of this note cannot" +
                "keep up with the speed of the rabbit who is running on the TV in front of him :P");

        restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(NOTES_QUERY_ID, null, this);
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        Uri noteUri = getContentResolver().insert(NotesProvider.CONTENT_URI,
                values);
        Log.d("MainActivity", "Inserted note " + noteUri.getLastPathSegment());
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if(SEARCH_NOTE.equals(currentAction)){
            String[] searchQueryArgs = mSearchQuery.split("\\s+");
            StringBuilder selPrefixPartSb = new StringBuilder();
            for (int i = 0; i<searchQueryArgs.length;i++) {
                if(i == 0) selPrefixPartSb.append("( ");
                selPrefixPartSb.append(DBOpenHelper.NOTE_TEXT + " LIKE ? ");
                if(i == (searchQueryArgs.length-1)){
                    selPrefixPartSb.append(") ");
                } else{
                    selPrefixPartSb.append("AND ");
                }
            }

            String searchSelection = selPrefixPartSb.toString();
            String[] mSelectionArgs = new String[searchQueryArgs.length];
            for (int i=0;i<searchQueryArgs.length;i++) {
                mSelectionArgs[i] = "%"+searchQueryArgs[i]+"%";
            }

            return new CursorLoader(this,
                    NotesProvider.CONTENT_URI,
                    DBOpenHelper.ALL_COLUMNS,
                    searchSelection,
                    mSelectionArgs,
                    SORT_ORDER);

        }

        return new CursorLoader(this,
                NotesProvider.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    public void openEditorForNewNote(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK){
            restartLoader();
        }
    }

}
