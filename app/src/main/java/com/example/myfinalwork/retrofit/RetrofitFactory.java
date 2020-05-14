package com.example.myfinalwork.retrofit;


import android.nfc.Tag;
import android.util.Log;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitFactory {
    private static HttpLoggingInterceptor interceptor;
    private static Retrofit retrofit;
    private static final String url = "https://openapi.youdao.com/";

    private RetrofitFactory() {
    }

    private static final String TAG = "RetrofitFactory";

    static {
        //通用拦截
        interceptor = new HttpLoggingInterceptor(message -> {
            try {
                String text = URLDecoder.decode(message, "utf-8");
                Log.d(TAG, text);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e(TAG, message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //Retrofit实例化
        retrofit = new Retrofit.Builder()
                .baseUrl(url) //设置网络请求的Url地址
                .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(initClient())
                .build();
    }

    /*
       OKHttp创建
    */
    private static OkHttpClient initClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(interceptor);
        return httpClient.build();
    }

    /*
        具体服务实例化
     */
    public static <T> T create(Class<T> cla) {
        return retrofit.create(cla);
    }


}

