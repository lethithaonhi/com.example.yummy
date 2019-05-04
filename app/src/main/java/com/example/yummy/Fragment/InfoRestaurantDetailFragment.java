package com.example.yummy.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;

public class InfoRestaurantDetailFragment extends Fragment {
    private Restaurant restaurant;
    private Branch branch;

    public static InfoRestaurantDetailFragment getInstance(Restaurant restaurant, Branch branch) {
        InfoRestaurantDetailFragment fragment = new InfoRestaurantDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("restaurant", restaurant);
        bundle.putParcelable("branch", branch);
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_retaurantdetial_info, container, false);

        if (getArguments() != null) {
            restaurant = getArguments().getParcelable("restaurant");
            branch = getArguments().getParcelable("branch");
        }
        return v;
    }

}
