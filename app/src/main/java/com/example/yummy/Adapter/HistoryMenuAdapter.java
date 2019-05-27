package com.example.yummy.Adapter;

import android.app.Dialog;
import android.content.Context;
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
import com.squareup.picasso.Picasso;

import java.util.List;

public class HistoryMenuAdapter extends RecyclerView.Adapter<HistoryMenuAdapter.HistoryMenuHolder> {
    private Context context;
    private List<Order> data;
    private boolean isPartner;

    public HistoryMenuAdapter(Context context, List<Order> data, boolean isPartner){
        this.context = context;
        this.data = data;
        this.isPartner = isPartner;
    }

    @NonNull
    @Override
    public HistoryMenuAdapter.HistoryMenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
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
    public void onBindViewHolder(@NonNull HistoryMenuAdapter.HistoryMenuHolder holder, int i) {
        Order order = data.get(i);

        holder.tvNameRes.setText(order.getName_res());
        holder.tvAddress.setText(order.getAddress());
        holder.tvTotal.setText(order.getTotal()+"VND");
        holder.tvDate.setText(order.getDate());
        Picasso.get().load(order.getAvatar()).into(holder.imageView);

        if(isPartner && order.getIsStatus() != 3 && order.getIsStatus() != 4){
            Animation animShake = AnimationUtils.loadAnimation(context, R.anim.shake);
            holder.imAlarm.startAnimation(animShake);
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
            count += order.getMenuList().get(menu);
        }

        holder.tvCount.setText(count+" " + context.getResources().getString(R.string.item) +" - ");

        holder.viewContainer.setOnClickListener(v->{
            showDiaLogMenu(order);
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

    private void showDiaLogMenu(Order order){
        Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setTitle("");
        dialog.setContentView(R.layout.view_detail_order);
        dialog.setCancelable(true);
        dialog.show();
        initView(dialog, order);
    }

    private void initView(Dialog dialog, Order order){
        ImageView imback = dialog.findViewById(R.id.im_back);
        TextView tvNameRes = dialog.findViewById(R.id.tv_nameRes);
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
        ImageView imStatus = dialog.findViewById(R.id.im_status);

        if(order.getIsStatus() == 4){
            imStatus.setImageResource(R.drawable.cancel);
        }

        RecyclerView rcvMenu = dialog.findViewById(R.id.rcv_menu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rcvMenu.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvMenu.getContext(), layoutManager.getOrientation());
        rcvMenu.addItemDecoration(dividerItemDecoration);
        rcvMenu.setNestedScrollingEnabled(false);

        MenuOrderAdapter adapter = new MenuOrderAdapter(context, order.getMenuList());
        rcvMenu.setAdapter(adapter);

        imback.setOnClickListener(v->dialog.dismiss());
        Picasso.get().load(order.getAvatar()).into(imageView);
        tvNameRes.setText(order.getName_res() +" - "+order.getAddress_res());
        tvStatus.setText(order.getIsStatus()==3 ? R.string.complete:R.string.cancel);
        tvID.setText(order.getId());
        if(order.getDiscount() > 400) {
            vDiscount.setVisibility(View.VISIBLE);
            tvDiscount.setText(order.getDiscount() + "VND");
        }
        tvDistance.setText(String.format("%.02f",order.getDistance()) + " km");
        tvShip.setText(order.getFeeShip()+"VND");
        tvTotal.setText(order.getTotal()+"VND");
        tvName.setText(order.getName());
        tvPhone.setText(order.getPhone());
        tvAddress.setText(order.getAddress());
        tvCount.setText(order.getCount()+" "+context.getResources().getString(R.string.item));
    }
}
