package com.example.yummy.Utils;

import com.example.yummy.Database.MyDatabaseHelper;
import com.example.yummy.Model.Account;
import com.example.yummy.Model.Addresses;
import com.example.yummy.Model.Blog;
import com.example.yummy.Model.Order;
import com.example.yummy.Model.Restaurant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Common {
    public static MyDatabaseHelper db;
    public static Addresses myLocation;
    public static List<String> listResId;
    public static String myAddress; //city
    public static List<Restaurant> restaurantListAll;
    public static List<Map<String, String>> menuList;
    public static List<Restaurant> restaurantListCurrent;
    public static Restaurant restaurantPartner;
    public static HashMap<String, Integer> cityList;
    public static Account accountCurrent;
    public static String language;
    public static List<Blog> blogList;
    public static List<Order> orderListCurrent;
    public static List<Order> orderListAll;
    public static List<Account> accountList;
    public static boolean isAdd;
    public static List<Order> orderListRes;
    public static List<Order> orderListPartner;
    public static boolean isNetwork = true;
}
