package com.dans.apps.baratonchurch.ui;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.models.Request;
import com.dans.apps.baratonchurch.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duncan on 12/21/17.
 */

public class AnnouncementsAdapter extends
        RecyclerView.Adapter<AnnouncementRequestViewHolder>{
    private List<Request> items;
    private final Activity host;
    private final LayoutInflater layoutInflater;

    public AnnouncementsAdapter(Activity context) {
        this.layoutInflater = LayoutInflater.from(context);
        items= new ArrayList<>();
        this.host = context;
    }


    @Override
    public AnnouncementRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final AnnouncementRequestViewHolder holder =
                new AnnouncementRequestViewHolder(layoutInflater.inflate(R.layout.request_announcement_view,parent,
                        false));

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Request request = items.get(holder.getAdapterPosition());
                PopupMenu menu = new PopupMenu(host,v);
                menu.getMenuInflater().inflate(R.menu.request_announcement_item_menu,menu.getMenu());
                menu.getMenu().findItem(R.id.delete).setVisible(false);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String content = request.getRequestTitle()+"\n"+request.getAdditionalInformation();
                        UiUtils.shareText("Announcement",content,host);
                        return false;
                    }
                });
                menu.show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.body.toggle();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(AnnouncementRequestViewHolder holder, int position) {
        Request request = items.get(position);
        holder.title.setText(request.getRequestTitle());
        holder.createdAt.setText(request.getDate().toString());
        holder.info.setVisibility(View.GONE);
        holder.statusInfoContainer.setVisibility(View.GONE);
        holder.requestActionsContainer.setVisibility(View.GONE);
        holder.sender.setText(request.getSender());
        holder.body.setText(request.getAdditionalInformation());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addAnnouncements(List<Request> requests) {
        items.clear();
        items.addAll(requests);
        notifyDataSetChanged();
    }

    public List<Request> getItems() {
        return items;
    }
}
