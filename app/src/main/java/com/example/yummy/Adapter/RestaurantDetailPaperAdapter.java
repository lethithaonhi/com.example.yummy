package com.example.yummy.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.yummy.Fragment.DeliveryRestaurantDetailFragment;
import com.example.yummy.Fragment.InfoRestaurantDetailFragment;
import com.example.yummy.Fragment.ReviewsRestaurantDetailFragment;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;

public class RestaurantDetailPaperAdapter extends FragmentStatePagerAdapter {
    private Restaurant restaurant;
    private Branch branch;
    private String title[] = {"Delivery", "Info", "Reviews"};

    public RestaurantDetailPaperAdapter(FragmentManager fm, Restaurant restaurant, Branch branch) {
        super(fm);
        this.restaurant = restaurant;
        this.branch = branch;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return DeliveryRestaurantDetailFragment.getInstance(restaurant, branch);
        }else if( position == 1) {
            return InfoRestaurantDetailFragment.getInstance(restaurant, branch);
        }else{
            return ReviewsRestaurantDetailFragment.getInstance();
        }
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
