package com.example.yummy.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import java.util.List;

public class RestaurantHorizontalAdapter extends RecyclerView.Adapter<RestaurantHorizontalAdapter.RestaurantHorizontalHolder> {
    private List<Restaurant> data;
    private Context context;

    public RestaurantHorizontalAdapter (Context context, List<Restaurant> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RestaurantHorizontalHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_login, parent, false);
        return new RestaurantHorizontalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantHorizontalHolder restaurantHorizontalHolder, int i) {
        Restaurant restaurant = data.get(i);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    class RestaurantHorizontalHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress;
        ImageView imgRes;
        RestaurantHorizontalHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            imgRes = itemView.findViewById(R.id.img_res);
        }
    }
}