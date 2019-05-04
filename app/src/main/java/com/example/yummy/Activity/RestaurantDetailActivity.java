package com.example.yummy.Activity;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.yummy.Adapter.HistoryPaperAdapter;
import com.example.yummy.Adapter.RestaurantDetailPaperAdapter;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;

public class RestaurantDetailActivity extends AppCompatActivity {
    private Restaurant restaurant;
    private Branch branch;
    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_restaurant);

        restaurant = getIntent().getParcelableExtra("restaurant");
        branch = getIntent().getParcelableExtra("branch");
        setToolbar();
        setViewPaper();
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
}
