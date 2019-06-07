package com.example.yummy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Blog implements Parcelable {
    private String id;
    private String title;
    private String url;
    private String time;
    private String content;
    private String image;

    public Blog(){}

    protected Blog(Parcel in) {
        id = in.readString();
        title = in.readString();
        url = in.readString();
        time = in.readString();
        content = in.readString();
        image = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static final Creator<Blog> CREATOR = new Creator<Blog>() {
        @Override
        public Blog createFromParcel(Parcel in) {
            return new Blog(in);
        }

        @Override
        public Blog[] newArray(int size) {
            return new Blog[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(time);
        dest.writeString(content);
        dest.writeString(image);
    }
}
