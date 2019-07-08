package com.example.yummy.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yummy.Activity.RestaurantActivity;
import com.example.yummy.Activity.RestaurantDetailActivity;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        if(i==7){
            holder.vMore.setVisibility(View.VISIBLE);
        }else {
            holder.vMore.setVisibility(View.GONE);
        }

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

        holder.vMore.setOnClickListener(v->{
            Intent intent = new Intent(context, RestaurantActivity.class);
            intent.putExtra("type", 0);
            context.startActivity(intent);
        });

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        if (!checkTimeCloses(restaurant.getOpen_time(), today.format("%k:%M"), restaurant.getClose_open())) {
            holder.tvClose.setVisibility(View.VISIBLE);
        } else {
            holder.tvClose.setVisibility(View.GONE);
        }
    }

    private boolean checkTimeCloses(String open, String timeRes, String close){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm");
        try {
            Date dateOpen = inputParser.parse(open);
            Date dateClose = inputParser.parse(close);
            Date time = inputParser.parse(timeRes);
            if ( dateOpen.before(time) && dateClose.after(time)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
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
        TextView tvName, tvAddress, tvClose;
        ImageView imgRes;
        LinearLayout viewRoot, vMore;

        RestaurantHorizontalHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            imgRes = itemView.findViewById(R.id.img_res);
            viewRoot= itemView.findViewById(R.id.viewRoot);
            vMore = itemView.findViewById(R.id.v_more);
            tvClose = itemView.findViewById(R.id.close_res);
        }
    }
}