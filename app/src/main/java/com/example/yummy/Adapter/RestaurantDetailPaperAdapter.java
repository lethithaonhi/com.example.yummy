package com.example.yummy.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.yummy.Fragment.DeliveryRestaurantDetailFragment;
import com.example.yummy.Fragment.ImagesRestaurantDetailFragment;
import com.example.yummy.Fragment.InfoRestaurantDetailFragment;
import com.example.yummy.Fragment.ReviewsRestaurantDetailFragment;

public class RestaurantDetailPaperAdapter extends FragmentStatePagerAdapter {

    private String title[] = {"Delivery", "Info", "Reviews", "Images"};

    public RestaurantDetailPaperAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return DeliveryRestaurantDetailFragment.getInstance();
        }else if( position == 2) {
            return InfoRestaurantDetailFragment.getInstance();
        }else if(position == 3){
            return ReviewsRestaurantDetailFragment.getInstance();
        }else {
            return ImagesRestaurantDetailFragment.getInstance();
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
