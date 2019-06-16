package com.dans.apps.baratonchurch;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class UsefulLinksActivity extends AppCompatActivity {
    LinearLayoutManager layoutManager;
    private RecyclerView linksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useful_links);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linksList = findViewById(R.id.useful_links_list);
        layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        LinkAdapter adapter = new LinkAdapter(this);
        linksList.setAdapter(adapter);
        linksList.setLayoutManager(layoutManager);
        linksList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));


        String[] array = getResources().getStringArray(R.array.useful_links);
        ArrayList<String>links = new ArrayList<>(Arrays.asList(array));
        adapter.setLinks(links);

    }


    public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.ViewHolder> {
        LayoutInflater inflater;

        public LinkAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        ArrayList<String> links = new ArrayList<>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final ViewHolder holder =
                    new ViewHolder(inflater.inflate(R.layout.useful_links_list_item, parent, false));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String link = links.get(position);
            holder.link.setText(link);
        }

        @Override
        public int getItemCount() {
            return links.size();
        }

        public void setLinks(ArrayList<String> links) {
            this.links = links;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView link;

            public ViewHolder(final View itemView) {
                super(itemView);
                link = itemView.findViewById(R.id.link);
            }
        }

    }
}
