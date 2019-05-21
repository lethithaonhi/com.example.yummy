package com.example.yummy.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yummy.Model.Order;
import com.example.yummy.R;

public class OrderDetailActivity extends AppCompatActivity {
    private Order order;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_detail_order);

        order = getIntent().getParcelableExtra("order");
        initView();
    }

    private void initView(){
        ImageView imback = findViewById(R.id.im_back);
        TextView tvNameRes = findViewById(R.id.tv_nameRes);
        TextView tvStatus = findViewById(R.id.tv_status);
        TextView tvID = findViewById(R.id.tv_orderid);
        TextView tvCount = findViewById(R.id.tv_count);
        TextView tvDistance = findViewById(R.id.tv_distance);
        TextView tvShip = findViewById(R.id.tv_ship);
        TextView tvDiscount = findViewById(R.id.tv_discount);
        LinearLayout vDiscount = findViewById(R.id.view_discount);
        TextView tvTotal = findViewById(R.id.tv_total);
        TextView tvName = findViewById(R.id.tv_name);
        TextView tvPhone = findViewById(R.id.tv_phone);
        TextView tvAddress = findViewById(R.id.tv_address);

        imback.setOnClickListener(v->finish());
        tvNameRes.setText(order.getName_res());
        tvStatus.setText(order.getIsStatus()==3 ? R.string.complete:R.string.cancel);
        tvID.setText(order.getId());
        if(order.getDiscount() > 0) {
            vDiscount.setVisibility(View.VISIBLE);
            tvDiscount.setText(order.getDiscount() + "VND");
        }
        tvDistance.setText(order.getDistance() + "km");
        tvShip.setText(order.getFeeShip());
        tvTotal.setText(order.getTotal()+"VND");
        tvName.setText(order.getName());
        tvPhone.setText(order.getPhone());
        tvAddress.setText(order.getAddress());
        tvCount.setText(order.getCount());
    }
}
