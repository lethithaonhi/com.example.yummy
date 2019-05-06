package com.example.yummy.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yummy.Adapter.ImgRestaurantDetailAdapter;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;

public class ReviewsRestaurantDetailFragment extends Fragment {
    private Restaurant restaurant;
    private Branch branch;

    public static ReviewsRestaurantDetailFragment getInstance(Restaurant restaurant, Branch branch) {
        ReviewsRestaurantDetailFragment fragment = new ReviewsRestaurantDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("restaurant", restaurant);
        bundle.putParcelable("branch", branch);
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_restaurantdetail_review, container, false);
        if (getArguments() != null) {
            restaurant = getArguments().getParcelable("restaurant");
            branch = getArguments().getParcelable("branch");
        }
        initView(v);
        return v;
    }

    private void initView(View v){
        RecyclerView rcvImRes = v.findViewById(R.id.rcv_image_res);
        rcvImRes.setLayoutManager(new GridLayoutManager(getContext(), 3));
        ImgRestaurantDetailAdapter adapter = new ImgRestaurantDetailAdapter(getContext(), restaurant.getImgList(), restaurant, 0);
        rcvImRes.setAdapter(adapter);

    }
}
