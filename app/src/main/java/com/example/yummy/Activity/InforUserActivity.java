package com.example.yummy.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yummy.R;
import com.example.yummy.Utils.Common;

public class InforUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_account);

        TextView tvName = findViewById(R.id.tv_name);
        tvName.setText(Common.accountCurrent.getName());
        TextView tvUsername = findViewById(R.id.tv_username);
        tvUsername.setText(Common.accountCurrent.getUsername());
        EditText edName = findViewById(R.id.ed_nameinfo);
        edName.setText(Common.accountCurrent.getName());
        EditText edEmail = findViewById(R.id.ed_email);
        edEmail.setText(Common.accountCurrent.getEmail());
        TextView tvBirth = findViewById(R.id.tv_birth);
        tvBirth.setText(Common.accountCurrent.getDatebirth());
        TextView tvGender = findViewById(R.id.tv_sex);
        if(Common.accountCurrent.getGender() == 1){
            tvGender.setText(R.string.male);
        }else if(Common.accountCurrent.getGender() == 2){
            tvGender.setText(R.string.felmale);
        }else {
            tvGender.setText(R.string.none);
        }

        EditText tvPhone = findViewById(R.id.ed_phone);
        tvPhone.setText(Common.accountCurrent.getPhone());

        ImageView imClose = findViewById(R.id.im_close);
        imClose.setOnClickListener(v->finish());
    }
}
