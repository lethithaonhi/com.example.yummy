package com.example.yummy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;

public class Order implements Parcelable, Serializable {
    private String id;
    private String id_res;
    private String id_user;
    private HashMap<Menu, Integer> menuList;
    private String date;
    private String time;
    private String node;
    private long total;
    private int isStatus; // 0: chua xac nhan, 1: xac nhan & dang chuan bi, 2: dang giao, 3: da xong, 4: huy
    private String address;
    private String name_res;
    private String avatar;
    private String address_res;
    private int feeShip;
    private float distance;
    private int discount;
    private String name;
    private String phone;
    private int count;

    public Order(){}

    protected Order(Parcel in) {
        id = in.readString();
        id_res = in.readString();
        id_user = in.readString();
        menuList = (HashMap<Menu, Integer>) in.readSerializable();
        date = in.readString();
        time = in.readString();
        node = in.readString();
        total = in.readLong();
        isStatus = in.readInt();
        address = in.readString();
        name_res = in.readString();
        address_res = in.readString();
        feeShip = in.readInt();
        distance = in.readFloat();
        discount = in.readInt();
        name = in.readString();
        phone = in.readString();
        count = in.readInt();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

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

    public HashMap<Menu, Integer> getMenuList() {
        return menuList;
    }

    public void setMenuList(HashMap<Menu, Integer> menuList) {
        this.menuList = menuList;
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

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getIsStatus() {
        return isStatus;
    }

    public void setIsStatus(int isStatus) {
        this.isStatus = isStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName_res() {
        return name_res;
    }

    public void setName_res(String name_res) {
        this.name_res = name_res;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAddress_res() {
        return address_res;
    }

    public void setAddress_res(String address_res) {
        this.address_res = address_res;
    }

    public int getFeeShip() {
        return feeShip;
    }

    public void setFeeShip(int feeShip) {
        this.feeShip = feeShip;
    }

    public float getDistance() {
        return distance;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(id_res);
        dest.writeString(id_user);
        dest.writeSerializable(menuList);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(node);
        dest.writeLong(total);
        dest.writeInt(isStatus);
        dest.writeString(address);
        dest.writeString(name_res);
        dest.writeString(avatar);
        dest.writeString(address_res);
        dest.writeInt(feeShip);
        dest.writeFloat(distance);
        dest.writeInt(discount);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeInt(count);
    }
}
