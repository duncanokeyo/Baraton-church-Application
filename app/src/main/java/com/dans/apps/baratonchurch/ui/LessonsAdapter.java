package com.dans.apps.baratonchurch.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.ReadActivity;
import com.dans.apps.baratonchurch.models.LessonItem;
import com.dans.apps.baratonchurch.provider.Contract;
import com.dans.apps.baratonchurch.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duncan on 11/20/17.
 */

public class LessonsAdapter extends  RecyclerView.Adapter<LessonsAdapter.LessonsViewHolder>{

    private final Activity host;
    private List<LessonItem> items;
    private final LayoutInflater layoutInflater;

    public LessonsAdapter(Activity host) {
        this.host = host;
        this.layoutInflater = LayoutInflater.from(host);
        items= new ArrayList<>();
    }

    @Override
    public LessonsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LessonsViewHolder holder =
                new LessonsViewHolder(layoutInflater.inflate(R.layout.lesson_item,parent,false));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LessonItem item = items.get(holder.getAdapterPosition());
                Intent intent =new Intent(host,ReadActivity.class);
                intent.putExtra(Contract.Lesson.QUARTERLY_ID,item.getQuarterlyId());
                intent.putExtra(Contract.Lesson.ID,item.getId());
                intent.putExtra(Contract.Lesson.COVER_PATH,item.getCoverPath());
                intent.putExtra(Contract.Lesson.TITLE,item.getTitle());
                host.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(LessonsViewHolder holder, int position) {
        LessonItem item = items.get(position);
        String startDate = item.getStartDate();
        String endDate = item.getEndDate();

        String startDateFormatted = DateUtils.getHumanFriendlyFormat(startDate);
        String endDateFormatted = DateUtils.getHumanFriendlyFormat(endDate);
        String period;
        if(startDateFormatted == null || endDateFormatted == null) {
            period = startDate + " to " + endDate;
        }else{
            period =startDateFormatted+" to "+endDateFormatted;
        }

        holder.lessonPeriod.setText(period);
        holder.lessonNumber.setText(item.getId());
        holder.title.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addLesson(LessonItem item) {
        items.add(item);
        notifyDataSetChanged();
    }
    public void addLessons(List<LessonItem>item){
        items = item;
        notifyDataSetChanged();
    }


    public static class LessonsViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView lessonNumber;
        public TextView lessonPeriod;

        public LessonsViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.favourite_reading_lesson_title);

            lessonNumber=itemView.findViewById(R.id.lesson_number);
            lessonPeriod=itemView.findViewById(R.id.lesson_period);
        }
    }
}
