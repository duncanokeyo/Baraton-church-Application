package com.dans.apps.baratonchurch.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dans.apps.baratonchurch.Constants;
import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.YoutubeAcitivity;
import com.dans.apps.baratonchurch.models.SermonVideo;
import com.dans.apps.baratonchurch.provider.Contract;
import com.dans.apps.baratonchurch.utils.LogUtils;
import com.dans.apps.baratonchurch.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duncan on 12/21/17.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private static final String TAG = "VideoAdapter";
    private final Activity host;
    private List<SermonVideo> items = new ArrayList<>();
    List<SermonVideo> filter = null;
    private final LayoutInflater layoutInflater;

    public VideoAdapter(Activity host) {
        this.host = host;
        this.items.clear();
        this.layoutInflater = LayoutInflater.from(host);
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final VideoViewHolder holder = new VideoViewHolder(layoutInflater.inflate(R.layout.video_list_item,parent,false));
        //todo implement onclick listeners

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SermonVideo item = items.get(holder.getAdapterPosition());
                Intent intent =new Intent(host,YoutubeAcitivity.class);
                intent.putExtra(YoutubeAcitivity.KEY_VIDEO_ID,item.getVideoId());
                host.startActivity(intent);
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SermonVideo item = items.get(holder.getAdapterPosition());
                PopupMenu popup = new PopupMenu(host,view);
                popup.getMenuInflater().inflate(R.menu.sermons_overflow_menu,popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        if(id  == R.id.action_share){
                            UiUtils.
                                    shareText("Sermon",
                                            "Check out this awesome baraton church sermon\n\n"+
                                                    Constants.YOUTUBE_URL_SEGMENT+item.getVideoId(),host);
                        }
                        return true;
                    }
                });

                popup.show();
            }
        });

        holder.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SermonVideo item = items.get(holder.getAdapterPosition());
                ContentValues values = new ContentValues();
                if(isSermonFavourite(item.getVideoId())){
                    //remove from favourites
                    values.put(Contract.Sermon.FAVOURITE,0);
                    setIsFavourite(false,holder.favourite.getDrawable(),holder.favourite);
                }else{
                    //add to favoutires
                    values.put(Contract.Sermon.FAVOURITE,1);
                    setIsFavourite(true,holder.favourite.getDrawable(),holder.favourite);
                }
                host.getContentResolver().
                        update(Contract.Sermon.CONTENT_URI,values,
                                Contract.Sermon.ID+" =?",new String[]{item.getVideoId()});

                notifyItemChanged(holder.getAdapterPosition());

            }
        });

        return holder;
    }

    public boolean isSermonFavourite(String videoID){
        boolean isFavourite = false;
        Cursor cursor = host.getContentResolver().query(Contract.Sermon.CONTENT_URI,new String[]{Contract.Sermon.FAVOURITE},
                Contract.Sermon.ID+" =?",new String[]{videoID},null);
        if(cursor.moveToFirst()){
            isFavourite = cursor.getInt(0)==1;
            LogUtils.d(TAG,videoID+" is Favourite "+isFavourite);

        }
        cursor.close();
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite, Drawable drawable,ImageView view){
        drawable = DrawableCompat.wrap(drawable);
        if(isFavourite){
            DrawableCompat.setTint(drawable, ContextCompat.getColor(host,R.color.yellow));
        }else{
            DrawableCompat.setTint(drawable, ContextCompat.getColor(host,R.color.grey400));
        }
        view.setImageDrawable(drawable);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public List<SermonVideo> getItems() {
        return items;
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        SermonVideo item = filter!=null?filter.get(position):items.get(position);

        holder.title.setText(item.getTitle());
        String publishedAt=item.getPulishedAt();
        try {
          publishedAt = com.dans.apps.baratonchurch.utils.DateUtils.
                  getHumanFriendlyDateTimeFromISODateTime(publishedAt);
        }catch (Exception e){}

        holder.humanDate.setText(publishedAt);
        Glide.with(host).load(item.getThumbnailUrl()).
                into(holder.videoThumbnail);
    }

    @Override
    public int getItemCount() {
        if(filter!=null){
            return filter.size();
        }
        return items.size();
    }

    public void addVideos(ArrayList<SermonVideo> videos) {
        items.clear();
        items=videos;
        notifyDataSetChanged();
    }

    //dont think i will use this, cursorloader offers much better interface
    //combined with query listeners
    public void addFilter(List<SermonVideo> newList){
        filter = newList;
        notifyDataSetChanged();
    }

    public void clearFilter(){
        filter = null;
        notifyDataSetChanged();
    }

    public void removeContents() {
        items.clear();
        notifyDataSetChanged();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder{
        public ImageView videoThumbnail;
        public TextView title;
        public TextView humanDate;
        public ImageView play;
        public ImageView favourite;
        public ImageView overflow;

        public VideoViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.video_title);
            humanDate=itemView.findViewById(R.id.published_at);
            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
            play = itemView.findViewById(R.id.play);
            favourite = itemView.findViewById(R.id.favourite_video);
            overflow = itemView.findViewById(R.id.video_overflow);
        }
    }
}
