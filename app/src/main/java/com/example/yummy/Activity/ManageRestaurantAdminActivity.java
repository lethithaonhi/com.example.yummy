package com.example.yummy.Activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yummy.Adapter.RestaurantAdapter;
import com.example.yummy.R;
import com.example.yummy.Receive.NetworkChangeReceiver;
import com.example.yummy.Utils.Common;

public class ManageRestaurantAdminActivity extends AppCompatActivity {
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_admin_detail);
        initView();
        registerReceiver();
    }

    private void registerReceiver(){
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(networkChangeReceiver != null)
            unregisterReceiver(networkChangeReceiver);
    }
    private void initView(){
        TextView tvType = findViewById(R.id.tv_type);
        tvType.setText(R.string.restaurant);
        LinearLayout vAccount = findViewById(R.id.v_account);
        vAccount.setVisibility(View.GONE);
        RecyclerView rcv = findViewById(R.id.rcv_manage);
        rcv.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcv.getContext(), layoutManager.getOrientation());
        rcv.addItemDecoration(dividerItemDecoration);

        LinearLayout btnAdd = findViewById(R.id.btn_add);
        btnAdd.setVisibility(View.GONE);

        RestaurantAdapter adapter = new RestaurantAdapter(Common.restaurantListAll, this, 0);
        rcv.setAdapter(adapter);
        ImageView imClose = findViewById(R.id.im_close);
        imClose.setOnClickListener(v->finish());
    }
}
