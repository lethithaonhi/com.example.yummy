package com.example.yummy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Discounts implements Parcelable {
    private String code;
    private int max_discount;
    private int min_order;
    private int discount;

    public Discounts(){}

    protected Discounts(Parcel in) {
        code = in.readString();
        max_discount = in.readInt();
        min_order = in.readInt();
        discount = in.readInt();
    }

    public static final Creator<Discounts> CREATOR = new Creator<Discounts>() {
        @Override
        public Discounts createFromParcel(Parcel in) {
            return new Discounts(in);
        }

        @Override
        public Discounts[] newArray(int size) {
            return new Discounts[size];
        }
    };


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getMax_discount() {
        return max_discount;
    }

    public void setMax_discount(int max_discount) {
        this.max_discount = max_discount;
    }

    public int getMin_order() {
        return min_order;
    }

    public void setMin_order(int min_order) {
        this.min_order = min_order;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeInt(max_discount);
        dest.writeInt(min_order);
        dest.writeInt(discount);
    }
}
