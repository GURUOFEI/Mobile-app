package com.example.textapp.notepad.bean;

public class NewsBean {
    public String anthor_name;
    private String uniquekey;
    private String title;
    public String getAnthor_name() {
        return anthor_name;
    }

    public void setAnthor_name(String anthor_name) {
        this.anthor_name = anthor_name;
    }

    public String getUniquekey() {
        return uniquekey;
    }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail_pic_s() {
        return thumbnail_pic_s;
    }

    public void setThumbnail_pic_s(String thumbnail_pic_s) {
        this.thumbnail_pic_s = thumbnail_pic_s;
    }

    public String getThumbnail_pic_s02() {
        return thumbnail_pic_s02;
    }

    public void setThumbnail_pic_s02(String thumbnail_pic_s02) {
        this.thumbnail_pic_s02 = thumbnail_pic_s02;
    }

    public int getIs_content() {
        return is_content;
    }

    public void setIs_content(int is_content) {
        this.is_content = is_content;
    }

    private String date;
    private String category;
    private String url;
    private String thumbnail_pic_s;
    private String thumbnail_pic_s02;
    private int is_content;
}
