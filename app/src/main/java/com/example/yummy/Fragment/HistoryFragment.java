package com.example.yummy.Fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.yummy.Adapter.HistoryOrderAdapter;
import com.example.yummy.Model.Order;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryFragment extends Fragment {
    private List<Order> data;
    private RecyclerView rcvOrderList;
    private LinearLayout viewNoOrder;

    public static HistoryFragment getInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        initView(v);
        return v;
    }

    private void initView(View v){
        viewNoOrder = v.findViewById(R.id.view_no_order);
        rcvOrderList = v.findViewById(R.id.rcv_order_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvOrderList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvOrderList.getContext(), layoutManager.getOrientation());
        rcvOrderList.addItemDecoration(dividerItemDecoration);

        HistoryAsyncTask myAsyncTask = new HistoryAsyncTask();
        myAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void getData(){
        data = new ArrayList<>();
        for (Order order : Common.orderListCurrent){
            if(order.getIsStatus() == 4 || order.getIsStatus() == 3){
                data.add(order);
            }
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
        Collections.sort(data, (obj1, obj2) -> {
            try {
                return dateFormat.parse(obj1.getTime()+" "+obj1.getDate()).compareTo(dateFormat.parse(obj2.getTime() +" "+obj2.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return -1;
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class HistoryAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            data = new ArrayList<>();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(data.size() > 0) {
                HistoryOrderAdapter adapter = new HistoryOrderAdapter(getContext(), data, false);
                rcvOrderList.setAdapter(adapter);
                viewNoOrder.setVisibility(View.GONE);
                rcvOrderList.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(Common.accountCurrent != null){
                getData();
            }
            return null;
        }
    }
}
