package com.example.yummy.Utils;

import android.location.Location;

import com.example.yummy.Database.MyDatabaseHelper;
import com.example.yummy.Model.Restaurant;

import java.util.List;

public class Common {
    public static MyDatabaseHelper db;
    public static Location myLocation;
    public static List<String> listResId;
    public static String myAddress;
    public static List<Restaurant> restaurantList;
}
