package com.example.yummy.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.Toast;

import com.example.yummy.Adapter.OnGoingCusAdater;
import com.example.yummy.Model.Menu;
import com.example.yummy.Model.Order;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OnGoingFragment extends Fragment {
    private List<Order> data;
    private Timer timer;
    private OnGoingCusAdater adapter;

    public static OnGoingFragment getInstance() {
        OnGoingFragment fragment = new OnGoingFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ongoing, container, false);

        data = new ArrayList<>();
        initView(v);
        return v;
    }

    private void initView(View v){
        LinearLayout viewNoOrder = v.findViewById(R.id.view_no_order);
        RecyclerView rcvOrderList = v.findViewById(R.id.rcv_order_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvOrderList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvOrderList.getContext(), layoutManager.getOrientation());
        rcvOrderList.addItemDecoration(dividerItemDecoration);

        if(Common.accountCurrent != null){
            getData();
            viewNoOrder.setVisibility(View.GONE);
            rcvOrderList.setVisibility(View.VISIBLE);
            adapter = new OnGoingCusAdater(getContext(), data);
            rcvOrderList.setAdapter(adapter);
        }
    }

    private void getData(){
        for (Order order : Common.orderListCurrent){
            if(order.getIsStatus() != 4 && order.getIsStatus() != 3){
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
            return 1;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Common.accountCurrent != null && data.size() > 0) {
            UtilsBottomBar.getOrderCurrent();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            try {
                timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        for (int i = 0; i < data.size(); i++) {
                            Order order = data.get(i);
                            int finalI = i;
                            mDatabase.child(Node.Order).child(order.getId_res()).child(order.getId()).child(Node.isStatus).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int status = dataSnapshot.getValue(Integer.class);
                                    if (status != order.getIsStatus()) {
                                        order.setIsStatus(status);
                                        adapter.notifyDataSetChanged();
                                        if(status == 0){
                                            showMessStatusOrder(R.string.on_sent);
                                        }else if(status == 1){
                                            showMessStatusOrder(R.string.on_confirm);
                                        }else if(status == 2){
                                            showMessStatusOrder(R.string.on_dispatch);
                                        }else{
                                            showMessStatusOrder(R.string.on_complete);
                                        }
                                    }
                                    if(status == 3){
                                        Toast.makeText(getContext(), "Success!!", Toast.LENGTH_SHORT).show();
                                        Common.orderListCurrent.clear();
                                        UtilsBottomBar.getOrderCurrent();
                                    }

                                    if (finalI == Common.orderListCurrent.size() && order.getIsStatus() == 3) {
                                        timer.cancel();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                };
                timer.schedule(timerTask, 30000, 30000);
            } catch (IllegalStateException e) {
                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showMessStatusOrder(int id) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder
                .setMessage(getContext().getResources().getString(id))
                .setCancelable(false)
                .setNegativeButton(getContext().getResources().getString(R.string.okay), (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
