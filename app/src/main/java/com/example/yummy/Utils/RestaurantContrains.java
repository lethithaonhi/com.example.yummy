package com.example.yummy.Utils;

public class RestaurantContrains {
    // Tên bảng: Note.
    public static final String TABLE_NAME = "Restaurant";
    public static final String RES_ID = "Res_Id";
    public static final String NAME_RES ="Name";
    public static final String OPEN_TIME = "Open_Time";
    public static final String CLOSE_TIME = "Close_Time";
    public static final String VIDEO = "Video";
    public static final String IMG_LIST = "Img_List";
    public static final String MENU_LIST = "Menu_List";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + RES_ID + " TEXT PRIMARY KEY, " + NAME_RES +" TEXT, "
            + OPEN_TIME + " TEXT, " + CLOSE_TIME + " TEXT, " + VIDEO +" TEXT, "
            + IMG_LIST +" TEXT, " + MENU_LIST + " TEXT" + ")";
}
