package com.example.yummy.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.yummy.Adapter.HistoryMenuAdapter;
import com.example.yummy.Model.Order;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private List<Order> data;

    public static HistoryFragment getInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        data = new ArrayList<>();
        getData();
        initView(v);
        return v;
    }

    private void initView(View v){
        LinearLayout viewNoOrder = v.findViewById(R.id.view_no_order);
        RecyclerView rcvOrderLidt = v.findViewById(R.id.rcv_order_list);
        rcvOrderLidt.setLayoutManager(new LinearLayoutManager(getContext()));

        if(Common.accountCurrent != null && data.size()>0){
            viewNoOrder.setVisibility(View.GONE);
            HistoryMenuAdapter adapter = new HistoryMenuAdapter(getContext(), data);
            rcvOrderLidt.setAdapter(adapter);
        }
    }

    private void getData(){
        for (Order order : Common.orderListCurrent){
            if(order.getIsStatus() == 4 || order.getIsStatus() == 5){
                data.add(order);
            }
        }
    }
}
