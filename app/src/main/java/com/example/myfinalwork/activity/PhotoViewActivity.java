package com.example.myfinalwork.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.myfinalwork.R;
import com.example.myfinalwork.utils.Base64Util;

/**
 * 图片张氏模块
 */
public class PhotoViewActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        setStatusBar(R.color.colorBlack);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        String data = getIntent().getExtras().getString("data");
        Bitmap bitmap = Base64Util.stringToBitmap(data);
        ImageView iv = findViewById(R.id.iv_photo);
        Glide.with(this).load(bitmap).into((iv));
        // 单击关闭
        iv.setOnClickListener(v -> finish());
    }
}
