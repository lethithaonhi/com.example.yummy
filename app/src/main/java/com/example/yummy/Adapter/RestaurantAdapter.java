package com.example.yummy.Adapter;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantHolder> {
    private List<Restaurant> restaurantList;
    private Context context;
    private Branch branch;
    private float[] min = new float[1];

    public RestaurantAdapter(List<Restaurant> restaurantList, Context context){
        this.restaurantList = restaurantList;
        this.context = context;
    }

    @NonNull
    @Override
    public RestaurantAdapter.RestaurantHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.RestaurantHolder holder, int i) {
        Restaurant restaurant = restaurantList.get(i);
        branch = getBranch(restaurant);
        holder.tvName.setText(restaurant.getName());
        holder.tvMark.setText(restaurant.getMark()+"");
        holder.tvDistance.setText(min[0]+" km");
        Picasso.get().load(branch.getAvatar()).into(holder.imRes);
        if(restaurant.getFreeship() == 1){
            holder.viewFreeship.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return restaurantList != null ? restaurantList.size() : 0;
    }

    private Branch getBranch(Restaurant restaurant){
        branch = restaurant.getBranchList().get(0);
        float min = branch.getDistance();
        for(Branch branchNew : restaurant.getBranchList()){
            if(min > branchNew.getDistance()){
                min = branchNew.getDistance();
                branch = branchNew;
            }
        }
        return branch;
    }

    class RestaurantHolder extends RecyclerView.ViewHolder {
        ImageView imRes;
        TextView tvName, tvAddress, tvMark, tvDistance;
        LinearLayout viewFreeship;
        RestaurantHolder(@NonNull View itemView) {
            super(itemView);

            imRes = itemView.findViewById(R.id.im_restaurant);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvMark = itemView.findViewById(R.id.tv_mark);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            viewFreeship = itemView.findViewById(R.id.view_freeship);
        }
    }
}
