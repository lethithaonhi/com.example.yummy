package com.example.yummy.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yummy.Adapter.RestaurantAdapter;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import java.util.Collections;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity {
    private List<Restaurant> restaurantList;
    private int type; //1: mark, 0: normal, 2: distance
    private String[] typeName ={"Suggest", "Hot", "NearBy"};
    private boolean isSearch = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        type = getIntent().getIntExtra("type", 0);
        restaurantList = Common.restaurantListCurrent;
        setRestaurantList();
        initView();
    }

    private void initView(){
        TextView tvType = findViewById(R.id.tv_type);
        tvType.setText(typeName[type]);
        EditText edSearch = findViewById(R.id.edt_search);
        ImageView btnSearch = findViewById(R.id.btn_search);
        RecyclerView rcvRes = findViewById(R.id.rcv_restaurant);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvRes.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvRes.getContext(), layoutManager.getOrientation());
        rcvRes.addItemDecoration(dividerItemDecoration);

        RestaurantAdapter adapter = new RestaurantAdapter(restaurantList, this);
        rcvRes.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> {
            isSearch = !isSearch;
            edSearch.setVisibility(isSearch ? View.VISIBLE:View.GONE);
            tvType.setVisibility(isSearch ? View.GONE:View.VISIBLE);
        });
    }

    private void setRestaurantList(){
        if(type == 1){
            Collections.sort(restaurantList, (obj1, obj2) -> Float.compare(obj1.getMark(), obj2.getMark()));
        }else if(type == 2){
            for (Restaurant restaurant : restaurantList){
                Collections.sort(restaurant.getBranchList(), (obj1, obj2) -> Float.compare(obj1.getDistance(), obj2.getDistance()));
            }

            Collections.sort(restaurantList, (obj1, obj2) -> Float.compare(obj1.getBranchList().get(0).getDistance(), obj2.getBranchList().get(0).getDistance()));
        }
    }
}
