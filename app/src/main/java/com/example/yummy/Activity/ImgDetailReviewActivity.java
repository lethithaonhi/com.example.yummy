package com.example.yummy.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yummy.Adapter.ImgRestaurantDetailAdapter;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;

public class ImgDetailReviewActivity extends AppCompatActivity {
    private Restaurant restaurant;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagedetail_review);
        restaurant = getIntent().getParcelableExtra("restaurant");
        initView();

    }

    private void initView(){
        TextView tvName = findViewById(R.id.tv_name);
        tvName.setText(restaurant.getName());
        TextView tvCountImg = findViewById(R.id.tv_countImg);
        tvCountImg.setText(restaurant.getImgList().size() + " images");

        RecyclerView rcvImRes = findViewById(R.id.rcv_image_res);
        rcvImRes.setLayoutManager(new GridLayoutManager(this, 3));
        ImgRestaurantDetailAdapter adapter = new ImgRestaurantDetailAdapter(this, restaurant.getImgList(), restaurant, 1);
        rcvImRes.setAdapter(adapter);

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());
    }
}
