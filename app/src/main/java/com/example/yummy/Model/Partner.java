package com.example.yummy.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Partner implements Parcelable {
    private String bank;
    private String boss;
    private String cmnd;
    private String stk;

    public Partner(){}

    protected Partner(Parcel in) {
        bank = in.readString();
        boss = in.readString();
        cmnd = in.readString();
        stk = in.readString();
    }
    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBoss() {
        return boss;
    }

    public void setBoss(String boss) {
        this.boss = boss;
    }

    public String getCmnd() {
        return cmnd;
    }

    public void setCmnd(String cmnd) {
        this.cmnd = cmnd;
    }

    public String getStk() {
        return stk;
    }

    public void setStk(String stk) {
        this.stk = stk;
    }
    public static final Creator<Partner> CREATOR = new Creator<Partner>() {
        @Override
        public Partner createFromParcel(Parcel in) {
            return new Partner(in);
        }

        @Override
        public Partner[] newArray(int size) {
            return new Partner[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bank);
        dest.writeString(boss);
        dest.writeString(cmnd);
        dest.writeString(stk);
    }
}
