package com.example.myfinalwork.fragment;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class FragmentViewPager extends FragmentPagerAdapter {

    private String[] mTitles;


    public FragmentViewPager(FragmentManager fm, String[] titles) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new SearchFragment();
            case 1:
                return new HistoryFragment();
            case 2:
                return new SettingFragment();
        }
        return new SearchFragment();
    }



    @Override
    public int getCount() {
        return mTitles.length;
    }


    /*
     *
     * 该函数是搭配TabLayout 布局所需重写的 ,如若不绑定TabLayout 布局，那么可以不重写
     *   mTl.setupWithViewPager(mVp);
     * */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
