package com.example.mainacticity.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mainacticity.MainActivity;
import com.example.mainacticity.R;
import com.example.mainacticity.RecommendationFragment;
import com.example.mainacticity.RecordActivity;
import com.example.mainacticity.UploadActivity;
import com.example.mainacticity.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private PopupWindow mPopWindow;
    private FragmentHomeBinding binding;
    private int PAGE_COUNT = 2;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private String MY_ID;
    private ArrayList<String> idList;
    private final static int PERMISSION_REQUEST_CODE = 1001;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        initViews();
        ViewPager pager = root.findViewById(R.id.view_pager);
        pager.setAdapter(new FragmentAdapter(getChildFragmentManager(),mFragmentList) {
            @Override
            public Fragment getItem(int i) {
                return fragmentList.get(i);
            }

            @Override
            public int getCount() {
                return PAGE_COUNT;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                CharSequence str = "";
                switch (position) {
                    case 0: str = "关注"; break;
                    case 1:
                        str = "推荐";
                        break;
                    default:
                        str = "错误";
                        break;
                }
                return str;
            }
        });
        pager.setCurrentItem(0);//初始设置ViewPager选中第一帧
        pager.setOffscreenPageLimit(2);
        TabLayout tabLayout = root.findViewById(R.id.tab_Layout);
        tabLayout.setupWithViewPager(pager);
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        MY_ID=null;
    }

    private void initViews() {
        RecommendationFragment fragment = RecommendationFragment.newInstance(2,new ArrayList<>(),MY_ID);
        RecommendationFragment fragment1;
        if(idList.isEmpty()) {
            fragment1 = RecommendationFragment.newInstance(2,new ArrayList<>(),MY_ID);
        }else {
            fragment1 = RecommendationFragment.newInstance(2,new ArrayList<>(),MY_ID);
        }
        //给FragmentList添加数据
        mFragmentList.add(fragment1);
        mFragmentList.add(fragment);
    }

    private void showPopupWindow() {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.upload, null);
        mPopWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.showAsDropDown(getView().findViewById(R.id.fab));
        View upload = contentView.findViewById(R.id.imageView2);
        View shoot = contentView.findViewById(R.id.imageView3);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UploadActivity.class);
                startActivity(intent);
            }
        });
        shoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });
    }

    private void requestPermission() {
        boolean hasCameraPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean hasAudioPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        if (hasCameraPermission && hasAudioPermission) {
            recordVideo();
        } else {
            List<String> permission = new ArrayList<String>();
            if (!hasCameraPermission) {
                permission.add(Manifest.permission.CAMERA);
            }
            if (!hasAudioPermission) {
                permission.add(Manifest.permission.RECORD_AUDIO);
            }
            ActivityCompat.requestPermissions(getActivity(), permission.toArray(new String[permission.size()]), PERMISSION_REQUEST_CODE);
        }

    }

    private void recordVideo() {
        Intent intent = new Intent(getActivity(), RecordActivity.class);
        startActivity(intent);
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
            Toast.makeText(getActivity(), "权限获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    public class FragmentAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<Fragment>();

        public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MY_ID = ((MainActivity)context).getMY_ID();
        idList = ((MainActivity) context).getGzid();
    }

}