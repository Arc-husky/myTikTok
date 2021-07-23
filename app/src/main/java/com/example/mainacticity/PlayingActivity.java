package com.example.mainacticity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainacticity.model.VideoInfoBean;
import com.example.mainacticity.ui.video.OnViewPagerListener;
import com.example.mainacticity.ui.video.ViewPagerLayoutManager;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * ViewPagerLayoutManager
 */
public class PlayingActivity extends AppCompatActivity {
    public final static String RECYCLERVIEW_VIDEO_LIST = "videos";
    public final static String RECYCLERVIEW_VIDEO_INDEX = "index";
    public final static String MY_ID_SAVE_KEY = "my-id";
    private static final String TAG = "ViewPagerActivity";
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private ViewPagerLayoutManager mLayoutManager;
    private List<VideoInfoBean> videos;
    private String myid;
    private int index_v;
    private HashMap<String,Object> gzids = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initView();

        initListener();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = getSharedPreferences(MainActivity.ID_SAVED, MODE_PRIVATE);
                int size=sp.getInt(MainActivity.MY_GZ_LIST_SIZE+myid,0);
                for(int i=0;i<size;i++) {
                    String key = sp.getString(MainActivity.MY_GZ_LIST+myid+i,null);
                    if(key!=null)
                        gzids.put(key,1);
                }
            }
        }).start();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler);
        Intent intent = getIntent();
        videos = (List<VideoInfoBean>) intent.getSerializableExtra(RECYCLERVIEW_VIDEO_LIST);
        index_v = intent.getIntExtra(RECYCLERVIEW_VIDEO_INDEX, 0);
        myid = intent.getStringExtra(MY_ID_SAVE_KEY);
        mLayoutManager = new ViewPagerLayoutManager(this, OrientationHelper.VERTICAL);
        mAdapter = new MyAdapter();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListener() {
        mLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {
                Log.e(TAG, "onInitComplete");
                playVideo(0);
            }
            @Override
            public void onPageRelease(boolean isNext, int position) {
                Log.e(TAG, "释放位置:" + position + " 下一页:" + isNext);
                int index = 0;
                if (isNext) {
                    index = 0;
                } else {
                    index = 1;
                }
                releaseVideo(index);
            }
            @Override
            public void onPageSelected(int position, boolean isBottom) {
                Log.e(TAG, "选中位置:" + position + "  是否是滑动到底部:" + isBottom);
                playVideo(0);
            }


        });
    }

    private void playVideo(int position) {
        View itemView = mRecyclerView.getChildAt(0);
        final VideoView videoView = itemView.findViewById(R.id.video_view);
//        final ImageView imgPlay = itemView.findViewById(R.id.img_play);
        final ImageView imgThumb = itemView.findViewById(R.id.img_thumb);
        final RelativeLayout rootView = itemView.findViewById(R.id.root_view);
        final MediaPlayer[] mediaPlayer = new MediaPlayer[1];
        videoView.start();
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                mediaPlayer[0] = mp;
                mp.setLooping(true);
                imgThumb.animate().alpha(0).setDuration(200).start();
                return false;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            }
        });


