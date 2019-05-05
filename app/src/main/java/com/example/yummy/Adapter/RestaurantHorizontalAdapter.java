package com.example.yummy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yummy.Activity.RestaurantDetailActivity;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantHorizontalAdapter extends RecyclerView.Adapter<RestaurantHorizontalAdapter.RestaurantHorizontalHolder> {
    private List<Restaurant> data;
    private Context context;
    private Branch branch;

    public RestaurantHorizontalAdapter (Context context, List<Restaurant> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RestaurantHorizontalHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_restaurant_horizontal, parent, false);
        return new RestaurantHorizontalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantHorizontalHolder holder, int i) {
        Restaurant restaurant = data.get(i);
        branch = getBranch(restaurant);
        holder.tvName.setText(restaurant.getName());
        holder.tvAddress.setText(branch.getAddress());
        Picasso.get().load(getBranch(restaurant).getAvatar()).into(holder.imgRes);

        holder.viewRoot.setOnClickListener(v->{
            Intent intent = new Intent(context, RestaurantDetailActivity.class);
            intent.putExtra("restaurant", restaurant);
            branch = getBranch(restaurant);
            intent.putExtra("branch", branch);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    private Branch getBranch(Restaurant restaurant){
        float[] distance = new float[1];
        float[] min = new float[1];
        branch = restaurant.getBranchList().get(0);
        Location.distanceBetween(branch.getLatitude(), branch.getLongitude(),
                Common.myLocation.getLatitude(), Common.myLocation.getLongitude(), min);
        for(Branch branchNew : restaurant.getBranchList()){
            Location.distanceBetween(branchNew.getLatitude(), branchNew.getLongitude(),
                    Common.myLocation.getLatitude(), Common.myLocation.getLongitude(), distance);
            if(min[0] > distance[0]){
                min[0] = distance[0];
                branch = branchNew;
            }
        }
        return branch;
    }

    class RestaurantHorizontalHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress;
        ImageView imgRes;
        LinearLayout viewRoot;
        RestaurantHorizontalHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            imgRes = itemView.findViewById(R.id.img_res);
            viewRoot= itemView.findViewById(R.id.viewRoot);
        }
    }

}