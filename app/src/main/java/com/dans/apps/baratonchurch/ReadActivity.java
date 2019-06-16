package com.dans.apps.baratonchurch;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dans.apps.baratonchurch.models.Verses;
import com.dans.apps.baratonchurch.network.Fetcher;
import com.dans.apps.baratonchurch.provider.Contract;
import com.dans.apps.baratonchurch.ui.JumpToDayDialogFragment;
import com.dans.apps.baratonchurch.ui.ReadFragment;
import com.dans.apps.baratonchurch.utils.DateUtils;
import com.dans.apps.baratonchurch.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

/**
 * Fetches read of every lesson
 */
public class ReadActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,TabLayout.OnTabSelectedListener, JumpToDayDialogFragment.Callback {

    String TAG = "ReadActivity";

    ImageView lessonCover;
    TextView date;
    String lessonID;
    String quarterlyID;
    String coverPath;
    String lessonTitle;

    ProgressBar progressBar;
    TextView noDays;
    Button retry;

    Toolbar toolbar;
    TabLayout tabLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ArrayList<com.dans.apps.baratonchurch.models.Days> days;
    ReadFragment readFragment;
    private boolean setIsFavourited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        progressBar = findViewById(R.id.days_progress_bar);
        noDays = findViewById(R.id.days_message);
        retry = findViewById(R.id.days_retry);
        date = findViewById(R.id.date);

        lessonCover = findViewById(R.id.lesson_cover);
        tabLayout = findViewById(R.id.tab_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        readFragment = (ReadFragment) getFragmentManager().findFragmentById(R.id.read_fragment);
        noDays.setVisibility(GONE);
        retry.setVisibility(GONE);

        Bundle bundle = getIntent().getExtras();

        coverPath = bundle.getString(Contract.Lesson.COVER_PATH);
        quarterlyID = bundle.getString(Contract.Lesson.QUARTERLY_ID);
        lessonID = bundle.getString(Contract.Lesson.ID);
        lessonTitle = bundle.getString(Contract.Lesson.TITLE);

        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(this).load(coverPath).apply(options).into(lessonCover);

        new DaysFetcher(this).execute(false);
        tabLayout.setOnTabSelectedListener(this);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                noDays.setVisibility(GONE);
                retry.setVisibility(GONE);
                new DaysFetcher(getApplicationContext()).execute(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read_activity_menu,menu);
        MenuItem item = menu.findItem(R.id.add_to_favourites);
        setIsFavourite(setIsFavourited,item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()){
            case R.id.jump_to_day_read: {
                if (days == null || (days != null && days.size() == 0)) {
                    return false;
                }
                JumpToDayDialogFragment fragment = new JumpToDayDialogFragment();
                fragment.addDays(days);
                fragment.setCallback(this);
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Fragment prev = manager.findFragmentByTag("jump_to_day");
                if (prev != null) {
                    transaction.remove(prev);
                }
                transaction.addToBackStack(null);
                fragment.show(manager, "jump_to_day");
                break;
            }
            case R.id.add_to_favourites: {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String fullReadPath = null;
                        if (readFragment != null) {
                            fullReadPath = readFragment.getFullReadPath();
                            if (fullReadPath == null) {
                                return;
                            }
                        }
                        LogUtils.d(TAG, "checking if is in favourites database " + fullReadPath);
                        Cursor cursor = getContentResolver().query(Contract.FavouriteRead.CONTENT_URI,
                                new String[]{Contract.FavouriteRead.DAY_ID},
                                Contract.FavouriteRead.FULL_READ_PATH + " =?", new String[]{fullReadPath}, null);
                        if (cursor != null) {
                            if (cursor.getCount() > 0) {
                                //remove from favourites
                                LogUtils.d(TAG, "in favourites database removing.... ");
                                getContentResolver().delete(Contract.FavouriteRead.CONTENT_URI,
                                        Contract.FavouriteRead.FULL_READ_PATH + " =?", new String[]{fullReadPath});
                                setIsFavourited = false;
                                invalidateOptionsMenu();
                            } else {
                                addToFavourites();
                                //add to favouites
                                LogUtils.d(TAG, "not in favourites database adding.... ");
                            }
                        } else {
                            addToFavourites();
                            //add to favourites
                            LogUtils.d(TAG, "not in favourites database adding.... ");
                        }
                    }
                });

