package com.dans.apps.baratonchurch.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dans.apps.baratonchurch.utils.LogUtils;
import com.dans.apps.baratonchurch.utils.SelectionBuilder;

/**
 * Created by duncan on 11/9/17.
 */

public class Provider extends ContentProvider {
    private String TAG="Provider";

    public static final int ROUTE_SERMON=1;
    public static final int ROUTE_SERMON_ID=2;
    public static final int ROUTE_VERSE=3;
    public static final int ROUTE_VERSE_ID=4;
    public static final int ROUTE_ANNOUNCEMENT=5;
    public static final int ROUTE_ANNOUNCEMENT_ID=6;
    public static final int ROUTE_REQUEST=7;
    public static final int ROUTE_REQUEST_ID=8;
    //sabbath school
    public static final int ROUTE_QUARTERLY=9;
    public static final int ROUTE_QUARTERLY_ID=10;
    public static final int ROUTE_LESSON=11;
    public static final int ROUTE_LESSON_ID=12;
    public static final int ROUTE_READ=13;
    public static final int ROUTE_READ_ID=14;
    public static final int ROUTE_DAY=15;
    public static final int ROUTE_DAY_ID=16;
    public static final int ROUTE_READ_VERSE=17;
    public static final int ROUTE_READ_VERSE_ID = 18;
    public static final int ROUTE_FAVOURITE_READ= 19;
    public static final int ROUTE_FAVOURITE_READ_ID= 20;
    //blog
    public static final int ROUTE_BLOG = 21;
    public static final int ROUTE_BLOG_ID =22;

