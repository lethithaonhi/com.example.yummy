package com.example.yummy.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Restaurant implements Parcelable {
    private String res_id;
    private String name;
    private String close_open;
    private String open_time;
    private String video;
    private List<String> menuIdList;
    private List<String> imgList;
    private List<Branch> branchList;
    private List<Menu> menuList;
    private String city;
    private float mark;
    private int freeship;
    private Discounts discounts;
    private List<Review> reviewList;
    private int isClose; //1:close, 0: open
    private List<String> keySearch;

    public Restaurant(){}

    public String getRes_id() {
        return res_id;
    }

    public void setRes_id(String res_id) {
        this.res_id = res_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClose_open() {
        return close_open;
    }

    public void setClose_open(String close_open) {
        this.close_open = close_open;
    }

    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public List<String> getMenuIdList() {
        return menuIdList;
    }

    public void setMenuIdList(List<String> menuIdList) {
        this.menuIdList = menuIdList;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

    public List<Branch> getBranchList() {
        return branchList;
    }

    public void setBranchList(List<Branch> branchList) {
        this.branchList = branchList;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public float getMark() {
        return mark;
    }

    public void setMark(float mark) {
        this.mark = mark;
    }

    public int getFreeship() {
        return freeship;
    }

    public void setFreeship(int freeship) {
        this.freeship = freeship;
    }

    public Discounts getDiscounts() {
        return discounts;
    }

    public void setDiscounts(Discounts discounts) {
        this.discounts = discounts;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    public int getIsClose() {
        return isClose;
    }

    public void setIsClose(int isClose) {
        this.isClose = isClose;
    }

    public List<String> getKeySearch() {
        return keySearch;
    }

    public void setKeySearch(List<String> keySearch) {
        this.keySearch = keySearch;
    }

    protected Restaurant(Parcel in) {
        res_id = in.readString();
        name = in.readString();
        close_open = in.readString();
        open_time = in.readString();
        video = in.readString();
        menuIdList = in.createStringArrayList();
        imgList = in.createStringArrayList();
        branchList = new ArrayList<>();
        menuList = new ArrayList<>();
        in.readList(branchList,Branch.class.getClassLoader());
        in.readList(menuList, Menu.class.getClassLoader());
        city = in.readString();
        mark = in.readFloat();
        freeship = in.readInt();
        discounts = in.readParcelable(Discounts.class.getClassLoader());
        reviewList = new ArrayList<>();
        in.readList(reviewList, Review.class.getClassLoader());
        isClose = in.readInt();
        keySearch = in.createStringArrayList();
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(res_id);
        dest.writeString(name);
        dest.writeString(close_open);
        dest.writeString(open_time);
        dest.writeString(video);
        dest.writeStringList(menuIdList);
        dest.writeStringList(imgList);
        dest.writeList(branchList);
        dest.writeList(menuList);
        dest.writeString(city);
        dest.writeFloat(mark);
        dest.writeInt(freeship);
        dest.writeParcelable(discounts, flags);
        dest.writeList(reviewList);
        dest.writeInt(isClose);
        dest.writeStringList(keySearch);
    }
}
