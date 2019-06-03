package com.example.yummy.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.yummy.Fragment.InfoPartnerFragment;
import com.example.yummy.Fragment.ManageAdminFragment;
import com.example.yummy.Fragment.RestaurantPartnerFragment;
import com.example.yummy.Fragment.SettingPartnerFragment;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.squareup.picasso.Picasso;

public class HomePartnerActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_partner);

        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (getSupportActionBar() != null) {
            setSupportActionBar(toolbar);
        }

        drawerLayout = findViewById(R.id.activity_main);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.okay, R.string.cancel);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                drawerToggle.setDrawerIndicatorEnabled(false);
                if (getSupportActionBar() != null)
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else {
                if (getSupportActionBar() != null)
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                drawerToggle.setDrawerIndicatorEnabled(true);
            }
        });

        NavigationView navigationView = findViewById(R.id.nv);
        navigationView.setCheckedItem(R.id.nv_res);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(Common.accountCurrent.getRole() == 3) {
            fragmentManager.beginTransaction().replace(R.id.frame_content, RestaurantPartnerFragment.newInstance()).commit();
        }else {
            fragmentManager.beginTransaction().replace(R.id.frame_content, ManageAdminFragment.newInstance()).commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        if(Common.accountCurrent.getRole() == 1){
            navigationView.getMenu().getItem(0).setTitle(R.string.manage);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
            item.setChecked(true);
            int id = item.getItemId();
            Fragment fragment = null;
            if (id == R.id.nv_res) {
                if(Common.accountCurrent.getRole() == 3) {
                    fragment = RestaurantPartnerFragment.newInstance();
                }else {
                    fragment = ManageAdminFragment.newInstance();
                }
            } else if (id == R.id.nv_settings) {
                fragment = SettingPartnerFragment.newInstance();
            } else if (id == R.id.nv_account) {
                fragment = InfoPartnerFragment.newInstance();
            }

            if (fragment != null) {
                fragmentManager.beginTransaction().replace(R.id.frame_content, fragment).commit();
                setTitle(item.getTitle());
                drawerLayout.closeDrawer(GravityCompat.START);
            }

            return true;
        });

        View headerLayout = navigationView.getHeaderView(0);
        ImageView imAvatar = headerLayout.findViewById(R.id.im_avatar);
        TextView tvOwner = headerLayout.findViewById(R.id.tv_owner);
        TextView tvName = headerLayout.findViewById(R.id.tv_name);
        if(Common.accountCurrent != null && Common.restaurantListCurrent != null) {
            Picasso.get().load(Common.accountCurrent.getAvatar()).into(imAvatar);
            if(Common.accountCurrent.getRole() == 3) {
                tvOwner.setText(getResources().getString(R.string.owner) + ": " + Common.restaurantListCurrent.get(0).getName() + " - " + Common.restaurantListCurrent.get(0).getCity());
            }else {
                tvOwner.setText(R.string.add_menu);
            }
            tvName.setText(Common.accountCurrent.getName());
        }

        LinearLayout btnSignOut = headerLayout.findViewById(R.id.btn_singOut);
        btnSignOut.setOnClickListener(v -> finish());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_partner, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
