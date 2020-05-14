package com.example.myfinalwork.fragment;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfinalwork.R;
import com.example.myfinalwork.dao.HistorySearchDao;


public class SettingFragment extends Fragment {

    private View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        HistorySearchDao historySearchDao = new HistorySearchDao(view.getContext(), "history_word", null, 2);

        ImageView imageView = view.findViewById(R.id.head_image);
        // 设置图片
        RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(360));
        Glide.with(this).load(R.mipmap.head) //图片地址
                .apply(options)
                .into(imageView);
        Button clearHistoryBtn = view.findViewById(R.id.clear_history_btn);
        clearHistoryBtn.setOnClickListener(v -> {
            SQLiteDatabase db = historySearchDao.getReadableDatabase();
            db.execSQL("delete  from history_word");
            Toast.makeText(view.getContext(), "清除完成", Toast.LENGTH_LONG).show();
            db.close();
        });

        return view;
    }


}
