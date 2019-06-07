package com.example.yummy.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yummy.Adapter.BannerAdapter;
import com.example.yummy.Adapter.NotificationAdapter;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationFragment extends Fragment {
    private int currentPage = 0;

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
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Collections.sort(Common.blogList, (obj1, obj2) -> {
            try {
                return dateFormat.parse(obj1.getTime()).compareTo(dateFormat.parse(obj2.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return -1;
        });

        Collections.reverse(Common.blogList);

        RecyclerView rcvNotify = v.findViewById(R.id.rcv_notify_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvNotify.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvNotify.getContext(), layoutManager.getOrientation());
        rcvNotify.addItemDecoration(dividerItemDecoration);
        NotificationAdapter adapter = new NotificationAdapter(getContext(), Common.blogList, false);
        rcvNotify.setAdapter(adapter);

        ViewPager viewPager = v.findViewById(R.id.viewPager);
        BannerAdapter bannerAdapter = new BannerAdapter(Objects.requireNonNull(getContext()),2);
        viewPager.setAdapter(bannerAdapter);

        /*After setting the adapter use the timer */
        final Handler handler = new Handler();
        final Runnable Update = () -> {
            if (currentPage == 4) {
                currentPage = 0;
            }
            viewPager.setCurrentItem(currentPage++, true);
        };

        Timer timer = new Timer(); // This will create a new Thread
        //delay in milliseconds before task is to be executed
        long DELAY_MS = 500;
        // time in milliseconds between successive task executions.
        long PERIOD_MS = 3000;
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }
}
