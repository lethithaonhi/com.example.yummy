package com.example.yummy.Activity;

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
import android.widget.Toast;

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
    private String resID="";
    private int year;
    private LineView lvOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_chart_admin);

        if(Common.orderListAll == null || Common.orderListAll.size() == 0){
            UtilsBottomBar.getOrderListAll();
        }
        initView();
    }

    private void initView(){
        Spinner spResYear = findViewById(R.id.sp_res);
        Spinner spYear = findViewById(R.id.sp_year);
        Button btnShowYear = findViewById(R.id.show_year);
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        year = today.year;
        resList = getRestaurant();
        List<String> resSPList = new ArrayList<>();
        resSPList.add("All");
        resSPList.addAll(resList.keySet());

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, resSPList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spResYear.setAdapter(adapter);
        spResYear.setSelection(0);

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

        List<String> yearSPList = getBottomSPMonth(today.year - 10, today.year);
        ArrayAdapter adtYear = new ArrayAdapter(this, android.R.layout.simple_spinner_item, yearSPList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spYear.setAdapter(adtYear);
        spYear.setSelection(yearSPList.size() - 1);

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
                ArrayList<String> dataBottomList = getBottomTextInYear(year, sum);
                if(resID.isEmpty()){
                    ArrayList<Integer> dataCurrentList = getOrderListInYear(Common.orderListAll, year,sum);
                    initLineView(lvOrder, dataCurrentList, dataBottomList);
                }else if(Common.orderListRes != null){
                    ArrayList<Integer> dataCurrentList = getOrderListInYear(Common.orderListRes, year,sum);
                    initLineView(lvOrder, dataCurrentList, dataBottomList);
                }else {
                    Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<Integer> dataCurrentList = getOrderListInYear(Common.orderListAll, today.year, today.month+1);
        ArrayList<String> dataBottomList = getBottomTextInYear(today.year, today.month+1);
        lvOrder = findViewById(R.id.line_view);
        lvOrder.setColorArray(new int[] {
                Color.parseColor("#F44336"), Color.parseColor("#9C27B0")
//                Color.parseColor("#2196F3"), Color.parseColor("#009688")
        });
        initLineView(lvOrder, dataCurrentList, dataBottomList);

    }

    private void initLineView(LineView lineView, ArrayList<Integer> dataCurrentList, ArrayList<String> dataBottomList) {
//        int lastMonth = today.month - 1;
//        if(today.month == 1){
//            lastMonth = 12;
//        }
//        ArrayList<String> lastList = new ArrayList<>();
//        dataLastList = getOrderList(Common.orderListAll, today.year, lastMonth);
//        for (int i = 0; i < getDaysInMonth(today.year, lastMonth); i++) {
//            lastList.add((i+1) + "-"+today.month);
//        }

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
            list.add(String.valueOf(i+1));
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

    private ArrayList<String> getBottomTextInYear(int year, int sum){
        ArrayList<String> currentList = new ArrayList<>();
        for (int i = 0; i < sum; i++) {
            currentList.add((i+1) + "-"+year);
        }
        return currentList;
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

        for(Restaurant restaurant : Common.restaurantListAll){
            data.put(restaurant.getName(), restaurant.getRes_id());
        }

        return data;
    }
}