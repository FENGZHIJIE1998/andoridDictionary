package com.example.myfinalwork;

import androidx.appcompat.app.ActionBar;
import androidx.viewpager.widget.ViewPager;


import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import com.example.myfinalwork.fragment.FragmentViewPager;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity {


    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);

        //设置加载页延迟
        setTheme(R.style.AppTheme);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        setStatusBar(R.color.colorMain);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        String[] titles = {"在线搜索", "历史记录", "设置"};


        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(titles[0]));
        tabLayout.addTab(tabLayout.newTab().setText(titles[1]));
        tabLayout.addTab(tabLayout.newTab().setText(titles[2]));
        viewPager = findViewById(R.id.view_pager);
        FragmentViewPager myFragmentViewPager = new FragmentViewPager(getSupportFragmentManager(), titles);
        viewPager.setAdapter(myFragmentViewPager);


        //表示将TabLayout 和Viewpager 进行关联
        tabLayout.setupWithViewPager(viewPager);


    }


}