//        imgPlay.setOnClickListener(new View.OnClickListener() {
//            boolean isPlaying = true;
//            @Override
//            public void onClick(View v) {
//                if (videoView.isPlaying()){
//                    imgPlay.animate().alpha(1f).start();
//                    videoView.pause();
//                    isPlaying = false;
//                }else {
//                    imgPlay.animate().alpha(0f).start();
//                    videoView.start();
//                    isPlaying = true;
//                }
//            }
//        });
    }

    private void releaseVideo(int index) {
        View itemView = mRecyclerView.getChildAt(index);
        final VideoView videoView = itemView.findViewById(R.id.video_view);
        final ImageView imgThumb = itemView.findViewById(R.id.img_thumb);
        //final ImageView imgPlay = itemView.findViewById(R.id.img_play);
        videoView.stopPlayback();
        imgThumb.animate().alpha(1).start();
        //imgPlay.animate().alpha(0f).start();
    }

    private Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            System.out.println("Exc=" + e);
            return null;
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        //private int[] imgs = {R.mipmap.img_video_1,R.mipmap.img_video_2};
        //videos = {R.raw.video_1,R.raw.video_2};
        private int now_index;
        public MyAdapter() {
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_playing, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    holder.drawable = LoadImageFromWebOperations(videos.get(((index_v) + position + (videos.size())) % videos.size()).getImageUrl());
                    holder.mHandler.sendEmptyMessage(100);
                }
            }).start();
            now_index = ((index_v) + position + (videos.size())) % videos.size();
            SharedPreferences sp = getSharedPreferences(MainActivity.ID_SAVED, MODE_PRIVATE);
            VideoInfoBean vinfo = videos.get(now_index);
            if(gzids.containsKey(vinfo.getStudentId())) {
                holder.gzbtn.setImageResource(R.mipmap.ygzbtn);
                holder.ygz = true;
            }
            holder.publishername.setText(videos.get(now_index).getUserName());
            holder.gzbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!holder.ygz) {
                        gzids.put(vinfo.getStudentId(),1);
                        holder.gzbtn.setImageResource(R.mipmap.ygzbtn);
                        int size = sp.getInt(MainActivity.MY_GZ_LIST_SIZE + myid, 0);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt(MainActivity.MY_GZ_LIST_SIZE + myid, size + 1);
                        editor.putString(MainActivity.MY_GZ_LIST + myid + size, vinfo.getStudentId());
                        editor.apply();
                    }else {
                        gzids.remove(vinfo.getStudentId());
                        holder.gzbtn.setImageResource(R.mipmap.gzbtn);
                        int size = sp.getInt(MainActivity.MY_GZ_LIST_SIZE + myid, 0);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt(MainActivity.MY_GZ_LIST_SIZE + myid, size - 1);
                        editor.remove(MainActivity.MY_GZ_LIST + myid + size);
                        editor.apply();
                    }
                    holder.ygz = !holder.ygz;
                }
            });
            holder.Personalbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PlayingActivity.this, PersonalPage.class);
                    intent.putExtra(PersonalPage.PERSON_ID,vinfo.getStudentId());
                    intent.putExtra(PersonalPage.USERNAME_KEY,vinfo.getUserName());
                    startActivity(intent);
                }
            });
            holder.dzbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.ydz) {
                        holder.dzbtn.setImageResource(R.mipmap.ydz);
                    }else {
                        holder.dzbtn.setImageResource(R.mipmap.dz);
                    }
                    holder.ydz = !holder.ydz;
                }
            });
            holder.videoView.setVideoURI(Uri.parse(videos.get(((index_v) + position + (videos.size())) % videos.size()).getVideoUrl()));
        }

        @Override
        public int getItemCount() {
            return 20;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img_thumb;
            VideoView videoView;
            //ImageView img_play;
            Drawable drawable;
            RelativeLayout rootView;
            ImageButton gzbtn,dzbtn;
            My_ImageViewPlus Personalbtn;
            TextView publishername;
            boolean ygz = false,ydz=false;
            private Handler mHandler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 100:
                            img_thumb.setImageDrawable(drawable);

                    }
                }
            };

            public ViewHolder(View itemView) {
                super(itemView);
                img_thumb = itemView.findViewById(R.id.img_thumb);
                videoView = itemView.findViewById(R.id.video_view);
                //img_play = itemView.findViewById(R.id.img_play);
                rootView = itemView.findViewById(R.id.root_view);
                gzbtn = itemView.findViewById(R.id.gzbtn);
                Personalbtn = itemView.findViewById(R.id.zztx);
                dzbtn = itemView.findViewById(R.id.dzbtn);
                publishername = itemView.findViewById(R.id.idTextView);
            }
        }
    }
}
