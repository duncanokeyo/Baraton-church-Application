package com.dans.apps.baratonchurch;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dans.apps.baratonchurch.models.LessonItem;
import com.dans.apps.baratonchurch.network.Fetcher;
import com.dans.apps.baratonchurch.provider.Contract;
import com.dans.apps.baratonchurch.ui.LessonsAdapter;
import com.dans.apps.baratonchurch.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LessonsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {
    String TAG = "LessonsActivity";

    String quarterlyID;
    String colorPrimary;
    String colorSecondary;
    String description;
    ImageView quarterliesImageCover;

    RecyclerView lessonList;
    LinearLayoutManager layoutManager;
    LessonsAdapter adapter;
    TextView quarterliesDescription;

    private ProgressBar progressBar;
    private TextView noLessons;
    private Button retry;
    private LinearLayout messageActionContainer;

    String userFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);
        Toolbar toolbar = findViewById(R.id.toolbar);
        lessonList = findViewById(R.id.lessons);
        quarterliesDescription = findViewById(R.id.quarterly_description);
        quarterliesImageCover = findViewById(R.id.quarterly_larger_cover);
        messageActionContainer = findViewById(R.id.message_action_container);

        progressBar = findViewById(R.id.progress_bar);
        noLessons = findViewById(R.id.no_lessons);
        retry = findViewById(R.id.retry);
        retry.setVisibility(View.GONE);
        noLessons.setVisibility(View.GONE);
        messageActionContainer.setVisibility(View.VISIBLE);

        Bundle bundle = getIntent().getExtras();
        quarterlyID = bundle.getString(Contract.Quarterly.ID);
        colorPrimary = bundle.getString(Contract.Quarterly.PRIMARY_COLOR);
        colorSecondary = bundle.getString(Contract.Quarterly.SECONDARY_COLOR);
        description = bundle.getString(Contract.Quarterly.DESCRIPTION);

        quarterliesDescription.setText(description);

        adapter = new LessonsAdapter(this);

        String imagePath = bundle.getString(Contract.Quarterly.COVER_PATH);
        Glide.with(this).load(imagePath).into(quarterliesImageCover);

        //getLoaderManager().initLoader(0,null,this);
        setSupportActionBar(toolbar);
        layoutManager=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        lessonList.setAdapter(adapter);
        lessonList.setLayoutManager(layoutManager);
        lessonList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        getSupportActionBar().setTitle(getResources().getString(R.string.lessons));
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retry.setVisibility(View.GONE);
                noLessons.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                new LessonFetcher(getApplicationContext()).execute(true);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new LessonFetcher(this).execute(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lessons_activity_menu,menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        if (mSearchView != null) {
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setQueryHint(getResources().
                    getString(R.string.search_lessons));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if(userFilter==null) {
            return new CursorLoader(this, Contract.Lesson.CONTENT_URI, Lessons.PROJECTION,
                    Contract.Lesson.QUARTERLY_ID + " =?", new String[]{quarterlyID}, null);
        }else{
            return new CursorLoader(this,Contract.Lesson.CONTENT_URI, Lessons.PROJECTION,
                    Contract.Lesson.QUARTERLY_ID+" =? AND "+Contract.Lesson.TITLE+" LIKE ?",
                    new String[]{quarterlyID,"%"+userFilter+"%"},null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        LogUtils.d(TAG,"cursor size "+cursor.getCount());
        if(cursor!=null && cursor.moveToFirst()){
            List<LessonItem> list = new ArrayList<>();
            LogUtils.d(TAG,"cursor!=null && cursor.moveToFirst()");
            noLessons.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            retry.setVisibility(View.GONE);
            messageActionContainer.setVisibility(View.GONE);
            do {
                String entryId = cursor.getString(Lessons.ENTRY_ID);
                String title = cursor.getString(Lessons.TITLE);
                String id = cursor.getString(Lessons.ID);
                String quarterlyID = cursor.getString(Lessons.QUARTERLY_ID);
                String startDate = cursor.getString(Lessons.START_DATE);
                String endDate = cursor.getString(Lessons.END_DATE);
                String index = cursor.getString(Lessons.INDEX);
                String path = cursor.getString(Lessons.PATH);
                String fullPath = cursor.getString(Lessons.FULL_PATH);
                String coverPath = cursor.getString(Lessons.COVER_PATH);

                LessonItem item = new LessonItem(title, entryId, quarterlyID, id,
                        startDate, endDate, path, fullPath, coverPath);

                LogUtils.d(TAG, "Adding item " + title);
                list.add(item);
                //adapter.addLesson(item);
            }while (cursor.moveToNext());
            adapter.addLessons(list);
        }else{
            LogUtils.e(TAG," no lessons fetched");
            //todo show fetch error
            noLessons.setText(R.string.no_lessons);
            noLessons.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            retry.setVisibility(View.VISIBLE);
            messageActionContainer.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
        if (userFilter == null && newFilter == null) {
            return false;
        }
        if (userFilter != null && userFilter.equals(newFilter)) {
            return false;
        }

        userFilter = newFilter;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    interface Lessons {
        String[] PROJECTION = {
                Contract.Lesson.ENTRY_ID,
                Contract.Lesson.TITLE,
                Contract.Lesson.ID,
                Contract.Lesson.QUARTERLY_ID,
                Contract.Lesson.START_DATE,
                Contract.Lesson.END_DATE,
                Contract.Lesson.INDEX,
                Contract.Lesson.PATH,
                Contract.Lesson.FULL_PATH,
                Contract.Lesson.COVER_PATH
        };

        int ENTRY_ID = 0;
        int TITLE = 1;
        int ID = 2;
        int QUARTERLY_ID = 3;
        int START_DATE = 4;
        int END_DATE = 5;
        int INDEX = 6;
        int PATH = 7;
        int FULL_PATH = 8;
        int COVER_PATH = 9;
    }


    private void restartLoader() {
        getLoaderManager().restartLoader(0,null,this);
    }


    public class LessonFetcher extends AsyncTask<Boolean,Void,Boolean>{
        Context context;
        String language;

        public LessonFetcher(Context context) {
            this.context = context;
            language = PreferenceManager.
                    getDefaultSharedPreferences(context).
                    getString(SettingsActivity.KEY_LANGUAGE, Constants.DefaultLanguage);
        }

        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            if(shouldFetchLessons(quarterlyID,booleans[0])){
                return fetchLessonsFromNetwork(quarterlyID,booleans[0]);
            }else {
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success){
                LogUtils.i(TAG," (on receive) received a positive response");
                restartLoader();
            }else{
                LogUtils.i(TAG,"(on receive) received a negative response");
                progressBar.setVisibility(View.GONE);
                noLessons.setText(R.string.lessons_fetch_error);
                noLessons.setVisibility(View.VISIBLE);
                retry.setVisibility(View.VISIBLE);
                messageActionContainer.setVisibility(View.VISIBLE);
            }
        }

        private boolean shouldFetchLessons(String quarterID,boolean forceRefresh) {
            ContentResolver resolver = context.getContentResolver();
            if(quarterID!=null){
                //check if lessons with the provided quartery id exist in the database
                Cursor cursor = resolver.query(Contract.Lesson.CONTENT_URI,new String[]{Contract.Lesson.ID},
                        Contract.Lesson.QUARTERLY_ID+"=?",new String[]{quarterID},null);
                if(cursor!=null){
                    if(cursor.getCount()>0){
                        LogUtils.d(TAG,"There are lessons associated with quarterly id : "+quarterID);
                        if(forceRefresh){
                            LogUtils.d(TAG,"Force refersh requested");
                            cursor.close();
                            return true;
                        }else{
                            cursor.close();
                            return false;
                        }
                    }else{
                        LogUtils.d(TAG,"No lessons associated with quarterly id : "+quarterID);
                        cursor.close();
                        return true;
                    }

                }else{return true;}
            }else{return false;}
        }

        private boolean fetchLessonsFromNetwork(String quarterID,boolean forceRefresh){
            ContentResolver resolver = context.getContentResolver();
            try {
                String specificQuarterly = Fetcher.getLessons(language,quarterID);
                if(specificQuarterly!=null) {
                    if(forceRefresh){
                        LogUtils.d(TAG,"Force refreshing lessons database");
                        //delete lessons associated with the specific quarterly id
                        //delete the full tree to avoid to avoid data innaccuracy
                        resolver.delete(Contract.Lesson.CONTENT_URI,Contract.Lesson.QUARTERLY_ID+" =?",
                                new String[]{quarterID});
                        resolver.delete(Contract.Day.CONTENT_URI,Contract.Day.QUARTERLY_ID+" =?",new String[]{quarterID});
                        resolver.delete(Contract.Read.CONTENT_URI,Contract.Read.QUARTERLY_ID+" =?",new String[]{quarterID});
                        resolver.delete(Contract.ReadVerses.CONTENT_URI,Contract.ReadVerses.QUARTERLY_ID+" =?",new String[]{quarterlyID});
                    }

                    JSONObject specificQuartelyObject = new JSONObject(specificQuarterly);
                    JSONArray lessons = specificQuartelyObject.getJSONArray("lessons");
                    for (int i = 0; i < lessons.length(); i++) {
                        ContentValues lessonValues = new ContentValues();
                        JSONObject lesson = lessons.getJSONObject(i);
                        String lessonTitle = lesson.getString("title");
                        String lessonStartDate = lesson.getString("start_date");
                        String lessonEndDate = lesson.getString("end_date");
                        String lessonId = lesson.getString("id");
                        String lessonIndex = lesson.getString("index");
                        String lessonPath = lesson.getString("path");
                        String lessonFullpath = lesson.getString("full_path");
                        String lessonCover = lesson.getString("cover");

                        lessonValues.put(Contract.Lesson.ENTRY_ID, String.valueOf(System.nanoTime()));
                        lessonValues.put(Contract.Lesson.TITLE, lessonTitle);
                        lessonValues.put(Contract.Lesson.START_DATE, lessonStartDate);
                        lessonValues.put(Contract.Lesson.END_DATE, lessonEndDate);
                        lessonValues.put(Contract.Lesson.ID, lessonId);
                        lessonValues.put(Contract.Lesson.QUARTERLY_ID, quarterID);
                        lessonValues.put(Contract.Lesson.INDEX, lessonIndex);
                        lessonValues.put(Contract.Lesson.PATH, lessonPath);
                        lessonValues.put(Contract.Lesson.FULL_PATH, lessonFullpath);
                        lessonValues.put(Contract.Lesson.COVER_PATH, lessonCover);

                        resolver.insert(Contract.Lesson.CONTENT_URI, lessonValues);
                    }
                }else{
                    return false;
                }
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }


}
