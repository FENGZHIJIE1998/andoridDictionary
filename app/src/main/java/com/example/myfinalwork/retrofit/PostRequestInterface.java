package com.example.myfinalwork.retrofit;

import com.example.myfinalwork.response.TranslationImageResponse;
import com.example.myfinalwork.response.TranslationTextResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface PostRequestInterface {
    @FormUrlEncoded
    @POST("/api")
    Call<TranslationTextResponse> translationText(@FieldMap Map<String, String> params);


    @POST("/ocrtransapi")
    @FormUrlEncoded
    Call<TranslationImageResponse> translationImage(@FieldMap Map<String, String> params);
    // 注解里传入 网络请求 的部分URL地址ocrtransapi
    // Retrofit把网络请求的URL分成了两部分：一部分放在Retrofit对象里，另一部分放在网络请求接口里
    // 如果接口里的url是一个完整的网址，那么放在Retrofit对象里的URL可以忽略
    // getCall()是接受网络请求数据的方法
}
