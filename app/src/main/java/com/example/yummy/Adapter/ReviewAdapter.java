package com.example.yummy.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yummy.Model.Review;
import com.example.yummy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {
    private Context context;
    private List<Review> data;

    public ReviewAdapter(Context context, List<Review> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_review, parent, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int i) {
        Review review = data.get(i);

        holder.tvContent.setText(review.getContent());
        holder.tvTime.setText(review.getTime());
        holder.tvName.setText(review.getName());
        holder.tvMark.setText(review.getMark()+"");
        if(!review.getAvatar().isEmpty())
        Picasso.get().load(review.getAvatar()).into(holder.imAvatar);

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size():0;
    }

    class ReviewHolder extends RecyclerView.ViewHolder {
        TextView tvMark, tvName, tvTime, tvContent;
        CircleImageView imAvatar;
        ReviewHolder(@NonNull View itemView) {
            super(itemView);

            tvMark = itemView.findViewById(R.id.tv_mark);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvContent = itemView.findViewById(R.id.tv_content);
            imAvatar = itemView.findViewById(R.id.im_avatar);

        }
    }
}
