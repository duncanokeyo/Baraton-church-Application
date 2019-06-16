package com.dans.apps.baratonchurch.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.models.Days;
import com.dans.apps.baratonchurch.network.Fetcher;
import com.dans.apps.baratonchurch.provider.Contract;
import com.dans.apps.baratonchurch.utils.LogUtils;
import com.dans.apps.baratonchurch.utils.UiUtils;
import com.dans.apps.baratonchurch.widget.ReadView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ReadFragment extends Fragment implements ReadView.HighlightsCommentsCallback{
    String TAG = "ReadFragment";

    ProgressBar progressBar;
    TextView noRead;
    Button retry;
    ReadView page;

    String quarterlyID;
    String lessonID;
    String dayID;
    String title;
    String fullReadPath;
    String content;
    String date;
    String itemIndex;

    ArrayList<com.dans.apps.baratonchurch.models.Verses> bibleVerses = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_read, container, false);
        page = v.findViewById(R.id.page);
        progressBar = v.findViewById(R.id.progress_bar);
        noRead = v.findViewById(R.id.message);
        retry = v.findViewById(R.id.retry);
        page.setHighlightsCommentsCallback(this);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                noRead.setVisibility(View.GONE);
                retry.setVisibility(View.GONE);
                new FetchRead(getActivity().getContentResolver(),false).execute();
            }
        });

        return v;
    }


    /**
     * called when we have populated the database with the read..
     * @param data
     */
    public void viewRead(Cursor data){
        Cursor cursor = data!=null?data:fetchRead();
        if(cursor.moveToFirst()){
            bibleVerses.clear();
                progressBar.setVisibility(View.GONE);
                noRead.setVisibility(View.GONE);
                retry.setVisibility(View.GONE);

                String content = cursor.getString(0);
                itemIndex = cursor.getString(1);
                //fetch the relevant verses for this read
                Cursor verses = getActivity().getContentResolver().
                        query(Contract.ReadVerses.CONTENT_URI, Verses.PROJECTION,
                                Contract.ReadVerses.READ_ID+" =? AND "+ Contract.ReadVerses.LESSON_ID+" =? AND "+
                                        Contract.ReadVerses.QUARTERLY_ID+" =?",
                                new String[]{dayID,lessonID,quarterlyID},null);
                if(verses!=null && verses.moveToFirst()){
                    do{
                        String bibleName = verses.getString(Verses.BIBLE_NAME);
                        String bibleVerse = verses.getString(Verses.BIBLE_VERSES);
                        LogUtils.d(TAG,"bible sender "+bibleName);
                        LogUtils.d(TAG,"bible verse "+bibleVerse);

                        com.dans.apps.baratonchurch.models.Verses verse = new com.dans.apps.baratonchurch.models.Verses();
                        verse.setBibleVersion(bibleName);

                        try {
                            JSONObject object = new JSONObject(bibleVerse);
                            Iterator<String>keys = object.keys();
                            while(keys.hasNext()){
                                String key = keys.next();
                                String value =object.getString(key);
                                verse.insertVerse(key,value);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        bibleVerses.add(verse);
                    }while (verses.moveToNext());
                    verses.close();
                }
                showContent(content);
                this.content=content;
        }
        if(cursor!=null) {
            cursor.close();
        }
    }


    private Cursor fetchRead(){
        LogUtils.v(TAG,"Fetching read for Quarterly : "
                +quarterlyID+" Lesson : "+lessonID+" Day :"+dayID);
        return getActivity().getContentResolver().query(Contract.Read.CONTENT_URI,
                new String[]{Contract.Read.CONTENT,Contract.Read.INDEX},
                Contract.Read.DAY_ID+" =? AND "+ Contract.Read.LESSON_ID+" =? AND "+
                        Contract.Read.QUARTERLY_ID+" =?",
                new String[]{dayID,lessonID,quarterlyID},null);
    }

    public String getQuarterlyID() {return quarterlyID;}
    public String getLessonID() {return lessonID;}
    public String getDayID() {return dayID;}
    public String getTitle() {return title;}
    public String getFullReadPath() {return fullReadPath;}
    public String getContent(){return content;}
    public String getDate(){return date;}
    public ArrayList<com.dans.apps.baratonchurch.models.Verses> getBibleVerses() {
        return bibleVerses;
    }

    //shows the content users are supposed to read
    private void showContent(String content) {
        page.loadRead(content);
    }

    /**
     * called by Read activity when a specific day has been selected
     * @param day
     */
    public void onDaySelected(Days day) {
        inValidateFields();
        if(day!=null){
            dayID = day.getId();
            lessonID = day.getLessonID();
            title=day.getTitle();
            quarterlyID = day.getQuarterlyID();
            date =day.getDate();
            fullReadPath = day.getFullReadPath();
            if(!isDetached() && isAdded()) {
               // getLoaderManager().restartLoader(6, null, this);
                //first lets check if this read exists in the database...
                Cursor cursor = fetchRead();
                if(cursor.getCount()==0){
                    LogUtils.d(TAG,"this read does not exist in the database.. Fetching");
                    cursor.close();
                    new FetchRead(getActivity().getContentResolver(),false).execute();
                }else{
                    LogUtils.d(TAG,"this read exists in the database");
                    //cursor will be closed in this method
                    viewRead(cursor);
                }
            }
        }
    }

    public void inValidateFields(){
        dayID=null;
        lessonID = null;
        title=null;
        quarterlyID = null;
        date= null;
        itemIndex = null;
        fullReadPath =null;
    }

    @Override
    public void onVerseClicked(String verse) {
        LogUtils.d(TAG,"verse clicked "+verse);
        if(bibleVerses.size()>0) {
            VersesDialogFragment fragment = VersesDialogFragment.newInstance(bibleVerses, verse);
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment prev = manager.findFragmentByTag("verses");
            if (prev != null) {
                transaction.remove(prev);
            }
            transaction.addToBackStack(null);
            fragment.show(manager, "verses");
        }
    }

    /**
     * refetches the daily read data
     */
    public void refreshRead() {
        //you cannot refresh what has not been loaded ...
        if(getContent()!=null){
            new FetchRead(getActivity().getContentResolver(),true).execute();
        }
    }


    interface Verses{
        String [] PROJECTION = {
                Contract.ReadVerses.BIBLE_NAME,
                Contract.ReadVerses.BIBLE_VERSES
        };

        int BIBLE_NAME = 0;
        int BIBLE_VERSES = 1;
    }

    /**
     * called when there is no read in the database..
     */
    public class FetchRead extends AsyncTask<Void,Void,Boolean>{
        ContentResolver resolver;
        boolean update;
        public FetchRead(ContentResolver resolver, boolean update) {
            this.resolver = resolver;
            this.update = update;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if(fullReadPath == null){
                    return false;
                }
                ContentValues values = new ContentValues();
                String reads = Fetcher.getJson(fullReadPath+"/index.json");
                JSONObject object = new JSONObject(reads);
                String content = object.getString("content");
                String date = object.getString("date");
                String index = object.getString("index");
                String title = object.getString("title");
                String readID = object.getString("id");

                if(!update) {
                    values.put(Contract.Read.QUARTERLY_ID, quarterlyID);
                    values.put(Contract.Read.DAY_ID, dayID);
                    values.put(Contract.Read.ENTRY_ID, Long.valueOf(System.nanoTime()));
                    values.put(Contract.Read.LESSON_ID, lessonID);
                    values.put(Contract.Read.ID, readID);
                }

                values.put(Contract.Read.CONTENT, content);
                values.put(Contract.Read.DATE, date);
                values.put(Contract.Read.INDEX, index);
                values.put(Contract.Read.TITLE, title);

                if(update){
                    LogUtils.d(TAG,"update initiated... ");
                    LogUtils.d(TAG,"item index "+itemIndex);
                    resolver.update(Contract.Read.CONTENT_URI,values,
                            Contract.Read.INDEX+"=?",
                            new String[]{itemIndex});

                    resolver.delete(Contract.ReadVerses.CONTENT_URI,
                            Contract.ReadVerses.READ_ID+" =? AND "+ Contract.ReadVerses.LESSON_ID+" =? AND "+
                                    Contract.ReadVerses.QUARTERLY_ID+" =?",
                            new String[]{dayID,lessonID,quarterlyID});
                }else{
                    resolver.insert(Contract.Read.CONTENT_URI,values);
                }

                //lets get the bible verses now
                JSONArray array = object.getJSONArray("bible");
                for (int a = 0; a < array.length(); a++) {
                    ContentValues bibleVerses = new ContentValues();
                    JSONObject read = array.getJSONObject(a);
                    String name = read.getString("sender");//bible sender
                    String verses = read.getString("verses");//bible verses

                    bibleVerses.put(Contract.ReadVerses.ENTRY_ID, String.valueOf(System.nanoTime()));
                    bibleVerses.put(Contract.ReadVerses.QUARTERLY_ID, quarterlyID);
                    bibleVerses.put(Contract.ReadVerses.BIBLE_NAME, name);
                    bibleVerses.put(Contract.ReadVerses.BIBLE_VERSES, verses);
                    bibleVerses.put(Contract.ReadVerses.LESSON_ID, lessonID);
                    bibleVerses.put(Contract.ReadVerses.READ_ID, readID);


                        resolver.insert(Contract.ReadVerses.CONTENT_URI, bibleVerses);

                }
            }catch (Exception e){
                return false;
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            noRead.setVisibility(View.GONE);
            retry.setVisibility(View.GONE);
            page.loadRead("");
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (isAdded() && !isDetached()) {
                if (success) {
                    progressBar.setVisibility(View.GONE);
                    noRead.setVisibility(View.GONE);
                    retry.setVisibility(View.GONE);
                } else {
                    boolean connected = UiUtils.isOnline(getActivity());
                    progressBar.setVisibility(View.GONE);
                    if (getContent() == null) {
                        noRead.setVisibility(View.VISIBLE);
                        if (connected) {
                            noRead.setText(R.string.error_fetching_data);
                        } else {
                            noRead.setText(R.string.no_connection);
                        }
                        retry.setVisibility(View.VISIBLE);
                    }
                }
                viewRead(null);
            }
        }
    }

}
