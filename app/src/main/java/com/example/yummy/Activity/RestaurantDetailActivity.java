package com.example.yummy.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yummy.Adapter.MenuRestaurantDetailAdapter;
import com.example.yummy.Adapter.RestaurantDetailPaperAdapter;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Menu;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class RestaurantDetailActivity extends AppCompatActivity implements MenuRestaurantDetailAdapter.OnCountChangeListener {
    private Restaurant restaurant;
    private Branch branch;
    private ViewPager viewPager;
    private LinearLayout viewDelivery;
    private FrameLayout viewReview;
    private Map<Menu, Integer> listOrderMenu = new HashMap<>();
    private TextView tvCount, tvMoneyAll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        restaurant = getIntent().getParcelableExtra("restaurant");
        branch = getIntent().getParcelableExtra("branch");
        setToolbar();
        setViewPaper();
        initView();
    }

    private void initView(){
        ImageView imRes = findViewById(R.id.imgQuanAnChiTiet);
        Picasso.get().load(branch.getAvatar()).into(imRes);
        TextView tvMark = findViewById(R.id.tv_mark);
        tvMark.setText(restaurant.getMark()+"");
        viewDelivery = findViewById(R.id.view_delivery);
        viewReview = findViewById(R.id.view_review);
        tvCount = findViewById(R.id.tv_count);
        tvMoneyAll = findViewById(R.id.tv_moneyAll);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(restaurant.getName());
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbarLayout);
        if(restaurant != null) {
            collapsingToolbar.setTitle(restaurant.getName());
        }
    }

    private void setViewPaper(){
        viewPager = findViewById(R.id.view_pager);
        RestaurantDetailPaperAdapter adapter = new RestaurantDetailPaperAdapter(getSupportFragmentManager(), restaurant, branch);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onCountChanged(int count, Menu menu) {
        if(count > 0) {
            listOrderMenu.put(menu, count);
            int sum = 0;
            long moneyAll = 0;
            for (Menu menu1 : listOrderMenu.keySet()) {
                sum = sum + listOrderMenu.get(menu1);
                moneyAll = moneyAll + menu1.getPrices();
            }
            viewDelivery.setVisibility(View.VISIBLE);
            viewReview.setVisibility(View.GONE);
            tvCount.setText(count + "");
            tvMoneyAll.setText(moneyAll + " VND");
        }else {
            listOrderMenu.remove(menu);
            viewDelivery.setVisibility(View.GONE);
            viewDelivery.setVisibility(View.VISIBLE);
        }
    }
}
