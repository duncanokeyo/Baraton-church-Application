package com.dans.apps.baratonchurch;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.dans.apps.baratonchurch.models.Blog;
import com.dans.apps.baratonchurch.network.Fetcher;
import com.dans.apps.baratonchurch.provider.Contract;
import com.dans.apps.baratonchurch.ui.BlogAdapter;
import com.dans.apps.baratonchurch.utils.LogUtils;

import java.util.ArrayList;

import static android.view.View.GONE;

public class BlogsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    BlogAdapter blogAdapter;
    LinearLayoutManager layoutManager;
    RecyclerView list;
    SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogs);
        Toolbar toolbar = findViewById(R.id.toolbar);
        refreshLayout = findViewById(R.id.refresh_blog);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getLoaderManager().initLoader(0,null,this);

        list  = findViewById(R.id.blog_list);
        layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        blogAdapter = new BlogAdapter(this);
        list.setAdapter(blogAdapter);
        list.setLayoutManager(layoutManager);
        list.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new BlogFetcher(getApplicationContext()).execute();
            }
        });

    }

    public class BlogFetcher extends AsyncTask<Boolean,Void,Boolean> {
        Context context;
        public BlogFetcher(Context context){
            this.context = context;
        }
        @Override
        protected Boolean doInBackground(Boolean... booleans) {
            try{
                Fetcher.updateBlogTable(context);
                return true;
            }catch (Exception e){
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
           if(refreshLayout!=null){
               refreshLayout.setRefreshing(false);
           }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Blog.CONTENT_URI, QueryBlogProperties.PROJECTION,
                null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<Blog>blogs = new ArrayList<>();
        if(data!=null && data.moveToFirst()){
            do{
                String id = data.getString(QueryBlogProperties.ID);
                String date = data.getString(QueryBlogProperties.DATE);
                String title = data.getString(QueryBlogProperties.TITLE);
                String author = data.getString(QueryBlogProperties.AUTHOR);
                String content = data.getString(QueryBlogProperties.CONTENT);

                Blog blog = new Blog(content,author,date,title);
                blog.setId(id);
                blogs.add(blog);
            }while (data.moveToNext());
        }

        blogAdapter.addBlog(blogs);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    interface QueryBlogProperties {
        int id = 4;
        String[] PROJECTION = {
                Contract.Blog.ID,
                Contract.Blog.TITLE,
                Contract.Blog.AUTHOR,
                Contract.Blog.DATE,
                Contract.Blog.CONTENT
        };
        int ID = 0;
        int TITLE = 1;
        int AUTHOR = 2;
        int DATE = 3;
        int CONTENT = 4;
    }

}
