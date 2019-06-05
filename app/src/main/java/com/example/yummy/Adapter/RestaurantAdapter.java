package com.example.yummy.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yummy.Activity.RestaurantDetailActivity;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantHolder> implements Filterable {
    private List<Restaurant> restaurantList;
    private List<Restaurant> dataFilter;
    private String query = "";
    private Context context;
    private Branch branch;
    private float min;

    public RestaurantAdapter(List<Restaurant> restaurantList, Context context){
        this.restaurantList = restaurantList;
        this.context = context;
        dataFilter = restaurantList;
    }

    @NonNull
    @Override
    public RestaurantAdapter.RestaurantHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.RestaurantHolder holder, int i) {
        Restaurant restaurant = dataFilter.get(i);
        
        branch = getBranch(restaurant);
        holder.tvName.setText(restaurant.getName());
        holder.tvMark.setText(restaurant.getMark()+"");
        holder.tvDistance.setText(new DecimalFormat("##.##").format(min/1000)+" km");
        holder.tvAddress.setText(branch.getAddress());
        if(!branch.getAvatar().isEmpty())
            Picasso.get().load(branch.getAvatar()).into(holder.imRes);
        if(restaurant.getFreeship() == 1){
            holder.viewFreeship.setVisibility(View.VISIBLE);
        }

        if(restaurant.getDiscounts()!= null && restaurant.getDiscounts().getDiscount() != 0){
            holder.viewDiscount.setVisibility(View.VISIBLE);
            holder.tvDiscount.setText(restaurant.getDiscounts().getDiscount() + "%");
        }
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
        return dataFilter != null ? dataFilter.size() : 0;
    }

    private Branch getBranch(Restaurant restaurant){
        branch = restaurant.getBranchList().get(0);
        min = branch.getDistance();
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
        TextView tvName, tvAddress, tvMark, tvDistance, tvDiscount;
        LinearLayout viewFreeship, viewRoot, viewDiscount;
        RestaurantHolder(@NonNull View itemView) {
            super(itemView);

            imRes = itemView.findViewById(R.id.im_restaurant);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvMark = itemView.findViewById(R.id.tv_mark);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            viewFreeship = itemView.findViewById(R.id.view_freeship);
            viewRoot = itemView.findViewById(R.id.view_rooot);
            viewDiscount = itemView.findViewById(R.id.view_discount);
            tvDiscount = itemView.findViewById(R.id.tv_discount);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                query = charSequence.toString();
                if (!query.isEmpty()) {
                    List<Restaurant> filteredList = new ArrayList<>();
                    for (Restaurant restaurant : restaurantList) {
                        if (restaurant.getName().toLowerCase().contains(query.toLowerCase())) {
                            filteredList.add(restaurant);
                        }
                    }

                    dataFilter = filteredList;

                } else {
                    dataFilter = restaurantList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = dataFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dataFilter = (List<Restaurant>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
