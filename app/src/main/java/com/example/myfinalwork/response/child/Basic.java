package com.example.myfinalwork.response.child;

import java.util.List;

import lombok.Data;

@Data
public class Basic {
    private String phonetic;

    private String ukPhonetic;

    private String usPhonetic;

    private String ukSpeech;

    private String usSpeech;

    private List<String> explains;


}
