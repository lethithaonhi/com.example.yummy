package com.example.yummy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Branch implements Parcelable {
    private int id_db;
    private String id;
    private String avatar;
    private String address;
    private double latitude;
    private double longitude;
    private float distance;
    private int isDelete; //1: delete, 0: no
    private String district;

    public Branch(){}

    protected Branch(Parcel in) {
        id_db = in.readInt();
        id = in.readString();
        avatar = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        distance = in.readFloat();
        isDelete = in.readInt();
        district = in.readString();
    }

    public int getId_db() {
        return id_db;
    }

    public void setId_db(int id_db) {
        this.id_db = id_db;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public static final Creator<Branch> CREATOR = new Creator<Branch>() {
        @Override
        public Branch createFromParcel(Parcel in) {
            return new Branch(in);
        }

        @Override
        public Branch[] newArray(int size) {
            return new Branch[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_db);
        dest.writeString(id);
        dest.writeString(avatar);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeFloat(distance);
        dest.writeInt(isDelete);
        dest.writeString(district);
    }
}
