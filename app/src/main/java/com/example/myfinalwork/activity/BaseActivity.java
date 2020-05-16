package com.example.myfinalwork.activity;

import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myfinalwork.R;

public class BaseActivity extends AppCompatActivity {
    /**
     * 设置通知栏颜色
     */
    public void setStatusBar(int color) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(color));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }
}
