package com.dans.apps.baratonchurch.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.widget.ExpandableTextView;

/**
 * Created by duncan on 10/10/18.
 */

public class AnnouncementRequestViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    ExpandableTextView body;
    TextView createdAt;
    TextView sender;
    ImageView more;
    LinearLayout statusInfoContainer;
    LinearLayout requestActionsContainer;
    Button approve;
    Button reject;
    ImageView statusImage;
    TextView statusText;
    ImageView info;

    public AnnouncementRequestViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        body = itemView.findViewById(R.id.body);
        createdAt = itemView.findViewById(R.id.createdAt);
        more = itemView.findViewById(R.id.more);
        sender = itemView.findViewById(R.id.sender);
        statusInfoContainer = itemView.findViewById(R.id.status_info_container);
        requestActionsContainer = itemView.findViewById(R.id.request_actions_container);
        approve = itemView.findViewById(R.id.approve);
        reject = itemView.findViewById(R.id.reject);
        statusImage = itemView.findViewById(R.id.status_image);
        statusText = itemView.findViewById(R.id.status_text);
        info = itemView.findViewById(R.id.info);
        body.setInterpolator(new OvershootInterpolator());
    }
}
