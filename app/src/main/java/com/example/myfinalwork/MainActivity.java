package com.example.myfinalwork;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myfinalwork.fragment.FragmentViewPager;
import com.example.myfinalwork.response.TranslationResponse;
import com.example.myfinalwork.response.child.Web;
import com.example.myfinalwork.utils.Base64Util;
import com.example.myfinalwork.utils.MD5Util;
import com.example.myfinalwork.utils.Sha256Util;
import com.example.myfinalwork.utils.TimeUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.conscrypt.OpenSSLMessageDigestJDK;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity {


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;

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
        ImageView camera = findViewById(R.id.camera);
        camera.setOnClickListener(v -> {
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


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                new ImageTask().execute();
            }
        }
    }


    /**
     * 解析JSON
     */
    private TranslationResponse parseJSON(String json) {
        Gson gson = new Gson();
        TranslationResponse resp = gson.fromJson(json, TranslationResponse.class);
        return resp;
    }

    /**
     * 异步任务
     */
    class ImageTask extends AsyncTask<Void, Integer, Boolean> {

        private TranslationResponse resp;

        private final static String APP_ID = "0cc942aae8ef010c";
        private final static String APP_KEY = "HYZ8DP9glQPtOOhfoONYZdc4luEO9DU7";
        private final static String url = "https://openapi.youdao.com/ocrtransapi";

        /**
         * 发送请求
         *
         * @param params
         * @return
         */
        @Override
        protected Boolean doInBackground(Void... params) {

            byte[] imageData = null;
            // 读取图片字节数组
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();//字节输出流，不需要指定文件，内存中存在
                byte[] flush = new byte[1024 * 10];//设置缓冲，这样便于传输，大大提高传输效率
                int len = -1;//设置每次传输的个数,若没有缓冲的数据大，则返回剩下的数据，没有数据返回-1
                while ((len = inputStream.read(flush)) != -1) {
                    baos.write(flush, 0, len);//每次读取len长度数据后，将其写出
                }
                baos.flush();//刷新管道数据
                byte[] image = baos.toByteArray();//最终获得的字节数组
                inputStream.close();
                //对字节数组Base64编码
                String base64 = "data:image/jpg;base64," + Base64Util.encrypt(image);
                // 返回Base64编码过的字节数组字符串
                System.out.println("本地图片转换Base64:  ");
                System.out.println(base64);
                sendRequestWithOkHttp(base64);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        /**
         * 更新UI
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Boolean result) {

        }

        private TranslationResponse sendRequestWithOkHttp(String base64) {

            try {
                OkHttpClient client = new OkHttpClient();


                String salt = UUID.randomUUID().toString();

                String sign = APP_ID + base64 + salt + APP_KEY;
                RequestBody requestBody = new FormBody.Builder()
                        .add("type", "1")
                        .add("form", "auto")
                        .add("to", "auto")
                        .add("appKey", "0cc942aae8ef010c")
                        .add("salt", salt)
                        .add("sign", MD5Util.encrypt(sign))
                        .add("q", base64)
                        .build();
                System.out.println(salt);

                System.out.println(MD5Util.encrypt(sign));
                Request request = new Request.Builder().url(url).post(requestBody).build();
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();
                System.out.println(responseData);
                // TranslationResponse resp = parseJSON(responseData);
                //  return resp;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

