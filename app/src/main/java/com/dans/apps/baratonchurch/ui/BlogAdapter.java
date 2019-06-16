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
import com.dans.apps.baratonchurch.BlogReadActivity;
import com.dans.apps.baratonchurch.LessonsActivity;
import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.models.Blog;
import com.dans.apps.baratonchurch.models.QuarterlyItem;
import com.dans.apps.baratonchurch.provider.Contract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duncan on 11/19/17.
 */

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder>{
    private final Activity host;
    private List<Blog> items;
    private final LayoutInflater layoutInflater;

    public BlogAdapter(Activity host) {
        this.host = host;
        this.layoutInflater = LayoutInflater.from(host);
        items = new ArrayList<>();
    }

    @Override
    public BlogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final BlogViewHolder holder = new BlogViewHolder(layoutInflater.inflate(R.layout.blog_list_item,parent,false));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(host, BlogReadActivity.class);
                Blog item = items.get(holder.getAdapterPosition());
                intent.putExtra(BlogReadActivity.KEY_BLOG_ID,item.getId());
                host.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(BlogViewHolder holder, int position) {
        Blog item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.humanDate.setText(item.getDate());
        holder.author.setText(item.getAuthor());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Blog getBloItem(int position){
        return items.get(position);
    }

    public void clear(){
        items.clear();
        notifyDataSetChanged();
    }

    public void addBlog(ArrayList<Blog>blogs){
        items.clear();
        items.addAll(blogs);
        notifyDataSetChanged();
    }
    public void addBlog(Blog item){
        items.add(item);
        notifyDataSetChanged();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView humanDate;
        public TextView author;
        public BlogViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.blog_content_title);
            humanDate = itemView.findViewById(R.id.blog_date);
            author = itemView.findViewById(R.id.blog_author);

        }
    }
}
