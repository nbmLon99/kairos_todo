package com.nbmlon.managemodule;


import android.net.Uri;

import java.util.ArrayList;



public class Todoitem {
    private String title;            //제목
    private String content = "";            //내용
    private String end_time = "";           //완료시간
    private int photoLen = 0;               //사진 개수
    private Boolean Done = false;           //checkBox 완료여부

    private ArrayList<Uri> ImageUris = new ArrayList<>();


    /** identify with Title **/
    public Todoitem(String title) { this.title = title; }

    public String getEnd_time() { return end_time; }
    public void setEnd_time(String end_time) { this.end_time = end_time; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getPhotoLen() { return photoLen; }
    public void setPhotoLen(int photo) { this.photoLen = photo;}

    public Boolean getDone() { return Done; }
    public void setDone(Boolean checked) { this.Done = checked; }

    public ArrayList<Uri> getImageUris() { return ImageUris; }
    public void setImageUris(ArrayList<Uri> imageUris) { ImageUris = imageUris; }
}