package com.example.mainacticity.model;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {
    @SerializedName("result")
    public VideoInfoBean videoInfoBean;
    @SerializedName("success")
    public boolean success;
    @SerializedName("error")
    public String error;
}
