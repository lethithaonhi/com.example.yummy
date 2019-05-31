package com.example.yummy.Adapter;

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

        if (order.getIsStatus() == 1 || order.getIsStatus() > 1){
            holder.tvConfirm.setTextColor(context.getResources().getColor(R.color.red));
            holder.imConfirm.setImageResource(R.drawable.bg_circle_green);
            holder.vConfirm.setBackgroundResource(R.color.bg_green);
            holder.tvStatus.setText(R.string.on_confirm);
        }

        if(order.getIsStatus() == 2 || order.getIsStatus() > 2){
            holder.tvRoute.setTextColor(context.getResources().getColor(R.color.red));
            holder.imRoute.setImageResource(R.drawable.bg_circle_green);
            holder.vRoute.setBackgroundResource(R.color.bg_green);
            holder.tvStatus.setText(R.string.on_dispatch);
        }

        if(order.getIsStatus() == 3 || order.getIsStatus() > 3){
            holder.tvComplete.setTextColor(context.getResources().getColor(R.color.red));
            holder.imComplete.setImageResource(R.drawable.bg_circle_green);
            holder.vComplete.setBackgroundResource(R.color.bg_green);
            holder.tvStatus.setText(R.string.on_complete);
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size():0;
    }

    class OnGoingCusHolder extends RecyclerView.ViewHolder {
        TextView tvStatus, tvSent, tvConfirm, tvRoute, tvComplete, tvNameRes, tvDate;
        ImageView imSent, imConfirm, imRoute, imComplete;
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
        }
    }
}
