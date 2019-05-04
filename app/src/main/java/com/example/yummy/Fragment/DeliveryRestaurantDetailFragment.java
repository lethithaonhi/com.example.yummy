package com.example.yummy.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yummy.R;

public class DeliveryRestaurantDetailFragment extends Fragment {

    public static DeliveryRestaurantDetailFragment getInstance() {
        DeliveryRestaurantDetailFragment fragment = new DeliveryRestaurantDetailFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_retaurantdetail_delivery, container, false);
        return v;
    }
}
