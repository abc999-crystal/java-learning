package com.zdd.demo;

import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        /*------------ 转换为png -------------*/
        Thumbnails.of(new File("/Users/axin/IdeaProjects/axin-framework/world/src/main/java/com/axin/world/picTest/tifdemo2.tiff"))
                .size(1440, 2560)
                .outputFormat("png")
                .toFile("image-conver.png");
    }
}