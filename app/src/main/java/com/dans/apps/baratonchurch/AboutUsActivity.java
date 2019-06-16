package com.dans.apps.baratonchurch;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dans.apps.baratonchurch.utils.LogUtils;

public class AboutUsActivity extends AppCompatActivity {
    String TAG = "AboutUsActivity";
    TextView chaplain;
    TextView assistants;
    TextView contacts;
    ImageView location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button email = findViewById(R.id.email);
        Button call = findViewById(R.id.call);
        final String chaplainPhoneNumber = getResources().getString(R.string.chaplain_phone_number);
        final String chaplainEmailAddress = getResources().getString(R.string.chaplain_email_address);

        chaplain = findViewById(R.id.chaplain);
        assistants = findViewById(R.id.assistants);
        contacts = findViewById(R.id.contacts);
        location = findViewById(R.id.location);

        chaplain.setText(Html.fromHtml(getResources().getString(R.string.chaplain)));
        assistants.setText(Html.fromHtml(getResources().getString(R.string.assistants)));
        contacts.setText(Html.fromHtml(getResources().getString(R.string.contacts)));

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + chaplainEmailAddress));
                    //intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }catch(Exception e){

                }
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", chaplainPhoneNumber, null)));
                }catch (Exception e){

                }
            }
        });


     /*   location.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                location.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = location.getWidth(); //height is ready
                LogUtils.d(TAG,"created width is "+width);
            }
        });*/
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        Glide.with(this).load(Constants.CHURCH_LOCATION).into(location);
    }
}
