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
    /**
     * 文本翻译接口
     */
    @FormUrlEncoded
    @POST("/api")
    Call<TranslationTextResponse> translationText(@FieldMap Map<String, String> params);

    /**
     * 图片翻译接口
     */
    @POST("/ocrtransapi")
    @FormUrlEncoded
    Call<TranslationImageResponse> translationImage(@FieldMap Map<String, String> params);

}
