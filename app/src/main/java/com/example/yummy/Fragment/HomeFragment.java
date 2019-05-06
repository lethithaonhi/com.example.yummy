package com.example.yummy.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yummy.Activity.RestaurantActivity;
import com.example.yummy.Adapter.RestaurantHorizontalAdapter;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.UtilsBottomBar;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener{
    private RecyclerView rcvExp;

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
