package com.example.myfinalwork.activity;

import androidx.appcompat.app.ActionBar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myfinalwork.R;
import com.example.myfinalwork.response.TranslationImageResponse;
import com.example.myfinalwork.utils.Base64Util;

/**
 * 翻译图片活动
 */
public class TranslationImageActivity extends BaseActivity {
    public static final int SCREEN_ORIENTATION_SENSOR_LANDSCAPE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tranlastion_image);
        // 获取传进来的数据
        TranslationImageResponse response = (TranslationImageResponse) this.getIntent().getExtras().getSerializable("data");
        setStatusBar(R.color.colorMain);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        TextView context = findViewById(R.id.context);
        TextView tranContent = findViewById(R.id.tran_content);

        findViewById(R.id.back).setOnClickListener(v -> finish());
        // 如果数据存在
        if (response != null && response.getResRegions() != null && !response.getResRegions().isEmpty()) {
            // 设置原文
            context.setText(response.getResRegions().get(0).getContext());
            // 设置译文
            tranContent.setText(response.getResRegions().get(0).getTranContent());
            // 设置图片
            ImageView renderImg = findViewById(R.id.render_image);
            Bitmap bitmap = Base64Util.stringToBitmap(response.getRender_image());
            renderImg.setImageBitmap(bitmap);
            // 设置图片放大
            renderImg.setOnClickListener(v -> {
                Intent intent = new Intent(TranslationImageActivity.this, PhotoViewActivity.class);
                //用Bundle携带数据
                Bundle bundle = new Bundle();
                bundle.putString("data", response.getRender_image());
                intent.putExtras(bundle);
                startActivity(intent);
            });
        } else {
            context.setText("没有识别到图片，请重试");
        }

    }
}
