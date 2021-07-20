package com.example.mainacticity.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mainacticity.R;
import com.example.mainacticity.RecommendationFragment;
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
    }

    private void initViews() {
        //给FragmentList添加数据
        mFragmentList.add(new RecommendationFragment());
        mFragmentList.add(new RecommendationFragment());
    }

    private void showPopupWindow() {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.upload, null);
        mPopWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.showAsDropDown(getView().findViewById(R.id.fab));
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