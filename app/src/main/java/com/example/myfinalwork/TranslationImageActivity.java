package com.example.myfinalwork;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myfinalwork.response.TranslationImageResponse;
import com.example.myfinalwork.utils.Base64Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

public class TranslationImageActivity extends BaseActivity {
    public static final int SCREEN_ORIENTATION_SENSOR_LANDSCAPE = 6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tranlastion_image);
        TranslationImageResponse response = (TranslationImageResponse) this.getIntent().getExtras().getSerializable("data");
        setStatusBar(R.color.colorMain);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }



        findViewById(R.id.back).setOnClickListener(v -> finish());

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


    }
}
