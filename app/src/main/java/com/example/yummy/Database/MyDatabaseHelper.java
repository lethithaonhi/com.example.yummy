package com.example.yummy.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.Utils.BranchContrains;
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
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "MyDatabaseHelper.onUpgrade ... ");
        // Hủy (drop) bảng cũ nếu nó đã tồn tại.
        db.execSQL("DROP TABLE IF EXISTS " + RestaurantContrains.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BranchContrains.TABLE_NAME);
        onCreate(db);
    }

    public void addBranch(Branch branch, String resID) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BranchContrains.BRA_ID, branch.getId());
        values.put(BranchContrains.RES_ID, resID);
        values.put(BranchContrains.AVATAR, branch.getAvatar());
        values.put(BranchContrains.ADDESS, branch.getAddress());
        values.put(BranchContrains.LATITUDE, branch.getLatitude());
        values.put(BranchContrains.LONGITUDE, branch.getLongitude());

        db.insert(BranchContrains.TABLE_NAME, null, values);
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

        // Trèn một dòng dữ liệu vào bảng.
        db.insert(RestaurantContrains.TABLE_NAME, null, values);


        // Đóng kết nối database.
        db.close();
    }

    private Restaurant getResFromCursor(Cursor cursor) {
        Restaurant restaurant = new Restaurant();
        restaurant.setRes_id(cursor.getString(0));
        restaurant.setName(cursor.getString(1));
        restaurant.setOpen_time(cursor.getString(2));
        restaurant.setClose_open(cursor.getString(3));
        restaurant.setVideo(cursor.getString(4));
        restaurant.setImgList(convertStringToList(cursor.getString(5)));
        restaurant.setMenuIdList(convertStringToList(cursor.getString(6)));

        return restaurant;
    }

    private Branch getBranchFromCursor(Cursor cursor){
        Branch branch = new Branch();
        branch.setId(cursor.getString(0));
        branch.setAvatar(cursor.getString(2));
        branch.setAddress(cursor.getString(3));
        branch.setLatitude(cursor.getDouble(4));
        branch.setLongitude(cursor.getDouble(5));

        return branch;
    }

    public List<Restaurant> getRestaurant(List<String> idList) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Restaurant> dataList = new ArrayList<>();
        String query = "SELECT * FROM " + RestaurantContrains.TABLE_NAME + " WHERE " + RestaurantContrains.RES_ID
                + " = ? ";
        for (String id : idList) {
            String[] selectionArgs = new String[]{id};
            List<Branch> branchList = getBranch(id);
            Cursor cursor = db.rawQuery(query, selectionArgs);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        Restaurant restaurant = getResFromCursor(cursor);
                        restaurant.setBranchList(branchList);
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

    private List<Branch> getBranch(String resID){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Branch> dataList = new ArrayList<>();
        String query = "SELECT * FROM " + BranchContrains.TABLE_NAME + " WHERE " + BranchContrains.RES_ID
                + " = ? ";
            String[] selectionArgs = new String[]{String.valueOf(resID)};
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
        return new ArrayList<>(Arrays.asList(s.split(",")));
    }
}
