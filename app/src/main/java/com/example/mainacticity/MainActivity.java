package com.example.mainacticity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.mainacticity.databinding.ActivityMainBinding;
import com.example.mainacticity.model.VideoInfoBean;
import com.example.mainacticity.model.VideoListResponse;
import com.example.mainacticity.placeholder.video;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String MY_ID;
    private final String ID_SAVED = "id_saved";
    private final String MY_ID_SAVE_KEY = "my-id";
    private List<video> videoList = new ArrayList<>();

    public List<video> getVideoList() {return videoList;}

    public String getMY_ID() {
        return MY_ID;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sp = MainActivity.this.getSharedPreferences(ID_SAVED, MODE_PRIVATE);
        MY_ID = sp.getString(MY_ID_SAVE_KEY,"3190101936");

        Window window = getWindow();
        changeStatusBarTextColor(window,true);
        BottomNavigationView navView = findViewById(R.id.nav_view);
//         Passing each menu ID as a set of Ids because each
//         menu should be considered as top level destinations.

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_upload);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

    public void changeStatusBarTextColor(Window window, boolean isBlack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = window.getDecorView();
            int flags = 0;
            if (isBlack) {
                flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            }
            decor.setSystemUiVisibility(flags);
        }
    }

    private void getData(List<String> studentIds){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                List<VideoInfoBean> messages = GetMessageFromRemote(studentIds);
                if(messages !=null) {
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            for(int i = 0;i<messages.size();i++) {
                                VideoInfoBean data = messages.get(i);
                                videoList.add(new video(data.getStudentId(),
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = MainActivity.this.getSharedPreferences(ID_SAVED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(MY_ID_SAVE_KEY,MY_ID);
        editor.apply();
    }
}