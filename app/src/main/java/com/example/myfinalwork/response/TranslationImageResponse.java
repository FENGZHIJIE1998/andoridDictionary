
package com.example.myfinalwork.response;

import com.example.myfinalwork.response.child.ResRegions;

import java.io.Serializable;
import java.util.List;

import lombok.Data;


@Data
public class TranslationImageResponse implements Serializable {

    private String orientation;
    private String lanFrom;
    private String textAngle;
    private String errorCode;
    private String lanTo;
    private List<ResRegions> resRegions;
    private String render_image;


}