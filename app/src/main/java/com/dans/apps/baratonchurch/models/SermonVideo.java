package com.dans.apps.baratonchurch.models;

/**
 * Created by duncan on 12/21/17.
 */

public class SermonVideo {
    String videoId;
    String channelId;
    String pulishedAt;
    String title;
    String thumbnailUrl;
    String mediumThumbnailUrl;
    boolean isFavourite;

    public SermonVideo(String videoId, String channelId, String pulishedAt,
                       String title, String thumbnailUrl, String mediumUrl,boolean isFavourute) {
        this.videoId = videoId;
        this.channelId = channelId;
        this.pulishedAt = pulishedAt;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.mediumThumbnailUrl = mediumUrl;
        this.isFavourite = isFavourute;
    }

    public String getMediumThumbnailUrl(){
        return mediumThumbnailUrl;
    }
    public String getVideoId() {
        return videoId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getPulishedAt() {
        return pulishedAt;
    }

    public String getTitle() {
        return title;
    }
    public boolean isFavourite(){
        return isFavourite;
    }
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
