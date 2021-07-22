package com.example.mainacticity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.mainacticity.databinding.ActivityMainBinding;
import com.example.mainacticity.placeholder.video;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String MY_ID = null;
    public static final String ID_SAVED = "id_saved";
    public static final String MY_ID_SAVE_KEY = "my-id";
    private List<video> videoList = new ArrayList<>();
    public final static String MY_GZ_LIST = "gz-list";
    public final static String MY_GZ_LIST_SIZE = "gz-Size";
    NavController navController;
//    Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 100:
//                    SharedPreferences sp = getSharedPreferences(ID_SAVED, MODE_PRIVATE);
//                    gzid.clear();
//                    int size=sp.getInt(MY_GZ_LIST_SIZE+MY_ID,0);
//                    for(int i=0;i<size;i++) {
//                        gzid.add ( sp.getString(MY_GZ_LIST+MY_ID+i,null));
//                    }
//            }
//        }
//    };
    private ArrayList<String> gzid = new ArrayList<>();

    public ArrayList<String> getGzid() {return gzid;}

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
        MY_ID = sp.getString(MY_ID_SAVE_KEY, null);
//        mHandler.sendEmptyMessage(100);
        Window window = getWindow();
        changeStatusBarTextColor(window, true);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_upload);
        NavigationUI.setupWithNavController(binding.navView, navController);
        navController.navigate(R.id.navigation_notifications);
        if (MY_ID == null) {
            Intent intent = new Intent(MainActivity.this, loginActivity.class);
            startActivityForResult(intent, 101);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            MY_ID = data.getStringExtra(MY_ID_SAVE_KEY);
            navController.navigate(R.id.navigation_home);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mHandler.sendEmptyMessage(100);
//        navController.navigate(R.id.navigation_mine);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = getSharedPreferences(ID_SAVED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(MY_ID_SAVE_KEY,MY_ID);
        editor.putInt(MY_GZ_LIST_SIZE+MY_ID, gzid.size());
        for (int i = 0; i < gzid.size(); i++) {
            editor.putString(MY_GZ_LIST+MY_ID+i, gzid.get(i));
        }
        editor.apply();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermission = true;
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                hasPermission = false;
                break;
            }
        }
        if (hasPermission) {
            recordVideo();
        } else {
            Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void recordVideo() {
        Intent intent = new Intent(this, RecordActivity.class);
        intent.putExtra(MY_ID_SAVE_KEY,MY_ID);
        startActivity(intent);
    }

}