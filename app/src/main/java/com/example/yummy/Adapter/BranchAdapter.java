package com.example.yummy.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yummy.Model.Branch;
import com.example.yummy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchHolder> {
    private Context context;
    private List<Branch> dataList;

    public BranchAdapter(Context context, List<Branch> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public BranchHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_branch_cus, parent, false);
        return new BranchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchHolder holder, int i) {
        Branch branch = dataList.get(i);

        holder.tvAddress.setText(branch.getAddress());
        Picasso.get().load(branch.getAvatar()).into(holder.imAvatar);
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size():0;
    }

    class BranchHolder extends RecyclerView.ViewHolder {
        ImageView imAvatar;
        TextView tvAddress;
        BranchHolder(@NonNull View itemView) {
            super(itemView);

            imAvatar = itemView.findViewById(R.id.im_avatar);
            tvAddress = itemView.findViewById(R.id.tv_address);
        }
    }
}
