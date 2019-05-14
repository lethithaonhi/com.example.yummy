package com.example.yummy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Account implements Parcelable {
    private String userId;
    private String username;
    private String name;
    private String phone;
    private String email;
    private int gender; //1 male, 2: felmade, 3:none
    private String datebirth;
    private String avatar;
    private int role;
    private String password;
    private List<Addresses> addressList;

    public Account(){}

    protected Account(Parcel in) {
        userId = in.readString();
        username = in.readString();
        name = in.readString();
        phone = in.readString();
        email = in.readString();
        gender = in.readInt();
        datebirth = in.readString();
        avatar = in.readString();
        role = in.readInt();
        password = in.readString();
        addressList = new ArrayList<>();
        in.readList(addressList, Addresses.class.getClassLoader());
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getDatebirth() {
        return datebirth;
    }

    public void setDatebirth(String datebirth) {
        this.datebirth = datebirth;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Addresses> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Addresses> addressList) {
        this.addressList = addressList;
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(username);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeInt(gender);
        dest.writeString(datebirth);
        dest.writeString(avatar);
        dest.writeInt(role);
        dest.writeString(password);
        dest.writeList(addressList);
    }
}
