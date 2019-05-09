package com.example.yummy.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yummy.Adapter.CityAdapter;
import com.example.yummy.Adapter.RestaurantAdapter;
import com.example.yummy.Adapter.RestaurantHorizontalAdapter;
import com.example.yummy.Fragment.HomeFragment;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.UtilsBottomBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RestaurantActivity extends AppCompatActivity {
    private List<Restaurant> restaurantList;
    private int type; //1: mark, 0: normal, 2: distance, 3:discount
    private String[] typeName ={"Suggest", "Hot", "NearBy", "Discount"};
    private boolean isSearch = false;
    private CityAdapter cityAdapter;
    private TextView tvAddress;
    private RecyclerView rcvRes;
    private Dialog dialog;
    private  RestaurantAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        type = getIntent().getIntExtra("type", 0);
        initView();
    }

    private void initView(){
        TextView tvType = findViewById(R.id.tv_type);
        tvType.setText(typeName[type]);
        EditText edSearch = findViewById(R.id.edt_search);
        ImageView btnSearch = findViewById(R.id.btn_search);
        tvAddress = findViewById(R.id.tv_address);
        TextView tvCIty = findViewById(R.id.tv_city);
        tvCIty.setText(Common.myAddress);
        ImageView btnClose = findViewById(R.id.btn_close);
        ImageView btnMap = findViewById(R.id.btn_map);
        rcvRes = findViewById(R.id.rcv_restaurant);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvRes.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvRes.getContext(), layoutManager.getOrientation());
        rcvRes.addItemDecoration(dividerItemDecoration);
        RestaurantMainAsyncTask myAsyncTask = new RestaurantMainAsyncTask();
        myAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        LinearLayout viewCity = findViewById(R.id.view_city);
        viewCity.setOnClickListener(v -> {
            createDialog();
        });

        btnSearch.setOnClickListener(v -> {
            edSearch.setVisibility(View.VISIBLE);
            viewCity.setVisibility(View.GONE);
            tvType.setVisibility(View.GONE);
            btnClose.setVisibility(View.VISIBLE);
            btnSearch.setVisibility(View.GONE);
        });

        btnClose.setOnClickListener(v->{
            viewCity.setVisibility(View.VISIBLE);
            edSearch.setText("");
            edSearch.setVisibility(View.GONE);
            tvType.setVisibility(View.VISIBLE);
            btnClose.setVisibility(View.GONE);
            btnSearch.setVisibility(View.VISIBLE);
            UtilsBottomBar.hideKeyboard(this);
        });

        btnMap.setOnClickListener(v -> {

        });
        getAddressCurrent();

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s.toString());
            }
        });
    }

    private void setRestaurantList(){
        if(type == 1){
            Collections.sort(restaurantList, (obj1, obj2) -> (int) (obj1.getMark() - obj2.getMark()));
        }else if(type == 2){
            for (Restaurant restaurant : restaurantList){
                Collections.sort(restaurant.getBranchList(), (obj1, obj2) -> Float.compare(obj1.getDistance(), obj2.getDistance()));
            }

            Collections.sort(restaurantList, (obj1, obj2) -> Float.compare(obj1.getBranchList().get(0).getDistance(), obj2.getBranchList().get(0).getDistance()));
        }else if (type== 3){
            Collections.sort(restaurantList, (ob1, ob2) -> ob2.getDiscounts().getDiscount() - ob1.getDiscounts().getDiscount());
        }
    }

    private void createDialog(){
        dialog = new Dialog(RestaurantActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_city);
        dialog.show();

        EditText edSearchCity = dialog.findViewById(R.id.edt_search_city);
        ImageView close = dialog.findViewById(R.id.close_dialog);
        close.setOnClickListener(v-> dismissDialog());
        RecyclerView rcvCity = dialog.findViewById(R.id.rcv_city);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvCity.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvCity.getContext(), layoutManager.getOrientation());
        rcvCity.addItemDecoration(dividerItemDecoration);

        cityAdapter = new CityAdapter(this, Common.cityList);
        rcvCity.setAdapter(cityAdapter);

        TextView tvDone = dialog.findViewById(R.id.tv_done);
        tvDone.setOnClickListener(v-> {
            finish();
            Intent intent = new Intent(this, RestaurantActivity.class);
            intent.putExtra("type", type);
            startActivity(intent);
        });

        edSearchCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cityAdapter.getFilter().filter(s.toString());
            }
        });
    }

    public void dismissDialog(){
        if(dialog != null){
            dialog.dismiss();
        }
    }

    private void getAddressCurrent(){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Common.myLocation.getLatitude(), Common.myLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String knownName = addresses.get(0).getFeatureName();
            tvAddress.setText(address);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class RestaurantMainAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            restaurantList = new ArrayList<>();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new RestaurantAdapter(restaurantList, getBaseContext());
            rcvRes.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            restaurantList = Common.db.getRestaurant(Common.listResId, Common.myAddress);
            for (Restaurant restaurant :restaurantList){
                for (Branch branch: restaurant.getBranchList()){
                    branch.setDistance(UtilsBottomBar.getDistanceBranch(branch));
                }
            }
            setRestaurantList();
            return null;
        }


    }
}
