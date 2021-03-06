package com.example.yummy.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yummy.Model.Order;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OnGoingCusAdater extends RecyclerView.Adapter<OnGoingCusAdater.OnGoingCusHolder> {
    private Context context;
    private List<Order> data;

    public OnGoingCusAdater(Context context, List<Order> data){
        this.context = context;
        this.data = data;
    }

    public int getPos (Order order){
        return data.indexOf(order) != -1 ? data.indexOf(order) : 0;
    }

    @NonNull
    @Override
    public OnGoingCusHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_history_on, parent, false);
        return new OnGoingCusHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnGoingCusHolder holder, int i) {
        Order order = data.get(i);

        holder.tvNameRes.setText(order.getName_res());
        holder.tvDate.setText(order.getTime() + " - " + order.getDate());
        holder.btnCancel.setVisibility(order.getIsStatus() !=0 ? View.GONE:View.VISIBLE);

        holder.btnCancel.setOnClickListener(v-> showMessChangeStatus(order, holder));

        if(order.getIsStatus() == 4){
           setCancel(holder);
        }else {
            if (order.getIsStatus() == 1 || order.getIsStatus() > 1) {
                holder.tvConfirm.setTextColor(context.getResources().getColor(R.color.red));
                holder.imConfirm.setImageResource(R.drawable.bg_circle_green);
                holder.vConfirm.setBackgroundResource(R.color.bg_green);
                holder.tvStatus.setText(R.string.on_confirm);
            }

            if (order.getIsStatus() == 2 || order.getIsStatus() > 2) {
                holder.tvRoute.setTextColor(context.getResources().getColor(R.color.red));
                holder.imRoute.setImageResource(R.drawable.bg_circle_green);
                holder.vRoute.setBackgroundResource(R.color.bg_green);
                holder.tvStatus.setText(R.string.on_dispatch);
            }

            if (order.getIsStatus() == 3 || order.getIsStatus() > 3) {
                holder.tvComplete.setTextColor(context.getResources().getColor(R.color.red));
                holder.imComplete.setImageResource(R.drawable.bg_circle_green);
                holder.vComplete.setBackgroundResource(R.color.bg_green);
                holder.tvStatus.setText(R.string.on_complete);
            }
        }
    }

    private void setCancel(OnGoingCusHolder holder){
        holder.tvConfirm.setTextColor(context.getResources().getColor(R.color.red));
        holder.tvRoute.setTextColor(context.getResources().getColor(R.color.red));
        holder.tvComplete.setTextColor(context.getResources().getColor(R.color.red));
        holder.imSent.setImageResource(R.drawable.bg_circle_red);
        holder.imConfirm.setImageResource(R.drawable.bg_circle_red);
        holder.vConfirm.setBackgroundResource(R.color.red);
        holder.imRoute.setImageResource(R.drawable.bg_circle_red);
        holder.vRoute.setBackgroundResource(R.color.red);
        holder.imComplete.setImageResource(R.drawable.bg_circle_red);
        holder.vComplete.setBackgroundResource(R.color.red);
        holder.tvStatus.setText(R.string.on_cancel);
        holder.vClose.setVisibility(View.VISIBLE);
        holder.tvFinish.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size():0;
    }

    class OnGoingCusHolder extends RecyclerView.ViewHolder {
        TextView tvStatus, tvSent, tvConfirm, tvRoute, tvComplete, tvNameRes, tvDate, tvFinish;
        ImageView imSent, imConfirm, imRoute, imComplete, btnCancel, vClose;
        View vConfirm, vRoute, vComplete;

        OnGoingCusHolder(@NonNull View itemView) {
            super(itemView);

            tvStatus = itemView.findViewById(R.id.tv_status);
            tvSent = itemView.findViewById(R.id.tv_sent);
            tvConfirm = itemView.findViewById(R.id.tv_confirm);
            tvRoute = itemView.findViewById(R.id.tv_route);
            tvComplete = itemView.findViewById(R.id.tv_complete);
            tvNameRes = itemView.findViewById(R.id.tv_nameRes);
            imSent = itemView.findViewById(R.id.im_sent);
            imConfirm = itemView.findViewById(R.id.im_confirm);
            imRoute = itemView.findViewById(R.id.im_route);
            imComplete = itemView.findViewById(R.id.im_complete);
            vConfirm = itemView.findViewById(R.id.v_confirm);
            vRoute = itemView.findViewById(R.id.v_route);
            vComplete = itemView.findViewById(R.id.v_complete);
            tvDate = itemView.findViewById(R.id.tv_date);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
            vClose = itemView.findViewById(R.id.close);
            tvFinish = itemView.findViewById(R.id.tv_finish);
        }
    }

    private void showMessChangeStatus(Order order, OnGoingCusHolder holder){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("");
        builder.setMessage(R.string.mess_cancel_status);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.sure, (dialogInterface, i) ->{
            setCancel(holder);
            DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
            mData.child(Node.Order).child(order.getId_res()).child(order.getId()).child(Node.isStatus).setValue(4);
        });
        builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
