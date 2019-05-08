package com.example.yummy.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yummy.Adapter.BannerAdapter;
import com.example.yummy.R;

import java.util.Objects;

public class NotificationFragment extends Fragment {

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification, container, false);
        initView(v);
        return v;
    }

    private void initView(View v){
        RecyclerView rcvNotify = v.findViewById(R.id.rcv_notify_list);
        rcvNotify.setLayoutManager(new LinearLayoutManager(getContext()));

        ViewPager viewPager = v.findViewById(R.id.viewPager);
        BannerAdapter bannerAdapter = new BannerAdapter(Objects.requireNonNull(getContext()),2);
        viewPager.setAdapter(bannerAdapter);
    }
}
