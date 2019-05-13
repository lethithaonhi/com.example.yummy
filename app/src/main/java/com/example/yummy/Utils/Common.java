package com.example.yummy.Utils;

import android.location.Location;

import com.example.yummy.Database.MyDatabaseHelper;
import com.example.yummy.Model.Account;
import com.example.yummy.Model.Restaurant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Common {
    public static MyDatabaseHelper db;
    public static Location myLocation;
    public static List<String> listResId;
    public static String myAddress;
    public static List<Restaurant> restaurantListAll;
    public static List<Map> menuList;
    public static List<Restaurant> restaurantListCurrent;
    public static HashMap<String, Integer> cityList;
    public static Account accountCurrent;
    public static String language;
}
