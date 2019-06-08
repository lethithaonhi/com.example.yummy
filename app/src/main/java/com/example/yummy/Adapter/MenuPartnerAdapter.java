package com.example.yummy.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.yummy.Model.Menu;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MenuPartnerAdapter extends RecyclerSwipeAdapter<MenuPartnerAdapter.MenuPartnerHolder> {
    private Context context;
    private List<Menu> dataList;

    public MenuPartnerAdapter (Context context, List<Menu> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MenuPartnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_partner, parent, false);
        return new MenuPartnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuPartnerHolder holder, int i) {
        Menu menu = dataList.get(i);

        holder.tvName.setText(menu.getName());
        holder.tvDes.setText(menu.getDescribe());
        holder.tvPrice.setText(menu.getPrices() + " VND");
        Picasso.get().load(menu.getImage()).into(holder.imMenu);

        holder.btnDelete.setOnClickListener(v -> showDialogDelete(menu));
        holder.btnEdit.setOnClickListener(v->showDialogEdit(menu));
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });

        if(menu.getIsDelete() != 1) {
            holder.imClose.setVisibility(View.GONE);
            holder.imDelete.setImageResource(R.drawable.ic_delete_white_24dp);
        }else {
            holder.imClose.setVisibility(View.VISIBLE);
            holder.imDelete.setImageResource(R.drawable.open);
        }
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size():0;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }


    class MenuPartnerHolder extends RecyclerView.ViewHolder {
        ImageView imMenu, imClose, imDelete;
        TextView tvName, tvDes, tvCount, tvPrice;
        SwipeLayout swipeLayout;
        LinearLayout btnEdit, btnDelete;
        MenuPartnerHolder(@NonNull View itemView) {
            super(itemView);

            imMenu = itemView.findViewById(R.id.im_menu);
            tvName = itemView.findViewById(R.id.tv_nameMenu);
            tvDes = itemView.findViewById(R.id.tv_desMenu);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCount = itemView.findViewById(R.id.tv_countMenu);
            swipeLayout = itemView.findViewById(R.id.swipe);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            imClose = itemView.findViewById(R.id.im_close);
            imDelete = itemView.findViewById(R.id.im_delete);

            ViewTreeObserver vto = imMenu.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    imMenu.getViewTreeObserver().removeOnPreDrawListener(this);
                    imMenu.getLayoutParams().height = imMenu.getMeasuredWidth();
                    imMenu.requestLayout();
                    return true;
                }
            });
        }
    }

    private void showDialogDelete(Menu menu) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(menu.getIsDelete() == 1 ? context.getResources().getString(R.string.mess_open) : context.getResources().getString(R.string.mess_delete))
                .setCancelable(false)
                .setPositiveButton(menu.getIsDelete() == 1 ? context.getResources().getString(R.string.yes) : context.getResources().getString(R.string.delete), ((dialog, which) -> {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    int isDel = menu.getIsDelete();
                    if(isDel == 0)
                        isDel = 1;
                    else
                        isDel = 0;
                    mDatabase.child(Node.ThucDonQuanAn).child(Common.accountCurrent.getPartner().getBoss()).child(menu.getType()).child(menu.getMenu_id()).child(Node.isDelete).setValue(isDel);
                    menu.setIsDelete(isDel);
                    Common.db.updateMenu(menu);
                    dialog.dismiss();
                    notifyDataSetChanged();
                    Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show();
                }))
                .setNegativeButton(context.getResources().getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showDialogEdit(Menu menu){
        Dialog dialog = new Dialog(context);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_edit_menu);
        dialog.show();

        EditText edName = dialog.findViewById(R.id.ed_name);
        EditText edPrice = dialog.findViewById(R.id.ed_price);
        EditText edDes = dialog.findViewById(R.id.ed_des);
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnEdit = dialog.findViewById(R.id.btn_edit);

        edName.setText(menu.getName());
        edPrice.setText(menu.getPrices()+"");
        edDes.setText(menu.getDescribe());

        btnCancel.setOnClickListener(v->dialog.dismiss());
        btnEdit.setOnClickListener(v->{
            String name = edName.getText().toString().trim();
            String prices = edPrice.getText().toString().trim();
            String des = edDes.getText().toString().trim();

            if (!name.isEmpty() && !prices.isEmpty()){
                menu.setName(name);
                menu.setPrices(Integer.parseInt(prices));
                if(!des.isEmpty())
                    menu.setDescribe(des);
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child(Node.ThucDonQuanAn).child(Common.accountCurrent.getPartner().getBoss()).child(menu.getType()).child(menu.getMenu_id()).setValue(menu);
                Common.db.updateMenu(menu);
                dialog.dismiss();
                notifyDataSetChanged();
                Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, R.string.empty_user, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
