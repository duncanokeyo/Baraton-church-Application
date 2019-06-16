package com.dans.apps.baratonchurch.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.dans.apps.baratonchurch.BroadcastNotifier;
import com.dans.apps.baratonchurch.Constants;
import com.dans.apps.baratonchurch.SettingsActivity;
import com.dans.apps.baratonchurch.network.Fetcher;
import com.dans.apps.baratonchurch.provider.Contract;
import com.dans.apps.baratonchurch.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Central service that helps us to fetch sabbath school data on request SyncAdapter is Automatic<br>
 * Quarterly --> Lessons --> Days (could be day 1,2,3,4,5,6) --> read<br>
 *
 * Data is fetched once and cached in the database<br>
 *
 * Classes that call this service are expected to implement Broadcast receiver
 * and offer refresh mechanisms in case there is an error response
 *
 */
public class SabbathSchoolFetcherService extends IntentService {

    String TAG = "Sabbath School Fetcher";

    public static String GENERAL_ACTION_QUARTERLY = "sabbath_school_action_fetcher_quarterly";
    public static String GENERAL_ACTION_LESSONS="sabbath_school_action_fetcher_lessons";
    public static String GENERAL_ACTION_DAYS="sabbath_school_action_fetcher_days";

    public static final String KEY_FETCHER_ACTION = "fetcher_action";
    public static final String KEY_FORCE_REFRESH="force_refresh";
    public static final String KEY_FULL_READ_PATH="full_read_path";
    public static int ACTION_FETCH_QUARTERLIES = 1;
    public static int ACTION_FETCH_LESSONS = 2;
    public static int ACTION_FETCH_DAYS = 3;
    public static int ACTION_FETCH_READ = 4;

    //if force refresh is enabled, data in the database is cleared, and new data is
    //inserted, but this is only when there is new data.
    public boolean forceRefresh = false;

    public static String RESULTS = "Results";
    public static int RESULT_OK = 1;
    public static int RESULT_ERROR = 2;

    BroadcastNotifier broadcastNotifier;
    ContentResolver resolver;

    String language;
    String quarterlyID;
    String lessonID;
    String dayID;

