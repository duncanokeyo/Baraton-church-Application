package com.dans.apps.baratonchurch.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.YoutubeAcitivity;
import com.dans.apps.baratonchurch.models.Favourites;
import com.dans.apps.baratonchurch.network.Fetcher;
import com.dans.apps.baratonchurch.provider.Contract;
import com.dans.apps.baratonchurch.utils.DateUtils;
import com.dans.apps.baratonchurch.utils.LogUtils;
import com.dans.apps.baratonchurch.utils.UiUtils;
import com.dans.apps.baratonchurch.BlogReadActivity;
import com.dans.apps.baratonchurch.widget.ExpandableTextView;
import com.dans.apps.baratonchurch.widget.ReadView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.view.View.GONE;

public class HomeFragment extends Fragment
        implements View.OnClickListener,LoaderManager.LoaderCallbacks<Cursor>{
    String TAG = "HomeFragment";
    ExpandableTextView dailyVerseMessage;
    TextView dailyVerseReference;
    ImageView shareDailyVerse;
    ProgressBar fetchDailyVerseProgress;
    TextView fetchDailyVerseError;
    //////
    Button moreSermons;
    ImageView latestSermonCover;
    ImageView latestSermonPlay;
    ProgressBar latestSermonFetchProgress;
    TextView latestSermonFetchError;
    TextView latestSermonTitle;
    //////
    RecyclerView favouritesList;
    ProgressBar favouritesListFetchProgress;
    TextView noFavourites;
    /////
    FavouritesAdapter adapter;
    LinearLayoutManager layoutManager;
    CardView lastestVideoCard;
    CardView dailyVerseCard;
    LinearLayout dailyVerseLinearLayout;
    LinearLayout latestSermonLinearLayout;

    CardView blogCard;
    ProgressBar fetchBlogProgressBar;
    LinearLayout blogLinearLayout;
    RelativeLayout blogContent;
    Button moreBlogs;
    TextView blogTitle;
    ImageView blogImage;
    TextView blogAuthor;
    TextView blogDate;
    ReadView blogContentRead;
    Button readblog;
    String currentBlogID;
    int [] RELOAD_CARD_NUMBERS = {-1,-1};
    RequestOptions options = new RequestOptions();

    int userType;

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public interface  CallBack {
        void onMoreVideosClicked();
        void onMoreBlogsClicked();
    }

    CallBack sDummyCallbacks = new CallBack() {
        @Override
        public void onMoreVideosClicked() {}

        @Override
        public void onMoreBlogsClicked() {

        }
    };

    private CallBack callbacks = sDummyCallbacks;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        dailyVerseMessage= rootView.findViewById(R.id.daily_verse_message);
        dailyVerseReference= rootView.findViewById(R.id.bible_reference);
        shareDailyVerse=rootView.findViewById(R.id.share_verse);
        fetchDailyVerseProgress=rootView.findViewById(R.id.fetch_verse_progress);
        fetchDailyVerseError=rootView.findViewById(R.id.fetch_verse_error);
        dailyVerseCard=rootView.findViewById(R.id.daily_verse_card);
        //////
        moreSermons=rootView.findViewById(R.id.more);
        latestSermonCover=rootView.findViewById(R.id.sermon_cover);
        latestSermonPlay=rootView.findViewById(R.id.play_sermon);
        latestSermonFetchProgress=rootView.findViewById(R.id.latest_sermon_fetch_progress);
        latestSermonFetchError=rootView.findViewById(R.id.error_fetching_sermon);
        latestSermonTitle=rootView.findViewById(R.id.sermon_title);
        lastestVideoCard = rootView.findViewById(R.id.latest_sermon_card);
        //////
        //blog//
        blogCard = rootView.findViewById(R.id.blog_card);
        fetchBlogProgressBar=rootView.findViewById(R.id.fetch_blog_progress);
        blogLinearLayout=rootView.findViewById(R.id.blog_linearLayout);
        blogContent=rootView.findViewById(R.id.blog_card_content);
        moreBlogs=rootView.findViewById(R.id.more_blogs);
        blogTitle=rootView.findViewById(R.id.blog_content_title);
       // blogImage = rootView.findViewById(R.id.blog_image);
        blogAuthor=rootView.findViewById(R.id.blog_author);
        blogContentRead = rootView.findViewById(R.id.blog_extract);
        blogDate = rootView.findViewById(R.id.blog_date);
        readblog = rootView.findViewById(R.id.read_blog);
        ///////
        favouritesList=rootView.findViewById(R.id.favourite_reading_list);
        favouritesListFetchProgress=rootView.findViewById(R.id.fetch_favourites_progress);
        noFavourites =rootView.findViewById(R.id.no_favourites);
        dailyVerseLinearLayout =rootView.findViewById(R.id.daily_verse_linearLayout);
        latestSermonLinearLayout = rootView.findViewById(R.id.latest_sermon_linearlayout);
        adapter = new FavouritesAdapter(getActivity());
        layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        favouritesList.setLayoutManager(layoutManager);
        favouritesList.setHasFixedSize(true);
        favouritesList.setNestedScrollingEnabled(false);
        favouritesList.setAdapter(adapter);

        setViewsInitialState();
        shareDailyVerse.setOnClickListener(this);
        moreSermons.setOnClickListener(this);
        readblog.setOnClickListener(this);
        dailyVerseCard.setOnClickListener(this);
        blogCard.setOnClickListener(this);
        moreBlogs.setOnClickListener(this);
        latestSermonPlay.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //ripples
            dailyVerseCard.setOnClickListener(this);
            lastestVideoCard.setOnClickListener(this);
        }

        options.centerCrop();

        new HomeSermonFetcher(getActivity().getApplicationContext()).execute(false);
        new DailyVerseFetcher(getActivity().getApplicationContext()).execute(false);
        new BlogFetcher(getActivity().getApplicationContext()).execute(false);

        setHasOptionsMenu(true);
        blogLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BlogFetcher(getActivity().getApplicationContext()).execute(false);
            }
        });
        latestSermonLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HomeSermonFetcher(getActivity().getApplicationContext()).execute(true);
            }
        });
        dailyVerseLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DailyVerseFetcher(getActivity().getApplicationContext()).execute(true);
            }
        });

        dailyVerseMessage.setInterpolator(new OvershootInterpolator());

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof CallBack)) {
            throw new ClassCastException(
                    "Activity must implement fragment's callbacks.");
        }
        callbacks = (CallBack) activity;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = sDummyCallbacks;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(QueryFavouritesProperties.id,
                null,this);
      //  getLoaderManager().initLoader(QueryDailyVerseProperties.id,
        //        null,this);
        //getLoaderManager().initLoader(QuerySermonProperties.id,
          //      null,this);
    }

    private void setViewsInitialState() {
        dailyVerseMessage.setText("");
        dailyVerseReference.setText("");
        latestSermonPlay.setVisibility(GONE);
        latestSermonTitle.setVisibility(View.GONE);
        noFavourites.setVisibility(GONE);

        blogLinearLayout.setVisibility(GONE);
        blogContent.setVisibility(GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.read_blog:
            case R.id.blog_card:{
                Intent intent = new Intent(getActivity(), BlogReadActivity.class);
                intent.putExtra(BlogReadActivity.KEY_BLOG_ID,currentBlogID);
                getActivity().startActivity(intent);
                break;
            }
            case R.id.more_blogs:{
                if(callbacks!=null){
                    callbacks.onMoreBlogsClicked();
                }
                break;
            }
            case R.id.play_sermon: {
                String videoId = (String) lastestVideoCard.getTag();
                if (videoId != null) {
                    LogUtils.d(TAG, "starting video activity" + videoId);
                    Intent intent = new Intent(getActivity(), YoutubeAcitivity.class);
                    intent.putExtra(YoutubeAcitivity.KEY_VIDEO_ID, videoId);
                    getActivity().startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), R.string.null_video_id, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.daily_verse_card:{
                dailyVerseMessage.toggle();
                break;
            }
            case R.id.more: {
                callbacks.onMoreVideosClicked();
                break;
            }
            case R.id.share_verse: {
                String message = dailyVerseMessage.getText().toString().trim();
                String bibleVerse = dailyVerseReference.getText().toString().trim();
                if (message != null) {
                    if(bibleVerse!=null){
                        message=message+"\n\n"+bibleVerse;
                    }
                    Intent txtIntent = new Intent(android.content.Intent.ACTION_SEND);
                    txtIntent.setType("text/plain");
                    txtIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Message");
                    txtIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                    getActivity().startActivity(Intent.createChooser(txtIntent, "Share"));
                }
                break;
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        CursorLoader loader = null;
        if(id == QueryFavouritesProperties.id) {
            loader=new CursorLoader(getActivity(),
                    Contract.FavouriteRead.CONTENT_URI, QueryFavouritesProperties.PROJECTION,
                    null, null, null);
        }else if(id == QueryDailyVerseProperties.id){
            loader=new CursorLoader(getActivity(),
                    Contract.DailyVerse.CONTENT_URI,QueryDailyVerseProperties.PROJECTION,null,null,null);
        }else if(id == QuerySermonProperties.id){
            loader=new CursorLoader(getActivity(),
                    Contract.Sermon.CONTENT_URI,QuerySermonProperties.PROJECTION,
                    null,null,null);
        }else if(id == QueryBlogProperties.id){
            loader = new CursorLoader(getActivity(),
                    Contract.Blog.CONTENT_URI,QueryBlogProperties.PROJECTION,
            null,null,null);
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int id = loader.getId();
        switch (id){
            case QueryBlogProperties.id:{
                fetchBlogProgressBar.setVisibility(GONE);
                blogLinearLayout.setVisibility(GONE);
                blogContent.setVisibility(View.VISIBLE);
                if(cursor!=null && cursor.moveToFirst() && cursor.getCount()>0){
                    String title = cursor.getString(QueryBlogProperties.TITLE);
                    String author = cursor.getString(QueryBlogProperties.AUTHOR);
                    currentBlogID = cursor.getString(QueryBlogProperties.ID);
                    String date = cursor.getString(QueryBlogProperties.DATE);
                    String extract = cursor.getString(QueryBlogProperties.EXCERPT);

                    blogTitle.setText(title);
                    blogDate.setText(date);
                    blogAuthor.setText(author);
                    blogContentRead.loadRead(extract);

                }else{
                    fetchBlogProgressBar.setVisibility(GONE);
                    blogLinearLayout.setVisibility(View.VISIBLE);
                    blogContent.setVisibility(GONE);
                }
                break;
            }
            case QueryFavouritesProperties.id:{
                if(cursor!=null && cursor.moveToFirst() && cursor.getCount()>0){
                    favouritesListFetchProgress.setVisibility(GONE);
                    noFavourites.setVisibility(View.GONE);
                    ArrayList<Favourites>favourites = new ArrayList<>();
                    do{
                        String entryID = cursor.getString(QueryFavouritesProperties.ENTRY_ID);
                        String date = cursor.getString(QueryFavouritesProperties.DATE);
                        String lessonCoverPath = cursor.getString(QueryFavouritesProperties.LESSON_COVER_PATH);
                        String lessonTitle = cursor.getString(QueryFavouritesProperties.LESSON_TITLE);
                        String title = cursor.getString(QueryFavouritesProperties.TITLE);
                        String content = cursor.getString(QueryFavouritesProperties.CONTENT);
                        String reference = cursor.getString(QueryFavouritesProperties.REFERENCE);

                        Favourites favourite =
                                new Favourites(entryID,date,lessonCoverPath,lessonTitle,title,content,reference);
                        favourites.add(favourite);
                    }while(cursor.moveToNext());
                    adapter.addFavourites(favourites);
                }else{
                    favouritesListFetchProgress.setVisibility(GONE);
                    noFavourites.setVisibility(View.VISIBLE);
                }
                break;
            }

            case QueryDailyVerseProperties.id:{
                if(cursor!=null && cursor.moveToFirst()){
                    RELOAD_CARD_NUMBERS[0]=-1;
                    //returned cursor must have only one row
                    String message = cursor.getString(QueryDailyVerseProperties.MESSAGE);
                    String bibleVersion = cursor.getString(QueryDailyVerseProperties.VERSION);
                    String reference = cursor.getString(QueryDailyVerseProperties.REFERENCE);

                    //check if todays date is greater that saved date
                    String time =cursor.getString(QueryDailyVerseProperties.TIME);
                    String today= new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                    if(DateUtils.compareDates(today,time) == 0){
                        LogUtils.d(TAG,"dates are the same");
                        dailyVerseLinearLayout.setVisibility(GONE);
                        fetchDailyVerseProgress.setVisibility(GONE);
                        dailyVerseReference.setText(reference+"-"+bibleVersion);
                        dailyVerseMessage.setText(message);
                    }else{
                        new DailyVerseFetcher(getActivity().getApplicationContext()).execute(true);
                    }
                }else{
                    //let the user perform the refresh
                    dailyVerseLinearLayout.setVisibility(View.VISIBLE);
                    boolean isConnected  = UiUtils.isOnline(getActivity().getApplicationContext());
                    if(!isConnected){
                        fetchDailyVerseError.setText(R.string.no_connection);
                    }else{
                        fetchDailyVerseError.setText(R.string.fetch_verse_error);
                    }
                    fetchDailyVerseProgress.setVisibility(GONE);
                    dailyVerseReference.setText("");
                    dailyVerseMessage.setText("");
                    RELOAD_CARD_NUMBERS[0]=1;
                }
                break;
            }

            case QuerySermonProperties.id:{
                if(cursor!=null && cursor.moveToFirst()){
                    RELOAD_CARD_NUMBERS[1]=-1;
                    LogUtils.d(TAG,"returned cursor size is "+cursor.getCount());
                    latestSermonFetchProgress.setVisibility(GONE);
                    latestSermonLinearLayout.setVisibility(GONE);
                    latestSermonTitle.setVisibility(View.VISIBLE);
                    latestSermonPlay.setVisibility(View.VISIBLE);

                    String title = cursor.getString(QuerySermonProperties.TITLE);
                    String defaultUrl = cursor.getString(QuerySermonProperties.DEFAULT_THUMBNAIL_URL);
                    String mediumUrl = cursor.getString(QuerySermonProperties.MEDIUM_THUMBNAIL_URL);
                    String date = cursor.getString(QuerySermonProperties.PUBLISHED_AT);
                    String mediaID = cursor.getString(QuerySermonProperties.ID);

                    latestSermonTitle.setText(title);
                    lastestVideoCard.setTag(mediaID);
                    String posterUrl=mediumUrl!=null?mediumUrl:defaultUrl;

                    Glide.with(getActivity()).load(posterUrl).apply(options).
                            into(latestSermonCover);
                    break;
                }else{ // no content in the database
                    latestSermonFetchProgress.setVisibility(GONE);
                    latestSermonLinearLayout.setVisibility(View.VISIBLE);
                    boolean isConnected  = UiUtils.isOnline(getActivity().getApplicationContext());
                    if(!isConnected){
                        latestSermonFetchError.setText(R.string.no_connection);
                    }else{
                        latestSermonFetchError.setText(R.string.latest_sermon_fetch_error);
                    }
                    latestSermonTitle.setVisibility(View.GONE);
                    latestSermonPlay.setVisibility(View.GONE);
                    RELOAD_CARD_NUMBERS[1]=1;
                }
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    interface QueryDailyVerseProperties{
        int id =1;
        String [] PROJECTION ={
                Contract.DailyVerse.TIME,
                Contract.DailyVerse.VERSION,
                Contract.DailyVerse.REFERENCE,
                Contract.DailyVerse.MESSAGE
        };
        int TIME = 0;
        int VERSION =1;
        int REFERENCE = 2;
        int MESSAGE= 3;
    }

    interface QueryFavouritesProperties {
        int id = 2;
        String[]PROJECTION={
                Contract.FavouriteRead.ENTRY_ID,
                Contract.FavouriteRead.DATE,
                Contract.FavouriteRead.LESSON_COVER_PATH,
                Contract.FavouriteRead.LESSON_TITLE,
                Contract.FavouriteRead.TITLE,
                Contract.FavouriteRead.CONTENT,
                Contract.FavouriteRead.REFERENCE
        };

        int ENTRY_ID =0;
        int DATE = 1;
        int LESSON_COVER_PATH =2;
        int LESSON_TITLE = 3;
        int TITLE = 4;
        int CONTENT = 5;
        int REFERENCE = 6;
    }

    interface QuerySermonProperties{
        int id = 3;
        String [] PROJECTION ={
                Contract.Sermon.ENTRY_ID,
                Contract.Sermon.ID,
                Contract.Sermon.KIND,
                Contract.Sermon.CHANNEL_ID,
                Contract.Sermon.PUBLISHED_AT,
                Contract.Sermon.TITLE,
                Contract.Sermon.DEFAULT_THUMBNAIL_URL,
                Contract.Sermon.MEDIUM_THUMBNAIL_URL
        };
        int ID =1;
        int KIND = 2;
        int CHANNEL_ID = 3;
        int PUBLISHED_AT = 4;
        int TITLE = 5;
        int DEFAULT_THUMBNAIL_URL = 6;
        int MEDIUM_THUMBNAIL_URL = 7;
    }

    interface QueryBlogProperties {
        int id = 4;
        String[] PROJECTION = {
                Contract.Blog.ID,
                Contract.Blog.TITLE,
                Contract.Blog.AUTHOR,
                Contract.Blog.DATE,
                Contract.Blog.EXCERPT
        };
        int ID = 0;
        int TITLE = 1;
        int AUTHOR = 2;
        int DATE = 3;
        int EXCERPT = 4;
    }

    public class DailyVerseFetcher extends AsyncTask<Boolean,Void,Boolean>{
        Context context;
        public DailyVerseFetcher(Context context) {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dailyVerseLinearLayout.setVisibility(GONE);
            fetchDailyVerseProgress.setVisibility(View.VISIBLE);
            dailyVerseMessage.setText("");
            dailyVerseReference.setText("");
        }

        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            boolean forceRefresh = booleans[0];
            try {
                ContentResolver resolver = context.getContentResolver();
                Cursor cursor = resolver.query(Contract.DailyVerse.CONTENT_URI,
                        new String[]{Contract.DailyVerse.ENTRY_ID},null,null,null);
                if(cursor.getCount()>0){
                    LogUtils.d(TAG,"There is content in the database");
                    if(forceRefresh){
                        Fetcher.checkCurrentBibleText(context);
                    }
                }else{
                    LogUtils.d(TAG,"no content in the database");
                    Fetcher.checkCurrentBibleText(context);
                }
                return true;
            }catch(Exception e){
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(isAdded() && !isDetached()) {
                restartDailyVerseLoader();
            }
        }
    }

    public class BlogFetcher extends AsyncTask<Boolean,Void,Boolean>{
        Context context;
        public BlogFetcher(Context context){
            this.context = context;
        }
        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            try{
                Fetcher.updateBlogTable(context);
                return true;
            }catch (Exception e){
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            fetchBlogProgressBar.setVisibility(View.VISIBLE);
            blogLinearLayout.setVisibility(GONE);
            blogContent.setVisibility(GONE);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(isAdded() && !isDetached()){
                LogUtils.d(TAG,"on post execute called ----> "+aBoolean);
                restartBlogLoader();
            }
        }
    }

    public class HomeSermonFetcher extends AsyncTask<Boolean,Void,Boolean>{
        Context context;
        public HomeSermonFetcher(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            boolean forceRefresh = booleans[0];
            LogUtils.d(TAG,"(HomeSermonFetcher) is force refresh enabled "+forceRefresh);
            try {
                Fetcher.checkSermons(context);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            latestSermonFetchProgress.setVisibility(View.VISIBLE);
            latestSermonLinearLayout.setVisibility(GONE);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(isAdded() &&  !isDetached()) {
                restartSermonLoader();
            }
        }
    }

    /////we have not registered init loader here
    public void restartSermonLoader(){
        getLoaderManager().restartLoader(QuerySermonProperties.id,null,this);
    }
    private void restartBlogLoader() {
        getLoaderManager().restartLoader(QueryBlogProperties.id,null,this);
    }
    public void restartDailyVerseLoader(){
        getLoaderManager().restartLoader(QueryDailyVerseProperties.id,null,this);
    }


    ///called by base activity when device goes online
    public void ondeviceConnected() {
        if(isAdded() && !isDetached()) {
            if (RELOAD_CARD_NUMBERS[0] == 1) {
                new DailyVerseFetcher(getActivity()).execute(true);
            }
            if (RELOAD_CARD_NUMBERS[1] == 1) {
                new HomeSermonFetcher(getActivity()).execute(true);
            }
        }
    }

}