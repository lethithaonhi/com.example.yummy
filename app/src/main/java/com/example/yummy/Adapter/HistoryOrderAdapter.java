package com.example.yummy.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yummy.Model.Menu;
import com.example.yummy.Model.Order;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class HistoryOrderAdapter extends RecyclerView.Adapter<HistoryOrderAdapter.HistoryMenuHolder> {
    private Context context;
    private List<Order> data;
    private boolean isPartner;
    private MediaPlayer endPlayer;
    private LinearLayout btnCancelOrder;

    public HistoryOrderAdapter(Context context, List<Order> data, boolean isPartner){
        this.context = context;
        this.data = data;
        this.isPartner = isPartner;
    }

    @NonNull
    @Override
    public HistoryOrderAdapter.HistoryMenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if(!isPartner) {
            view = inflater.inflate(R.layout.item_history_cus, parent, false);
        }else {
            view = inflater.inflate(R.layout.item_order_part, parent, false);
        }
        return new HistoryMenuHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryOrderAdapter.HistoryMenuHolder holder, int i) {
        Order order = data.get(i);

        holder.tvNameRes.setText(order.getName_res());
        holder.tvAddress.setText(order.getAddress());
        holder.tvTotal.setText(UtilsBottomBar.convertStringToMoney((int) order.getTotal()));
        holder.tvDate.setText(order.getDate());
        if(!order.getAvatar().isEmpty())
        Picasso.get().load(order.getAvatar()).into(holder.imageView);

        if(isPartner && order.getIsStatus() != 3 && order.getIsStatus() != 4){
            holder.imAlarm.setVisibility(View.VISIBLE);
            Animation animShake = AnimationUtils.loadAnimation(context, R.anim.shake);
            holder.imAlarm.startAnimation(animShake);
        }else {
            holder.imAlarm.setVisibility(View.GONE);
        }

        String status;
        if(order.getIsStatus() == 0){
            status = context.getResources().getString(R.string.sent);
        }else if (order.getIsStatus() == 1){
            status = context.getResources().getString(R.string.confirmed);
        }else if (order.getIsStatus() == 2){
            status = context.getResources().getString(R.string.delivered);
        }else if (order.getIsStatus() == 3){
            status = context.getResources().getString(R.string.complete);
            if(!isPartner)
                holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_square_green));
            else
                holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_green));
        }else {
            status = context.getResources().getString(R.string.cancel);
            if(!isPartner)
                holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_square_red));
            else
                holder.tvStatus.setBackground(context.getResources().getDrawable(R.drawable.bg_circle_red));
        }

        holder.tvStatus.setText(status);

        int count =0;
        for(Menu menu : order.getMenuList().keySet()){
            if(order.getMenuList().get(menu) != null)
                count += order.getMenuList().get(menu);
        }

        holder.tvCount.setText(count+" " + context.getResources().getString(R.string.item) +" - ");

        holder.viewContainer.setOnClickListener(v->{
            if(!isPartner)
                showOrderCus(order);
            else {
                showOrderPart(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size():0;
    }

    class HistoryMenuHolder extends RecyclerView.ViewHolder {
        TextView tvNameRes, tvAddress, tvTotal, tvCount, tvDate, tvStatus;
        ImageView imageView, imAlarm;
        LinearLayout viewContainer;
        HistoryMenuHolder(@NonNull View itemView) {
            super(itemView);

            tvNameRes = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvTotal = itemView.findViewById(R.id.tv_total);
            tvCount = itemView.findViewById(R.id.tv_count);
            imageView = itemView.findViewById(R.id.img_res);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
            viewContainer = itemView.findViewById(R.id.view_container);
            imAlarm = itemView.findViewById(R.id.im_alarm);
        }
    }

    private void showOrderCus(Order order){
        Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setTitle("");
        dialog.setContentView(R.layout.view_detail_order);
        dialog.setCancelable(true);
        dialog.show();
        ImageView imback = dialog.findViewById(R.id.im_back);
        TextView tvNameRes = dialog.findViewById(R.id.tv_nameRes);
        tvNameRes.setText(order.getName_res() +" - "+order.getAddress_res());
        imback.setOnClickListener(v->dialog.dismiss());
        initView(dialog, order);
    }

    @SuppressLint("SetTextI18n")
    private void initView(Dialog dialog, Order order){
        TextView tvStatus = dialog.findViewById(R.id.tv_status);
        TextView tvID = dialog.findViewById(R.id.tv_orderid);
        TextView tvCount = dialog.findViewById(R.id.tv_count);
        TextView tvDistance = dialog.findViewById(R.id.tv_distance);
        TextView tvShip = dialog.findViewById(R.id.tv_ship);
        TextView tvDiscount = dialog.findViewById(R.id.tv_discount);
        LinearLayout vDiscount = dialog.findViewById(R.id.view_discount);
        TextView tvTotal = dialog.findViewById(R.id.tv_total);
        TextView tvName = dialog.findViewById(R.id.tv_name);
        TextView tvPhone = dialog.findViewById(R.id.tv_phone);
        TextView tvAddress = dialog.findViewById(R.id.tv_address);
        ImageView imageView = dialog.findViewById(R.id.im_avatar);
        imStatus = dialog.findViewById(R.id.im_status);
        TextView tvDate = dialog.findViewById(R.id.tv_date);

        TextView tvAddressRes = dialog.findViewById(R.id.tv_address_res);
        tvAddressRes.setText(order.getAddress_res());

        TextView tvDescribe = dialog.findViewById(R.id.tv_describe);
        tvDescribe.setText(order.getNode());

        if(order.getIsStatus() == 4){
            imStatus.setImageResource(R.drawable.cancel);
        }else if (order.getIsStatus() == 3){
            imStatus.setImageResource(R.drawable.paid);
        }else {
            imStatus.setVisibility(View.GONE);
        }

        RecyclerView rcvMenu = dialog.findViewById(R.id.rcv_menu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rcvMenu.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvMenu.getContext(), layoutManager.getOrientation());
        rcvMenu.addItemDecoration(dividerItemDecoration);
        rcvMenu.setNestedScrollingEnabled(false);

        MenuOrderAdapter adapter = new MenuOrderAdapter(context, order.getMenuList());
        rcvMenu.setAdapter(adapter);

        if(!order.getAvatar().isEmpty())
        Picasso.get().load(order.getAvatar()).into(imageView);
        tvStatus.setText(order.getIsStatus()==3 ? R.string.complete:R.string.cancel);
        tvID.setText(order.getId());
        if(order.getDiscount() > 400) {
            vDiscount.setVisibility(View.VISIBLE);
            tvDiscount.setText("-"+ UtilsBottomBar.convertStringToMoney(order.getDiscount()));
        }
        tvDistance.setText(String.format("%.02f",order.getDistance()) + " km");
        tvShip.setText(UtilsBottomBar.convertStringToMoney(order.getFeeShip()));
        tvTotal.setText(UtilsBottomBar.convertStringToMoney((int) order.getTotal()));
        tvName.setText(order.getName());
        tvPhone.setText(order.getPhone());
        tvAddress.setText(order.getAddress());
        tvCount.setText(order.getCount()+" "+context.getResources().getString(R.string.item));
        tvDate.setText(order.getTime() + " " + order.getDate());
    }

    private TextView tvStatus, tvConfirm, tvRoute, tvComplete;
    private ImageView imConfirm, imRoute, imComplete;
    private View vConfirm, vRoute, vComplete;
    private PulsatorLayout mPulsator;
    private ImageView imStatus;

    private void showOrderPart(Order order){
        Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setTitle("");
        dialog.setContentView(R.layout.activity_order_detail_partner);
        dialog.setCancelable(true);
        dialog.show();
        initView(dialog, order);
        mPulsator = dialog.findViewById(R.id.pulsator);
        if(order.getIsStatus() < 3)
            mPulsator.start();

        ImageView btnChange = dialog.findViewById(R.id.btn_change);
        btnChange.setOnClickListener(v->{
            if(order.getIsStatus() < 3){
                showMessChangeStatus(order, order.getIsStatus());
            }
        });

        TextView tvAddressRes = dialog.findViewById(R.id.tv_address_res);
        tvAddressRes.setText(order.getAddress_res());

        TextView tvDescribe = dialog.findViewById(R.id.tv_describe);
        tvDescribe.setText(order.getNode());

        tvStatus = dialog.findViewById(R.id.tv_status);
        tvConfirm = dialog.findViewById(R.id.tv_confirm);
        tvRoute = dialog.findViewById(R.id.tv_route);
        tvComplete = dialog.findViewById(R.id.tv_complete);
        imConfirm = dialog.findViewById(R.id.im_confirm);
        imRoute = dialog.findViewById(R.id.im_route);
        imComplete = dialog.findViewById(R.id.im_complete);
        vConfirm = dialog.findViewById(R.id.v_confirm);
        vRoute = dialog.findViewById(R.id.v_route);
        vComplete = dialog.findViewById(R.id.v_complete);
        btnCancelOrder = dialog.findViewById(R.id.btn_cancel);
        btnCancelOrder.setOnClickListener(v-> {
            if(order.getIsStatus() < 3)
                showMessChangeStatus(order, 3);
        });
        setStatus(order.getIsStatus());
    }

    private void setStatus(int status){
        if (status == 1 || status > 1){
            tvConfirm.setTextColor(context.getResources().getColor(R.color.red));
            imConfirm.setImageResource(R.drawable.bg_circle_green);
            vConfirm.setBackgroundResource(R.color.bg_green);
            tvStatus.setText(R.string.on_confirm);
        }

        if(status == 2 || status > 2){
            tvRoute.setTextColor(context.getResources().getColor(R.color.red));
            imRoute.setImageResource(R.drawable.bg_circle_green);
            vRoute.setBackgroundResource(R.color.bg_green);
            tvStatus.setText(R.string.on_dispatch);
        }

        if(status == 3 || status > 3){
            tvComplete.setTextColor(context.getResources().getColor(R.color.red));
            imComplete.setImageResource(R.drawable.bg_circle_green);
            vComplete.setBackgroundResource(R.color.bg_green);
            tvStatus.setText(R.string.on_complete);
        }
    }

    private void showMessChangeStatus(Order order, int status){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("");
        builder.setMessage(status != 3 ? R.string.mess_change_status : R.string.mess_cancel_status);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.sure, (dialogInterface, i) ->{
            order.setIsStatus(status+1);
            DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
            mData.child(Node.Order).child(Common.accountCurrent.getPartner().getBoss()).child(order.getId()).child(Node.isStatus).setValue(status+1);

            if(status >= 2){
                mPulsator.stop();
                initEndOrder();
                imStatus.setVisibility(View.VISIBLE);
                if(status == 3){
                    imStatus.setImageResource(R.drawable.cancel);
                    btnCancelOrder.setVisibility(View.GONE);
                }
            }
            setStatus(status+1);
        });
        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void initEndOrder() {
        endPlayer = MediaPlayer.create(context, R.raw.endorder);
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
}
