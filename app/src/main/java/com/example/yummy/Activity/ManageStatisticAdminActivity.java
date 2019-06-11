package com.example.yummy.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Model.Account;
import com.example.yummy.Model.Blog;
import com.example.yummy.Model.Order;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.UtilsBottomBar;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import im.dacer.androidcharts.LineView;

public class ManageStatisticAdminActivity extends AppCompatActivity {
    private HashMap<String, String> resList = new HashMap<>();
    private String resID="", resIDMonth;
    private int year, yearMonth, month;
    private LineView lvOrder, lvOrderMonth;
    private List<String> monthList;
    private ArrayList<Integer> dataCurrentList, dataCurrentListMonth;
    private ArrayList<String> dataBottomList, dataBottomListMonth;
    private int countOpenRes, countCloseRes, openAc, closeAc, openBlog, closeBlog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_chart_admin);

        if(Common.orderListAll == null || Common.orderListAll.size() == 0){
            UtilsBottomBar.getOrderListAll();
        }
        initView();
    }

    @SuppressLint("SetTextI18n")
    private void initView(){
        Spinner spResYear = findViewById(R.id.sp_res);
        Spinner spYear = findViewById(R.id.sp_year);
        Spinner spResMont = findViewById(R.id.sp_res_1);
        Spinner spYearMonth = findViewById(R.id.sp_year_1);
        Spinner spMonth = findViewById(R.id.sp_month);
        Button btnShowYearMonth = findViewById(R.id.show_year_1);
        Button btnShowYear = findViewById(R.id.show_year);
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        //init Statistic by year
        year = today.year;
        resList = getRestaurant();
        List<String> resSPList = new ArrayList<>();
        resSPList.add("All");
        resSPList.addAll(resList.keySet());

        ArrayAdapter adapterYear = new ArrayAdapter(this, android.R.layout.simple_spinner_item, resSPList);
        adapterYear.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spResYear.setAdapter(adapterYear);
        spResYear.setSelection(0);

        List<String> yearSPList = getBottomSPMonth(today.year - 10, today.year);
        ArrayAdapter adtYear = new ArrayAdapter(this, android.R.layout.simple_spinner_item, yearSPList);
        adtYear.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spYear.setAdapter(adtYear);
        spYear.setSelection(yearSPList.size() - 1);

        spResYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if(pos == 0){
                    resID ="";
                }else {
                    resID = resList.get(resSPList.get(pos));
                    if(resID != null){
                        UtilsBottomBar.getOrderRes(resID);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
               year = Integer.parseInt(yearSPList.get(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnShowYear.setOnClickListener(v->{
            if(year > 0 && resID != null){
                int sum;
                if(year == today.year){
                    sum = today.month + 1;
                }else {
                    sum =12;
                }
                dataBottomList = getBottomTextInYear(year, today.month, sum, true);
                if(resID.isEmpty()){
                    ArrayList<Integer> dataCurrentList = getOrderListInYear(Common.orderListAll, year,sum);
                    initLineView(lvOrder, dataCurrentList, dataBottomList);
                }else if(Common.orderListRes != null){
                    if (Common.orderListRes.size() > 0) {
                        dataCurrentList = getOrderListInYear(Common.orderListRes, year, sum);
                        initLineView(lvOrder, dataCurrentList, dataBottomList);
                    }else {
                        Toast.makeText(this, R.string.empty_list, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });

        dataCurrentList = getOrderListInYear(Common.orderListAll, today.year, today.month+1);
        dataBottomList = getBottomTextInYear(today.year, today.month, today.month+1, true);
        lvOrder = findViewById(R.id.line_view);
        lvOrder.setColorArray(new int[] {
                Color.parseColor("#F44336"), Color.parseColor("#9C27B0")
        });

        //init Statistic by month
        dataCurrentListMonth = getOrderListInMonth(Common.orderListAll, today.year, today.monthDay,today.month+1);
        dataBottomListMonth = getBottomTextInYear(today.year,today.month+1, today.monthDay, false);
        lvOrderMonth = findViewById(R.id.line_view_month);
        lvOrder.setColorArray(new int[] {
                Color.parseColor("#2196F3"), Color.parseColor("#009688")
        });
        ArrayAdapter adapterMonth = new ArrayAdapter(this, android.R.layout.simple_spinner_item, resSPList);
        adapterMonth.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spResMont.setAdapter(adapterMonth);
        spResYear.setSelection(0);

        ArrayAdapter adtYearMonth = new ArrayAdapter(this, android.R.layout.simple_spinner_item, yearSPList);
        adtYearMonth.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spYearMonth.setAdapter(adtYearMonth);
        spYearMonth.setSelection(yearSPList.size() - 1);

        monthList = getBottomSPMonth(1, today.month+1);
        ArrayAdapter adtMonth = new ArrayAdapter(this, android.R.layout.simple_spinner_item, monthList);
        adtMonth.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spMonth.setAdapter(adtMonth);
        spMonth.setSelection(monthList.size() - 1);

        spResMont.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if(pos == 0){
                    resIDMonth ="";
                }else {
                    resIDMonth = resList.get(resSPList.get(pos));
                    if(resIDMonth != null){
                        UtilsBottomBar.getOrderRes(resIDMonth);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spYearMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if(yearMonth == today.year){
                    monthList = getBottomSPMonth(1, today.month + 1);
                }else {
                    monthList = getBottomSPMonth(1, 12);
                }
                adtMonth.notifyDataSetChanged();
                yearMonth = Integer.parseInt(yearSPList.get(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                month = Integer.parseInt(monthList.get(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnShowYearMonth.setOnClickListener(v->{
            if(yearMonth > 0 && resIDMonth != null && month > 0){
                int sum;
                if(yearMonth == today.year && month == today.month + 1){
                    sum = today.monthDay;
                }else {
                    sum = getDaysInMonth(yearMonth, month);
                }
                dataBottomListMonth = getBottomTextInYear(yearMonth, month, sum, false);
                if(resIDMonth.isEmpty()){
                    dataCurrentListMonth = getOrderListInMonth(Common.orderListAll, yearMonth,sum, month);
                    initLineView(lvOrderMonth, dataCurrentListMonth, dataBottomListMonth);
                }else if(Common.orderListRes != null){
                    if(Common.orderListRes.size() > 0) {
                        dataCurrentListMonth = getOrderListInMonth(Common.orderListRes, yearMonth, sum, month);
                        initLineView(lvOrderMonth, dataCurrentListMonth, dataBottomListMonth);
                    }else {
                        Toast.makeText(this, R.string.empty_list, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });

        initLineView(lvOrder, dataCurrentList, dataBottomList);
        initLineView(lvOrderMonth, dataCurrentListMonth, dataBottomListMonth);

        getResOpenClose();
        getAccountOpenClose();
        getBlogOpenClose();
        TextView tvActiveRes = findViewById(R.id.tv_active_res);
        TextView tvNotActiveRes = findViewById(R.id.tv_notac_res);
        TextView tvActiveAc = findViewById(R.id.tv_active_ac);
        TextView tvNotActiveAc = findViewById(R.id.tv_notac_ac);
        TextView tvActiveBlog = findViewById(R.id.tv_active_blog);
        TextView tvNotActiveBlog = findViewById(R.id.tv_notac_blog);
        tvActiveRes.setText(countOpenRes+" " + getResources().getString(R.string.restaurant));
        tvNotActiveRes.setText(countCloseRes+" " + getResources().getString(R.string.restaurant));
        tvActiveAc.setText(openAc + " " +getResources().getString(R.string.account));
        tvNotActiveAc.setText(closeAc + " " +getResources().getString(R.string.account));
        tvActiveBlog.setText(openBlog + " " +getResources().getString(R.string.blog));
        tvNotActiveBlog.setText(closeBlog + " " +getResources().getString(R.string.blog));
        TextView tvDetailRes = findViewById(R.id.tv_detail_res);
        TextView tvDetailAc = findViewById(R.id.tv_detail_ac);
        TextView tvDetailBlog = findViewById(R.id.tv_detail_blog);

        tvDetailAc.setOnClickListener(v-> startActivity(new Intent(this, ManageAccountAdminActivity.class)));
        tvDetailRes.setOnClickListener(v-> startActivity(new Intent(this, ManageRestaurantAdminActivity.class)));
        tvDetailBlog.setOnClickListener(v-> startActivity(new Intent(this, ManageBlogAdminActivity.class)));
    }

    private void initLineView(LineView lineView, ArrayList<Integer> dataCurrentList, ArrayList<String> dataBottomList) {
        lineView.setBottomTextList(dataBottomList);
        lineView.setDrawDotLine(true);
        lineView.setShowPopup(LineView.LAYER_TYPE_NONE);
        ArrayList<ArrayList<Integer>> dataLists = new ArrayList<>();
        dataLists.add(dataCurrentList);
        lineView.setDataList(dataLists);
    }

    private int getDaysInMonth(int year, int month){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        return calendar.getActualMaximum(Calendar.DATE);
    }

    private List<String> getBottomSPMonth(int begin, int end){
        List<String> list = new ArrayList<>();
        for(int i=begin; i <= end; i++){
            list.add(String.valueOf(i));
        }
        return list;
    }

    private ArrayList<Integer> getOrderListInYear(List<Order> orderList, int year, int sum){
        ArrayList<Integer> dataList = new ArrayList<>();
        if(orderList != null && orderList.size() > 0) {
            for (int i = 0; i < sum; i++) {
                int count = 0;
                for(Order order : orderList){
                    if((i+1) == getMonthOrder(order.getDate()) && year == getYearOrder(order.getDate())){
                        count++;
                    }
                }
                dataList.add(count);
            }
        }
        return dataList;
    }

    private ArrayList<Integer> getOrderListInMonth(List<Order> orderList, int year, int sum, int month){
        ArrayList<Integer> dataList = new ArrayList<>();
        if(orderList != null && orderList.size() > 0) {
            for (int i = 0; i < sum; i++) {
                int count = 0;
                for(Order order : orderList){
                    if((i+1) == getDayhOrder(order.getDate()) && year == getYearOrder(order.getDate()) && month == getMonthOrder(order.getDate())){
                        count++;
                    }
                }
                dataList.add(count);
            }
        }
        return dataList;
    }

    private ArrayList<String> getBottomTextInYear(int year, int month, int sum,boolean isYear){
        ArrayList<String> currentList = new ArrayList<>();
        for (int i = 0; i < sum; i++) {
            if(isYear) {
                currentList.add((i + 1) + "-" + year);
            }else {
                currentList.add((i+1) + "-" + month);
            }
        }
        return currentList;
    }

    private int getDayhOrder(String date){
        String[] dateArr = date.split("-");
        return Integer.parseInt(dateArr[0]);
    }

    private int getMonthOrder(String date){
        String[] dateArr = date.split("-");
        return Integer.parseInt(dateArr[1]);
    }

    private int getYearOrder(String date){
        String[] dateArr = date.split("-");
        return Integer.parseInt(dateArr[2]);
    }

    private HashMap<String, String> getRestaurant(){
        HashMap<String, String> data = new HashMap<>();

        if(Common.restaurantListAll != null && Common.restaurantListAll.size() > 0) {
            for (Restaurant restaurant : Common.restaurantListAll) {
                data.put(restaurant.getName(), restaurant.getRes_id());
            }
        }

        return data;
    }

    private void getResOpenClose(){
        for(Restaurant restaurant: Common.restaurantListAll){
            if(restaurant.getIsClose() == 1){
                countCloseRes ++;
            }else {
                countOpenRes++;
            }
        }
    }

    private void getAccountOpenClose(){
        for(Account account : Common.accountList){
            if(account.getIsClose() == 1){
                closeAc++;
            }else {
                openAc++;
            }
        }
    }

    private void getBlogOpenClose(){
        for(Blog blog : Common.blogList){
            if(blog.getIsClose() == 1){
                closeBlog++;
            }else {
                openBlog++;
            }
        }
    }
}