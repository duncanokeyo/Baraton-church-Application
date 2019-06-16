package com.dans.apps.baratonchurch.provider;

/**
 * Created by duncan on 11/9/17.
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 *
 */
public class Contract {

    public static final String CONTENT_AUTHORITY="com.dans.app.buc";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+CONTENT_AUTHORITY);

    private static final String PATH_VERSE="verses";
    private static final String PATH_ANNOUNCEMENT="announcements";
    private static final String PATH_REQUESTS="requests";
    private static final String PATH_SERMON="sermons";
    private static final String PATH_QUARTERLY="quarterlies";
    private static final String PATH_LESSON="lessons";
    private static final String PATH_READ="readings";
    private static final String PATH_READ_VERSES = "readverses";
    private static final String PATH_FAVOURITE_READ = "favourite_readings";
    private static final String PATH_DAY="days";
    private static final String PATH_BLOG ="blogs";

    interface CommonColumns{
        /**Uniquely identifies an entry*/
        String ENTRY_ID ="entry_id";
        /**date and time of entry**/
        String TIME ="time";
    }

    interface SabbathSchoolCommonColumns extends CommonColumns{
        String TITLE ="title";
        String ID="id";
        String QUARTERLY_ID="quarterly_id";
        String INDEX ="item_index";
        String PATH="path";
    }

    public static class Blog implements BaseColumns, CommonColumns{
        public static final String CONTENT_TYPE=
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.buc.blogs";
        public static final String CONTENT_ITEM_TYPE=
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.buc.blog";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_BLOG).build();

        public static Uri buildUri(String Id) {
            return CONTENT_URI.buildUpon().appendPath(Id).build();
        }
        public static String getID(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String TABLE_NAME = "Blog";
        public static String TITLE = "title";
        public static String URL = "url";
        public static String ID="blog_id";
        public static String CONTENT ="content";
        public static String DATE="date";
        public static String AUTHOR ="author";
        public static String EXCERPT = "excerpt";
    }

    /**
     * Database columns for the announcement  class
     */
    public static class Announcements implements BaseColumns,CommonColumns{

        public static final String CONTENT_TYPE=
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.buc.announcements";
        public static final String CONTENT_ITEM_TYPE=
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.buc.announcement";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ANNOUNCEMENT).build();

        public static Uri buildAnnouncementUri(String AnnouncementId) {
            return CONTENT_URI.buildUpon().appendPath(AnnouncementId).build();
        }

        public static String getAnnouncementID(Uri AnnouncementUri) {
            return AnnouncementUri.getPathSegments().get(1);
        }

        public static String TABLE_NAME = "Announcement";
        public static String TITLE="title";
        public static String ORIGIN="origin";
        public static String MESSAGE="message";
    }

    /**
     * the verse posted. will be updated everyday
     */
    public static class DailyVerse implements BaseColumns,CommonColumns{
        public static final String CONTENT_TYPE=
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.buc.verses";
        public static final String CONTENT_ITEM_TYPE=
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.buc.verse";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_VERSE).build();

        public static Uri buildVerseUri(String VerseId) {
            return CONTENT_URI.buildUpon().appendPath(VerseId).build();
        }

        public static String getVerseID(Uri VerseUri) {
            return VerseUri.getPathSegments().get(1);
        }

        public static String TABLE_NAME = "BibleText";

        public static String MESSAGE="message";
        public static String REFERENCE="reference";
        public static String VERSION="version";
    }

    public static class Sermon implements BaseColumns,CommonColumns{
        public static final String CONTENT_TYPE=
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.buc.sermons";
        public static final String CONTENT_ITEM_TYPE=
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.buc.sermon";

        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_SERMON).build();

        public static Uri buildSermontUri(String SermonId){
            return  CONTENT_URI.buildUpon().appendPath(SermonId).build();
        }
        public static String getSermonID(Uri SermonUri){
            return SermonUri.getPathSegments().get(1);
        }

        public static String TABLE_NAME="Sermon";

        public  static String KIND="kind";
        public  static String ID = "id";
        public  static String CHANNEL_ID ="channel_id";
        public  static String PUBLISHED_AT="published_at";
        public  static String TITLE="title";
        public  static String DEFAULT_THUMBNAIL_URL="default_thumbnail_url";
        public  static String MEDIUM_THUMBNAIL_URL="medium_thumbnail_url";
        public  static String FAVOURITE = "favourite";
    }

    public static class Requests implements BaseColumns,CommonColumns{
        public static final String CONTENT_TYPE=
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.buc.requests";
        public static final String CONTENT_ITEM_TYPE=
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.buc.request";

        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_REQUESTS).build();

        public static Uri buildRequestUri(String RequestId){
            return  CONTENT_URI.buildUpon().appendPath(RequestId).build();
        }

        public static String getRequestID(Uri RequestUri){
            return RequestUri.getPathSegments().get(1);
        }

        public static String TABLE_NAME="Request";
    }

    public static class Quarterly implements BaseColumns,SabbathSchoolCommonColumns{
        public static final String CONTENT_TYPE=
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.buc.quarterlies";
        public static final String CONTENT_ITEM_TYPE=
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.buc.quarterly";

        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().
                appendPath(PATH_QUARTERLY).build();

        public static Uri buildQuaterlyUri(String QuaterlyId){
            return  CONTENT_URI.buildUpon().appendPath(QuaterlyId).build();
        }

        public static String getQuaterlyID(Uri QuaterlyUri){
            return QuaterlyUri.getPathSegments().get(1);
        }

        public static String TABLE_NAME="Quarterly";

        public static String DESCRIPTION="Description";
        public static String HUMAN_DATE="Humandate";
        public static String START_DATE="StartDate";
        public static String END_DATE="EndDate";
        public static String PRIMARY_COLOR="PrimaryColor";
        public static String SECONDARY_COLOR="SecondaryColor";
        public static String FULL_PATH="FullPath";
        public static String LANG="Lang";
        public static String COVER_PATH="CoverPath";
    }

    public static class Lesson implements BaseColumns,SabbathSchoolCommonColumns{
        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.buc.lessons";
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.buc.lesson";

        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_LESSON).build();

        public static Uri buildLessonUri(String LessonId){
            return  CONTENT_URI.buildUpon().appendPath(LessonId).build();
        }

        public static String getLessonID(Uri LessonUri){
            return LessonUri.getPathSegments().get(1);
        }

        public static final String TABLE_NAME="Lesson";
        public static String START_DATE ="StartDate";
        public static String END_DATE="EndDate";
        public static String FULL_PATH="FullPath";
        public static final String COVER_PATH = "CoverPath";
    }

    public static class Read implements BaseColumns, SabbathSchoolCommonColumns{
        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.buc.readings";
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.buc.read";
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_READ).build();
        public static final String LESSON_ID = "lesson_id";

        public static Uri buildReadUri(String ReadId){
            return  CONTENT_URI.buildUpon().appendPath(ReadId).build();
        }
        public static String getReadID(Uri ReadUri){
            return ReadUri.getPathSegments().get(1);
        }

        public static final String TABLE_NAME="DailyRead";
        public static final String DAY_ID = "day_id" ;
        public static String DATE="Date";
        public static String CONTENT="content";
    }

    public static class ReadVerses implements BaseColumns,SabbathSchoolCommonColumns{
        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.buc.readverses";
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.buc.readverse";
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_READ_VERSES).build();
        public static final String LESSON_ID = "lesson_id";

        public static Uri buildReadVersesUri(String ReadVerse){
            return  CONTENT_URI.buildUpon().appendPath(ReadVerse).build();
        }
        public static String getReadVersesID(Uri ReadVerses){
            return ReadVerses.getPathSegments().get(1);
        }

        public static final String TABLE_NAME="ReadVerses";
        public static final String READ_ID = "read_id" ;
        public static String BIBLE_NAME="BibleName";
        public static String BIBLE_VERSES="Verses";
    }

    public static class Day implements BaseColumns,SabbathSchoolCommonColumns{
        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.buc.days";
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.buc.day";
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_DAY).build();

        public static Uri buildDayUri(String DayId){
            return  CONTENT_URI.buildUpon().appendPath(DayId).build();
        }

        public static String geDayID(Uri DayUri){
            return DayUri.getPathSegments().get(1);
        }

        public static final String TABLE_NAME="Day";

        public static String DATE="Date";
        public static String PATH="path";
        public static final String LESSON_ID = "lesson_id";
        public static String FULL_PATH="fullpath";
        public static String READ_PATH="readpath";
        public static String FULL_READ_PATH="fullreadpath";
    }

    public static class FavouriteRead implements BaseColumns,CommonColumns{
        public static final String CONTENT_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.buc.favourite_readings";
        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.buc.favourite_reading";
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE_READ).build();

        public static Uri buildFavouriteReadingsUri(String FavouriteId){
            return  CONTENT_URI.buildUpon().appendPath(FavouriteId).build();
        }

        public static String getFavouriteReadingID(Uri FavouriteUri){
            return FavouriteUri.getPathSegments().get(1);
        }

        public static final String TABLE_NAME="FavouriteReading";

        public static String DATE="Date";
        public static String FULL_READ_PATH="path";
        public static String LESSON_TITLE="lesson_title";
        public static String TITLE = "title";
        public static String LESSON_COVER_PATH="lesson_cover_path";
        public static String CONTENT = "content";
        public static String REFERENCE ="bible_references";
        public static final String LESSON_ID = "lesson_id";
        public static final String QUARTERLY_ID="quarter_id";
        public static final String DAY_ID = "day_id";

    }

}