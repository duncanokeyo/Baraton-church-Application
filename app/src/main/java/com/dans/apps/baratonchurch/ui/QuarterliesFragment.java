package com.dans.apps.baratonchurch.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dans.apps.baratonchurch.Constants;
import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.SettingsActivity;
import com.dans.apps.baratonchurch.models.QuarterlyItem;
import com.dans.apps.baratonchurch.network.Fetcher;
import com.dans.apps.baratonchurch.provider.Contract;
import com.dans.apps.baratonchurch.utils.LogUtils;
import com.dans.apps.baratonchurch.utils.UiUtils;

public class QuarterliesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    String TAG = "QuarterliesFragment";

    private RecyclerView list;
    private ProgressBar progressBar;
    private TextView noQuarterLies;

    LinearLayoutManager layoutManager;
    QuarterliesAdapter quarterliesAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quarterlies,container,false);
        list  = rootView.findViewById(R.id.quarterlies);
        progressBar = rootView.findViewById(R.id.progress_bar);
        noQuarterLies = rootView.findViewById(R.id.no_quarterlies);
        noQuarterLies.setVisibility(View.GONE);
        quarterliesAdapter = new QuarterliesAdapter(getActivity());
        layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        list.setAdapter(quarterliesAdapter);
        list.setLayoutManager(layoutManager);
        list.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        new FetchQuarterlies(getActivity()).execute(false);
        setHasOptionsMenu(true);
        swipeRefreshLayout = rootView.findViewById(R.id.refresh_quarterlies);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchQuarterlies(getActivity()).execute(true);
            }
        });
        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader loader = new CursorLoader(getActivity(),
                Contract.Quarterly.CONTENT_URI,Quarterlies.PROJECTION,null,null,null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(swipeRefreshLayout!=null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if(cursor!=null && cursor.moveToFirst()){
            if(noQuarterLies.getVisibility() == View.VISIBLE){
                noQuarterLies.setVisibility(View.GONE);
            }
            progressBar.setVisibility(View.GONE);
            do{
                String entryID = cursor.getString(Quarterlies.ENTRYID);
                String id = cursor.getString(Quarterlies.ID);
                String title = cursor.getString(Quarterlies.TITLE);
                String description = cursor.getString(Quarterlies.DESCRIPTION);
                String humanDate = cursor.getString(Quarterlies.HUMAN_DATE);
                String primaryColor = cursor.getString(Quarterlies.PRIMARY_COLOR);
                String secondaryColor = cursor.getString(Quarterlies.SECONDARY_COLOR);
                String lang = cursor.getString(Quarterlies.LANG);
                String fullPath = cursor.getString(Quarterlies.FULLPATH);
                String coverPath = cursor.getString(Quarterlies.COVER_PATH);

                QuarterlyItem item = new QuarterlyItem(entryID,title,description,id,
                        lang,humanDate,coverPath,fullPath,primaryColor,secondaryColor,null,null,null);
                if(quarterliesAdapter!=null){
                    quarterliesAdapter.addQuarterly(item);
                }
            }while (cursor.moveToNext());
        }else{
            boolean isConnected = UiUtils.isOnline(getActivity().getApplicationContext());
            noQuarterLies.setVisibility(View.VISIBLE);
            if(isConnected) {
                noQuarterLies.setText(R.string.quarterlies_fetch_error);
            }else{
                noQuarterLies.setText(R.string.no_connection);
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    interface Quarterlies {
        String[] PROJECTION = {
                Contract.Quarterly.ENTRY_ID,
                Contract.Quarterly.ID,
                Contract.Quarterly.TITLE,
                Contract.Quarterly.DESCRIPTION,
                Contract.Quarterly.HUMAN_DATE,
                Contract.Quarterly.START_DATE,
                Contract.Quarterly.END_DATE,
                Contract.Quarterly.PRIMARY_COLOR,
                Contract.Quarterly.SECONDARY_COLOR,
                Contract.Quarterly.LANG,
                Contract.Quarterly.INDEX,
                Contract.Quarterly.FULL_PATH,
                Contract.Quarterly.COVER_PATH
        };

        int ENTRYID=0;
        int ID=1;
        int TITLE=2;
        int DESCRIPTION = 3;
        int HUMAN_DATE =4;
        int START_DATE = 5;
        int END_DATE = 6;
        int PRIMARY_COLOR = 7;
        int SECONDARY_COLOR = 8;
        int LANG = 9;
        int INDEX= 10;
        int FULLPATH = 11;
        int COVER_PATH = 12;

    }

    public void restartLoader(){
        getLoaderManager().restartLoader(0,null,this);
    }

    /**
     * Fetches quarterlies
     */
    public class FetchQuarterlies extends AsyncTask<Boolean,Boolean,Boolean> {
        String language;
        Context context;
        public FetchQuarterlies(Context context) {
            this.context = context;
            language = PreferenceManager.
                    getDefaultSharedPreferences(context).
                    getString(SettingsActivity.KEY_LANGUAGE, Constants.DefaultLanguage);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            noQuarterLies.setVisibility(View.GONE);
        }

        //fetches
        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            try {
                Fetcher.checkQuarterlies(booleans[0],language,context,false);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(!isAdded()){
                return;
            }
            restartLoader();
        }

    }

    public void ondeviceConnected() {
        if(isAdded() && !isDetached()){
            if(quarterliesAdapter!=null){
                if(quarterliesAdapter.getItemCount() == 0){
                    new FetchQuarterlies(getActivity()).execute(false);
                }
            }
        }
    }


}
