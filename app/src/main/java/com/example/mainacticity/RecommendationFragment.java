package com.example.mainacticity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainacticity.placeholder.TopicSet;
import com.example.mainacticity.placeholder.video;
import com.example.mainacticity.ui.video.PlayActivity;
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

/**
 * A fragment representing a list of Items.
 */
public class RecommendationFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private MyRecommendationRecyclerViewAdapter myAdapter;
    public static final String RECYCLERVIEW_DATA_LIST = "data-list";
    private Context mContext;
    private ArrayList<String> ids;
    private Thread myThread;
    private List<VideoInfoBean> lst;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 100:
                    for(int i = 0;i<lst.size();i++) {
                        VideoInfoBean data = lst.get(i);
                        myAdapter.addData(myAdapter.getItemCount(),new video(data.getStudentId(),
                                data.getUserName(),
                                data.getImageUrl(),
                                data.getVideoUrl(),
                                "123"));
                    };
            }
        }
    };
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecommendationFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecommendationFragment newInstance(int columnCount, ArrayList<String> ids) {
        RecommendationFragment fragment = new RecommendationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putStringArrayList(RECYCLERVIEW_DATA_LIST,ids);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            ids = getArguments().getStringArrayList(RECYCLERVIEW_DATA_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View recomm = inflater.inflate(R.layout.fragment_video_list, container, false);
        View view1 = recomm.findViewById(R.id.list);
        View view2 = recomm.findViewById(R.id.horizontalList);
        myAdapter = new MyRecommendationRecyclerViewAdapter(getParentFragment(),new ArrayList<>());
        myAdapter.setOnItemClickListener(new MyRecommendationRecyclerViewAdapter.IOnItemClickListener() {
            @Override
            public void onItemCLick(int position, video data) {
                Intent intent = new Intent(getParentFragment().getActivity(), PlayingActivity.class);
                intent.putExtra(PlayActivity.RECYCLERVIEW_VIDEO_INDEX, position);
                intent.putExtra(PlayActivity.RECYCLERVIEW_VIDEO_LIST, (Serializable) lst);
                startActivity(intent);
            }

            @Override
            public void onItemLongCLick(int position, video data) {

            }
        });
        // Set the adapter
        if (view1 instanceof RecyclerView) {
            Context context = view1.getContext();
            RecyclerView recyclerView = (RecyclerView) view1;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(myAdapter);
        }

        if (view2 instanceof RecyclerView) {
            Context context = view2.getContext();
            RecyclerView recyclerView = (RecyclerView) view2;
            recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
            recyclerView.setAdapter(new HorizontalListAdapter(TopicSet.getData()));
        }
        getData(ids);
        return recomm;
    }

    private void getData(List<String> studentIds){
        myThread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                List<VideoInfoBean> messages = GetMessageFromRemote(studentIds);
                if(messages !=null) {
                    lst = messages;
                    mHandler.sendEmptyMessage(100);
                }
            }
        });
        myThread.start();
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
                Toast.makeText(getParentFragment().getActivity(),"网络连接失败",Toast.LENGTH_SHORT).show();
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getParentFragment().getActivity(),"网络异常"+e.toString(),Toast.LENGTH_SHORT).show();
        }
        if(messages == null) return null;
        else if(messages.success) return messages.feeds;
        else {
            Toast.makeText(getParentFragment().getActivity(),"网络连接失败",Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}