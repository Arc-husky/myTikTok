package com.example.mainacticity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainacticity.model.VideoInfoBean;
import com.example.mainacticity.model.VideoListResponse;
import com.example.mainacticity.placeholder.video;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PersonalPage extends AppCompatActivity {
    private final int mColumnCount = 2;
    public static final String PERSON_ID = "person-id";
    private String MY_ID = "";
    private final String TEXT_BEGIN = "ID:";
    private List<VideoInfoBean> videoList;
    private MyRecommendationRecyclerViewAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);

        Intent intent = getIntent();
        MY_ID = intent.getStringExtra(PERSON_ID);
        TextView idView = findViewById(R.id.userId1);
        idView.setText(TEXT_BEGIN+MY_ID);

        ImageButton exitBtn = findViewById(R.id.exitPersonal);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        videoList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.rec);
        myAdapter = new MyRecommendationRecyclerViewAdapter(this, new ArrayList<>());
        myAdapter.setOnItemClickListener(new MyRecommendationRecyclerViewAdapter.IOnItemClickListener() {
            @Override
            public void onItemCLick(int position, video data) {
                Intent intent;
                intent = new Intent(PersonalPage.this, PlayingActivity.class);
                intent.putExtra(PlayingActivity.RECYCLERVIEW_VIDEO_INDEX, position);
                intent.putExtra(PlayingActivity.RECYCLERVIEW_VIDEO_LIST, (Serializable) videoList);
                intent.putExtra(PlayingActivity.MY_ID_SAVE_KEY, MY_ID);
                startActivity(intent);
            }

            @Override
            public void onItemLongCLick(int position, video data) {

            }
        });
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), mColumnCount));
        }
        recyclerView.setAdapter(myAdapter);
        List<String> ids = new ArrayList<>();
        ids.add(MY_ID);
        getData(ids);
    }

    private void getData(List<String> studentIds){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                List<VideoInfoBean> messages = GetMessageFromRemote(studentIds);
                if(messages !=null) {
                    videoList.addAll(messages);
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < messages.size(); i++) {
                                VideoInfoBean data = messages.get(i);
                                myAdapter.addData(myAdapter.getItemCount(), new video(data.getStudentId(),
                                        data.getUserName(),
                                        data.getImageUrl(),
                                        data.getVideoUrl(),
                                        "123"));
                            }
                        }
                    });
                }
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private List<VideoInfoBean> GetMessageFromRemote(List<String> studentIds)
    {
        String urlStr = Constants.BASE_URL+"video";
        if(!studentIds.isEmpty()) {
            for(int i = 0;i<studentIds.size();i++) {
                if(i!=0) {
                    urlStr += "|";
                }else {
                    urlStr += "?";
                }
                urlStr += "student_id=" + studentIds.get(i);
            }
        }
        VideoListResponse messages = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(6000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("token",Constants.token);
            if(conn.getResponseCode()==200) {
                InputStream in = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                messages = new Gson().fromJson(bufferedReader,
                        new TypeToken<VideoListResponse>(){}.getType());
                bufferedReader.close();
                in.close();
            }else {
                Toast.makeText(this,"网络连接失败",Toast.LENGTH_SHORT).show();
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"网络异常"+e.toString(),Toast.LENGTH_SHORT).show();
        }
        if(messages == null) return null;
        else if(messages.success) return messages.feeds;
        else {
            Toast.makeText(this,"网络连接失败",Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}