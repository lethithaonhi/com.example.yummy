package com.example.yummy.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yummy.R;

public class InfoRestaurantDetailFragment extends Fragment {

    public static InfoRestaurantDetailFragment getInstance() {
        InfoRestaurantDetailFragment fragment = new InfoRestaurantDetailFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_retaurantdetial_info, container, false);
        return v;
    }

}
