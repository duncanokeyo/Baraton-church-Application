package com.dans.apps.baratonchurch.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.SingleReadActivity;
import com.dans.apps.baratonchurch.models.Favourites;
import com.dans.apps.baratonchurch.provider.Contract;

import java.util.ArrayList;

/**
 * Created by duncan on 12/22/17.
 */

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder>{
    ArrayList<Favourites> items;
    Activity host;
    LayoutInflater layoutInflater;
    RequestOptions options = new RequestOptions();
    public FavouritesAdapter(Activity activity) {
        host=activity;
        layoutInflater=LayoutInflater.from(activity);
        items=new ArrayList<>();
        options.centerCrop();
    }

    @Override
    public FavouritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final FavouritesViewHolder holder =
                new FavouritesViewHolder(layoutInflater.inflate(R.layout.favourite_list_item_home,parent,false));
        //todo implement onclick listener
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Favourites item = items.get(holder.getAdapterPosition());
                host.getContentResolver().delete(Contract.FavouriteRead.CONTENT_URI,
                        Contract.FavouriteRead.ENTRY_ID +" =?",new String[]{item.getEntryID()});
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Favourites item = items.get(holder.getAdapterPosition());
                Intent intent = new Intent(host, SingleReadActivity.class);
                intent.putExtra(SingleReadActivity.KEY_FAVOURITE,item);
                host.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(FavouritesViewHolder holder, int position) {
        Favourites item = items.get(position);
        holder.lessonTitle.setText(item.getLessonTitle());
        holder.readingTitle.setText(item.getTitle());

        Glide.with(host).load(item.getLessonCover()).apply(options).
                into(holder.lessonCover);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addFavourites(ArrayList<Favourites> favourites) {
        items= favourites;
        notifyDataSetChanged();
    }

    public static class FavouritesViewHolder extends RecyclerView.ViewHolder{
        TextView lessonTitle;
        TextView readingTitle;
        ImageView lessonCover;
        Button remove;
        public FavouritesViewHolder(View itemView) {
            super(itemView);
            lessonTitle = itemView.findViewById(R.id.favourite_reading_lesson_title);
            readingTitle = itemView.findViewById(R.id.favourite_reading_title);
            lessonCover =itemView.findViewById(R.id.favourites_lesson_cover);
            remove = itemView.findViewById(R.id.remove_from_favourites);
        }
    }
}
