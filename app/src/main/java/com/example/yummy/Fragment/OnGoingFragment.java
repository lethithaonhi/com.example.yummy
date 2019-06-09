package com.example.yummy.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import android.widget.Toast;

import com.example.yummy.Adapter.OnGoingCusAdater;
import com.example.yummy.Adapter.RestaurantHorizontalAdapter;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Menu;
import com.example.yummy.Model.Order;
import com.example.yummy.Model.Restaurant;
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
    private RecyclerView rcvOrderList;
    private LinearLayout viewNoOrder;
    private MediaPlayer endPlayer;

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
        viewNoOrder = v.findViewById(R.id.view_no_order);
        rcvOrderList = v.findViewById(R.id.rcv_order_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvOrderList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvOrderList.getContext(), layoutManager.getOrientation());
        rcvOrderList.addItemDecoration(dividerItemDecoration);
        if(Common.orderListCurrent != null && Common.orderListCurrent.size() > 0){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            Collections.sort(Common.orderListCurrent, (obj1, obj2) -> {
                try {
                    return dateFormat.parse(obj1.getDate()+ " " + obj1.getTime()).compareTo(dateFormat.parse(obj1.getDate()+" "+obj2.getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return -1;
            });

            Collections.reverse(Common.orderListCurrent);
        }

        OnGoingAsyncTask myAsyncTask = new OnGoingAsyncTask();
        myAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getData(){
        data = new ArrayList<>();
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
            return -1;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Common.accountCurrent != null && data.size() > 0) {
            if(Common.orderListCurrent.size() == 0) {
                UtilsBottomBar.getOrderCurrent();
                getData();
            }
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
                                        int pos = adapter.getPos(order);
                                        data.remove(pos);
                                        data.add(pos, order);
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
                                        initMessOrder();
                                        if(status == 3){
                                            Toast.makeText(getContext(), "Success!!", Toast.LENGTH_SHORT).show();
                                        }
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

    private void initMessOrder() {
        endPlayer = MediaPlayer.create(getContext(), R.raw.newmess);
        if (endPlayer != null) {
            endPlayer.setVolume(1.0f, 1.0f);
            endPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            endPlayer.start();
            endPlayer.setOnCompletionListener((mp) -> {
                endPlayer.stop();
                endPlayer.release();
                endPlayer = null;
            });
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

    @SuppressLint("StaticFieldLeak")
    private class OnGoingAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            data = new ArrayList<>();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(data.size() > 0) {
                adapter = new OnGoingCusAdater(getContext(), data);
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
