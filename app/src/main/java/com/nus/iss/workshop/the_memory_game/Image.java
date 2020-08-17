package com.nus.iss.workshop.the_memory_game;

import android.graphics.Bitmap;


public class Image {

    private int id;
    private Bitmap bitmap;
    private String url;

    public Image(int id, Bitmap bitmap){
        this.id = id;
        this.bitmap = bitmap;
    }

    public Image(int id, String url){
        this.id = id;
        this.url = url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getId(){
        return id;
    }

    public String url(){
        return url;
    }

    public void setId(int id){this.id = id;}

    public void setBitmap(Bitmap bitmap){this.bitmap = bitmap;}

    public void setUrl(String url){this.url = url;}
}
