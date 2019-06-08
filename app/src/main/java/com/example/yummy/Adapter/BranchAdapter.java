package com.example.yummy.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Model.Branch;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
        if(!branch.getAvatar().isEmpty())
            Picasso.get().load(branch.getAvatar()).into(holder.imAvatar);
        if(branch.getIsDelete() == 1){
            holder.imClose.setVisibility(View.VISIBLE);
            holder.btnOpen.setVisibility(View.VISIBLE);
            holder.btnClose.setVisibility(View.GONE);
        }else {
            holder.imClose.setVisibility(View.GONE);
            holder.btnOpen.setVisibility(View.GONE);
            holder.btnClose.setVisibility(View.VISIBLE);
        }

        holder.btnClose.setOnClickListener(v-> showDialogClose(branch));
        holder.btnOpen.setOnClickListener(v->showDialogOpen(branch));
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size():0;
    }

    class BranchHolder extends RecyclerView.ViewHolder {
        ImageView imAvatar, imClose, btnClose, btnOpen;
        TextView tvAddress;
        BranchHolder(@NonNull View itemView) {
            super(itemView);

            imAvatar = itemView.findViewById(R.id.im_avatar);
            tvAddress = itemView.findViewById(R.id.tv_address);
            imClose = itemView.findViewById(R.id.im_close);
            btnClose = itemView.findViewById(R.id.btn_close);
            btnOpen = itemView.findViewById(R.id.btn_open);
        }
    }

    private void showDialogClose(Branch branch){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("");
        builder.setMessage(R.string.close_branch);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            branch.setIsDelete(1);
            Common.db.updateBranch(branch);
            mDatabase.child(Node.Branch).child(Common.restaurantPartner.getRes_id()).child(branch.getId()).child(Node.isDelete).setValue(1);
            Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
        });
        builder.setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDialogOpen(Branch branch){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("");
        builder.setMessage(R.string.open_branch);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            branch.setIsDelete(0);
            Common.db.updateBranch(branch);
            mDatabase.child(Node.Branch).child(Common.restaurantPartner.getRes_id()).child(branch.getId()).child(Node.isDelete).setValue(0);
            Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
        });
        builder.setNegativeButton(R.string.no, (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
