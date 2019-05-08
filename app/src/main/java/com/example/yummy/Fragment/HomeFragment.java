package com.example.yummy.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yummy.Activity.RestaurantActivity;
import com.example.yummy.Adapter.BannerAdapter;
import com.example.yummy.Adapter.RestaurantHorizontalAdapter;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.UtilsBottomBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment implements View.OnClickListener{
    private RecyclerView rcvExp;
    private int currentPage = 0;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        rcvExp = v.findViewById(R.id.rcv_exp);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rcvExp.setLayoutManager(layoutManager);
        RestaurantAsyncTask myAsyncTask = new RestaurantAsyncTask();
        myAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        TextView tvMore = v.findViewById(R.id.tv_more);
        tvMore.setOnClickListener(this);

        LinearLayout viewNearBy = v.findViewById(R.id.view_nearby);
        viewNearBy.setOnClickListener(this);

        LinearLayout viewHot = v.findViewById(R.id.view_hot);
        viewHot.setOnClickListener(this);

        LinearLayout viewDiscount = v.findViewById(R.id.view_discount);
        viewDiscount.setOnClickListener(this);

        ViewPager viewPager = v.findViewById(R.id.viewPager);
        BannerAdapter bannerAdapter = new BannerAdapter(Objects.requireNonNull(getContext()),1);
        viewPager.setAdapter(bannerAdapter);

        /*After setting the adapter use the timer */
        final Handler handler = new Handler();
        final Runnable Update = () -> {
            if (currentPage == 4) {
                currentPage = 0;
            }
            viewPager.setCurrentItem(currentPage++, true);
        };

        Timer timer = new Timer(); // This will create a new Thread
        //delay in milliseconds before task is to be executed
        long DELAY_MS = 500;
        // time in milliseconds between successive task executions.
        long PERIOD_MS = 3000;
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tv_more){
            Intent intent = new Intent(getContext(), RestaurantActivity.class);
            intent.putExtra("type", 0);
            startActivity(intent);
        }else if(v.getId() == R.id.view_nearby){
            Intent intent = new Intent(getContext(), RestaurantActivity.class);
            intent.putExtra("type", 2);
            startActivity(intent);
        }else if(v.getId() == R.id.view_hot){
            Intent intent = new Intent(getContext(), RestaurantActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
        }else if(v.getId() == R.id.view_discount){
            Intent intent = new Intent(getContext(), RestaurantActivity.class);
            intent.putExtra("type", 3);
            startActivity(intent);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class RestaurantAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Common.restaurantListCurrent = new ArrayList<>();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            RestaurantHorizontalAdapter adapter = new RestaurantHorizontalAdapter(getContext(), Common.restaurantListCurrent);
            rcvExp.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Common.restaurantListCurrent = Common.db.getRestaurant(Common.listResId, Common.myAddress);
            for (Restaurant restaurant : Common.restaurantListCurrent){
                for (Branch branch: restaurant.getBranchList()){
                    branch.setDistance(getDistanceBranch(branch));
                }
            }
            Common.menuList = UtilsBottomBar.getMenuList();
            return null;
        }

        private float getDistanceBranch(Branch branch){
            float[] distance = new float[1];
            Location.distanceBetween(branch.getLatitude(), branch.getLongitude(),
                    Common.myLocation.getLatitude(), Common.myLocation.getLongitude(), distance);
                Common.db.updateBranch(distance[0], branch.getId_db());
            return distance[0];
        }
    }
}
