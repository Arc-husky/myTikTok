package com.example.mainacticity.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.mainacticity.R;
import com.example.mainacticity.RecommendationFragment;
import com.example.mainacticity.databinding.FragmentHomeBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private int PAGE_COUNT = 2;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
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
                    case 1: str = "推荐"; break;
                    default: str = "错误"; break;
                }
                return str;
            }
        });
        pager.setCurrentItem(0);//初始设置ViewPager选中第一帧
        pager.setOffscreenPageLimit(2);
        TabLayout tabLayout = root.findViewById(R.id.tab_Layout);

        tabLayout.setupWithViewPager(pager);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initViews() {
        //给FragmentList添加数据
        mFragmentList.add(new RecommendationFragment());
        mFragmentList.add(new RecommendationFragment());
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
}