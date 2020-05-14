
package com.example.myfinalwork.response.child;

import java.io.Serializable;

import lombok.Data;

@Data
public class ResRegions implements Serializable {

    private String boundingBox;
    private int linesCount;
    private int lineheight;
    private String context;
    private int linespace;
    private String tranContent;

}