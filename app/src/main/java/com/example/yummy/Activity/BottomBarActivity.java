package com.example.yummy.Activity;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.yummy.Fragment.AccountFragment;
import com.example.yummy.Fragment.HistoryTabFragment;
import com.example.yummy.Fragment.HomeFragment;
import com.example.yummy.Fragment.NotificationFragment;
import com.example.yummy.Model.Blog;
import com.example.yummy.Model.Order;
import com.example.yummy.R;
import com.example.yummy.Receive.NetworkChangeReceiver;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BottomBarActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce = false;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bottombar);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        disableShiftMode(bottomNavigation);

        bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        Common.blogList = new ArrayList<>();
        getBlog();
        if(Common.accountCurrent != null && Common.orderListCurrent == null) {
            Common.orderListCurrent = new ArrayList<>();
            UtilsBottomBar.getOrderCurrent();
        }

        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    private void getBlog(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(Node.Blog).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Common.blogList = new ArrayList<>();
               for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                   Blog blog = dataSnapshot1.getValue(Blog.class);
                   if(blog != null)
                       blog.setId(dataSnapshot1.getKey());
                       Common.blogList.add(blog);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private BottomNavigationMenuView menuView;
    @SuppressLint("RestrictedApi")
    public void disableShiftMode(BottomNavigationView view) {
        menuView = (BottomNavigationMenuView) view.getChildAt(0);
        BottomNavigationItemView items = (BottomNavigationItemView) menuView.getChildAt(0);
        // set once again checked value, so view will be updated
        items.setChecked(true);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("notify", "Unable to get shift mode field");
        } catch (IllegalAccessException e) {
            Log.e("notify", "Unable to change value of shift mode");
        }
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        setItems(id);
        return true;
    }

    private void setItems( int id){
        switch (id) {
            case R.id.tab_home:
                UtilsBottomBar.startFragment(getSupportFragmentManager(), HomeFragment.newInstance());
                break;
            case R.id.tab_history:
                UtilsBottomBar.startFragment(getSupportFragmentManager(), HistoryTabFragment.newInstance());
                break;
            case R.id.tab_bell:
                UtilsBottomBar.startFragment(getSupportFragmentManager(), NotificationFragment.newInstance());
                break;
            case R.id.tab_info:
                UtilsBottomBar.startFragment(getSupportFragmentManager(), AccountFragment.newInstance());
                break;
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onResume() {
        super.onResume();
        setItems(menuView.getSelectedItemId());
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.twice_exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(networkChangeReceiver != null)
            unregisterReceiver(networkChangeReceiver);
    }
}
