package com.dans.apps.baratonchurch;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.dans.apps.baratonchurch.models.Blog;
import com.dans.apps.baratonchurch.provider.Contract;
import com.dans.apps.baratonchurch.widget.ReadView;

public class BlogReadActivity extends AppCompatActivity {

    public static final String KEY_BLOG_ID = "blog_id";
    TextView blogTitle;
    ReadView blogContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_read);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        blogTitle = findViewById(R.id.blog_title);
        blogContent = findViewById(R.id.blog_content);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String blogID = getIntent().getExtras().getString(KEY_BLOG_ID,"-1");
        if(blogID!="-1"){
            new BlogFetcher(getApplicationContext(),blogID).execute();
        }
    }


    public class BlogFetcher extends AsyncTask<Void,Void,Blog> {
        Context context;
        String blogID;
        public BlogFetcher(Context context,String blogID){
            this.context = context;
            this.blogID = blogID;
        }

        @Override
        protected Blog doInBackground(Void... voids) {
            Blog blog = null;
            Cursor cursor =
                    context.getContentResolver().
                            query(Contract.Blog.CONTENT_URI,QueryBlogProperties.PROJECTION,
                                    Contract.Blog.ID+" =?",new String[]{blogID},null);
            if(cursor!=null){
                if(cursor.moveToFirst()) {
                    String content = cursor.getString(QueryBlogProperties.CONTENT);
                    String title = cursor.getString(QueryBlogProperties.TITLE);
                    String date = cursor.getString(QueryBlogProperties.DATE);
                    String author = cursor.getString(QueryBlogProperties.AUTHOR);

                    int index = content.indexOf("<div class");
                    if(index!=-1){
                        content = content.substring(0,index);
                    }
                    blog = new Blog(content,author,date,title);
                }
                cursor.close();
            }
            return blog;
        }

        @Override
        protected void onPostExecute(Blog blog) {
            if(blog!=null){
                if(!isDestroyed()){
                    blogContent.loadRead(blog.getContent());
                    blogTitle.setText(blog.getTitle());
                }
            }
        }
    }

    interface QueryBlogProperties {
        int id = 4;
        String[] PROJECTION = {
                Contract.Blog.ID,
                Contract.Blog.CONTENT,
                Contract.Blog.TITLE,
                Contract.Blog.AUTHOR,
                Contract.Blog.DATE,
        };

        int ID = 0;
        int CONTENT = 1;
        int TITLE = 2;
        int AUTHOR = 3;
        int DATE = 4;
    }


}
