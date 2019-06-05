package com.example.yummy.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.yummy.Model.Account;
import com.example.yummy.Model.Menu;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AccountAdminAdapter  extends RecyclerSwipeAdapter<AccountAdminAdapter.AccountAdminHolder> {
    private Context context;
    private List<Account> data;

    public AccountAdminAdapter(Context context, List<Account> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public AccountAdminHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_admin_manage, parent, false);
        return new AccountAdminHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountAdminHolder holder, int position) {
        Account account = data.get(position);
        holder.tvName.setText(account.getName());
        holder.tvUserName.setText(account.getUsername());
        holder.tvPhone.setText(account.getPhone());
        holder.tvPass.setText(account.getPhone() != null ? account.getPhone(): "Hide");
        Picasso.get().load(account.getAvatar()).into(holder.imAvatar);
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        holder.btnClose.setOnClickListener(v->showDialogClose(account));
        if(account.getIsClose() != 1) {
            holder.btnClose.setBackgroundColor(context.getResources().getColor(R.color.red));
            holder.imDelete.setImageResource(R.drawable.ic_lock);
        }else {
            holder.btnClose.setBackgroundColor(context.getResources().getColor(R.color.green));
            holder.imDelete.setImageResource(R.drawable.unlock);
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size():0;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    class AccountAdminHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        ImageView imAvatar, imDelete;
        TextView tvName, tvUserName, tvPhone, tvPass;
        LinearLayout btnClose;
        AccountAdminHolder(@NonNull View itemView) {
            super(itemView);

            swipeLayout = itemView.findViewById(R.id.swipe);
            imAvatar = itemView.findViewById(R.id.im_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvUserName = itemView.findViewById(R.id.tv_username);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvPass = itemView.findViewById(R.id.tv_pass);
            btnClose = itemView.findViewById(R.id.btn_delete);
            imDelete = itemView.findViewById(R.id.im_delete);
        }
    }

    private void showDialogClose(Account account) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(account.getIsClose() == 1 ? context.getResources().getString(R.string.mess_open) : context.getResources().getString(R.string.mess_close))
                .setCancelable(false)
                .setPositiveButton(account.getIsClose()== 1 ? context.getResources().getString(R.string.yes) : context.getResources().getString(R.string.close), ((dialog, which) -> {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    int isClose = account.getIsClose();
                    if(isClose == 0)
                        isClose = 1;
                    else
                        isClose = 0;

                    mDatabase.child(Node.user).child(account.getUserId()).child(Node.isClose).setValue(isClose);

                    account.setIsClose(isClose);
                    dialog.dismiss();
                    notifyDataSetChanged();
                    Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show();
                }))
                .setNegativeButton(context.getResources().getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