                break;
            }
            case R.id.refresh_daly_read:{
                if(readFragment!=null){
                    readFragment.refreshRead();
                }
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void addToFavourites(){
        if(readFragment == null ){return;}
        if(readFragment.getContent() == null){return;}
        try {
            ContentValues values = new ContentValues();
            values.put(Contract.FavouriteRead.DATE, readFragment.getDate());
            values.put(Contract.FavouriteRead.ENTRY_ID,String.valueOf(System.nanoTime()));
            values.put(Contract.FavouriteRead.FULL_READ_PATH, readFragment.getFullReadPath());
            values.put(Contract.FavouriteRead.LESSON_TITLE, lessonTitle);
            values.put(Contract.FavouriteRead.TITLE, readFragment.getTitle());
            values.put(Contract.FavouriteRead.LESSON_COVER_PATH, coverPath);
            values.put(Contract.FavouriteRead.CONTENT, readFragment.getContent());
            values.put(Contract.FavouriteRead.LESSON_ID, readFragment.getLessonID());
            values.put(Contract.FavouriteRead.QUARTERLY_ID, readFragment.getQuarterlyID());
            values.put(Contract.FavouriteRead.DAY_ID, readFragment.getDayID());


            //returns the bible verse
            ArrayList<Verses> verses = readFragment.getBibleVerses();
            JSONArray array = new JSONArray();
            for(Verses verse: verses){
                JSONObject object = new JSONObject();
                object.put("bible_version",verse.getBibleVersion());
                Map<String,String>map = verse.getMap();
                for(String key:map.keySet()){
                    object.put(key,map.get(key));
                }
                array.put(object);
            }
            String references = array.toString(2);
            values.put(Contract.FavouriteRead.REFERENCE,references);
            getContentResolver().insert(Contract.FavouriteRead.CONTENT_URI,values);

            Toast.makeText(this,R.string.added_to_favourites,Toast.LENGTH_SHORT).show();
            setIsFavourited= true;
        }catch (Exception e){
            Toast.makeText(this,R.string.error_adding_to_favourites,Toast.LENGTH_SHORT).show();
            setIsFavourited =false;
            e.printStackTrace();
        }
        invalidateOptionsMenu();
    }

    public void setIsFavourite(boolean isFavourite, MenuItem item){
        Drawable drawable = item.getIcon();
        drawable = DrawableCompat.wrap(drawable);
        if(isFavourite){
            DrawableCompat.setTint(drawable, ContextCompat.getColor(this,R.color.yellow));
        }else{
            DrawableCompat.setTint(drawable, ContextCompat.getColor(this,android.R.color.white));
        }
        item.setIcon(drawable);
    }


    public void setStatusBarTitle(String title){
        collapsingToolbarLayout.setTitle(title);

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        if(days!=null) {
            com.dans.apps.baratonchurch.models.Days day = days.get(position);
            String simpleDate = DateUtils.getHumanFriendlyDateWithDay(day.getDate());
            date.setText(simpleDate);
            if (readFragment != null) {
                setStatusBarTitle(day.getTitle());
                readFragment.onDaySelected(day);
                LogUtils.d(TAG, "checking if reading is in favourites");
                //we need to check if this day's reading exists in the database
                Cursor cursor = getContentResolver().query(Contract.FavouriteRead.CONTENT_URI,
                        new String[]{Contract.FavouriteRead.DAY_ID},
                        Contract.FavouriteRead.FULL_READ_PATH+" =?",new String[]{day.getFullReadPath()},null);
                if(cursor!=null && cursor.getCount()>0){
                  setIsFavourited = true;
                  LogUtils.d(TAG,"Reading is in favourites");
                }else{
                    LogUtils.d(TAG,"Reading is not in favourites");
                    setIsFavourited = false;
                }
                invalidateOptionsMenu();

            }
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}
    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    //Fetch the daily read first
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,Contract.Day.CONTENT_URI,Days.PROJECTION,
                Contract.Day.LESSON_ID+"=? AND "+Contract.Day.QUARTERLY_ID+"=?",
                new String[]{lessonID,quarterlyID},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<com.dans.apps.baratonchurch.models.Days>daily = new ArrayList<>();
        if(cursor!=null && cursor.moveToFirst()) {
            do{
                progressBar.setVisibility(GONE);
                retry.setVisibility(View.GONE);
                noDays.setVisibility(View.GONE);

                String title = cursor.getString(Days.TITLE);
                String id = cursor.getString(Days.ID);
                String quarterlyID = cursor.getString(Days.QUARTERLY_ID);
                String lessonID = cursor.getString(Days.LESSON_ID);
                String date = cursor.getString(Days.DATE);
                String fullReadPath = cursor.getString(Days.FULL_READ_PATH);

                com.dans.apps.baratonchurch.models.Days days = new com.dans.apps.baratonchurch.models.Days();
                days.setDate(date);
                days.setTitle(title);
                days.setId(id);
                days.setQuarterlyID(quarterlyID);
                days.setLessonID(lessonID);
                days.setFullReadPath(fullReadPath);

                daily.add(days);
            }while (cursor.moveToNext());
            days=daily;
        }else{
            progressBar.setVisibility(GONE);
            retry.setVisibility(View.VISIBLE);
            noDays.setText(R.string.no_days);
            noDays.setVisibility(View.VISIBLE);
        }

        tabLayout.removeAllTabs();
        if(days!=null) {
            for (int i = 0; i < days.size(); i++) {
                tabLayout.addTab(tabLayout.newTab().setText(days.get(i).getTitle()));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    @Override
    public void onDaySelected(int position) {
        if(tabLayout!=null){
            tabLayout.getTabAt(position).select();
        }
    }

    public void restartLoader(){
        getLoaderManager().restartLoader(0,null,this);
    }

    interface Days{
        String [] PROJECTION = {
                Contract.Day.TITLE,
                Contract.Day.ID,
                Contract.Day.QUARTERLY_ID,
                Contract.Day.LESSON_ID,
                Contract.Day.DATE,
                Contract.Day.FULL_READ_PATH
        };


        int TITLE = 0;
        int ID = 1;
        int QUARTERLY_ID = 2;
        int LESSON_ID = 3;
        int DATE = 4;
        int FULL_READ_PATH = 5;
    }


    public class DaysFetcher extends AsyncTask<Boolean,Void,Boolean> {
        Context context;
        String language;
        public DaysFetcher(Context context) {
            this.context = context;
            language = PreferenceManager.
                    getDefaultSharedPreferences(context).
                    getString(SettingsActivity.KEY_LANGUAGE, Constants.DefaultLanguage);
        }

        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            if(shouldFetchDays(booleans[0],quarterlyID,lessonID)){
                Map<String,String>map = fetchDaysFromNetwork(booleans[0],quarterlyID, lessonID);
                if(map.size()>0){
                    //we are to fetch reads for all the days...
                    try{
                      fetchDayRead(map);
                      //it doesnt matter if an exception is thrown here
                        //the method was a bonus call
                        //what matters is  that we have days data
                        return true;
                    }catch (Exception e){
                        e.printStackTrace();
                        return true;
                    }
                }else{
                    return false;
                }
            }else{
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
                noDays.setText(R.string.days_fetch_error);
                noDays.setVisibility(View.VISIBLE);
                retry.setVisibility(View.VISIBLE);
                progressBar.setVisibility(GONE);
            }
        }

        /**
         * Fetches days associated with the lessonId
         * @param forceRefresh
         * @param quarterID
         * @param lessonID
         */
        private boolean shouldFetchDays(boolean forceRefresh,String quarterID,String lessonID){
            if(quarterID!=null && lessonID!=null){
                //again, we check if days associated with the lesson exist.
                Cursor cursor = getContentResolver().
                        query(Contract.Day.CONTENT_URI,new String[]{Contract.Day.ID},
                                Contract.Day.LESSON_ID+"=?",new String[]{lessonID},null);
                if(cursor!=null){
                    if(cursor.getCount()>0){
                        LogUtils.d(TAG,"There are days associated with lesson "+lessonID);
                        if(forceRefresh) {
                            LogUtils.d(TAG,"Force refresh requested ");
                            cursor.close();
                            return true;
                        }else{
                            cursor.close();
                           return false;
                        }
                    }else{
                        LogUtils.d(TAG,"There are no lessons associated with lesson  "+lessonID);
                        cursor.close();
                        return true;
                    }

                }else{return true;}
            }else{return false;}
        }

        private Map<String,String>fetchDaysFromNetwork(boolean forceRefresh,String quarterlyID, String lessonID){
            ContentResolver resolver = context.getContentResolver();
            Map<String,String>map = new HashMap<>();
            try {
                String s = Fetcher.getDays(language, quarterlyID, lessonID);
                if(s!=null) {
                    if(forceRefresh){
                        //delete everything from the days database, read database, verse database
                        resolver.delete(Contract.Day.CONTENT_URI,
                                Contract.Day.LESSON_ID
                                        +" =?"
                                        +" AND "
                                        +Contract.Day.QUARTERLY_ID+" =?"
                                ,new String[]{lessonID,quarterlyID});
                        resolver.delete(Contract.Read.CONTENT_URI,
                                Contract.Read.LESSON_ID
                                        +" =?"
                                        +" AND "
                                        +Contract.Day.QUARTERLY_ID+" =?",
                                new String[]{lessonID,quarterlyID});

                        resolver.delete(Contract.ReadVerses.CONTENT_URI,
                                Contract.ReadVerses.LESSON_ID
                                        +" =?"
                                        +" AND "
                                        +Contract.ReadVerses.QUARTERLY_ID+" =?",
                                new String[]{lessonID,quarterlyID});
                    }

                    JSONObject o = new JSONObject(s);
                    JSONArray daysRead = o.getJSONArray("days");

                    for (int i = 0; i < daysRead.length(); i++) {
                        ContentValues values = new ContentValues();
                        JSONObject day = daysRead.getJSONObject(i);

                        String title = day.getString("title");
                        String date = day.getString("date");
                        String id = day.getString("id");
                        String index = day.getString("index");
                        String path = day.getString("path");
                        String fullPath = day.getString("full_path");
                        String readPath = day.getString("read_path");
                        String fullReadPath = day.getString("full_read_path");

                        values.put(Contract.Day.ENTRY_ID, String.valueOf(System.nanoTime()));
                        values.put(Contract.Day.QUARTERLY_ID, quarterlyID);
                        values.put(Contract.Day.LESSON_ID, lessonID);
                        values.put(Contract.Day.TITLE, title);
                        values.put(Contract.Day.DATE, date);
                        values.put(Contract.Day.ID, id);
                        values.put(Contract.Day.INDEX, index);
                        values.put(Contract.Day.PATH, path);
                        values.put(Contract.Day.FULL_PATH, fullPath);
                        values.put(Contract.Day.READ_PATH, readPath);
                        values.put(Contract.Day.FULL_READ_PATH, fullReadPath);

                        getContentResolver().insert(Contract.Day.CONTENT_URI, values);
                        map.put(id,fullReadPath);
                    }
                    return map;
                }else{
                    return map;
                }
            }catch (Exception e){
                e.printStackTrace();
                return map;
            }

        }

        private void fetchDayRead(Map<String, String> map) throws Exception {
            for (String key : map.keySet()) {
                tryAndFetchReads(key, map.get(key));
                LogUtils.d(TAG, "Fetching read for day ---->" + key);
            }
        }

        private void tryAndFetchReads(String key, String url) throws Exception{
            ContentValues values  = new ContentValues();
            String reads = Fetcher.getJson(url);
            JSONObject object = new JSONObject(reads);
            String content = object.getString("content");
            String date = object.getString("date");
            String index = object.getString("index");
            String title = object.getString("title");
            String readID = object.getString("id");

            values.put(Contract.Read.QUARTERLY_ID,quarterlyID);
            values.put(Contract.Read.DAY_ID,key);
            values.put(Contract.Read.ENTRY_ID,Long.valueOf(System.nanoTime()));
            values.put(Contract.Read.LESSON_ID,lessonID);
            values.put(Contract.Read.CONTENT,content);
            values.put(Contract.Read.ID,readID);  //read id is the same as day id
            values.put(Contract.Read.DATE,date);
            values.put(Contract.Read.INDEX,index);
            values.put(Contract.Read.TITLE,title);

            getContentResolver().insert(Contract.Read.CONTENT_URI,values);

            //lets get the bible verses now
            JSONArray array = object.getJSONArray("bible");
            for(int a = 0; a<array.length();a++){
                ContentValues bibleVerses = new ContentValues();
                JSONObject read = array.getJSONObject(a);
                String name = read.getString("name");//bible name
                String verses = read.getString("verses");//bible verses

                bibleVerses.put(Contract.ReadVerses.ENTRY_ID,String.valueOf(System.nanoTime()));
                bibleVerses.put(Contract.ReadVerses.QUARTERLY_ID,quarterlyID);
                bibleVerses.put(Contract.ReadVerses.BIBLE_NAME,name);
                bibleVerses.put(Contract.ReadVerses.BIBLE_VERSES,verses);
                bibleVerses.put(Contract.ReadVerses.LESSON_ID,lessonID);
                bibleVerses.put(Contract.ReadVerses.READ_ID,readID);

                getContentResolver().insert(Contract.ReadVerses.CONTENT_URI,bibleVerses);
            }
        }


    }
}
