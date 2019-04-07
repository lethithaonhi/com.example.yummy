package com.example.yummy.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yummy.R;

public class NotificationFragment extends Fragment {

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification, container, false);
        initView(v);
        return v;
    }

    private void initView(View v){
        RecyclerView rcvNotify = v.findViewById(R.id.rcv_notify_list);
        rcvNotify.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
