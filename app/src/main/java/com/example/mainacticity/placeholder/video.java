package com.example.mainacticity.placeholder;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class video implements Serializable {

    private static final long serialVersionUID = 1L;

    public final String studentId;
    public final String userName;
    public final String cover;
    public final String videoUrl;
    public final String likeNumber;

    public video(String studentId,String userName,String cover,String videoUrl, String likeNumber) {
        this.cover = cover;
        this.likeNumber = likeNumber;
        this.studentId = studentId;
        this.userName = userName;
        this.videoUrl = videoUrl;
    }
}
