package com.example.yummy.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yummy.R;

public class ReviewsRestaurantDetailFragment extends Fragment {

    public static ReviewsRestaurantDetailFragment getInstance() {
        ReviewsRestaurantDetailFragment fragment = new ReviewsRestaurantDetailFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_restaurantdetail_review, container, false);
        return v;
    }
}
