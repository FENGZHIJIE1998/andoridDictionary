package com.example.myfinalwork.response;

import com.example.myfinalwork.response.child.Basic;
import com.example.myfinalwork.response.child.Dict;
import com.example.myfinalwork.response.child.Web;
import com.example.myfinalwork.response.child.Webdict;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class TranslationResponse implements Serializable {
    private String errorCode;

    private String query;

    private List<String> translation;

    private Basic basic;

    private List<Web> web;

    private Dict dict;

    private Webdict webdict;

    private String l;

    private String tSpeakUrl;

    private String speakUrl;


    @Override
    public String toString() {
        return "TranslationResponse{" +
                "errorCode='" + errorCode + '\'' +
                ", query='" + query + '\'' +
                ", translation=" + translation +
                ", basic=" + basic +
                ", web=" + web +
                ", dict=" + dict +
                ", webdict=" + webdict +
                ", l='" + l + '\'' +
                ", tSpeakUrl='" + tSpeakUrl + '\'' +
                ", speakUrl='" + speakUrl + '\'' +
                '}';
    }
}
