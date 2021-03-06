package com.example.yummy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Addresses implements Parcelable{
    private String id;
    private String name;
    private double latitude;
    private double longitude;

    public Addresses(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    protected Addresses(Parcel in) {
        id = in.readString();
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Parcelable.Creator<Addresses> CREATOR = new Parcelable.Creator<Addresses>() {
        @Override
        public Addresses createFromParcel(Parcel in) {
            return new Addresses(in);
        }

        @Override
        public Addresses[] newArray(int size) {
            return new Addresses[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
