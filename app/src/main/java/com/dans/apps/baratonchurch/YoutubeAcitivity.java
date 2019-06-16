package com.dans.apps.baratonchurch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

/**
 * Created by duncan on 12/21/17.
 */

public class YoutubeAcitivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final int RECOVERY_REQUEST = 1;
    public static final String KEY_VIDEO_ID = "video_id";
    private YouTubePlayerView youTubeView;
    String videoId;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_youtube);
        youTubeView = findViewById(R.id.youtube_view);
        youTubeView.initialize(Constants.YOUTUBE_API_KEY,this);
        Bundle extra = getIntent().getExtras();
        videoId = extra.getString(KEY_VIDEO_ID);
        if(videoId==null){
            Toast.makeText(this,R.string.null_video_id,Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer youTubePlayer, boolean restored) {
        if(!restored){
            youTubePlayer.cueVideo(videoId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult youTubeInitializationResult) {
        if(youTubeInitializationResult.isUserRecoverableError()){
            youTubeInitializationResult.getErrorDialog(this,RECOVERY_REQUEST).show();
        }else{
            String error = String.format(getString(R.string.youtube_player_error),
                    youTubeInitializationResult.toString());
            Toast.makeText(this,error,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RECOVERY_REQUEST){
            getYouTubePlayerProvider().initialize(Constants.YOUTUBE_API_KEY,this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider(){
        return youTubeView;
    }
}
