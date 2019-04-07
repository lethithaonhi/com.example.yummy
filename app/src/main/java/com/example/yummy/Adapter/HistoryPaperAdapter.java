package com.example.yummy.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.example.yummy.Fragment.HistoryFragment;
import com.example.yummy.Fragment.OnGoingFragment;

public class HistoryPaperAdapter extends FragmentStatePagerAdapter {

    private String title[] = {"History", "Ongoing"};

    public HistoryPaperAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return OnGoingFragment.getInstance();
        else
            return HistoryFragment.getInstance();
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
