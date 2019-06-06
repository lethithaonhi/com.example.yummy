package com.example.yummy.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.example.yummy.Activity.RestaurantDetailActivity;
import com.example.yummy.Model.Account;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private int type; //1:cus, 0: admin

    public RestaurantAdapter(List<Restaurant> restaurantList, Context context, int type){
        this.restaurantList = restaurantList;
        this.context = context;
        dataFilter = restaurantList;
        this.type = type;
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

        if(restaurant.getIsClose() == 0) {
            holder.btnClose.setBackgroundColor(context.getResources().getColor(R.color.red));
            holder.imDelete.setImageResource(R.drawable.ic_lock);
        }else {
            holder.btnClose.setBackgroundColor(context.getResources().getColor(R.color.green));
            holder.imDelete.setImageResource(R.drawable.unlock);
        }

        if(type == 0){
            if(restaurant.getIsClose() == 0){
                holder.imClose.setVisibility(View.GONE);
            }else {
                holder.imClose.setVisibility(View.VISIBLE);
            }
        }

        if(type == 0) {
            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                @Override
                public void onOpen(SwipeLayout layout) {
                    YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                }
            });
        }

        holder.btnClose.setOnClickListener(v->showDialogClose(restaurant));
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
        ImageView imRes, imClose, imDelete;
        TextView tvName, tvAddress, tvMark, tvDistance, tvDiscount;
        LinearLayout viewFreeship, viewRoot, viewDiscount;
        SwipeLayout swipeLayout;
        LinearLayout btnEdit, btnClose, vTrash;
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
            imDelete = itemView.findViewById(R.id.im_delete);
            swipeLayout = itemView.findViewById(R.id.swipe);
            btnClose = itemView.findViewById(R.id.btn_delete);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            imClose = itemView.findViewById(R.id.im_close);
            vTrash = itemView.findViewById(R.id.trash);
            if(type == 0){
                tvDistance.setVisibility(View.GONE);
                tvMark.setVisibility(View.GONE);
                imClose.setVisibility(View.VISIBLE);
            }
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

    private void showDialogClose(Restaurant restaurant) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(restaurant.getIsClose() == 1 ? context.getResources().getString(R.string.mess_open) : context.getResources().getString(R.string.mess_close))
                .setCancelable(false)
                .setPositiveButton(restaurant.getIsClose()== 1 ? context.getResources().getString(R.string.yes) : context.getResources().getString(R.string.close), ((dialog, which) -> {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    int isClose = restaurant.getIsClose();
                    if(isClose == 0)
                        isClose = 1;
                    else
                        isClose = 0;

                    mDatabase.child(Node.QuanAn).child(restaurant.getRes_id()).child(Node.isClose).setValue(isClose);

                    restaurant.setIsClose(isClose);
                    dialog.dismiss();
                    notifyDataSetChanged();
                    Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show();
                }))
                .setNegativeButton(context.getResources().getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
