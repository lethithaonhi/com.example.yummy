package com.example.yummy.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

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

public class BlogActivity extends AppCompatActivity {
    private int currentPage = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notification);
        initView();
    }

    private void initView(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Collections.sort(Common.blogList, (obj1, obj2) -> {
            try {
                return dateFormat.parse(obj1.getTime()).compareTo(dateFormat.parse(obj2.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 1;
        });

        Collections.reverse(Common.blogList);

        RecyclerView rcvNotify = findViewById(R.id.rcv_notify_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvNotify.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvNotify.getContext(), layoutManager.getOrientation());
        rcvNotify.addItemDecoration(dividerItemDecoration);
        NotificationAdapter adapter = new NotificationAdapter(this, Common.blogList, false);
        rcvNotify.setAdapter(adapter);

        ViewPager viewPager = findViewById(R.id.viewPager);
        BannerAdapter bannerAdapter = new BannerAdapter(this,2);
        viewPager.setAdapter(bannerAdapter);

        TextView tvHeader = findViewById(R.id.tv_header);
        tvHeader.setText(R.string.blog);

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
