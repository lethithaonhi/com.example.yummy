package com.example.yummy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Menu implements Parcelable {
    private String menu_id;
    private String type;
    private String name;
    private int prices;
    private String image;

    public Menu(){}

    protected Menu(Parcel in) {
        menu_id = in.readString();
        type = in.readString();
        name = in.readString();
        prices = in.readInt();
        image = in.readString();
    }

    public String getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(String menu_id) {
        this.menu_id = menu_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrices() {
        return prices;
    }

    public void setPrices(int prices) {
        this.prices = prices;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static final Creator<Menu> CREATOR = new Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(menu_id);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeInt(prices);
        dest.writeString(image);
    }
}
