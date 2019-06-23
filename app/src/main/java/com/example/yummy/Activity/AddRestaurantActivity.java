package com.example.yummy.Activity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Adapter.AddResMenuAdapter;
import com.example.yummy.Adapter.CityAdapter;
import com.example.yummy.Adapter.DistinctAdapter;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Receive.NetworkChangeReceiver;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddRestaurantActivity extends AppCompatActivity implements AddResMenuAdapter.OnChangeListMenu{
    private TextView tvOpen, tvClose;
    private Calendar myCalender;
    private List<String> checkList;
    private String userId;
    private NetworkChangeReceiver networkChangeReceiver;
    private  CityAdapter cityAdapter;
    private RecyclerView rcvDistinct;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_add_restaurant);

        checkList = new ArrayList<>();
        userId = getIntent().getStringExtra("userID");
        initView();
        registerReceiver();
    }

    private void initView() {
        EditText edName = findViewById(R.id.edt_name);
        tvOpen = findViewById(R.id.tv_open);
        tvClose = findViewById(R.id.tv_close);
        RadioGroup radioGroup = findViewById(R.id.radioGrp);
        EditText edFeeShip = findViewById(R.id.ed_ship);
        EditText edVideo = findViewById(R.id.edt_video);
        RecyclerView rcvType = findViewById(R.id.rcv_type);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvType.setLayoutManager(layoutManager);
        rcvType.setNestedScrollingEnabled(false);

        if(Common.menuList != null && Common.blogList.size() > 0) {
            AddResMenuAdapter adapter = new AddResMenuAdapter(this);
            rcvType.setAdapter(adapter);
            adapter.onChangeListMenu(this);
        }
        Button btnCreate = findViewById(R.id.btn_create);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rdb_no) {
                edFeeShip.setVisibility(View.GONE);
            } else {
                edFeeShip.setVisibility(View.VISIBLE);
            }
        });
        myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);
        tvOpen.setText(hour + ":" + minute);
        tvClose.setText(hour + ":" + minute);

        RecyclerView rcvCity = findViewById(R.id.rcv_city);
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvCity.setLayoutManager(layout);
        rcvCity.setNestedScrollingEnabled(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvCity.getContext(), layoutManager.getOrientation());
        rcvCity.addItemDecoration(dividerItemDecoration);

        cityAdapter = new CityAdapter(this, Common.cityList, true);
        rcvCity.setAdapter(cityAdapter);

        rcvDistinct = findViewById(R.id.rcv_distinct);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvDistinct.setLayoutManager(layoutManager1);
        rcvDistinct.setNestedScrollingEnabled(false);

        DistinctAsyncTask asyncTask = new DistinctAsyncTask();
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        tvOpen.setOnClickListener(v -> openTimeDialog(tvOpen, hour, minute));
        tvClose.setOnClickListener(v -> openTimeDialog(tvClose, hour, minute));

        btnCreate.setOnClickListener(v -> {
            String name = edName.getText().toString().trim();
            String video = edVideo.getText().toString().trim();
            String openTime = tvOpen.getText().toString();
            String closeTime = tvClose.getText().toString();
            boolean isFreeShip = edFeeShip.getVisibility() == View.GONE;
            int freeShip = isFreeShip ? 0 : Integer.parseInt(edFeeShip.getText().toString().trim());

            if(!name.isEmpty() && !video.isEmpty() && checkTime(openTime, closeTime) && checkList.size() > 0){
                Restaurant restaurant = new Restaurant();
                restaurant.setName(name);
                restaurant.setOpen_time(openTime);
                restaurant.setClose_open(closeTime);
                restaurant.setFreeship(freeShip);
                restaurant.setVideo(video);
                restaurant.setMenuIdList(checkList);
                DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
                String key = mData.child(Node.QuanAn).push().getKey();
                mData.child(Node.QuanAn).child(key).setValue(restaurant);
                if(userId != null){
                    mData.child(Node.Partner).child(userId).child(Node.Boss).setValue(key);
                }
                if(CityAdapter.city != null && !CityAdapter.city.isEmpty()){
                    mData.child(Node.DiaDiem).child(CityAdapter.city).push().setValue(key);
                }

                UtilsBottomBar.showSuccessView(this, getString(R.string.success),true);
            }else {
                Toast.makeText(this, R.string.empty_user, Toast.LENGTH_SHORT).show();
            }


        });
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

    private boolean checkTime(String open, String close){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm");
        try {
            Date dateOpen = inputParser.parse(open);
            Date dateClose = inputParser.parse(close);
            if ( dateOpen.before(dateClose)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void openTimeDialog(TextView txtTime, int hour, int minute) {
        TimePickerDialog.OnTimeSetListener myTimeListener = (view, hourOfDay, minute1) -> {
            if (view.isShown()) {
                myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalender.set(Calendar.MINUTE, minute1);
                txtTime.setText(hourOfDay + ":" + minute1);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
        timePickerDialog.setTitle("Choose hour:");
        if (timePickerDialog.getWindow() != null)
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, R.string.not_back, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnChangeListMenu(List<String> checkList) {
        this.checkList = checkList;
    }

    @SuppressLint("StaticFieldLeak")
    private class DistinctAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            DistinctAdapter adapter = new DistinctAdapter(getBaseContext(), Common.distinctList);
            rcvDistinct.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            UtilsBottomBar.getDistinct(cityAdapter.getCity());
            return null;
        }
    }
}
