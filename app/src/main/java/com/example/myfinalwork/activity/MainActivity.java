package com.example.myfinalwork.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;


import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;

import android.os.AsyncTask;
import android.os.Bundle;

import android.provider.MediaStore;

import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myfinalwork.R;
import com.example.myfinalwork.fragment.FragmentViewPager;
import com.example.myfinalwork.response.TranslationImageResponse;
import com.example.myfinalwork.response.TranslationTextResponse;

import com.example.myfinalwork.retrofit.PostRequestInterface;
import com.example.myfinalwork.retrofit.RetrofitFactory;
import com.example.myfinalwork.utils.Base64Util;

import com.example.myfinalwork.utils.MD5Util;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;


import java.io.File;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 主活动
 */
public class MainActivity extends BaseActivity {

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 设置键盘不会影响布局
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
        // 设置通知栏颜色
        setStatusBar(R.color.colorMain);
        // 隐藏导航栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // 设置TabLayout
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

        // 设置相机模式
        ImageView camera = findViewById(R.id.camera);
        camera.setOnClickListener(v -> {
            /**
             * 设置对话窗
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("!");
            builder.setMessage("请选择图片获取方式");
            builder.setPositiveButton("相机", (dialog, which) -> {
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.myfinalwork.fileprovider", outputImage);
                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            });
            builder.setNegativeButton("相册", (dialog, which) -> {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            });
            //一样要show
            builder.show();
        });
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    /**
     * 权限请求返回结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                new ImageTask().execute();
            }
        } else if (requestCode == CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                imageUri = intent.getData();
                new ImageTask().execute();
            }
        }
    }

    /**
     * 异步任务
     */
    private class ImageTask extends AsyncTask<Void, Integer, Boolean> {

        private TranslationImageResponse resp;
        private final static String APP_ID = "0cc942aae8ef010c";
        private final static String APP_KEY = "sBaPGIUEoBJwXQbKero8qvpWeHtHIGqB";

        /**
         * 发送请求
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            // 读取图片字节数组
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                assert inputStream != null;
                String base64 = Base64Util.loadAsBase64(inputStream);

                //resp = new Gson().fromJson(getFromRaw(), TranslationImageResponse.class);

                sendRequestWithOkHttp(base64);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        /**
         *         从resources中的raw 文件夹中获取文件并读取数据
         */
//        public String getFromRaw() {
//            String result = "";
//            try {
//                InputStream in = getResources().openRawResource(R.raw.output);
//                //获取文件的字节数
//                //创建byte数组
//                byte[] buffer = new byte[in.available()];
//                //将文件中的数据读到byte数组中
//                in.read(buffer);
//                result = new String(buffer, "UTF-8");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return result;
//        }

        /**
         * 跳转新页面
         */
        @Override
        protected void onPostExecute(Boolean result) {
            System.out.println(resp);
            Intent intent = new Intent(MainActivity.this, TranslationImageActivity.class);
            //用Bundle携带数据
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", resp);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        /**
         * 发送请求到有道智云
         */
        private void sendRequestWithOkHttp(String base64) {
            try {
                PostRequestInterface request = RetrofitFactory.create(PostRequestInterface.class);
                String salt = String.valueOf(System.currentTimeMillis());
                String sign = APP_ID + base64 + salt + APP_KEY;
                Map<String, String> map = new HashMap<>();
                map.put("type", "1");
                map.put("from", "auto");
                map.put("to", "auto");
                map.put("appKey", APP_ID);
                map.put("salt", salt);
                map.put("sign", MD5Util.encrypt(sign));
                map.put("q", base64);
                map.put("render", "1");
                Call<TranslationImageResponse> call = request.translationImage(map);
                Response<TranslationImageResponse> response = call.execute();
                resp = response.body();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

