package com.example.yummy.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.yummy.Activity.LoginActivity;
import com.example.yummy.Activity.OrderCustomActivity;
import com.example.yummy.Activity.RestaurantDetailActivity;
import com.example.yummy.Adapter.MenuRestaurantDetailAdapter;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Menu;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DeliveryRestaurantDetailFragment extends Fragment implements MenuRestaurantDetailAdapter.OnCountChangeListener {
    private Restaurant restaurant;
    private Branch branch;
    private TextView tvStatus;
    private TextView tvCount, tvMoneyAll;
    private LinearLayout viewDelivery;
    private HashMap<Menu, Integer> listOrderMenu = new HashMap<>();

    public static DeliveryRestaurantDetailFragment getInstance(Restaurant restaurant, Branch branch) {
        DeliveryRestaurantDetailFragment fragment = new DeliveryRestaurantDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("restaurant", restaurant);
        bundle.putParcelable("branch", branch);
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_retaurantdetail_delivery, container, false);
        if (getArguments() != null) {
            restaurant = getArguments().getParcelable("restaurant");
            branch = getArguments().getParcelable("branch");
        }
        initView(v);
        return v;
    }

    private void initView(View v){
        viewDelivery = v.findViewById(R.id.view_delivery);
        tvCount = v.findViewById(R.id.tv_count);
        tvMoneyAll = v.findViewById(R.id.tv_moneyAll);
        TextView btnDelivery = v.findViewById(R.id.btn_delivery);
        btnDelivery.setOnClickListener(vl->{
            if(Common.accountCurrent != null) {
                Intent intent = new Intent(getContext(), OrderCustomActivity.class);
                intent.putExtra("menulist", listOrderMenu);
                intent.putExtra("restaurant", restaurant);
                intent.putExtra("branch", branch);
                startActivity(intent);
            }else {
                Toast.makeText(getContext(), R.string.login_first, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        TextView tvAddress = v.findViewById(R.id.tv_Address);
        TextView tvTime = v.findViewById(R.id.txtGioHoatDong);
        tvStatus = v.findViewById(R.id.txtTrangThaiHoatDong);
        RecyclerView rvMenu = v.findViewById(R.id.recyclMenu);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        rvMenu.setLayoutManager(layoutManager);

        if(restaurant != null && branch != null){
            tvAddress.setText(branch.getAddress());
            tvTime.setText(restaurant.getOpen_time() +" - "+restaurant.getClose_open());
            setStatusActive();

            MenuRestaurantDetailAdapter adapter = new MenuRestaurantDetailAdapter(getContext(), restaurant.getMenuList());
            rvMenu.setAdapter(adapter);
            adapter.setOnCountChangeListener(this);
        }

        TextView tvDiscount = v.findViewById(R.id.tv_discount);
        String discount = "Discount: "+restaurant.getDiscounts().getDiscount()+"% - Code: "+restaurant.getDiscounts().getCode()+
                "\nMin order: "+restaurant.getDiscounts().getMin_order() +"VND - Max discount: "+restaurant.getDiscounts().getMax_discount()+"VND";
        tvDiscount.setText(discount);

        String freeship = "";
        if(restaurant.getFreeship() == 0)
            freeship = "FreeShip under 2km, only 15000VND for 2km - 5km\n";
        freeship =  freeship + "Verify: "+restaurant.getFreeship() +"VND/Km";
        TextView tvFreeship = v.findViewById(R.id.tv_freeship);
        tvFreeship.setText(freeship);
    }

    private void setStatusActive(){
        //Lấy thời gian hiện tai
        Calendar calendar = Calendar.getInstance();
        //Format thời gian lấy giờ:phút
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String currentTime = dateFormat.format(calendar.getTime());
        String openTime=restaurant.getOpen_time();
        String closeTime=restaurant.getClose_open();

        try{
            Date datehientai = dateFormat.parse(currentTime);
            Date datemocua = dateFormat.parse(openTime);
            Date datedongcua = dateFormat.parse(closeTime);

            if(datehientai.after(datemocua) && datehientai.before(datedongcua)){
                tvStatus.setText(getResources().getString(R.string.open_time));
            }else{
                tvStatus.setText(getResources().getString(R.string.close));
            }
        }catch (Exception e){
            System.out.print("Message: " + e.getMessage());
        }
    }

    @Override
    public void onCountChanged(int count, Menu menu) {
        if (Common.accountCurrent != null) {
            int sum = 0;
            long moneyAll = 0;
            if (count > 0) {
                listOrderMenu.put(menu, count);
                for (Menu menu1 : listOrderMenu.keySet()) {
                    sum = sum + listOrderMenu.get(menu1);
                    moneyAll = moneyAll + menu1.getPrices();
                }
                viewDelivery.setVisibility(View.VISIBLE);
                if (getContext() instanceof RestaurantDetailActivity)
                    ((RestaurantDetailActivity) getContext()).setHideReview(true);
                tvCount.setText(sum + "");
                tvMoneyAll.setText(moneyAll + " VND");
            } else {
                listOrderMenu.remove(menu);
            }
            if (sum == 0) {
                viewDelivery.setVisibility(View.GONE);
                if (getContext() instanceof RestaurantDetailActivity)
                    ((RestaurantDetailActivity) getContext()).setHideReview(false);
            }
        } else {
            Toast.makeText(getContext(), R.string.login_first, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
        }
    }
}
