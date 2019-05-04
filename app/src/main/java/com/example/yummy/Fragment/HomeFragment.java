package com.example.yummy.Fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yummy.Adapter.RestaurantHorizontalAdapter;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView rcvExp;
    private List<Restaurant> restaurantList;

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
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @SuppressLint("StaticFieldLeak")
    private class RestaurantAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            restaurantList = new ArrayList<>();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            RestaurantHorizontalAdapter adapter = new RestaurantHorizontalAdapter(getContext(), restaurantList);
            rcvExp.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            restaurantList = Common.db.getRestaurant(Common.listResId, Common.myAddress);
            return null;
        }
    }

}
