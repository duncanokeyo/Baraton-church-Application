package com.dans.apps.baratonchurch.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dans.apps.baratonchurch.LessonsActivity;
import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.models.QuarterlyItem;
import com.dans.apps.baratonchurch.provider.Contract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duncan on 11/19/17.
 */

public class QuarterliesAdapter extends RecyclerView.Adapter<QuarterliesAdapter.QuarterliesViewHolder>{
    private final Activity host;
    private List<QuarterlyItem> items;
    private final LayoutInflater layoutInflater;

    public QuarterliesAdapter(Activity host) {
        this.host = host;
        this.layoutInflater = LayoutInflater.from(host);
        items = new ArrayList<>();
    }

    @Override
    public QuarterliesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final QuarterliesViewHolder holder = new QuarterliesViewHolder(layoutInflater.inflate(R.layout.quarterly_item,parent,false));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(host, LessonsActivity.class);
                QuarterlyItem item = items.get(holder.getAdapterPosition());
                intent.putExtra(Contract.Quarterly.ID,item.getId());
                intent.putExtra(Contract.Quarterly.PRIMARY_COLOR,item.getPrimaryColor());
                intent.putExtra(Contract.Quarterly.SECONDARY_COLOR,item.getSecondaryColor());
                intent.putExtra(Contract.Quarterly.DESCRIPTION,item.getDescription());
                intent.putExtra(Contract.Quarterly.COVER_PATH,item.getCoverPath());
                host.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(QuarterliesViewHolder holder, int position) {
        QuarterlyItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.humanDate.setText(item.getHuman_date());

        Glide.with(host).load(item.getCoverPath()).
                into(holder.cover);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public QuarterlyItem getQuarterlyItem(int position){
        return items.get(position);
    }

    public void clear(){
        items.clear();
        notifyDataSetChanged();
    }

    public void addQuarterly(QuarterlyItem item){
        items.add(item);
        notifyDataSetChanged();
    }

    public static class QuarterliesViewHolder extends RecyclerView.ViewHolder{
        public ImageView cover;
        public TextView title;
        public TextView humanDate;
        public ImageView share;
        public ImageView readLater;
        public QuarterliesViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.favourite_reading_title);
            cover = itemView.findViewById(R.id.favourites_lesson_cover);

            humanDate = itemView.findViewById(R.id.favourite_reading_lesson_title);
            //share=itemView.findViewById(R.id.share);
        //    readLater=itemView.findViewById(R.id.favorite);
        }
    }
}
