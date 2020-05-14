package com.example.myfinalwork;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myfinalwork.utils.Base64Util;

import lombok.val;

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

        iv.setOnClickListener(v -> finish());
    }
}
