package com.dans.apps.baratonchurch.network;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;

import com.dans.apps.baratonchurch.Constants;
import com.dans.apps.baratonchurch.models.QuarterlyItem;
import com.dans.apps.baratonchurch.models.SermonVideo;
import com.dans.apps.baratonchurch.provider.Contract;
import com.dans.apps.baratonchurch.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * fetches data from the remote server
 * Created by duncan on 11/12/17.
 */

public class Fetcher {
    static String TAG="Fetcher";

    static OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30,TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS).build();

    /**
     *
     * @param language language code ie en for english
     * @return Json string representing the quarterlies
     */
    public static String ListQuarterlies(String language) throws Exception {
        String url = Constants.SABBATH_SCHOOL_API_URLS.ListQuaterlies.replace("{lang}",language);
        LogUtils.d(TAG,"ListQuartelies url "+url);
        String result = fetchJsonString(url);
        LogUtils.d(TAG,"Response "+result);
        return fetchJsonString(url);
    }
    /**
     * returns the quartely and the lessons in the quartely
     * @param language
     * @param quarterlyID
     * @return
     * @throws Exception
     */
    public static String getLessons(String language, String quarterlyID) throws Exception{
        String url = Constants.SABBATH_SCHOOL_API_URLS.Quartalies.
                replace("{lang}",language).replace("{quarterly_id}",quarterlyID);
        LogUtils.d(TAG,"Fetching lessons Quarterly url "+url);
        String result = fetchJsonString(url);
        LogUtils.d(TAG,"Response "+result);
        return result;
    }

    /**
     * returns a specific lesson and the days in the lesson
     * ie day 1 day 2
     * @param language
     * @param quarterlyId
     * @param lessonID
     * @return
     * @throws Exception
     */
    public static String getLesson(String language,String quarterlyId,String lessonID) throws Exception{
        String url= Constants.SABBATH_SCHOOL_API_URLS.Lessons.replace("{lang}",language).
                replace("{quartely_id}",quarterlyId).replace("{lesson_id}",lessonID);
        LogUtils.d(TAG,"getLesson url"+url);
        String result = fetchJsonString(url);
        LogUtils.d(TAG,"Response "+result);
        return result;
    }
    /**
     * we can directly access the read for a specific day
     * @param language
     * @param quarterlyId
     * @param lessonID
     * @return
     */
    public static String getDays(String language,String quarterlyId,String lessonID) throws Exception {
        String url= Constants.SABBATH_SCHOOL_API_URLS.Lessons.replace("{lang}",language).
                replace("{quarterly_id}",quarterlyId).replace("{lesson_id}",lessonID);

        LogUtils.d(TAG,"getLesson url"+url);
        String result = fetchJsonString(url);
        LogUtils.d(TAG,"Response "+result);
        return result;
    }

    public static String getDaysRead(String language,String quartelyID,String lessonID,String dayID) throws Exception{
        String url= Constants.SABBATH_SCHOOL_API_URLS.Lessons.replace("{lang}",language).
                replace("{quartely_id}",quartelyID).replace("{lesson_id}",lessonID).
                replace("{day_id}",dayID);

        LogUtils.d(TAG,"get day read"+url);
        String result = fetchJsonString(url);
        LogUtils.d(TAG,"Response "+result);
        return result;
    }

    public static String getJson(String url) throws Exception {
        return fetchJsonString(url);
    }
    /**
     * Returns the json response from remote network calls
     * should be used to retrieve response from the sabbath school api
     * since we are sure the response is json
     * @param url
     * @return
     * @throws Exception
     */
    private static String fetchJsonString(String url) throws Exception{
        String content = null;
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if(response != null && response.isSuccessful()){
            content= response.body().string();
        }
        return content;
    }

    /**
     *
     * @param context
     * @throws Exception
     */
    synchronized public static void checkSermons(@NonNull  Context context) throws Exception {
        ContentResolver resolver = context.getContentResolver();
        ArrayList<String>videoIds = new ArrayList<>();
        Cursor cursor = context.getContentResolver().
                query(Contract.Sermon.CONTENT_URI,new String[]{Contract.Sermon.ID},null,null,null);
        if(cursor.moveToFirst()){
            do{
                videoIds.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        cursor.close();

        if(videoIds.isEmpty()){
            populateSermonsTable(context);
            return;
        }
        //check the latest
        String json = fetchJsonString(Constants.CHANNEL_VIDEO_LIST);
        ArrayList<SermonVideo>videos = parseSermonJson(context,json,false);
        LogUtils.d(TAG,"network fetch returns "+videos.size()+" videos");

        for(SermonVideo video:videos){
            if(!videoIds.contains(video.getVideoId())){ //insert this
                LogUtils.d(TAG,"no sermon in the database with id #"+video.getVideoId());
                ContentValues values = new ContentValues();
                values.put(Contract.Sermon.ENTRY_ID,String.valueOf(System.nanoTime()));
                values.put(Contract.Sermon.ID,video.getVideoId());
                values.put(Contract.Sermon.KIND,"video");
                values.put(Contract.Sermon.CHANNEL_ID,video.getChannelId());
                values.put(Contract.Sermon.PUBLISHED_AT,video.getPulishedAt());
                values.put(Contract.Sermon.TITLE,video.getTitle());
                values.put(Contract.Sermon.DEFAULT_THUMBNAIL_URL,video.getThumbnailUrl());
                values.put(Contract.Sermon.MEDIUM_THUMBNAIL_URL,video.getMediumThumbnailUrl());
                resolver.insert(Contract.Sermon.CONTENT_URI,values); // insert new content
            }
        }

    }

    /**
     * completely refetch
     */
    private static void populateSermonsTable(Context context) throws Exception {
        //first delete what ever is inside
        String json = fetchJsonString(Constants.CHANNEL_VIDEO_LIST);
        parseSermonJson(context,json,true);
    }

    private static ArrayList<SermonVideo> parseSermonJson(Context context, String json, boolean performInsertion) {
        LogUtils.d(TAG,"parsing string "+json);
        ContentResolver resolver = context.getContentResolver();
        ArrayList<SermonVideo>videos = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(json);
            JSONArray items = object.getJSONArray("items");//item is array
            for(int i = 0 ;i<items.length();i++){
                JSONObject itemObject = items.getJSONObject(i);
                JSONObject id = itemObject.getJSONObject("id");
                String kind = id.getString("kind");
                String videoID = id.getString("videoId");

                JSONObject snippet=itemObject.getJSONObject("snippet");
                String publishedAt = snippet.getString("publishedAt");
                String channelId=snippet.getString("channelId");
                String title = snippet.getString("title");

                JSONObject thumbnails=snippet.getJSONObject("thumbnails");
                JSONObject defaultThumbnail=thumbnails.getJSONObject("default");
                String url = defaultThumbnail.getString("url");
                JSONObject mediumThumbnail = thumbnails.getJSONObject("medium");
                String mediumUrl = mediumThumbnail.getString("url");

                LogUtils.d(TAG,"kind "+kind);
                LogUtils.d(TAG,"vedio id "+videoID);
                LogUtils.d(TAG,"published at "+publishedAt);
                LogUtils.d(TAG,"channelId "+channelId);
                LogUtils.d(TAG,"title "+title);
                LogUtils.d(TAG,"thumbnail url "+url);

                if(performInsertion){
                    ContentValues values = new ContentValues();
                    values.put(Contract.Sermon.ENTRY_ID,String.valueOf(System.nanoTime()));
                    values.put(Contract.Sermon.ID,videoID);
                    values.put(Contract.Sermon.KIND,kind);
                    values.put(Contract.Sermon.CHANNEL_ID,channelId);
                    values.put(Contract.Sermon.PUBLISHED_AT,publishedAt);
                    values.put(Contract.Sermon.TITLE,title);
                    values.put(Contract.Sermon.DEFAULT_THUMBNAIL_URL,url);
                    values.put(Contract.Sermon.MEDIUM_THUMBNAIL_URL,mediumUrl);
                    resolver.insert(Contract.Sermon.CONTENT_URI,values);
                }else {
                    SermonVideo video = new SermonVideo(videoID, channelId, publishedAt, title, url,mediumUrl,false);
                    videos.add(video);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return videos;
    }

    synchronized public static void updateBlogTable(Context context) throws Exception{
        Request request = new Request.Builder().url(Constants.BUC_CHURCH_BLOG).build();
        Response response = client.newCall(request).execute();
        ArrayList<String>ids = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Contract.Blog.CONTENT_URI,new String[]{Contract.Blog.ID},
                null,null,null);
        if (cursor.getCount() > 0) {
            cursor.close();
            return;
        }
        cursor.close();

        if(response!=null && response.isSuccessful()){
            String reponseData = response.body().string();
            JSONObject object = new JSONObject(reponseData);

            JSONArray array = object.getJSONArray("posts");
            for(int i = 0; i<array.length();i++){
                JSONObject item = array.getJSONObject(i);
                String id = item.getString("id");
                if(ids.contains(id)){
                    continue;
                }
                String title = item.getString("title_plain");
                String url = item.getString("url");
                String date = item.getString("date");
                String content = item.getString("content");
                String extract = item.getString("excerpt");
                JSONObject authorInfo = item.getJSONObject("author");
                String author = authorInfo.getString("name");

                ContentValues values = new ContentValues();

                try{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        title = Html.fromHtml(title,Html.FROM_HTML_MODE_COMPACT).toString();
                    }else{
                        title = Html.fromHtml(title).toString();
                    }
                }catch (Exception e){}

                values.put(Contract.Blog.ENTRY_ID,String.valueOf(System.nanoTime()));
                values.put(Contract.Blog.ID,id);
                values.put(Contract.Blog.TITLE,title);
                values.put(Contract.Blog.URL,url);
                values.put(Contract.Blog.CONTENT,content);
                values.put(Contract.Blog.AUTHOR,author);
                values.put(Contract.Blog.DATE,date);
                values.put(Contract.Blog.EXCERPT,extract);

                context.getContentResolver().insert(Contract.Blog.CONTENT_URI,values);
            }
        }
    }

    /**
     * Fetches current text from daily manna and updates the database
     * @return
     */
    public static void checkCurrentBibleText(Context context) throws IOException, JSONException {
        Request request = new Request.Builder().url(Constants.BUC_API.DailyVerses).build();
        Response response = client.newCall(request).execute();
        if(response!=null && response.isSuccessful()){
            String responseData = response.body().string();
            LogUtils.d(TAG,"response data "+responseData);
            JSONObject object = new JSONObject(responseData);
            JSONObject verse = object.getJSONObject("verse");
            JSONObject details = verse.getJSONObject("details");
            if(details!=null) {
                String message = details.getString("text");
                String reference = details.getString("reference");
                String version = details.getString("version");
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                LogUtils.d(TAG,"message ->"+message);
                LogUtils.d(TAG,"reference ->"+reference);
                LogUtils.d(TAG,"version ->"+version);
                ContentValues values = new ContentValues();
                values.put(Contract.DailyVerse.ENTRY_ID,String.valueOf(System.nanoTime()));
                values.put(Contract.DailyVerse.VERSION,version);
                values.put(Contract.DailyVerse.MESSAGE,message);
                values.put(Contract.DailyVerse.TIME,date);
                values.put(Contract.DailyVerse.REFERENCE,reference);
                //delete previous content
                context.getContentResolver().delete(Contract.DailyVerse.CONTENT_URI,null,null);
                //insert new updated content
                context.getContentResolver().insert(Contract.DailyVerse.CONTENT_URI,values);
            }
        }
    }

    /**
     * @param forceRefresh (set true in calling activity on user request)
     * @param language
     * @param context
     * @param checkLatest  (should be set true in the syncadapter)
     */
    synchronized public static void checkQuarterlies(boolean forceRefresh, String language, Context context, boolean checkLatest)
            throws Exception {
        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(Contract.Quarterly.CONTENT_URI,
                new String[]{Contract.Quarterly.INDEX}, null, null, null);

        if(cursor.getCount() == 0 || forceRefresh){
            LogUtils.d(TAG,"There is no content in the quarterlies database or force Refresh is enabled");
            fetchQuarterliesFromNetwork(context,true,Constants.DefaultLanguage,true);
        }else{
            LogUtils.d(TAG,"There is content in the database... ");
            if(checkLatest && cursor.moveToFirst()){
                ArrayList<QuarterlyItem>items=fetchQuarterliesFromNetwork(context,false,
                        Constants.DefaultLanguage,false);
                LogUtils.d(TAG,"quarterlies count returned from network : "+items.size());
                LogUtils.d(TAG,"quaterlies count from database "+cursor.getCount());
                //todo its much authentic to check the quarterly indexes as compared to count
                //but as for not count suffices
                if(items.size()!=cursor.getCount()){
                    LogUtils.d(TAG,"count do not match, refreshing database");
                    //its much better we do full tree delete
                    resolver.delete(Contract.Quarterly.CONTENT_URI,null,null);
                    //resolver.delete(Contract.Lesson.CONTENT_URI,null,null);
                    //resolver.delete(Contract.Day.CONTENT_URI,null,null);
                    for(QuarterlyItem item : items){
                        ContentValues values = new ContentValues();
                        values.put(Contract.Quarterly.TITLE, item.getTitle());
                        values.put(Contract.Quarterly.DESCRIPTION, item.getDescription());
                        values.put(Contract.Quarterly.HUMAN_DATE, item.getHuman_date());
                        values.put(Contract.Quarterly.START_DATE, item.getStartDate());
                        values.put(Contract.Quarterly.END_DATE, item.getEndDate());
                        values.put(Contract.Quarterly.PRIMARY_COLOR, item.getPrimaryColor());
                        values.put(Contract.Quarterly.SECONDARY_COLOR, item.getSecondaryColor());
                        values.put(Contract.Quarterly.ID, item.getId());
                        values.put(Contract.Quarterly.LANG, language);
                        values.put(Contract.Quarterly.INDEX, item.getIndex());
                        values.put(Contract.Quarterly.PATH, item.getFullPath());
                        values.put(Contract.Quarterly.FULL_PATH, item.getFullPath());
                        values.put(Contract.Quarterly.COVER_PATH, item.getCoverPath());
                        values.put(Contract.Quarterly.ENTRY_ID, String.valueOf(System.nanoTime()));

                        resolver.insert(Contract.Quarterly.CONTENT_URI, values);
                    }
                }
            }
        }

        cursor.close();
    }

    private static ArrayList<QuarterlyItem>fetchQuarterliesFromNetwork(Context context,
                                                                       boolean deletePrevious, String language, boolean performInsertion)
            throws Exception {
        ArrayList<QuarterlyItem> quarterlyItems = new ArrayList<>();

        ContentResolver resolver = context.getContentResolver();
        LogUtils.d(TAG, "Fetching quarterlies from network");

        String quarterliesList = Fetcher.ListQuarterlies("en"); //returns an array of quarterlies
        if (quarterliesList != null) {
            //we only delete previous content when we are sure there is data returned
            if (deletePrevious) {
                resolver.delete(Contract.Quarterly.CONTENT_URI, null, null);
                LogUtils.d(TAG, "Force refreshing lessons database");
                //delete lessons associated with the specific quarterly id
                //delete the full tree too since the previous data might not match the new data
                resolver.delete(Contract.Lesson.CONTENT_URI, null, null);
                resolver.delete(Contract.Day.CONTENT_URI, null, null);
                resolver.delete(Contract.Read.CONTENT_URI, null, null);
                resolver.delete(Contract.ReadVerses.CONTENT_URI, null, null);
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
                if (performInsertion) {
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
                } else {
                    QuarterlyItem item = new QuarterlyItem(String.valueOf(System.nanoTime()),
                            title, description, id, language, humanDate,
                            cover, fullPath, colorPrimary, colorPrimaryDark,index,startDate,endDate);
                    quarterlyItems.add(item);
                }
            }
        } else {
            LogUtils.e(TAG, "no quarterly list returned");
        }

        return quarterlyItems;
    }
}
