package com.example.yummy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    private String id;
    private String id_res;
    private String id_user;
    private String content;
    private String date;
    private String time;
    private float mark;

    public Review(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_res() {
        return id_res;
    }

    public void setId_res(String id_res) {
        this.id_res = id_res;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getMark() {
        return mark;
    }

    public void setMark(float mark) {
        this.mark = mark;
    }

    protected Review(Parcel in) {
        id = in.readString();
        id_res = in.readString();
        id_user = in.readString();
        content = in.readString();
        date = in.readString();
        time = in.readString();
        mark = in.readFloat();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(id_res);
        dest.writeString(id_user);
        dest.writeString(content);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeFloat(mark);
    }
}