    public SabbathSchoolFetcherService() {
        super("SabbathSchoolFetcherService");
        broadcastNotifier = new BroadcastNotifier(this);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent!=null){
            Bundle bundle = intent.getExtras();
            resolver=getContentResolver();
            //fetch the daily reading for a particular lesson
            lessonID = bundle.getString(Contract.Lesson.ID);
            quarterlyID = bundle.getString(Contract.Quarterly.QUARTERLY_ID);
            dayID  = bundle.getString(Contract.Day.ID);
            forceRefresh = bundle.getBoolean(KEY_FORCE_REFRESH,false);
            language = PreferenceManager.
                    getDefaultSharedPreferences(getApplication()).
                    getString(SettingsActivity.KEY_LANGUAGE, Constants.DefaultLanguage);
            String fullReadPath = bundle.getString(KEY_FULL_READ_PATH);
            int action = bundle.getInt(KEY_FETCHER_ACTION);

            if(action == ACTION_FETCH_QUARTERLIES){
                broadcastNotifier.setCategory(ACTION_FETCH_QUARTERLIES);
                fetchQuarterlies();
            }else if(action == ACTION_FETCH_LESSONS){
                broadcastNotifier.setCategory(ACTION_FETCH_LESSONS);
                fetchLessons(quarterlyID);
            }else if(action == ACTION_FETCH_DAYS){
                broadcastNotifier.setCategory(ACTION_FETCH_DAYS);
                fetchDays(language,quarterlyID,lessonID);
            }else if(action == ACTION_FETCH_READ){ // will i really use this ?
                fetchDayRead(fullReadPath);
            }else{
                //no category defined, broadcast error
                if(broadcastNotifier!=null){
                    broadcastNotifier.broadcastError();
                }
            }

        }else{
            broadcastNotifier.broadcastError();
        }
    }

    /**
     * Fetches quarterlies , using the provided language
     */
    private void fetchQuarterlies() {
        LogUtils.d(TAG,"fetch quarterlies requested . Language ("+language+")");
        Cursor cursor = resolver.query(Contract.Quarterly.CONTENT_URI,
                new String[]{Contract.Quarterly.ID}, null, null, null);
        if(cursor!=null){
            if(cursor.getCount()>0){
                LogUtils.d(TAG,"There is content in the quarterlies database");
                if(forceRefresh){
                    LogUtils.d(TAG,"Force Refresh requested, fetching from network");
                    fetchQuarterliesFromNetwork(true,language);
                }else{
                    broadcastNotifier.broadcastSuccess();
                }
            }else{
                LogUtils.d(TAG,"There is no content in the quarterlies database .");
                fetchQuarterliesFromNetwork(false,language);
            }
            cursor.close();
        }else{
            broadcastNotifier.broadcastError();
        }

    }

    private void fetchQuarterliesFromNetwork(boolean deletePrevious, String language) {
        LogUtils.d(TAG,"Fetching quarterlies from network");
        try {
            String quarterliesList = Fetcher.ListQuarterlies("en"); //returns an array of quarterlies
            if (quarterliesList != null) {
                //we only delete previous content when we are sure there is data returned
                if(deletePrevious){
                   resolver.delete(Contract.Quarterly.CONTENT_URI,null,null);
                    LogUtils.d(TAG,"Force refreshing lessons database");
                    //delete lessons associated with the specific quarterly id
                    //delete the full tree too since the previous data might not match the new data
                    resolver.delete(Contract.Lesson.CONTENT_URI,null,null);
                    resolver.delete(Contract.Day.CONTENT_URI,null,null);
                    resolver.delete(Contract.Read.CONTENT_URI,null,null);
                    resolver.delete(Contract.ReadVerses.CONTENT_URI,null,null);
                }

                JSONArray quarterlies = new JSONArray(quarterliesList);
                for (int i = 0; i < quarterlies.length(); i++) {
                    ContentValues values = new ContentValues();
                    JSONObject object = quarterlies.getJSONObject(i);//quarterlies object in the array.
                    String title = object.getString("title");
                    String description = object.getString("description");
                    String humanDate = object.getString("human_date");
                    String startDate = object.getString("start_date");
                    String endDate = object.getString("end_date");
                    String colorPrimary = object.getString("color_primary");
                    String colorPrimaryDark = object.getString("color_primary_dark");
                    String id = object.getString("id");
                    String index = object.getString("index");
                    String path = object.getString("path");
                    String fullPath = object.getString("full_path");
                    String cover = object.getString("cover");

                    values.put(Contract.Quarterly.TITLE, title);
                    values.put(Contract.Quarterly.DESCRIPTION, description);
                    values.put(Contract.Quarterly.HUMAN_DATE, humanDate);
                    values.put(Contract.Quarterly.START_DATE, startDate);
                    values.put(Contract.Quarterly.END_DATE, endDate);
                    values.put(Contract.Quarterly.PRIMARY_COLOR, colorPrimary);
                    values.put(Contract.Quarterly.SECONDARY_COLOR, colorPrimaryDark);
                    values.put(Contract.Quarterly.ID, id);
                    values.put(Contract.Quarterly.LANG, language);
                    values.put(Contract.Quarterly.INDEX, index);
                    values.put(Contract.Quarterly.PATH, path);
                    values.put(Contract.Quarterly.FULL_PATH, fullPath);
                    values.put(Contract.Quarterly.COVER_PATH, cover);
                    values.put(Contract.Quarterly.ENTRY_ID, String.valueOf(System.nanoTime()));

                    resolver.insert(Contract.Quarterly.CONTENT_URI, values);
                }
            }else{
                LogUtils.e(TAG,"no quarterly list returned");
                broadcastNotifier.broadcastError();
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            broadcastNotifier.broadcastError();
            return;
        }

        broadcastNotifier.broadcastSuccess();
    }

    /**
     * Fetches lessons associated with the provided quartery id
     * @param quarterID
     */
    private void fetchLessons(String quarterID) {
       if(quarterID!=null){
           //check if lessons with the provided quartery id exist in the database
           Cursor cursor = resolver.query(Contract.Lesson.CONTENT_URI,new String[]{Contract.Lesson.ID},
           Contract.Lesson.QUARTERLY_ID+"=?",new String[]{quarterID},null);
           if(cursor!=null){
               if(cursor.getCount()>0){
                   LogUtils.d(TAG,"There are lessons associated with quarterly id : "+quarterID);
                   if(forceRefresh){
                       LogUtils.d(TAG,"Force refersh requested");
                       fetchLessonsFromNetwork(quarterID);
                   }else{
                       broadcastNotifier.broadcastSuccess();
                   }
               }else{
                   LogUtils.d(TAG,"No lessons associated with quarterly id : "+quarterID);
                   fetchLessonsFromNetwork(quarterID);
               }
               cursor.close();
           }else{
               broadcastNotifier.broadcastError();
           }
       }else{
           broadcastNotifier.broadcastError();
       }
    }

    private void fetchLessonsFromNetwork(String quarterID){
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
                broadcastNotifier.broadcastError();
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
            broadcastNotifier.broadcastError();
            return;
        }
        broadcastNotifier.broadcastSuccess();
    }

    /**
     * Fetches days associated with the lessonId
     * @param language
     * @param quarterID
     * @param lessonID
     */
    private void fetchDays(String language,String quarterID,String lessonID){
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
                        Map<String,String>map = fetchDaysFromNetwork(language, quarterID, lessonID);
                        if(map!=null && map.size()>0){
                            fetchDayRead(map);
                        }else{
                            broadcastNotifier.broadcastError();
                        }
                    }else{
                        broadcastNotifier.broadcastSuccess();
                    }
                }else{
                    LogUtils.d(TAG,"There are no lessons associated with lesson  "+lessonID);
                    Map<String,String>map = fetchDaysFromNetwork(language, quarterID, lessonID);
                    if(map!=null && map.size()>0){
                        fetchDayRead(map);
                    }else{
                        broadcastNotifier.broadcastError();
                    }
                }
                cursor.close();
            }else{
                broadcastNotifier.broadcastError();
            }
        }else{
            broadcastNotifier.broadcastError();
        }
    }

    private Map<String,String>fetchDaysFromNetwork(String language, String quarterlyID, String lessonID){
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
            broadcastNotifier.broadcastError();
            return map;
        }

    }


    private void fetchDayRead(String fullReadPath) {
        try {
            tryAndFetchReads(dayID,fullReadPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void fetchDayRead(Map<String,String>map){
        for(String key:map.keySet()){
            try {
                tryAndFetchReads(key,map.get(key));
            } catch (Exception e) {
                e.printStackTrace();
            }
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

        broadcastNotifier.broadcastSuccess();
    }

    /**
     * Fetches the reads and the s
     * @param dayId
     */
    private void tryAndFetchReads(String dayId) throws Exception {
        ContentValues values  = new ContentValues();
        String reads =Fetcher.getDaysRead("en",quarterlyID,lessonID,dayId);
        JSONObject object = new JSONObject(reads);

        String content = object.getString("content");
        String date = object.getString("date");
        String index = object.getString("index");
        String title = object.getString("title");
        String readID = object.getString("id");

        values.put(Contract.Read.QUARTERLY_ID,quarterlyID);
        values.put(Contract.Read.DAY_ID,dayId);
        values.put(Contract.Read.ENTRY_ID,Long.valueOf(System.nanoTime()));
        values.put(Contract.Read.CONTENT,content);
        values.put(Contract.Read.DATE,date);
        values.put(Contract.Read.LESSON_ID,lessonID);
        values.put(Contract.Read.INDEX,index);
        values.put(Contract.Read.ID,readID);
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

        broadcastNotifier.broadcastSuccess();

    }

}
