package com.example.yummy.Activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AddRestaurantActivity extends AppCompatActivity {
    private TextView tvOpen, tvClose;
    private Calendar myCalender;
    private List<String> checkList;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_add_restaurant);

        checkList = new ArrayList<>();
        userId = getIntent().getStringExtra("userID");
        initView();
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
        AddResMenuAdapter adapter = new AddResMenuAdapter(Common.menuList.get(0));
        rcvType.setAdapter(adapter);
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
                mData.child(Node.QuanAn).push().setValue(restaurant);
                if(userId != null){
                    mData.child(Node.Partner).child(userId).child(Node.Boss).setValue(key);
                }

                Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
                finish();
            }

        });
    }

    private boolean checkTime(String open, String close){
        SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm");
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

    public class AddResMenuAdapter extends RecyclerView.Adapter<AddResMenuAdapter.AddResMenuHolder> {
        private Map<String, String> data;
        private List<String> menuList;

        AddResMenuAdapter(Map<String,String> data) {
            this.data = data;
            menuList = new ArrayList<>(data.values());
        }

        @NonNull
        @Override
        public AddResMenuHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(AddRestaurantActivity.this);
            View view = inflater.inflate(R.layout.item_city, viewGroup, false);
            return new AddResMenuHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AddResMenuHolder holder, int pos) {
            String name = menuList.get(pos);
            holder.tvName.setText(name);

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String key = getKey(name);
                if(isChecked){
                    checkList.add(key);
                }else {
                    checkList.remove(key);
                }
            });
        }

        @Override
        public int getItemCount() {
            return menuList != null ? menuList.size() : 0;
        }

        class AddResMenuHolder extends RecyclerView.ViewHolder {
            CheckBox checkBox;
            TextView tvName, tvCount;

            AddResMenuHolder(@NonNull View itemView) {
                super(itemView);

                checkBox = itemView.findViewById(R.id.checkbox_city);
                tvName = itemView.findViewById(R.id.tv_nameCity);
                tvCount = itemView.findViewById(R.id.tv_countCity);
                tvCount.setVisibility(View.GONE);
            }
        }

        private String getKey(String menu) {
            for (Map.Entry<String,String> entry : data.entrySet()) {
                if (entry.getValue().contains(menu)) {
                    return entry.getKey();
                }
            }
            return "";
        }

    }
}
