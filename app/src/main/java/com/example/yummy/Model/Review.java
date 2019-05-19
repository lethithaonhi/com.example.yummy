package com.example.yummy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    private String id;
    private String id_res;
    private String id_user;// name
    private String content;
    private String time;
    private float mark;
    private String avatar;
    private String name;

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected Review(Parcel in) {
        id = in.readString();
        id_res = in.readString();
        id_user = in.readString();
        content = in.readString();
        time = in.readString();
        mark = in.readFloat();
        avatar = in.readString();
        name = in.readString();
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
        dest.writeString(time);
        dest.writeFloat(mark);
        dest.writeString(avatar);
        dest.writeString(name);
    }
}
