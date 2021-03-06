package com.example.yummy.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Adapter.MenuOrderAdapter;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Menu;
import com.example.yummy.Model.Order;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Receive.NetworkChangeReceiver;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class OrderCustomActivity extends AppCompatActivity {
    private Restaurant restaurant;
    private HashMap<Menu, Integer> menuList = new HashMap<>();
    private Branch branch;
    private long count = 0;
    private int feeShip = 0, discount=0, item = 0 ;;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_cus);

        restaurant = getIntent().getParcelableExtra("restaurant");
        branch = getIntent().getParcelableExtra("branch");
        branch.setDistance(UtilsBottomBar.getDistanceBranch(branch, Common.myLocation.getLatitude(), Common.myLocation.getLongitude()));
        menuList = (HashMap<Menu, Integer>) getIntent().getSerializableExtra("menulist");
        if (restaurant != null && branch != null && menuList != null) {
            initView();
        } else {
            finish();
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }
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
    private void initView() {
        RecyclerView rcvMenu = findViewById(R.id.rcv_menu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvMenu.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvMenu.getContext(), layoutManager.getOrientation());
        rcvMenu.addItemDecoration(dividerItemDecoration);
        rcvMenu.setNestedScrollingEnabled(false);

        MenuOrderAdapter adapter = new MenuOrderAdapter(this, menuList);
        rcvMenu.setAdapter(adapter);

        ImageView imgBack = findViewById(R.id.im_back);
        TextView tvNameRes = findViewById(R.id.tv_nameRes);
        TextView tvEdit = findViewById(R.id.tv_edit);
        EditText edName = findViewById(R.id.ed_name);
        EditText edPhone = findViewById(R.id.ed_phone);
        EditText edNode = findViewById(R.id.ed_node);
        TextView tvAddress = findViewById(R.id.tv_address);
        TextView tvTime = findViewById(R.id.tv_time);
        TextView tvDate = findViewById(R.id.tv_date);
        TextView tvCount = findViewById(R.id.tv_count);
        TextView tvTotal = findViewById(R.id.tv_total);
        TextView tvFeeShip = findViewById(R.id.tv_ship);
        TextView tvDistance = findViewById(R.id.tv_distance);
        Button btnSubmit = findViewById(R.id.btn_submit);
        TextView tvDiscount = findViewById(R.id.tv_discount);
        TextView tvCode = findViewById(R.id.tv_code);
        LinearLayout viewDiscount = findViewById(R.id.view_discount);

        if (restaurant.getFreeship() == 0) {
            if (branch.getDistance() <= 2000) {
                feeShip = 0;
            } else if (branch.getDistance() <= 5000)
                feeShip = 15000;
            else
                feeShip = (int) (5000 * (branch.getDistance() / 1000));
        } else {
            feeShip = (int) (restaurant.getFreeship() * (branch.getDistance() / 1000));
        }

        if (feeShip > 0) {
            if (feeShip / 100 > 5) {
                feeShip = (feeShip / 1000 + 1) * 1000;
            } else {
                feeShip = (feeShip / 100 + 500);

            }
        }

        tvDistance.setText(String.format("%.02f", branch.getDistance() / 1000) + " km");

        tvFeeShip.setText(UtilsBottomBar.convertStringToMoney(feeShip));

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        tvTime.setText(today.format("%k:%M:%S"));

        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
        tvDate.setText(dateformat.format(c.getTime()));

        for (Menu menu : menuList.keySet()) {
            count += (menu.getPrices()* menuList.get(menu));
            item += menuList.get(menu);
        }

        if(restaurant.getDiscounts() != null && count >= restaurant.getDiscounts().getMin_order()){
            discount = (int) ((count * restaurant.getDiscounts().getDiscount())/100);

            if (discount / 100 > 5) {
                discount = (discount / 1000 + 1) * 1000;
            } else {
                discount = (discount / 100 + 500);

            }

            if(discount != 0 && discount > restaurant.getDiscounts().getMax_discount())
                discount = restaurant.getDiscounts().getMax_discount();

                tvDiscount.setText("-"+UtilsBottomBar.convertStringToMoney(discount));
                viewDiscount.setVisibility(View.VISIBLE);
                tvCode.setText(restaurant.getDiscounts().getCode());

        }

        count = count + feeShip - discount;

        imgBack.setOnClickListener(v -> finish());
        tvNameRes.setText(restaurant.getName());
        tvAddress.setText(Common.myLocation.getName());
        tvEdit.setOnClickListener(v -> startActivity(new Intent(this, ChangeAddressActivity.class)));
        tvCount.setText(item + " " + getResources().getString(R.string.item));
        tvTotal.setText(UtilsBottomBar.convertStringToMoney((int) count));
        edName.setText(Common.accountCurrent.getName());
        edPhone.setText(Common.accountCurrent.getPhone());

        btnSubmit.setOnClickListener(v -> {
            String name = edName.getText().toString().trim();
            String phone = edPhone.getText().toString().trim();
            String node = edNode.getText().toString().trim();
            String date = tvDate.getText().toString().trim();
            String time = tvTime.getText().toString().trim();
            String address = tvAddress.getText().toString().trim();

            if (!name.isEmpty() && !phone.isEmpty() && !date.isEmpty() && !time.isEmpty() && !address.isEmpty()) {
                Order order = new Order();
                order.setId_user(Common.accountCurrent.getUserId());
                order.setAddress(address);
                order.setNode(node);
                order.setDate(date);
                order.setTime(time);
                order.setTotal(count);
                order.setIsStatus(0);
                order.setName_res(restaurant.getName());
                order.setAvatar(branch.getAvatar());
                order.setAddress_res(branch.getAddress());
                order.setFeeShip(feeShip);
                order.setDistance(branch.getDistance() / 1000);
                order.setDiscount(discount);
                order.setName(name);
                order.setPhone(phone);
                order.setCount(item);

                DatabaseReference nodeRoot = FirebaseDatabase.getInstance().getReference();
                String key = nodeRoot.child(Node.Order).child(restaurant.getRes_id()).push().getKey();
                if (key != null) {
                    nodeRoot.child(Node.Order).child(restaurant.getRes_id()).child(key).setValue(order);

                    for (Menu menu : menuList.keySet()) {
                        String keyMenu = nodeRoot.child(Node.Order).child(restaurant.getRes_id()).child(key).push().getKey();
                        if (keyMenu != null) {
                            nodeRoot.child(Node.Order_Menu).child(restaurant.getRes_id()).child(key).child(keyMenu).setValue(menu);
                            nodeRoot.child(Node.Order_Menu).child(restaurant.getRes_id()).child(key).child(keyMenu).child(Node.count).setValue(menuList.get(menu));
                        }
                    }

                    UtilsBottomBar.showSuccessView(this, getString(R.string.order_success), true);
                    Handler handler = new Handler();
                    handler.postDelayed(() -> startActivity(new Intent(OrderCustomActivity.this, BottomBarActivity.class)), 5000);
                    UtilsBottomBar.getOrderCurrent();
                }else {
                    Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, R.string.empty_user, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
