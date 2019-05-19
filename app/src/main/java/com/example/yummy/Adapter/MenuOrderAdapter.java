package com.example.yummy.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yummy.Model.Menu;
import com.example.yummy.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuOrderAdapter extends RecyclerView.Adapter<MenuOrderAdapter.MenuOrderHolde> {
    private Context context;
    private HashMap<Menu, Integer> data;
    private List<Menu> dataMenu;

    public MenuOrderAdapter(Context context, HashMap<Menu,Integer> data){
        this.context = context;
        this.data = data;
        dataMenu = new ArrayList<>(data.keySet());
    }

    @NonNull
    @Override
    public MenuOrderHolde onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_menu_order, parent, false);
        return new MenuOrderHolde(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuOrderHolde holde, int i) {
        int count = 0;
        Menu menu = dataMenu.get(i);
        if(data.get(menu) != null) {
            count = data.get(menu);
        }

        holde.tvCount.setText("x"+count);
        holde.tvName.setText(menu.getName());
        holde.tvPrice.setText(menu.getPrices()+"VND");
    }

    @Override
    public int getItemCount() {
        return data!= null ? data.size():0;
    }

    class MenuOrderHolde extends RecyclerView.ViewHolder {
        TextView tvCount, tvName, tvPrice;

        MenuOrderHolde(@NonNull View itemView) {
            super(itemView);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
