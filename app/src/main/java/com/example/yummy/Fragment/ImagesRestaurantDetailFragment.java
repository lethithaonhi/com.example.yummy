package com.example.yummy.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yummy.R;

public class ImagesRestaurantDetailFragment extends Fragment {

    public static ImagesRestaurantDetailFragment getInstance() {
        ImagesRestaurantDetailFragment fragment = new ImagesRestaurantDetailFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_restaurantdetail_image, container, false);
        return v;
    }
}
