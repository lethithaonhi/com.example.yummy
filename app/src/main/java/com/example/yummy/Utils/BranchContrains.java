package com.example.yummy.Utils;

public class BranchContrains {
    public static final String TABLE_NAME = "Branch";
    public static final String ID = "Id";
    public static final String BRA_ID = "Bra_Id";
    public static final String RES_ID = "Res_Id";
    public static final String AVATAR ="Avatar";
    public static final String ADDESS = "Address";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String CITY = "City";
    public static final String DISTANCE = "Distance";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+ BRA_ID + " TEXT, " + RES_ID +" TEXT, " + AVATAR +" TEXT, "
            + ADDESS + " TEXT, " + LATITUDE + " REAL, " + LONGITUDE + " REAL, " + CITY + " TEXT, "+ DISTANCE + " REAL" + ")";

    public static final String DELETE_TABLE = "DELETE FROM " + TABLE_NAME;
}
