package com.dans.apps.baratonchurch;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dans.apps.baratonchurch.models.Favourites;
import com.dans.apps.baratonchurch.models.Verses;
import com.dans.apps.baratonchurch.ui.VersesDialogFragment;
import com.dans.apps.baratonchurch.utils.LogUtils;
import com.dans.apps.baratonchurch.widget.ReadView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class SingleReadActivity extends AppCompatActivity implements ReadView.HighlightsCommentsCallback {

    String TAG = "SingleReadActivity";

    public static String KEY_FAVOURITE = "favourite";
    Favourites favourite;
    ReadView readView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ArrayList<Verses> verses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_single);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        readView = findViewById(R.id.read_view);
        readView.setHighlightsCommentsCallback(this);
        ImageView lessonCover = findViewById(R.id.lesson_larger_cover);
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (getIntent().getExtras() != null) {
            favourite = getIntent().getExtras().getParcelable(KEY_FAVOURITE);
            collapsingToolbarLayout.setTitle(favourite.getTitle());
            Glide.with(this).load(favourite.getLessonCover()).into(lessonCover);
            readView.loadRead(favourite.getContent());
            final String bibleVerses = favourite.getBibleReference();
            Thread parseJsonVerse = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (bibleVerses == null) {
                            return;
                        }
                        JSONArray array = new JSONArray(bibleVerses);
                        for (int i = 0; i < array.length(); i++) {
                            Verses verse = new Verses();
                            JSONObject object = array.getJSONObject(i);
                            verse.setBibleVersion(object.getString("bible_version"));
                            Iterator<String> keys = object.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                String value = object.getString(key);
                                verse.insertVerse(key, value);
                            }
                            verses.add(verse);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            parseJsonVerse.run();
        }
    }

    @Override
    public void onVerseClicked(String verse) {
        if (verses.size() > 0) {
            VersesDialogFragment fragment = VersesDialogFragment.newInstance(verses, verse);
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
}
