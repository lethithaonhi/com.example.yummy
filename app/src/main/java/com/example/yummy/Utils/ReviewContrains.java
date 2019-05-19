package com.example.yummy.Utils;

public class ReviewContrains {
    public static final String TABLE_NAME = "Review";
    public static final String ID = "ID";
    public static final String Review_ID = "Review_Id";
    public static final String RES_ID = "Res_Id";
    public static final String NAME ="Name";
    public static final String MARK = "Mark";
    public static final String AVATAR = "Avatar";
    public static final String USER_ID = "User_Id";
    public static final String CONTENT = "Content";
    public static final String TIME = "Time";
    public static final String CITY = "City";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+ Review_ID + " TEXT, " +RES_ID +" TEXT, " + NAME +" TEXT, "
            + MARK + " INTEGER, " + AVATAR + " TEXT, " + USER_ID + " TEXT, "+ CONTENT + " TEXT, " + TIME + " TEXT, "+ CITY + " TEXT" + ")";

    public static final String DELETE_TABLE = "DELETE FROM " + TABLE_NAME;
}
