package com.example.yummy.Activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.example.yummy.R;

import java.util.Calendar;

public class AddRestaurantActivity extends AppCompatActivity {
    private TextView tvOpen, tvClose;
    private Calendar myCalender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_add_restaurant);

        initView();
    }

    private void initView(){
        EditText edName = findViewById(R.id.edt_name);
        tvOpen = findViewById(R.id.tv_open);
        tvClose = findViewById(R.id.tv_close);
        RadioGroup radioGroup = findViewById(R.id.radioGrp);
        EditText edFeeShip = findViewById(R.id.ed_ship);
        EditText edVideo = findViewById(R.id.edt_video);
        RecyclerView rcvType = findViewById(R.id.rcv_type);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcvType.setLayoutManager(layoutManager);
        Button btnCreate = findViewById(R.id.btn_create);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.rdb_no){
                edFeeShip.setVisibility(View.GONE);
            }else {
                edFeeShip.setVisibility(View.VISIBLE);
            }
        });
        myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);
        tvOpen.setText(hour +":"+minute);
        tvClose.setText(hour +":"+minute);

        tvOpen.setOnClickListener(v-> openTimeDialog(tvOpen, hour, minute));
        tvClose.setOnClickListener(v-> openTimeDialog(tvClose, hour, minute));
    }

    private void openTimeDialog(TextView txtTime, int hour, int minute){
        TimePickerDialog.OnTimeSetListener myTimeListener = (view, hourOfDay, minute1) -> {
            if (view.isShown()) {
                myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalender.set(Calendar.MINUTE, minute1);
                txtTime.setText(hourOfDay+":"+ minute1);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
        timePickerDialog.setTitle("Choose hour:");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }
}
