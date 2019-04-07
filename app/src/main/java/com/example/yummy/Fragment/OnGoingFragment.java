package com.example.yummy.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.yummy.R;

public class OnGoingFragment extends Fragment {

    public static OnGoingFragment getInstance() {
        OnGoingFragment fragment = new OnGoingFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ongoing, container, false);
        initView(v);
        return v;
    }

    private void initView(View v){
        LinearLayout viewNoOrder = v.findViewById(R.id.view_no_order);
        RecyclerView rcvOrderLidt = v.findViewById(R.id.rcv_order_list);
        rcvOrderLidt.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
