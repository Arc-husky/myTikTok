package com.example.mainacticity.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoListResponse {
    @SerializedName("feeds")
    public List<VideoInfoBean> feeds;
    @SerializedName("success")
    public boolean success;
}
