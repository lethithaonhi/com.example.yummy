package com.example.yummy.Utils;

public class MenuContrains {
    public static final String TABLE_NAME = "Menu";
    public static final String Menu_ID = "Menu_Id";
    public static final String RES_ID = "Res_Id";
    public static final String TYPE = "type";
    public static final String NAME ="Name";
    public static final String PRICES = "Prices";
    public static final String IMAGE = "Image";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + Menu_ID + " TEXT PRIMARY KEY, " + RES_ID +" TEXT, " +TYPE +" TEXT, " + NAME +" TEXT, "
            + PRICES + " INTEGER, " + IMAGE + " TEXT" + ")";
}
