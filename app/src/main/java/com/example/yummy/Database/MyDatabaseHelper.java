package com.example.yummy.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Discounts;
import com.example.yummy.Model.Menu;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.Model.Review;
import com.example.yummy.Utils.BranchContrains;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.MenuContrains;
import com.example.yummy.Utils.RestaurantContrains;
import com.example.yummy.Utils.ReviewContrains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Yummy_Manager";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "MyDatabaseHelper.onCreate ... ");
        db.execSQL(RestaurantContrains.CREATE_TABLE);
        db.execSQL(BranchContrains.CREATE_TABLE);
        db.execSQL(MenuContrains.CREATE_TABLE);
        db.execSQL(ReviewContrains.CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void clearData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + RestaurantContrains.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BranchContrains.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MenuContrains.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewContrains.TABLE_NAME);
        onCreate(db);
    }

    public void deleteData() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(RestaurantContrains.DELETE_TABLE);
            db.execSQL(MenuContrains.DELETE_TABLE);
            db.execSQL(BranchContrains.DELETE_TABLE);
            db.execSQL(ReviewContrains.DELETE_TABLE);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public int addBranch(Branch branch, String resID, String city) {
        if (checkExistBranch(branch.getAddress(), city) == 0) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(BranchContrains.BRA_ID, branch.getId());
            values.put(BranchContrains.RES_ID, resID);
            values.put(BranchContrains.AVATAR, branch.getAvatar());
            values.put(BranchContrains.ADDESS, branch.getAddress());
            values.put(BranchContrains.LATITUDE, branch.getLatitude());
            values.put(BranchContrains.LONGITUDE, branch.getLongitude());
            values.put(BranchContrains.CITY, city);
            values.put(BranchContrains.ISDELETE, branch.getIsDelete());

            return (int) db.insert(BranchContrains.TABLE_NAME, null, values);
        }
        return 0;
    }

    public void addMenu(Menu menu, String resID, String city) {
        if(checkExistMenu(resID, menu, city) == 0) {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(MenuContrains.Menu_ID, menu.getMenu_id());
            values.put(MenuContrains.TYPE, menu.getType());
            values.put(MenuContrains.RES_ID, resID);
            values.put(MenuContrains.NAME, menu.getName());
            values.put(MenuContrains.PRICES, menu.getPrices());
            values.put(MenuContrains.IMAGE, menu.getImage());
            values.put(MenuContrains.CITY, city);
            values.put(MenuContrains.DESCRIBE, menu.getDescribe() != null ? menu.getDescribe() : "");
            values.put(MenuContrains.ISDELETE, menu.getIsDelete());

            db.insert(MenuContrains.TABLE_NAME, null, values);
            db.close();
        }
    }

    public void addReview(Review review, String resID, String city) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ReviewContrains.Review_ID, review.getId());
        values.put(ReviewContrains.USER_ID,review.getId_user());
        values.put(ReviewContrains.RES_ID, resID);
        values.put(ReviewContrains.NAME, review.getName());
        values.put(ReviewContrains.MARK, review.getMark());
        values.put(ReviewContrains.CONTENT, review.getContent());
        values.put(ReviewContrains.TIME, review.getTime());
        values.put(ReviewContrains.AVATAR, review.getAvatar());
        values.put(ReviewContrains.CITY, city);

        db.insert(ReviewContrains.TABLE_NAME, null, values);
        db.close();
    }


    public void addRestaurant(Restaurant restaurant) {
        if (checkExistRes(restaurant.getName(), restaurant.getCity()) == 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(RestaurantContrains.RES_ID, restaurant.getRes_id());
            values.put(RestaurantContrains.NAME_RES, restaurant.getName());
            values.put(RestaurantContrains.OPEN_TIME, restaurant.getOpen_time());
            values.put(RestaurantContrains.CLOSE_TIME, restaurant.getClose_open());
            values.put(RestaurantContrains.VIDEO, restaurant.getVideo());
            values.put(RestaurantContrains.IMG_LIST, convertListToString(restaurant.getImgList()));
            values.put(RestaurantContrains.MENU_LIST, convertListToString(restaurant.getMenuIdList()));
            values.put(RestaurantContrains.CITY, restaurant.getCity());
            values.put(RestaurantContrains.MARK, restaurant.getMark());
            values.put(RestaurantContrains.FREESHIP, restaurant.getFreeship());
            values.put(RestaurantContrains.DISCOUNT, restaurant.getDiscounts() != null ? restaurant.getDiscounts().getDiscount() : 0);
            values.put(RestaurantContrains.MAX_DISCOUNT, restaurant.getDiscounts() != null ? restaurant.getDiscounts().getMax_discount() : 0);
            values.put(RestaurantContrains.MIN_ORDER, restaurant.getDiscounts() != null ? restaurant.getDiscounts().getMin_order() : 0);
            values.put(RestaurantContrains.CODE, restaurant.getDiscounts() != null ? restaurant.getDiscounts().getCode() : "");
            values.put(RestaurantContrains.IS_CLOSE, restaurant.getIsClose());
            // Trèn một dòng dữ liệu vào bảng.
            db.insert(RestaurantContrains.TABLE_NAME, null, values);


            // Đóng kết nối database.
            db.close();
        }
    }

    private Restaurant getResFromCursor(Cursor cursor) {
        Restaurant restaurant = new Restaurant();
        restaurant.setRes_id(cursor.getString(1));
        restaurant.setName(cursor.getString(2));
        restaurant.setOpen_time(cursor.getString(3));
        restaurant.setClose_open(cursor.getString(4));
        restaurant.setVideo(cursor.getString(5));
        restaurant.setImgList(convertStringToList(cursor.getString(6)));
        restaurant.setMenuIdList(convertStringToList(cursor.getString(7)));
        restaurant.setCity(cursor.getString(8));
        restaurant.setMark(cursor.getFloat(9));
        restaurant.setFreeship(cursor.getInt(10));

        Discounts discounts = new Discounts();
        discounts.setDiscount(cursor.getInt(11));
        discounts.setMax_discount(cursor.getInt(12));
        discounts.setMin_order(cursor.getInt(13));
        discounts.setCode(cursor.getString(14));
        restaurant.setDiscounts(discounts);
        restaurant.setIsClose(cursor.getInt(15));

        return restaurant;
    }

    private Branch getBranchFromCursor(Cursor cursor){
        Branch branch = new Branch();
        branch.setId(cursor.getString(1));
        branch.setAvatar(cursor.getString(3));
        branch.setAddress(cursor.getString(4));
        branch.setLatitude(cursor.getDouble(5));
        branch.setLongitude(cursor.getDouble(6));
        branch.setDistance(cursor.getFloat(8));
        branch.setIsDelete(cursor.getInt(9));

        return branch;
    }

    private Menu getMenuFromCursor(Cursor cursor){
        Menu menu = new Menu();
        menu.setMenu_id(cursor.getString(1));
        menu.setType(cursor.getString(3));
        menu.setName(cursor.getString(4));
        menu.setPrices(cursor.getInt(5));
        menu.setImage(cursor.getString(6));
        menu.setDescribe(cursor.getString(8));
        menu.setIsDelete(cursor.getInt(9));

        return menu;
    }

    private Review getReviewFromCursor(Cursor cursor){
        Review review = new Review();
        review.setId(cursor.getString(1));
        review.setName(cursor.getString(3));
        review.setMark(cursor.getLong(4));
        review.setAvatar(cursor.getString(5));
        review.setId_user(cursor.getString(6));
        review.setContent(cursor.getString(7));
        review.setTime(cursor.getString(8));

        return review;
    }

    public void updateMenu(Menu menu){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MenuContrains.NAME, menu.getName());
        values.put(MenuContrains.PRICES, menu.getPrices());
        values.put(MenuContrains.ISDELETE, menu.getIsDelete());
        // updating row
        db.update(MenuContrains.TABLE_NAME, values, MenuContrains.Menu_ID + " = ? AND "+ MenuContrains.TYPE + "=? AND "
                        +MenuContrains.CITY+" =? AND "+MenuContrains.RES_ID +" =?",
                new String[] {menu.getMenu_id(), menu.getType(), Common.myAddress, Common.accountCurrent.getPartner().getBoss()});
    }

    public void updateBranch(Branch branch){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BranchContrains.ISDELETE, branch.getIsDelete());
        // updating row
        db.update(BranchContrains.TABLE_NAME, values, BranchContrains.ADDESS + " = ? AND "+BranchContrains.LONGITUDE
                        + " = ? AND "+BranchContrains.LATITUDE+" =?",
                new String[] {branch.getAddress(), String.valueOf(branch.getLongitude()), String.valueOf(branch.getLatitude())});
    }

    public List<Restaurant> getRestaurant(List<String> idList, String address) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Restaurant> dataList = new ArrayList<>();
        String query = "SELECT * FROM " + RestaurantContrains.TABLE_NAME + " WHERE " + RestaurantContrains.RES_ID
                + " = ? AND "+ RestaurantContrains.CITY +" = ?";
        for (String id : idList) {
            String[] selectionArgs = new String[]{id, address};
            List<Branch> branchList = getBranch(id, address);
            Cursor cursor = db.rawQuery(query, selectionArgs);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        Restaurant restaurant = getResFromCursor(cursor);
                        if(restaurant.getIsClose() == 0) {
                            restaurant.setBranchList(branchList);
                            restaurant.setMenuList(getMenu(id, address));
                            restaurant.setReviewList(getReview(id, address));
                            dataList.add(restaurant);
                        }
                    } while (cursor.moveToNext());
                }
                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        return dataList;

    }

    public List<Restaurant> getRestaurantPartner(String idRes, String address) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Restaurant> dataList = new ArrayList<>();
        String query = "SELECT * FROM " + RestaurantContrains.TABLE_NAME + " WHERE " + RestaurantContrains.RES_ID
                + " = ? AND " + RestaurantContrains.CITY + " = ?";
        String[] selectionArgs = new String[]{idRes, address};
        List<Branch> branchList = getBranch(idRes, address);
        Cursor cursor = db.rawQuery(query, selectionArgs);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Restaurant restaurant = getResFromCursor(cursor);
                    restaurant.setBranchList(branchList);
                    restaurant.setMenuList(getMenu(idRes, address));
                    restaurant.setReviewList(getReview(idRes, address));
                    dataList.add(restaurant);
                } while (cursor.moveToNext());
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return dataList;
    }

    private List<Branch> getBranch(String resID, String city){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Branch> dataList = new ArrayList<>();
        String query = "SELECT * FROM " + BranchContrains.TABLE_NAME + " WHERE " + BranchContrains.RES_ID
                + " = ? AND " + BranchContrains.CITY +" = ?";
            String[] selectionArgs = new String[]{resID, city};
            Cursor cursor = db.rawQuery(query, selectionArgs);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        Branch branch = getBranchFromCursor(cursor);
                        if(branch.getIsDelete() == 0)
                            dataList.add(branch);
                    } while (cursor.moveToNext());
                }
                if (!cursor.isClosed()) {
                    cursor.close();
                }
        }
        return dataList;
    }

    private List<Menu> getMenu(String resID, String city){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Menu> dataList = new ArrayList<>();
        String query = "SELECT * FROM " + MenuContrains.TABLE_NAME + " WHERE " + MenuContrains.RES_ID
                + " = ? AND " + MenuContrains.CITY + " = ?";
        String[] selectionArgs = new String[]{resID, city};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Menu menu = getMenuFromCursor(cursor);
                    if(menu.getIsDelete() == 0)
                        dataList.add(menu);
                } while (cursor.moveToNext());
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return dataList;
    }

    private List<Review> getReview(String resID, String city){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Review> dataList = new ArrayList<>();
        String query = "SELECT * FROM " + ReviewContrains.TABLE_NAME + " WHERE " + ReviewContrains.RES_ID
                + " = ? AND " + ReviewContrains.CITY + " = ?";
        String[] selectionArgs = new String[]{resID, city};
        Cursor cursor = db.rawQuery(query, selectionArgs);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Review review = getReviewFromCursor(cursor);
                    dataList.add(review);
                } while (cursor.moveToNext());
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return dataList;
    }

    private int checkExistRes(String name, String city) {
        SQLiteDatabase db = this.getReadableDatabase();
            String query = "select count(*) from "+ RestaurantContrains.TABLE_NAME +" where "+RestaurantContrains.NAME_RES+" = ? AND "+RestaurantContrains.CITY+" =?";
            Cursor cursor = db.rawQuery(query, new String[] {name, city});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return  0;
    }

    private int checkExistBranch(String address, String city) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select count(*) from "+ BranchContrains.TABLE_NAME +" where "+BranchContrains.ADDESS+" = ? AND "+BranchContrains.CITY+" =?";
        Cursor cursor = db.rawQuery(query, new String[] {address, city});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return  0;
    }

    private int checkExistMenu(String resID, Menu menu, String city) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select count(*) from "+ MenuContrains.TABLE_NAME +" where "+MenuContrains.RES_ID+" = ? AND "+MenuContrains.CITY +" =? AND "+MenuContrains.NAME+" =?";
        Cursor cursor = db.rawQuery(query, new String[] {resID, city, menu.getName()});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return  0;
    }

    public void updateBranch(float distance, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BranchContrains.DISTANCE, distance);

        db.update(BranchContrains.TABLE_NAME, values, BranchContrains.ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    private String convertListToString(List<String> list) {
        String listString = "";
        for (String s : list) {
            listString += s + ", ";
        }
        return listString;
    }

    private List<String> convertStringToList(String s) {
        return new ArrayList<>(Arrays.asList(s.split(", ")));
    }
}