    private static final String AUTHORITY= Contract.CONTENT_AUTHORITY;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY,"sermons",ROUTE_SERMON);
        sUriMatcher.addURI(AUTHORITY,"sermons/*",ROUTE_SERMON_ID);
        sUriMatcher.addURI(AUTHORITY,"verses",ROUTE_VERSE);
        sUriMatcher.addURI(AUTHORITY,"verses/*",ROUTE_VERSE_ID);
        sUriMatcher.addURI(AUTHORITY,"announcements",ROUTE_ANNOUNCEMENT);
        sUriMatcher.addURI(AUTHORITY,"announcements/*",ROUTE_ANNOUNCEMENT_ID);
        sUriMatcher.addURI(AUTHORITY,"requests",ROUTE_REQUEST);
        sUriMatcher.addURI(AUTHORITY,"requests/*",ROUTE_REQUEST_ID);
        //sabbath school
        sUriMatcher.addURI(AUTHORITY,"quarterlies",ROUTE_QUARTERLY);
        sUriMatcher.addURI(AUTHORITY,"quarterlies/*",ROUTE_QUARTERLY_ID);
        sUriMatcher.addURI(AUTHORITY,"quarterlies",ROUTE_QUARTERLY);
        sUriMatcher.addURI(AUTHORITY,"lessons/",ROUTE_LESSON);
        sUriMatcher.addURI(AUTHORITY,"lessons/*",ROUTE_LESSON_ID);
        sUriMatcher.addURI(AUTHORITY,"readings/",ROUTE_READ);
        sUriMatcher.addURI(AUTHORITY,"readings/*",ROUTE_READ_ID);

        sUriMatcher.addURI(AUTHORITY,"readverses/",ROUTE_READ_VERSE);
        sUriMatcher.addURI(AUTHORITY,"readverses/*",ROUTE_READ_VERSE_ID);

        sUriMatcher.addURI(AUTHORITY,"days/",ROUTE_DAY);
        sUriMatcher.addURI(AUTHORITY,"days/*",ROUTE_DAY_ID);

        sUriMatcher.addURI(AUTHORITY,"favourite_readings/",ROUTE_FAVOURITE_READ);
        sUriMatcher.addURI(AUTHORITY,"favourite_readings/*",ROUTE_FAVOURITE_READ_ID);

        sUriMatcher.addURI(AUTHORITY,"blogs/",ROUTE_BLOG);
        sUriMatcher.addURI(AUTHORITY,"blogs/*",ROUTE_BLOG_ID);

    }

    ChurchDatabase mChurchDatabase;

    @Override
    public boolean onCreate() {
        mChurchDatabase = new ChurchDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db =mChurchDatabase.getReadableDatabase(); //read only access
        final SelectionBuilder builder = new SelectionBuilder();
        int match = sUriMatcher.match(uri);

        switch (match){
            case ROUTE_ANNOUNCEMENT:{
                 builder.table(Contract.Announcements.TABLE_NAME)
                        .where(selection,selectionArgs);
                break;
            }
            case ROUTE_ANNOUNCEMENT_ID: {
                String id = Contract.Announcements.getAnnouncementID(uri);
                builder.table(Contract.Announcements.TABLE_NAME);
                builder.where(Contract.Announcements.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_REQUEST:{
                 builder.table(Contract.Requests.TABLE_NAME)
                        .where(selection,selectionArgs);
                 break;
            }
            case ROUTE_REQUEST_ID:{
                String id = Contract.Requests.getRequestID(uri);
                builder.table(Contract.Requests.TABLE_NAME);
                builder.where(Contract.Requests.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_SERMON:{
                 builder.table(Contract.Sermon.TABLE_NAME)
                        .where(selection,selectionArgs);
                 break;
            }
            case ROUTE_SERMON_ID:{
                String id = Contract.Sermon.getSermonID(uri);
                builder.table(Contract.Sermon.TABLE_NAME);
                builder.where(Contract.Sermon.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_VERSE:{
                 builder.table(Contract.DailyVerse.TABLE_NAME)
                        .where(selection,selectionArgs);
                 break;
            }
            case ROUTE_VERSE_ID:{
                String id = Contract.DailyVerse.getVerseID(uri);
                builder.table(Contract.Sermon.TABLE_NAME);
                builder.where(Contract.DailyVerse.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_QUARTERLY:{
                builder.table(Contract.Quarterly.TABLE_NAME)
                        .where(selection,selectionArgs);
                break;
            }
            case ROUTE_QUARTERLY_ID:{
                String id =Contract.Quarterly.getQuaterlyID(uri);
                builder.table(Contract.Quarterly.TABLE_NAME);
                builder.where(Contract.Quarterly.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_LESSON:{
                builder.table(Contract.Lesson.TABLE_NAME)
                        .where(selection,selectionArgs);
                break;
            }
            case ROUTE_LESSON_ID:{
                String id =Contract.Lesson.getLessonID(uri);
                builder.table(Contract.Lesson.TABLE_NAME);
                builder.where(Contract.Lesson.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_READ:{
                builder.table(Contract.Read.TABLE_NAME)
                        .where(selection,selectionArgs);
                break;
            }
            case ROUTE_READ_ID:{
                String id =Contract.Read.getReadID(uri);
                builder.table(Contract.Read.TABLE_NAME);
                builder.where(Contract.Read.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_READ_VERSE:{
                builder.table(Contract.ReadVerses.TABLE_NAME)
                        .where(selection,selectionArgs);
                break;
            }
            case ROUTE_READ_VERSE_ID:{
                String id =Contract.ReadVerses.getReadVersesID(uri);
                builder.table(Contract.ReadVerses.TABLE_NAME);
                builder.where(Contract.ReadVerses.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_DAY:{
                builder.table(Contract.Day.TABLE_NAME)
                        .where(selection,selectionArgs);
                break;
            }
            case ROUTE_DAY_ID:{
                String id =Contract.Day.geDayID(uri);
                builder.table(Contract.Day.TABLE_NAME);
                builder.where(Contract.Day.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_FAVOURITE_READ:{
                builder.table(Contract.FavouriteRead.TABLE_NAME)
                        .where(selection,selectionArgs);
                break;
            }
            case ROUTE_FAVOURITE_READ_ID:{
                String id =Contract.FavouriteRead.getFavouriteReadingID(uri);
                builder.table(Contract.FavouriteRead.TABLE_NAME);
                builder.where(Contract.FavouriteRead.ENTRY_ID +"=?",id);
                break;
            }

            case ROUTE_BLOG:{
                builder.table(Contract.Blog.TABLE_NAME)
                        .where(selection,selectionArgs);
                break;
            }
            case ROUTE_BLOG_ID:{
                String id =Contract.Blog.getID(uri);
                builder.table(Contract.Blog.TABLE_NAME);
                builder.where(Contract.Blog.ENTRY_ID +"=?",id);
                break;
            }

            default:{
                throw new UnsupportedOperationException("Unkown uri : "+uri);
            }
        }

        Cursor c = builder.query(db,projection,sortOrder);
        Context ctx = getContext();
        assert ctx!=null;
        c.setNotificationUri(ctx.getContentResolver(),uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case ROUTE_ANNOUNCEMENT:
                return Contract.Announcements.CONTENT_TYPE;
            case ROUTE_ANNOUNCEMENT_ID:
                return Contract.Announcements.CONTENT_ITEM_TYPE;
            case ROUTE_SERMON:
                return Contract.Sermon.CONTENT_TYPE;
            case ROUTE_SERMON_ID:
                return Contract.Sermon.CONTENT_ITEM_TYPE;
            case ROUTE_VERSE:
                return Contract.DailyVerse.CONTENT_TYPE;
            case ROUTE_VERSE_ID:
                return Contract.DailyVerse.CONTENT_ITEM_TYPE;
            case ROUTE_REQUEST:
                return Contract.Requests.CONTENT_TYPE;
            case ROUTE_REQUEST_ID:
                return Contract.Requests.CONTENT_ITEM_TYPE;
            case ROUTE_QUARTERLY:   //sabbath school
                return Contract.Quarterly.CONTENT_TYPE;
            case ROUTE_QUARTERLY_ID:
                return Contract.Quarterly.CONTENT_ITEM_TYPE;
            case ROUTE_LESSON:
                return Contract.Lesson.CONTENT_TYPE;
            case ROUTE_LESSON_ID:
                return Contract.Lesson.CONTENT_ITEM_TYPE;
            case ROUTE_READ:
                return Contract.Read.CONTENT_TYPE;
            case ROUTE_READ_ID:
                return Contract.Read.CONTENT_ITEM_TYPE;
            case ROUTE_READ_VERSE:
                return Contract.ReadVerses.CONTENT_TYPE;
            case ROUTE_READ_VERSE_ID:
                return Contract.ReadVerses.CONTENT_ITEM_TYPE;
            case ROUTE_DAY:
                return Contract.Day.CONTENT_TYPE;
            case ROUTE_DAY_ID:
                return Contract.Day.CONTENT_ITEM_TYPE;
            case ROUTE_FAVOURITE_READ:
                return Contract.FavouriteRead.CONTENT_TYPE;
            case ROUTE_FAVOURITE_READ_ID:
                return Contract.FavouriteRead.CONTENT_ITEM_TYPE;
            case ROUTE_BLOG:
                return Contract.Blog.CONTENT_TYPE;
            case ROUTE_BLOG_ID:
                return Contract.Blog.CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mChurchDatabase.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        LogUtils.d(TAG,"# database insert content --> "+contentValues.toString());

        switch (match){
            case ROUTE_ANNOUNCEMENT:{
                db.insertOrThrow(Contract.Announcements.TABLE_NAME,null,contentValues);
                notifyChange(uri);
                return Contract.Announcements.
                        buildAnnouncementUri(contentValues.getAsString(Contract.Announcements.ENTRY_ID));
            }
            case ROUTE_BLOG:{
                db.insertOrThrow(Contract.Blog.TABLE_NAME,null,contentValues);
                notifyChange(uri);
                return Contract.Blog.
                        buildUri(contentValues.getAsString(Contract.Blog.ENTRY_ID));
            }
            case ROUTE_SERMON:{
                db.insertOrThrow(Contract.Sermon.TABLE_NAME,null,contentValues);
                notifyChange(uri);
                return Contract.Sermon.
                        buildSermontUri(contentValues.getAsString(Contract.Sermon.ENTRY_ID));
            }
            case ROUTE_VERSE:{
                db.insertOrThrow(Contract.DailyVerse.TABLE_NAME,null,contentValues);
                notifyChange(uri);
                return Contract.DailyVerse.buildVerseUri(contentValues.getAsString(Contract.DailyVerse.ENTRY_ID));
            }
            case ROUTE_REQUEST:{
                db.insertOrThrow(Contract.Requests.TABLE_NAME,null,contentValues);
                notifyChange(uri);
                return Contract.Requests.buildRequestUri(contentValues.getAsString(Contract.Requests.ENTRY_ID));
            }
            case ROUTE_QUARTERLY:{
                db.insertOrThrow(Contract.Quarterly.TABLE_NAME,null,contentValues);
                //notifyChange(uri);
                return Contract.Quarterly.buildQuaterlyUri(contentValues.getAsString(Contract.Quarterly.ENTRY_ID));
            }
            case ROUTE_LESSON:{
                db.insertOrThrow(Contract.Lesson.TABLE_NAME,null,contentValues);
                //notifyChange(uri);
                return Contract.Lesson.buildLessonUri(contentValues.getAsString(Contract.Lesson.ENTRY_ID));
            }
            case ROUTE_READ:{
                db.insertOrThrow(Contract.Read.TABLE_NAME,null,contentValues);
                //notifyChange(uri);
                return Contract.Read.buildReadUri(contentValues.getAsString(Contract.Read.ENTRY_ID));
            }
            case ROUTE_READ_VERSE:{
                db.insertOrThrow(Contract.ReadVerses.TABLE_NAME,null,contentValues);
               // notifyChange(uri);
                return Contract.ReadVerses.buildReadVersesUri(contentValues.getAsString(Contract.ReadVerses.ENTRY_ID));
            }
            case ROUTE_DAY:{
                db.insertOrThrow(Contract.Day.TABLE_NAME,null,contentValues);
                //notifyChange(uri);
                return Contract.Day.buildDayUri(contentValues.getAsString(Contract.Day.ENTRY_ID));
            }
            case ROUTE_FAVOURITE_READ:{
                db.insertOrThrow(Contract.FavouriteRead.TABLE_NAME,null,contentValues);
               // notifyChange(uri);
                return Contract.FavouriteRead.buildFavouriteReadingsUri(contentValues.getAsString(Contract.FavouriteRead.ENTRY_ID));
            }

            default:{
                throw  new UnsupportedOperationException("Unkown uri : "+uri);
            }
        }
    }

    // we wont really implement the delete method for sabbath school....
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        LogUtils.v(TAG, "delete(uri=" + uri + ", selection=" + selection +" selectionArgs = "+selectionArgs+ ")");
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mChurchDatabase.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count = 0;
        String id = uri.getLastPathSegment();
        switch (match){
            case ROUTE_ANNOUNCEMENT:{
                count = builder.table(Contract.Announcements.TABLE_NAME)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_ANNOUNCEMENT_ID:{

                count = builder.table(Contract.Announcements.TABLE_NAME)
                        .where(Contract.Announcements._ID+"=?",id)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_SERMON:{
                count = builder.table(Contract.Sermon.TABLE_NAME)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_SERMON_ID:{

                count = builder.table(Contract.Sermon.TABLE_NAME)
                        .where(Contract.Sermon._ID+"=?",id)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_REQUEST:{
                count = builder.table(Contract.Requests.TABLE_NAME)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_REQUEST_ID:{
                count = builder.table(Contract.Requests.TABLE_NAME)
                        .where(Contract.Requests._ID+"=?",id)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_VERSE:{
                count = builder.table(Contract.DailyVerse.TABLE_NAME)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_VERSE_ID:{
                count = builder.table(Contract.DailyVerse.TABLE_NAME)
                        .where(Contract.DailyVerse._ID+"=?",id)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_QUARTERLY:{
                count = builder.table(Contract.Quarterly.TABLE_NAME)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_QUARTERLY_ID:{
                count = builder.table(Contract.Quarterly.TABLE_NAME)
                        .where(Contract.Quarterly.QUARTERLY_ID+"=?",id)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_DAY:{
                count = builder.table(Contract.Day.TABLE_NAME)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_DAY_ID:{
                count = builder.table(Contract.Day.TABLE_NAME)
                        .where(Contract.Day.ID+"=?",id)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_READ:{
                count = builder.table(Contract.Read.TABLE_NAME)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_READ_ID:{
                count = builder.table(Contract.Read.TABLE_NAME)
                        .where(Contract.Read.ID+"=?",id)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_LESSON:{
                count = builder.table(Contract.Lesson.TABLE_NAME)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_LESSON_ID:{
                count = builder.table(Contract.Lesson.TABLE_NAME)
                        .where(Contract.Lesson.ID+"=?",id)
                        .where(selection,selectionArgs).delete(db);
                break;
            }

            case ROUTE_READ_VERSE:{
                count = builder.table(Contract.ReadVerses.TABLE_NAME)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_READ_VERSE_ID:{
                count = builder.table(Contract.ReadVerses.TABLE_NAME)
                        .where(Contract.ReadVerses.ID+"=?",id)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_BLOG:{
                count = builder.table(Contract.Blog.TABLE_NAME)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_BLOG_ID:{
                count = builder.table(Contract.Blog.TABLE_NAME)
                        .where(Contract.Blog.ID+"=?",id)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_FAVOURITE_READ:{
                count = builder.table(Contract.FavouriteRead.TABLE_NAME)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            case ROUTE_FAVOURITE_READ_ID:{
                count = builder.table(Contract.FavouriteRead.TABLE_NAME)
                        .where(Contract.FavouriteRead.ENTRY_ID +"=?",id)
                        .where(selection,selectionArgs).delete(db);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unkown uri : "+uri);
        }

        notifyChange(uri);
        return count;
    }

    private void notifyChange(Uri uri){
        Context context = getContext();
        ContentResolver resolver = context.getContentResolver();
        resolver.notifyChange(uri,null,false);
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        LogUtils.v(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
        final SQLiteDatabase db = mChurchDatabase.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        SelectionBuilder builder = new SelectionBuilder();

        switch (match){
            case ROUTE_ANNOUNCEMENT_ID:{
                final String id = Contract.Announcements.getAnnouncementID(uri);
                builder.table(Contract.Announcements.TABLE_NAME)
                        .where(Contract.Announcements.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_REQUEST_ID:{
                final String id = Contract.Requests.getRequestID(uri);
                builder.table(Contract.Requests.TABLE_NAME)
                        .where(Contract.Requests.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_SERMON_ID:{
                final String id = Contract.Sermon.getSermonID(uri);
                builder.table(Contract.Sermon.TABLE_NAME)
                        .where(Contract.Sermon.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_SERMON:{
                builder.table(Contract.Sermon.TABLE_NAME)
                        .where(selection,selectionArgs);
                break;
            }
            case ROUTE_VERSE_ID:{
                final String id = Contract.DailyVerse.getVerseID(uri);
                builder.table(Contract.DailyVerse.TABLE_NAME)
                        .where(Contract.DailyVerse.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_QUARTERLY_ID:{
                final String id = Contract.Quarterly.getQuaterlyID(uri);
                builder.table(Contract.Quarterly.TABLE_NAME)
                        .where(Contract.Quarterly.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_LESSON_ID:{
                final String id = Contract.Lesson.getLessonID(uri);
                builder.table(Contract.Lesson.TABLE_NAME)
                        .where(Contract.Lesson.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_READ_ID:{
                final String id = Contract.Read.getReadID(uri);
                builder.table(Contract.Read.TABLE_NAME)
                        .where(Contract.Read.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_READ:{
                builder.table(Contract.Read.TABLE_NAME)
                        .where(selection,selectionArgs);
                break;
            }
            case ROUTE_READ_VERSE_ID:{
                final String id = Contract.ReadVerses.getReadVersesID(uri);
                builder.table(Contract.ReadVerses.TABLE_NAME)
                        .where(Contract.ReadVerses.ENTRY_ID +"=?",id);
                break;
            }
            case ROUTE_READ_VERSE:{
                builder.table(Contract.ReadVerses.TABLE_NAME)
                        .where(selection,selectionArgs);
            }
            case ROUTE_DAY_ID:{
                final String id = Contract.Day.geDayID(uri);
                builder.table(Contract.Day.TABLE_NAME)
                        .where(Contract.Day.ENTRY_ID +"=?",id);
            }
            case ROUTE_FAVOURITE_READ_ID:{
                final String id = Contract.FavouriteRead.getFavouriteReadingID(uri);
                builder.table(Contract.FavouriteRead.TABLE_NAME)
                        .where(Contract.FavouriteRead.ENTRY_ID +"=?",id);
            }


        }
        int retVal = builder.where(selection,selectionArgs).update(db,values);
        notifyChange(uri);

        return retVal;
    }


    /**
     * local data cache.
     */
    static class ChurchDatabase extends SQLiteOpenHelper{
        public static final int DATABASE_VERSION = 1;

        private static final String TYPE_TEXT = " TEXT";
        private static final String TYPE_INTEGER_NOT_NULL = " INTEGER NOT NULL";
        private static final String TYPE_TEXT_NOT_NULL = " TEXT NOT NULL";
        private static final String TYPE_INTEGER = " INTEGER";
        private static final String COMMA_SEP = ",";
        private static final String INTEGER_DEFAULT = " INTEGER DEFAULT 0";

        private static final String SQL_CREATE_QUARTERLY_TABLE="CREATE TABLE "
                + Contract.Quarterly.TABLE_NAME+" ("
                + Contract.Quarterly._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Contract.Quarterly.ENTRY_ID + TYPE_TEXT_NOT_NULL + COMMA_SEP
                + Contract.Quarterly.TITLE + TYPE_TEXT+COMMA_SEP
                + Contract.Quarterly.DESCRIPTION+TYPE_TEXT+COMMA_SEP
                + Contract.Quarterly.HUMAN_DATE+TYPE_TEXT+COMMA_SEP
                + Contract.Quarterly.START_DATE+TYPE_TEXT+COMMA_SEP
                + Contract.Quarterly.END_DATE+TYPE_TEXT+COMMA_SEP
                + Contract.Quarterly.PRIMARY_COLOR+TYPE_TEXT+COMMA_SEP
                + Contract.Quarterly.SECONDARY_COLOR+TYPE_TEXT+COMMA_SEP
                + Contract.Quarterly.LANG+TYPE_TEXT+COMMA_SEP
                + Contract.Quarterly.ID+TYPE_TEXT+COMMA_SEP
                + Contract.Quarterly.INDEX+TYPE_TEXT+COMMA_SEP
                + Contract.Quarterly.PATH+TYPE_TEXT+COMMA_SEP
                + Contract.Quarterly.FULL_PATH+TYPE_TEXT+COMMA_SEP
                + Contract.Quarterly.COVER_PATH+TYPE_TEXT+","+
                "UNIQUE ("+Contract.Quarterly.ID+")  ON CONFLICT IGNORE" + ")";

        private static final String SQL_CREATE_LESSON_TABLE="CREATE TABLE "
                + Contract.Lesson.TABLE_NAME+" ("
                + Contract.Lesson._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Contract.Lesson.ENTRY_ID + TYPE_TEXT_NOT_NULL+COMMA_SEP
                + Contract.Lesson.TITLE + TYPE_TEXT+COMMA_SEP
                + Contract.Lesson.ID+TYPE_TEXT+COMMA_SEP
                + Contract.Lesson.QUARTERLY_ID + TYPE_TEXT+COMMA_SEP
                + Contract.Lesson.START_DATE+TYPE_TEXT+COMMA_SEP
                + Contract.Lesson.END_DATE+TYPE_TEXT+COMMA_SEP
                + Contract.Lesson.INDEX+TYPE_TEXT+COMMA_SEP
                + Contract.Lesson.PATH+TYPE_TEXT+COMMA_SEP
                + Contract.Lesson.FULL_PATH+TYPE_TEXT+COMMA_SEP
                + Contract.Lesson.COVER_PATH+TYPE_TEXT+")";

        private static final String SQL_CREATE_READ_TABLE="CREATE TABLE "
                + Contract.Read.TABLE_NAME+" ("
                + Contract.Read._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Contract.Read.ENTRY_ID + TYPE_TEXT_NOT_NULL + COMMA_SEP
                + Contract.Read.ID+TYPE_TEXT+COMMA_SEP
                + Contract.Read.DAY_ID+TYPE_TEXT+COMMA_SEP
                + Contract.Read.LESSON_ID+TYPE_TEXT+COMMA_SEP
                + Contract.Read.QUARTERLY_ID+COMMA_SEP
                + Contract.Read.DATE+TYPE_TEXT+COMMA_SEP
                + Contract.Read.INDEX+TYPE_TEXT+COMMA_SEP
                + Contract.Read.TITLE+TYPE_TEXT+COMMA_SEP
                + Contract.Read.CONTENT+TYPE_TEXT+")";

        private static final String SQL_CREATE_BLOG_TABLE="CREATE TABLE "
                + Contract.Blog.TABLE_NAME+" ("
                + Contract.Blog._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Contract.Blog.ENTRY_ID + TYPE_TEXT_NOT_NULL + COMMA_SEP
                + Contract.Blog.ID+TYPE_TEXT+COMMA_SEP
                + Contract.Blog.TITLE+TYPE_TEXT+COMMA_SEP
                + Contract.Blog.URL+TYPE_TEXT+COMMA_SEP
                + Contract.Blog.CONTENT+TYPE_TEXT+COMMA_SEP
                + Contract.Blog.AUTHOR+TYPE_TEXT+COMMA_SEP
                + Contract.Blog.DATE+TYPE_TEXT+COMMA_SEP
                + Contract.Blog.EXCERPT+TYPE_TEXT+")";

        private static final String SQL_CREATE_READVERSES_TABLE="CREATE TABLE "
                + Contract.ReadVerses.TABLE_NAME+" ("
                + Contract.ReadVerses._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Contract.ReadVerses.ENTRY_ID + TYPE_TEXT_NOT_NULL + COMMA_SEP
                + Contract.ReadVerses.ID+TYPE_TEXT+COMMA_SEP
                + Contract.ReadVerses.READ_ID+TYPE_TEXT+COMMA_SEP
                + Contract.ReadVerses.LESSON_ID+TYPE_TEXT+COMMA_SEP
                + Contract.ReadVerses.QUARTERLY_ID+COMMA_SEP
                + Contract.ReadVerses.BIBLE_NAME+TYPE_TEXT+COMMA_SEP
                + Contract.ReadVerses.BIBLE_VERSES+TYPE_TEXT+")";

        private static final String SQL_CREATE_DAY_TABLE="CREATE TABLE "
                + Contract.Day.TABLE_NAME+" ("
                + Contract.Day._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Contract.Day.ENTRY_ID + TYPE_TEXT_NOT_NULL + COMMA_SEP
                + Contract.Day.TITLE + TYPE_TEXT+COMMA_SEP
                + Contract.Day.ID+TYPE_TEXT+COMMA_SEP
                + Contract.Day.QUARTERLY_ID+COMMA_SEP
                + Contract.Day.LESSON_ID+ TYPE_TEXT+COMMA_SEP
                + Contract.Day.DATE+TYPE_TEXT+COMMA_SEP
                + Contract.Day.INDEX+TYPE_TEXT+COMMA_SEP
                + Contract.Day.PATH+TYPE_TEXT+COMMA_SEP
                + Contract.Day.FULL_PATH+TYPE_TEXT+COMMA_SEP
                + Contract.Day.READ_PATH+TYPE_TEXT+COMMA_SEP
                + Contract.Day.FULL_READ_PATH+TYPE_TEXT+ ")";

        private static final String SQL_CREATE_FAVOURITE_READ_TABLE="CREATE TABLE "
                + Contract.FavouriteRead.TABLE_NAME+" ("
                + Contract.FavouriteRead._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Contract.FavouriteRead.ENTRY_ID + TYPE_TEXT_NOT_NULL + COMMA_SEP
                + Contract.FavouriteRead.QUARTERLY_ID+TYPE_TEXT+COMMA_SEP
                + Contract.FavouriteRead.LESSON_ID+ TYPE_TEXT+COMMA_SEP
                + Contract.FavouriteRead.DAY_ID+TYPE_TEXT+COMMA_SEP
                + Contract.FavouriteRead.DATE+TYPE_TEXT+COMMA_SEP
                + Contract.FavouriteRead.LESSON_COVER_PATH+TYPE_TEXT+COMMA_SEP
                + Contract.FavouriteRead.LESSON_TITLE+TYPE_TEXT+COMMA_SEP
                + Contract.FavouriteRead.TITLE+TYPE_TEXT+COMMA_SEP
                + Contract.FavouriteRead.CONTENT+TYPE_TEXT+COMMA_SEP
                + Contract.FavouriteRead.REFERENCE+TYPE_TEXT+COMMA_SEP
                + Contract.FavouriteRead.FULL_READ_PATH+TYPE_TEXT+")";


        private static final String SQL_CREATE_SERMON_TABLE="CREATE TABLE "
                + Contract.Sermon.TABLE_NAME+" ("
                + Contract.Sermon._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Contract.Sermon.ENTRY_ID + TYPE_TEXT_NOT_NULL + COMMA_SEP
                + Contract.Sermon.ID+TYPE_TEXT+COMMA_SEP
                + Contract.Sermon.KIND+TYPE_TEXT+COMMA_SEP
                + Contract.Sermon.CHANNEL_ID+TYPE_TEXT+COMMA_SEP
                + Contract.Sermon.PUBLISHED_AT+TYPE_TEXT+COMMA_SEP
                + Contract.Sermon.TITLE+TYPE_TEXT+COMMA_SEP
                + Contract.Sermon.FAVOURITE+TYPE_INTEGER+COMMA_SEP
                + Contract.Sermon.DEFAULT_THUMBNAIL_URL+TYPE_TEXT+COMMA_SEP
                + Contract.Sermon.MEDIUM_THUMBNAIL_URL+TYPE_TEXT+","+
                "UNIQUE ("+Contract.Sermon.ID+")  ON CONFLICT IGNORE" + ")";


        private static final String SQL_CREATE_ANNOUNCEMENTS_TABLE="CREATE TABLE "
                + Contract.Announcements.TABLE_NAME+" ("
                + Contract.Announcements._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Contract.Announcements.ENTRY_ID + TYPE_TEXT_NOT_NULL + COMMA_SEP
                + Contract.Announcements.TIME + TYPE_INTEGER+COMMA_SEP
                + Contract.Announcements.TITLE+TYPE_TEXT+COMMA_SEP
                + Contract.Announcements.MESSAGE+TYPE_TEXT+COMMA_SEP
                + Contract.Announcements.ORIGIN+TYPE_TEXT+")";

        private static final String SQL_CREATE_VERSE_TABLE="CREATE TABLE "
                + Contract.DailyVerse.TABLE_NAME+" ("
                + Contract.DailyVerse._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Contract.DailyVerse.ENTRY_ID + TYPE_TEXT_NOT_NULL + COMMA_SEP
                + Contract.DailyVerse.TIME +TYPE_INTEGER+COMMA_SEP
                + Contract.DailyVerse.VERSION+TYPE_TEXT+COMMA_SEP
                + Contract.DailyVerse.REFERENCE+ TYPE_TEXT+COMMA_SEP
                + Contract.DailyVerse.MESSAGE + TYPE_TEXT_NOT_NULL+")";

//        private static final String SQL_CREATE_REQUEST_TABLE="CREATE"


        private static final String SQL_DELETE_ANNOUNCEMENTS_TABLE = "DROP TABLE IF EXISTS "
                + Contract.Announcements.TABLE_NAME;
        private static final String SQL_DELETE_VERSE_TABLE = "DROP TABLE IF EXISTS"
                + Contract.DailyVerse.TABLE_NAME;
        private static final String SQL_DELETE_REQUESTS_TABLE = "DROP TABLE IF EXISTS"
                + Contract.Requests.TABLE_NAME;
        private static final String SQL_DELETE_SERMON_TABLE = "DROP TABLE IF EXISTS"
                + Contract.Sermon.TABLE_NAME;

        //sabbath school
        private static final String SQL_DELETE_LESSON_TABLE = "DROP TABLE IF EXISTS"
                + Contract.Lesson.TABLE_NAME;
        private static final String SQL_DELETE_DAY_TABLE = "DROP TABLE IF EXISTS"
                + Contract.Day.TABLE_NAME;
        private static final String SQL_DELETE_READ_TABLE = "DROP TABLE IF EXISTS"
                + Contract.Read.TABLE_NAME;
        private static final String SQL_DELETE_QUARTERLY_TABLE = "DROP TABLE IF EXISTS"
                + Contract.Quarterly.TABLE_NAME;
        private static final String SQL_DELETE_READVERSES_TABLE = "DROP TABLE IF EXISTS"
                + Contract.ReadVerses.TABLE_NAME;
        private static final String SQL_DELETE_FAVOURITE_READ_TABLE = "DROP TABLE IF EXISTS"
                + Contract.FavouriteRead.TABLE_NAME;
        public ChurchDatabase(Context context) {
            super(context,"ChurchDatabase",null,DATABASE_VERSION);
        }

        //TODO write statements for the creation of sermons and other
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ANNOUNCEMENTS_TABLE);
            db.execSQL(SQL_CREATE_VERSE_TABLE);
            db.execSQL(SQL_CREATE_SERMON_TABLE);
            //sabbath school
            db.execSQL(SQL_CREATE_LESSON_TABLE);
            db.execSQL(SQL_CREATE_DAY_TABLE);
            db.execSQL(SQL_CREATE_READ_TABLE);
            db.execSQL(SQL_CREATE_QUARTERLY_TABLE);
            db.execSQL(SQL_CREATE_READVERSES_TABLE);
            db.execSQL(SQL_CREATE_FAVOURITE_READ_TABLE);
            db.execSQL(SQL_CREATE_BLOG_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ANNOUNCEMENTS_TABLE);
            db.execSQL(SQL_DELETE_VERSE_TABLE);
            db.execSQL(SQL_DELETE_SERMON_TABLE);
            db.execSQL(SQL_DELETE_REQUESTS_TABLE);
            //sabbath school
            db.execSQL(SQL_DELETE_LESSON_TABLE);
            db.execSQL(SQL_DELETE_DAY_TABLE);
            db.execSQL(SQL_DELETE_READ_TABLE);
            db.execSQL(SQL_DELETE_QUARTERLY_TABLE);
            db.execSQL(SQL_DELETE_READVERSES_TABLE);
            db.execSQL(SQL_DELETE_FAVOURITE_READ_TABLE);
            onCreate(db);
        }
    }
}
