package com.example.yummy.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.yummy.Fragment.AccountFragment;
import com.example.yummy.Fragment.HomeFragment;
import com.example.yummy.R;
import com.example.yummy.Utils.UtilsBottomBar;
import java.lang.reflect.Field;

public class BottomBarActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_bottombar);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        disableShiftMode(bottomNavigation);

        bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    @SuppressLint("RestrictedApi")
    public void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
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
        switch (item.getItemId()) {
            case R.id.tab_home:
                UtilsBottomBar.startFragment(getSupportFragmentManager(), HomeFragment.newInstance());
                break;
            case R.id.tab_history:
                UtilsBottomBar.startFragment(getSupportFragmentManager(), HomeFragment.newInstance());
                break;
            case R.id.tab_bell:
                UtilsBottomBar.startFragment(getSupportFragmentManager(), HomeFragment.newInstance());
                break;
            case R.id.tab_info:
                UtilsBottomBar.startFragment(getSupportFragmentManager(), AccountFragment.newInstance());
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        UtilsBottomBar.startFragment(getSupportFragmentManager(), HomeFragment.newInstance());
    }
}
