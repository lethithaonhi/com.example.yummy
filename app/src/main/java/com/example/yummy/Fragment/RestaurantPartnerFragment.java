package com.example.yummy.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yummy.R;

public class RestaurantPartnerFragment extends Fragment {

    public static RestaurantPartnerFragment newInstance() {
        Bundle args = new Bundle();
        RestaurantPartnerFragment fragment = new RestaurantPartnerFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_restaurant_home_partner, container, false);
        return rootView;

    }
}
