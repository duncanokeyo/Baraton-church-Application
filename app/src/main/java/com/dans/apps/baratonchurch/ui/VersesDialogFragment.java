package com.dans.apps.baratonchurch.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.models.Verses;
import com.dans.apps.baratonchurch.widget.ReadView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by duncan on 12/18/17.
 */

public class VersesDialogFragment extends DialogFragment implements TabLayout.OnTabSelectedListener {
    String TAG = "VersesDialogFragment";
    TabLayout tabLayout;
    ReadView readView;
    static String selectedReference;
    static ArrayList<Verses>verses;
    public static VersesDialogFragment newInstance(ArrayList<Verses>verse, String selected) {
        VersesDialogFragment dialog = new VersesDialogFragment();
        verses=verse;
        selectedReference = selected;
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getShowsDialog()){
            setStyle(DialogFragment.STYLE_NO_TITLE,0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.bible_verses,container,false);
        tabLayout = root.findViewById(R.id.verses_tab_layout);
        readView = root.findViewById(R.id.verses);
        tabLayout.setOnTabSelectedListener(this);
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.references);
        toolbar.inflateMenu(R.menu.dialog_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.close){
                    dismiss();
                }
                return false;
            }
        });
        for(Verses verse:verses){
            tabLayout.addTab(tabLayout.newTab().setText(verse.getBibleVersion()));
        }
        return  root;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        Verses verse = verses.get(tab.getPosition());
        Map<String,String> references = verse.getMap();

        for(String key:references.keySet()){
            if(selectedReference.toLowerCase().equals(key.toLowerCase())){
                readView.loadRead(references.get(key));
            }
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}
}
