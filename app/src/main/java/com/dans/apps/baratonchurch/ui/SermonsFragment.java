package com.dans.apps.baratonchurch.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.models.SermonVideo;
import com.dans.apps.baratonchurch.network.Fetcher;
import com.dans.apps.baratonchurch.provider.Contract;
import com.dans.apps.baratonchurch.utils.UiUtils;

import java.util.ArrayList;

/**
 * shows a video list of sermons from baraton university church application
 */
public class SermonsFragment extends Fragment
        implements SearchView.OnQueryTextListener,LoaderManager.LoaderCallbacks<Cursor>{
    String TAG = "SermonsFragment";
    private RecyclerView list;
    private ProgressBar progressBar;
    private TextView noVideos;

    LinearLayoutManager layoutManager;
    VideoAdapter videoAdapter;
    String currentFilter;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sermons, container, false);
        list  = rootView.findViewById(R.id.videos);
        progressBar = rootView.findViewById(R.id.progress_bar);
        noVideos = rootView.findViewById(R.id.no_videos);
        noVideos.setVisibility(View.GONE);
        videoAdapter = new VideoAdapter(getActivity());

        layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        list.setAdapter(videoAdapter);
        list.setLayoutManager(layoutManager);
        list.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        new SermonFetcher(getActivity()).execute(false);
        setHasOptionsMenu(true);
        swipeRefreshLayout = rootView.findViewById(R.id.refresh_sermons);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new SermonFetcher(getActivity()).execute(true);
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.sermons_fragment_menu,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
            searchView.setQueryHint(getResources().
                    getString(R.string.search_videos));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader loader;
        if(currentFilter!=null){
            loader= new CursorLoader(getActivity(), Contract.Sermon.CONTENT_URI,querySermonProperties.PROJECTION,
                    Contract.Sermon.TITLE+" LIKE ?",new String[]{"%"+currentFilter+"%"},null);
        }else{
            loader= new CursorLoader(getActivity(), Contract.Sermon.CONTENT_URI,querySermonProperties.PROJECTION,
                    null,null,null);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        videoAdapter.removeContents();
        swipeRefreshLayout.setRefreshing(false);
        if(cursor!=null && cursor.moveToFirst()){
            ArrayList<SermonVideo>videos = new ArrayList<>();
            noVideos.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            do{
                String id = cursor.getString(querySermonProperties.ID);
                //todo in the future enable the adapter to show audio too
                String kind = cursor.getString(querySermonProperties.KIND);
                String channelID = cursor.getString(querySermonProperties.CHANNEL_ID);
                String publishedAt = cursor.getString(querySermonProperties.PUBLISHED_AT);
                String title = cursor.getString(querySermonProperties.TITLE);
                String url = cursor.getString(querySermonProperties.DEFAULT_THUMBNAIL_URL);
                int isFavourite = cursor.getInt(querySermonProperties.ISFAVOURITE);
                SermonVideo video = new SermonVideo(id,channelID,publishedAt,title,url,null,isFavourite==1);
                videos.add(video);
            }while (cursor.moveToNext());
            videoAdapter.addVideos(videos);
        }else{
            progressBar.setVisibility(View.GONE);
            boolean isConnected = UiUtils.isOnline(getActivity().getApplicationContext());
            if(isConnected){
                noVideos.setText(R.string.no_videos);
            }else{
                noVideos.setText(R.string.no_connection);
            }
            noVideos.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    @Override
    public boolean onQueryTextChange(String newText) {
        String newFilter = !TextUtils.isEmpty(newText)?newText:null;
        if (currentFilter == null && newFilter == null) {
            return false;
        }
        if (currentFilter != null && currentFilter.equals(newFilter)) {
            return false;
        }
        currentFilter = newFilter;
        getLoaderManager().restartLoader(0,null,this);
        return true;
    }

    interface querySermonProperties{
        String [] PROJECTION ={
                Contract.Sermon.ENTRY_ID,
                Contract.Sermon.ID,
                Contract.Sermon.KIND,
                Contract.Sermon.CHANNEL_ID,
                Contract.Sermon.PUBLISHED_AT,
                Contract.Sermon.TITLE,
                Contract.Sermon.FAVOURITE,
                Contract.Sermon.DEFAULT_THUMBNAIL_URL
        };

        int ID =1;
        int KIND = 2;
        int CHANNEL_ID = 3;
        int PUBLISHED_AT = 4;
        int TITLE = 5;
        int ISFAVOURITE = 6;
        int DEFAULT_THUMBNAIL_URL = 7;
    }

    public class SermonFetcher extends AsyncTask<Boolean,Void,Boolean>{
        Context context;
        public SermonFetcher(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            boolean forceRefresh = booleans[0];
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
            progressBar.setVisibility(View.VISIBLE);
            noVideos.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(isAdded() &&  !isDetached()) {
                restartLoader();
            }
        }
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(0,null,this);
    }

    public void ondeviceConnected() {
        if(isAdded()&& !isDetached()){
            if(videoAdapter.getItemCount() == 0){
                new SermonFetcher(getActivity()).execute(false);
            }
        }
    }

}
