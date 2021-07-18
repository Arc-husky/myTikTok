package com.example.mainacticity.placeholder;

import com.example.mainacticity.R;

import java.util.ArrayList;
import java.util.List;

public class videoSet {
    public static List<video> getData() {
        List<video> Datas = new ArrayList<>();
        Datas.add(new video(R.mipmap.cover,"123"));
        Datas.add(new video(R.mipmap.cover,"456"));
        Datas.add(new video(R.mipmap.cover,"789"));
        Datas.add(new video(R.mipmap.cover,"3453"));
        Datas.add(new video(R.mipmap.cover,"654654"));
        Datas.add(new video(R.mipmap.cover,"23423"));
        Datas.add(new video(R.mipmap.cover,"6546"));
        Datas.add(new video(R.mipmap.cover,"3452"));
        Datas.add(new video(R.mipmap.cover,"56454"));
        Datas.add(new video(R.mipmap.cover,"65467"));
        return  Datas;
    }
}
