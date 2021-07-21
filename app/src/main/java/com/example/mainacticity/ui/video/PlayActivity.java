package com.example.mainacticity.ui.video;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainacticity.R;
import com.example.mainacticity.VideoInfoBean;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/***
 *
 */
public class PlayActivity extends AppCompatActivity {
    public final static String RECYCLERVIEW_VIDEO_LIST = "videos";
    public final static String RECYCLERVIEW_VIDEO_INDEX = "index";
    private static final String TAG = "--------------->";
    MyLayoutManager myLayoutManager;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private List<VideoInfoBean> videos;
    private int index;
    private OrientationUtils orientationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initView();
        initListener();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler);
        myLayoutManager = new MyLayoutManager(this, OrientationHelper.VERTICAL, false);
        Intent intent = getIntent();
        videos = (List<VideoInfoBean>) intent.getSerializableExtra(RECYCLERVIEW_VIDEO_LIST);
        index = intent.getIntExtra(RECYCLERVIEW_VIDEO_INDEX, 0);
        mAdapter = new MyAdapter();
        mRecyclerView.setLayoutManager(myLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListener() {
        myLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onInitComplete() {

            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
                Log.e(TAG, "释放位置:" + position + " 下一页:" + isNext);
                int pos = 0;
                if (isNext) {
                    pos = 0;
                } else {
                    pos = 1;
                }
                releaseVideo(pos);
            }

            @Override
            public void onPageSelected(int position, boolean isNext) {
                Log.e(TAG, "释放位置:" + position + " 下一页:" + isNext);

                int index = 0;
                if (isNext) {
                    index = 0;
                } else {
                    index = 1;
                }
                playVideo(index);
            }
        });
    }

    private void releaseVideo(int ind) {
        View itemView = mRecyclerView.getChildAt(ind);
        final StandardGSYVideoPlayer videoPlayer = itemView.findViewById(R.id.video_view);
        videoPlayer.onVideoPause();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void playVideo(int position) {
        Log.d(TAG, "Play" + Integer.toString(index) + "Video");
        View itemView = mRecyclerView.getChildAt(0);
        final StandardGSYVideoPlayer videoPlayer = itemView.findViewById(R.id.video_view);

        videoPlayer.startPlayLogic();

/**
 *
 */
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (orientationUtils != null) {
            orientationUtils.releaseListener();
        }
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        public MyAdapter() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_pager, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //增加封面
            ImageView imageView = new ImageView(PlayActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //Drawable drawable = LoadImageFromWebOperations(videos.get(index).getImageUrl());
            //imageView.setImageDrawable(drawable);
            holder.videoPlayer.setUp(videos.get((index + position + videos.size()) % videos.size()).getVideoUrl(), false, "测试视频");

            holder.videoPlayer.setThumbImageView(imageView);
            //增加title
            holder.videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
            //设置返回键
            holder.videoPlayer.getBackButton().setVisibility(View.VISIBLE);
            //设置旋转
            orientationUtils = new OrientationUtils(PlayActivity.this, holder.videoPlayer);
            //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
            holder.videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orientationUtils.resolveByClick();
                }
            });
            //是否可以滑动调整
            holder.videoPlayer.setIsTouchWiget(true);
            //设置返回按键功能
            holder.videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            holder.videoPlayer.startPlayLogic();
        }

        @Override
        public int getItemCount() {
            return 15;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img_thumb;
            ImageView img_play;
            StandardGSYVideoPlayer videoPlayer;
            RelativeLayout rootView;

            public ViewHolder(View itemView) {
                super(itemView);
                videoPlayer = itemView.findViewById(R.id.video_view);
            }
        }
    }

}