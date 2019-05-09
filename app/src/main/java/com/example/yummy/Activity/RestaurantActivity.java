package com.example.yummy.Activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yummy.Adapter.CityAdapter;
import com.example.yummy.Adapter.RestaurantAdapter;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import java.util.Collections;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity {
    private List<Restaurant> restaurantList;
    private int type; //1: mark, 0: normal, 2: distance, 3:discount
    private String[] typeName ={"Suggest", "Hot", "NearBy", "Discount"};
    private boolean isSearch = false;
    private CityAdapter cityAdapter;

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

        LinearLayout viewCity = findViewById(R.id.view_city);
        viewCity.setOnClickListener(v -> {
            createDialog();
        });

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
        }else if (type== 3){
            Collections.sort(restaurantList, (ob1, ob2) -> Integer.compare(ob1.getDiscounts().getDiscount(), ob2.getDiscounts().getDiscount()));
        }
    }

    private void createDialog(){
        Dialog dialog = new Dialog(RestaurantActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_city);
        dialog.show();

        EditText edSearchCity = dialog.findViewById(R.id.edt_search_city);

        RecyclerView rcvCity = dialog.findViewById(R.id.rcv_city);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvCity.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvCity.getContext(), layoutManager.getOrientation());
        rcvCity.addItemDecoration(dividerItemDecoration);

        cityAdapter = new CityAdapter(this, Common.cityList);
        rcvCity.setAdapter(cityAdapter);

        edSearchCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cityAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
