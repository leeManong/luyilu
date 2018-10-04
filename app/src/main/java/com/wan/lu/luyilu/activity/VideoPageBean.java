package com.wan.lu.luyilu.activity;

public class VideoPageBean {
    private String text;
    private String url;
    private String pic;



    public VideoPageBean(String text, String url,String pic) {
        this.text = text;
        this.url = url;
        this.pic = pic;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
