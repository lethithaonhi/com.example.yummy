package com.example.yummy.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.util.Attributes;
import com.example.yummy.Adapter.MenuPartnerAdapter;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class RestaurantManageActivity extends AppCompatActivity {
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_manage);

        type = getIntent().getIntExtra("type", 0);
        initView();
    }

    private void initView(){
        TextView tvType = findViewById(R.id.tv_type);
        ImageView imAdd = findViewById(R.id.btn_add);
        RecyclerView rcv = findViewById(R.id.rcv_partner);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcv.getContext(), layoutManager.getOrientation());
        rcv.addItemDecoration(dividerItemDecoration);

        String name;
        if(type == 0){
            name = getResources().getString(R.string.order);
        }else if(type == 1){
            name = getResources().getString(R.string.branch);
        }else if(type == 2){
            name = getResources().getString(R.string.menu);
            rcv.setItemAnimator(new FadeInLeftAnimator());
            MenuPartnerAdapter adapter = new MenuPartnerAdapter(this, Common.restaurantListCurrent.get(0).getMenuList());
            adapter.setMode(Attributes.Mode.Single);
            rcv.setAdapter(adapter);
        }else {
            name = getResources().getString(R.string.image);
        }
        tvType.setText(name);

    }
}
