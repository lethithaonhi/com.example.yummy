package com.example.yummy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.yummy.Adapter.AddressAdapter;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;

public class AddressHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_setting);

        LinearLayout viewHeader = findViewById(R.id.view_header);
        ViewCompat.setElevation(viewHeader, 10);

        RecyclerView recyclerView = findViewById(R.id.rcv_address);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        AddressAdapter addressAdapter = new AddressAdapter(this, Common.accountCurrent.getAddressList(), 0);
        recyclerView.setAdapter(addressAdapter);

        ImageView btnAdd = findViewById(R.id.btn_add);
        ImageView imCLose = findViewById(R.id.close_dialog);
        imCLose.setOnClickListener(v->finish());
        btnAdd.setOnClickListener(v-> startActivity(new Intent(this, ChangeAddressActivity.class)));
    }
}
