package com.example.yummy.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yummy.Adapter.MenuRestaurantDetailAdapter;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DeliveryRestaurantDetailFragment extends Fragment {
    private Restaurant restaurant;
    private Branch branch;
    private TextView tvStatus;

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
        }
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
}
