package com.example.yummy.Activity;

import android.app.Dialog;
import android.content.Intent;
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

import com.example.yummy.Adapter.AccountAdminAdapter;
import com.example.yummy.Model.Account;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;

import java.util.ArrayList;
import java.util.List;

public class ManageAccountAdminActivity extends AppCompatActivity {
    private List<Account> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_admin_detail);
        initView();
    }
    private void initView(){
        TextView tvType = findViewById(R.id.tv_type);
        tvType.setText(R.string.account_setting);
        LinearLayout vAccount = findViewById(R.id.v_account);
        LinearLayout vAdmin = findViewById(R.id.v_admin);
        LinearLayout vPartner = findViewById(R.id.v_partner);
        LinearLayout vCus = findViewById(R.id.v_cus);
        vAccount.setVisibility(View.VISIBLE);
        LinearLayout btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(v->startActivity(new Intent(this,AddAccountManageAdminActivity.class)));
        vAdmin.setOnClickListener(v->showViewAccount(1));
        vPartner.setOnClickListener(v->showViewAccount(2));
        vCus.setOnClickListener(v->showViewAccount(3));
    }

    private void showViewAccount(int type){ //1: admin, 2: partner, 3: customer
        Dialog dialog = new Dialog(ManageAccountAdminActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.view_list_account_admin);
        dialog.setTitle("");
        dialog.show();

        ImageView imBack = dialog.findViewById(R.id.im_back);
        imBack.setOnClickListener(v->dialog.dismiss());
        TextView tvType = dialog.findViewById(R.id.tv_type);
        RecyclerView rcvAccount = dialog.findViewById(R.id.rcv_account);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvAccount.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvAccount.getContext(), layoutManager.getOrientation());
        rcvAccount.addItemDecoration(dividerItemDecoration);

        if(type == 1) {
            tvType.setText(R.string.admin);
        }else if(type == 2){
            tvType.setText(R.string.partner);
        }else {
            tvType.setText(R.string.customer);
        }

        getAccountFromType(type);
        AccountAdminAdapter accountAdminAdapter = new AccountAdminAdapter(this, data);
        rcvAccount.setAdapter(accountAdminAdapter);
    }

    private void getAccountFromType(int type){
        data = new ArrayList<>();
        for(Account account : Common.accountList){
            if(account.getRole() == type){
                data.add(account);
            }
        }
    }
}
