package com.example.yummy.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.yummy.Activity.ImgDetailReviewActivity;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImgRestaurantDetailAdapter extends RecyclerView.Adapter<ImgRestaurantDetailAdapter.ImgRestaurantDetailHolder> {
    private Context context;
    private List<String> data;
    private int max = 9;
    private Dialog dialog;
    private ImageView imageView;
    private Restaurant restaurant;
    private int type;

    public ImgRestaurantDetailAdapter (Context context, List<String> data, Restaurant restaurant, int type){
        this.context = context;
        this.data = data;
        this.restaurant = restaurant;
        this.type = type;
    }

    @NonNull
    @Override
    public ImgRestaurantDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_imagereview_resdetail, parent, false);
        return new ImgRestaurantDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImgRestaurantDetailHolder holder, int i) {
        String url = data.get(i);
        Picasso.get().load(url).into(holder.img);

        if (type ==0 && (i == max - 1 || i == data.size())) {
            holder.viewMore.setVisibility(View.VISIBLE);
        }

        holder.img.setOnClickListener(v -> {
            if (type ==0 && (i == max - 1 || data.size() == i)) {
                Intent intent = new Intent(context, ImgDetailReviewActivity.class);
                intent.putExtra("restaurant", restaurant);
                context.startActivity(intent);
            } else
                holder.setDialog(url);
        });
    }

    @Override
    public int getItemCount() {
        if(data != null){
            if(type == 0 && data.size() > max){
                return max;
            }else {
                return data.size();
            }
        }else
            return 0;
    }

    class ImgRestaurantDetailHolder extends RecyclerView.ViewHolder {
        ImageView img;
        LinearLayout viewMore;

        ImgRestaurantDetailHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.im_resdetail);
            viewMore = itemView.findViewById(R.id.view_more);
            createDialog();
        }

        private void setDialog(String url){
            dialog.show();
            Picasso.get().load(url).into(imageView);
        }
    }

    private void createDialog(){
        dialog = new Dialog(context);
        dialog.setTitle("Show Images");
        dialog.setContentView(R.layout.dialog_imgreview_resdetail);
        imageView = dialog.findViewById(R.id.im_imgdetail);
    }

}
