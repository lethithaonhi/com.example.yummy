package com.example.yummy.Utils;

public class RestaurantContrains {
    public static final String TABLE_NAME = "Restaurant";
    private static final String ID = "Id";
    public static final String RES_ID = "Res_Id";
    public static final String NAME_RES ="Name";
    public static final String OPEN_TIME = "Open_Time";
    public static final String CLOSE_TIME = "Close_Time";
    public static final String VIDEO = "Video";
    public static final String IMG_LIST = "Img_List";
    public static final String MENU_LIST = "Menu_List";
    public static final String CITY = "City";
    public static final String MARK = "Mark";
    public static final String FREESHIP = "Freeship";
    public static final String DISCOUNT = "Discount";
    public static final String MAX_DISCOUNT = "Max_Discount";
    public static final String MIN_ORDER = "Min_Order";
    public static final String CODE = "Code";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+ RES_ID + " TEXT, " + NAME_RES +" TEXT, "
            + OPEN_TIME + " TEXT, " + CLOSE_TIME + " TEXT, " + VIDEO +" TEXT, "
            + IMG_LIST +" TEXT, " + MENU_LIST + " TEXT, " +CITY + " TEXT, " + MARK + " REAL, " + FREESHIP + " INTEGER, "
            + DISCOUNT + " INTEGER, "+ MAX_DISCOUNT + " INTEGER, "+ MIN_ORDER + " INTEGER, "+ CODE + " TEXT" + ")";

    public static final String DELETE_TABLE = "DELETE FROM " + TABLE_NAME;
}
