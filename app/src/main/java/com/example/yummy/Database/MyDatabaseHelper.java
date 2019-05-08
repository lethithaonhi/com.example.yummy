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
import com.example.yummy.Utils.BranchContrains;
import com.example.yummy.Utils.MenuContrains;
import com.example.yummy.Utils.RestaurantContrains;

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
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void clearData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + RestaurantContrains.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BranchContrains.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MenuContrains.TABLE_NAME);
        onCreate(db);
    }

    public void deleteData() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(RestaurantContrains.DELETE_TABLE);
            db.execSQL(MenuContrains.DELETE_TABLE);
            db.execSQL(BranchContrains.DELETE_TABLE);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public int addBranch(Branch branch, String resID, String city) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BranchContrains.BRA_ID, branch.getId());
        values.put(BranchContrains.RES_ID, resID);
        values.put(BranchContrains.AVATAR, branch.getAvatar());
        values.put(BranchContrains.ADDESS, branch.getAddress());
        values.put(BranchContrains.LATITUDE, branch.getLatitude());
        values.put(BranchContrains.LONGITUDE, branch.getLongitude());
        values.put(BranchContrains.CITY, city);

        return (int) db.insert(BranchContrains.TABLE_NAME, null, values);
    }

    public void addMenu(Menu menu, String resID, String city) {

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

        db.insert(MenuContrains.TABLE_NAME, null, values);
        db.close();
    }


    public void addRestaurant(Restaurant restaurant) {
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
        values.put(RestaurantContrains.DISCOUNT, restaurant.getDiscounts() != null ? restaurant.getDiscounts().getDiscount():0);
        values.put(RestaurantContrains.MAX_DISCOUNT, restaurant.getDiscounts() != null ? restaurant.getDiscounts().getMax_discount():0);
        values.put(RestaurantContrains.MIN_ORDER, restaurant.getDiscounts() != null ? restaurant.getDiscounts().getMin_order():0);
        values.put(RestaurantContrains.CODE, restaurant.getDiscounts() != null ? restaurant.getDiscounts().getCode():"");

        // Trèn một dòng dữ liệu vào bảng.
        db.insert(RestaurantContrains.TABLE_NAME, null, values);


        // Đóng kết nối database.
        db.close();
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

        return restaurant;
    }

    private Branch getBranchFromCursor(Cursor cursor){
        Branch branch = new Branch();
        branch.setId(cursor.getString(1));
        branch.setAvatar(cursor.getString(3));
        branch.setAddress(cursor.getString(4));
        branch.setLatitude(cursor.getDouble(5));
        branch.setLongitude(cursor.getDouble(6));
        branch.setDistance(cursor.getFloat(7));

        return branch;
    }

    private Menu getMenuFromCursor(Cursor cursor){
        Menu menu = new Menu();
        menu.setMenu_id(cursor.getString(1));
        menu.setType(cursor.getString(3));
        menu.setName(cursor.getString(4));
        menu.setPrices(cursor.getInt(5));
        menu.setImage(cursor.getString(6));
        menu.setDescribe(cursor.getString(7));

        return menu;
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
                        restaurant.setBranchList(branchList);
                        restaurant.setMenuList(getMenu(id, address));
                        dataList.add(restaurant);
                    } while (cursor.moveToNext());
                }
                if (!cursor.isClosed()) {
                    cursor.close();
                }
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
                    dataList.add(menu);
                } while (cursor.moveToNext());
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return dataList;
    }

    public int checkExistRes(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
            String query = "select count(*) from "+ RestaurantContrains.TABLE_NAME +" where "+RestaurantContrains.NAME_RES+" = ?";
            Cursor cursor = db.rawQuery(query, new String[] {name});

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

//    public int updateNote(Restaurant note) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(RestaurantContrains, note.getVideo());
//        values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
//
//        // updating row
//        return db.update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?",
//                new String[]{String.valueOf(note.getNoteId())});
//    }


    public void updateBranch(float distance, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BranchContrains.DISTANCE, distance);

        db.update(BranchContrains.TABLE_NAME, values, BranchContrains.ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public void deleteRes(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(RestaurantContrains.TABLE_NAME, RestaurantContrains.RES_ID + " = ?",
                new String[]{id});
        db.close();
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
