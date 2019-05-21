package com.example.yummy.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yummy.Model.Menu;
import com.example.yummy.Model.Order;
import com.example.yummy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HistoryMenuAdapter extends RecyclerView.Adapter<HistoryMenuAdapter.HistoryMenuHolder> {
    private Context context;
    private List<Order> data;

    public HistoryMenuAdapter(Context context, List<Order> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public HistoryMenuAdapter.HistoryMenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_history_cus, parent, false);
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


        holder.tvStatus.setText(order.getIsStatus() == 3 ? R.string.complete : R.string.cancel);

        int count =0;
        for(Menu menu : order.getMenuList().keySet()){
            count += order.getMenuList().get(menu);
        }

        holder.tvCount.setText(count+" " + context.getResources().getString(R.string.item) +" - ");
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size():0;
    }

    class HistoryMenuHolder extends RecyclerView.ViewHolder {
        TextView tvNameRes, tvAddress, tvTotal, tvCount, tvDate, tvStatus;
        ImageView imageView;
        HistoryMenuHolder(@NonNull View itemView) {
            super(itemView);

            tvNameRes = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvTotal = itemView.findViewById(R.id.tv_total);
            tvCount = itemView.findViewById(R.id.tv_count);
            imageView = itemView.findViewById(R.id.img_res);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}
