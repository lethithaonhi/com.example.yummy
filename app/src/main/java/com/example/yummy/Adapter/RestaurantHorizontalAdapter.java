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
    private int max = 8;

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
        if(!branch.getAvatar().isEmpty())
        Picasso.get().load(getBranch(restaurant).getAvatar()).into(holder.imgRes);

        holder.viewRoot.setOnClickListener(v->{
            Intent intent = new Intent(context, RestaurantDetailActivity.class);
            intent.putExtra("restaurant", restaurant);
            branch = getBranch(restaurant);
            intent.putExtra("branch", branch);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if(data != null){
            if(data.size() > max)
                return max;
            else
                return data.size();
        }else
            return 0;
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