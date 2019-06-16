package com.dans.apps.baratonchurch.ui;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.models.Days;
import com.dans.apps.baratonchurch.utils.DateUtils;

import java.util.ArrayList;

/**
 * Created by duncan on 12/19/17.
 */

public class JumpToDayDialogFragment extends DialogFragment {
    ArrayList<Days> days;
    ListView list;
    Callback callback;

    public void addDays(ArrayList<Days> days) {
        this.days = days;
    }

    public interface Callback{
        void onDaySelected(int position);
    }
    public void setCallback(Callback callback){
        this.callback=callback;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callback = null;
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
        View root=inflater.inflate(R.layout.day_read_chooser,container,false);
        list = root.findViewById(R.id.day_read_list);
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.select_day));
        toolbar.inflateMenu(R.menu.dialog_menu);
        CustomAdapter adapter = new CustomAdapter(getActivity(),days);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(callback!=null){
                    callback.onDaySelected(position);
                    dismiss();
                }
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.close){
                    dismiss();
                }
                return false;
            }
        });
        return  root;
    }
    public class CustomAdapter extends ArrayAdapter<Days> {
        private final ArrayList<Days> itemsArrayList;
        private final LayoutInflater inflater;

        public CustomAdapter(Context context, ArrayList<Days> itemsArrayList) {
            super(context, R.layout.day_read_chooser_row, itemsArrayList);
            this.itemsArrayList = itemsArrayList;
            this.inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.day_read_chooser_row, parent, false);
                holder = new ViewHolder();
                holder.date = convertView.findViewById(R.id.date);
                holder.title = convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Days day = itemsArrayList.get(position);

            holder.title.setText(day.getTitle());
            String date = day.getDate();
            if(date!=null) {
                String simpleDate = DateUtils.getHumanFriendlyDateWithDay(date);

                holder.date.setText(simpleDate.trim());
            }
            return convertView;
        }

        class ViewHolder {
            TextView date;
            TextView title;

            public ViewHolder() {
            }
        }
    }
}
