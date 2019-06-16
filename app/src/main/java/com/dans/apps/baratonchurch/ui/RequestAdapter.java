package com.dans.apps.baratonchurch.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.dans.apps.baratonchurch.Constants;
import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.models.Request;
import com.dans.apps.baratonchurch.utils.LogUtils;
import com.dans.apps.baratonchurch.widget.ExpandableTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by duncan on 10/8/18.
 */

public class RequestAdapter extends RecyclerView.Adapter<AnnouncementRequestViewHolder> {

    private List<Request> items;
    private final Activity host;
    private final LayoutInflater layoutInflater;
    private Drawable approved;
    private Drawable rejected;
    private ActionListener listener;
    private boolean isChaplain;
    private Pattern Keys = Pattern.compile("(^.+?\\:)", Pattern.MULTILINE);

    public List<Request> getItems() {
        return items;
    }

    interface ActionListener{
        void onDeleteRequest(Request request);
        void onRequestRejected(Request request,String reasonForRejection);
        void onRequestApproved(Request request);
    }

    public RequestAdapter(Activity context, ActionListener listener, boolean isChaplain) {
        this.layoutInflater = LayoutInflater.from(context);
        items= new ArrayList<>();
        this.host = context;
        this.listener =listener;
        this.isChaplain = isChaplain;
        approved = host.getResources().getDrawable(R.drawable.approved);
        rejected= host.getResources().getDrawable(R.drawable.rejected);
    }

    public void addRequests(List<Request>requests){
        LogUtils.d("RequestAdapter","#Add request called ---> "+requests.size());
        items.clear();
        items.addAll(requests);
        LogUtils.d("RequestAdapter","#Add request called (items) ---> "+items.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnnouncementRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final AnnouncementRequestViewHolder holder =
                new AnnouncementRequestViewHolder(layoutInflater.inflate(R.layout.request_announcement_view,parent,
                        false));
        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Request request = items.get(holder.getAdapterPosition());
                AlertDialog.Builder builder = new AlertDialog.Builder(host);
                builder.setTitle(R.string.reason_for_rejection);
                builder.setMessage(request.getReasonForRejection());
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.create().show();
            }
        });

        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Request request = items.get(holder.getAdapterPosition());
                AlertDialog.Builder builder = new AlertDialog.Builder(host);
                builder.setTitle(R.string.reason_for_rejection);
                final EditText editText = new EditText(host);
                builder.setView(editText);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listener!=null){
                            listener.onRequestRejected(request,editText.getText().toString());
                        }
                    }
                });

                builder.setNegativeButton(R.string.no_reason, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listener!=null){
                            listener.onRequestRejected(request,null);
                        }
                    }
                });
                builder.show();
                //request.setRequestStatus(Constants.RequestStatus.REJECTED);
            }
        });
        holder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Request request = items.get(holder.getAdapterPosition());
               // request.setRequestStatus(Constants.RequestStatus.APPROVED);
                if(listener!=null){
                    listener.onRequestApproved(request);
                }
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Request request = items.get(holder.getAdapterPosition());
                PopupMenu menu = new PopupMenu(host,v);
                menu.getMenuInflater().inflate(R.menu.request_announcement_item_menu,menu.getMenu());
                menu.getMenu().findItem(R.id.share).setVisible(false);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(host);
                        builder.setMessage(R.string.delete_prompt);
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(listener!=null){
                                    listener.onDeleteRequest(request);
                                    items.remove(holder.getAdapterPosition());
                                    notifyItemRemoved(holder.getAdapterPosition());
                                }
                            }
                        });
                        builder.show();
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
    public void onBindViewHolder(@NonNull AnnouncementRequestViewHolder holder, int position) {
        Request request = items.get(position);
        holder.title.setText(request.getRequestTitle());
        holder.createdAt.setText(request.getDate().toString());
        holder.sender.setText(request.getSender());
        int status = request.getRequestStatus();


        String reasonForRejection = request.getReasonForRejection();

        if(reasonForRejection!=null){
            holder.info.setVisibility(View.VISIBLE);
        }else{
            holder.info.setVisibility(View.GONE);
        }

        if(!isChaplain){
            holder.requestActionsContainer.setVisibility(View.GONE);
        }

        switch (status){
            case Constants.RequestStatus.APPROVED:
                holder.statusText.setText(R.string.approved);
                holder.statusImage.setVisibility(View.VISIBLE);
                holder.statusImage.setImageDrawable(approved);
                holder.info.setVisibility(View.GONE);
                holder.requestActionsContainer.setVisibility(View.GONE);
                break;
            case Constants.RequestStatus.REJECTED:
                holder.statusText.setText(R.string.rejected);
                holder.statusImage.setImageDrawable(rejected);
                if(isChaplain){
                    holder.info.setVisibility(View.GONE);
                }else {
                    holder.info.setVisibility(View.VISIBLE);
                }
                holder.requestActionsContainer.setVisibility(View.GONE);
                break;
            case Constants.RequestStatus.WAITING_APPROVAL:
                holder.statusText.setText(R.string.waiting_approval);
                holder.statusImage.setVisibility(View.GONE);
                holder.info.setVisibility(View.GONE);
                break;
        }

        HashMap<String,String>body = request.getSpecificRequestFields();
        if(body!=null){
            prettifyBody(holder.body,body,request.getAdditionalInformation());
        }else{
            holder.body.setText(request.getAdditionalInformation());
        }
    }

    private void prettifyBody(ExpandableTextView expandableTextView, HashMap<String, String> body, String additionalInformation) {
        StringBuilder builder = new StringBuilder();
        if(isChaplain){
            builder.append("Sender provided the following information in the request\n");
        }else{
            builder.append("You provided the following information in the request\n");
        }
        for(String key:body.keySet()){
            builder.append(key+" : "+body.get(key)).append("\n");
        }
        if(additionalInformation!=null){
            builder.append("Additional information : "+additionalInformation);
        }

        String finalString = builder.toString();
        if(isChaplain){
            finalString = finalString.replace("Date on which you wished","Date on which i wish to");
            finalString = finalString.replace("Date on which you wish the child","Date on which i wish");
        }
        Matcher matcher = Keys.matcher(finalString);
        Spannable mSpanText = new SpannableString(finalString);

        if (matcher != null) {
            while (matcher.find()) {
                mSpanText.setSpan(
                        new ForegroundColorSpan(host.getResources().getColor(
                                R.color.yellow)), matcher.start(), matcher.end(), 0);
            }
            expandableTextView.setText(mSpanText);
        } else {
            expandableTextView.setText(finalString);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
