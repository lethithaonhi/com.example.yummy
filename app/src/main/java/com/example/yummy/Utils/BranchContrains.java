package com.example.yummy.Utils;

public class BranchContrains {
    public static final String TABLE_NAME = "Branch";
    public static final String BRA_ID = "Bra_Id";
    public static final String RES_ID = "Res_Id";
    public static final String AVATAR ="Avatar";
    public static final String ADDESS = "Address";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + BRA_ID + " TEXT PRIMARY KEY, " + RES_ID +" TEXT, " + AVATAR +" TEXT, "
            + ADDESS + " TEXT, " + LATITUDE + " REAL, " + LONGITUDE + " REAL" + ")";
}
