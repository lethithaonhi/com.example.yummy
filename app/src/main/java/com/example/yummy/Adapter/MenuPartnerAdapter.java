package com.example.yummy.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.yummy.Model.Menu;
import com.example.yummy.R;
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
        holder.tvPrice.setText(menu.getPrices()+" VND");
        Picasso.get().load(menu.getImage()).into(holder.imMenu);
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        holder.swipeLayout.setOnDoubleClickListener((layout, surface) -> Toast.makeText(context, "DoubleClick", Toast.LENGTH_SHORT).show());
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
        ImageView imMenu;
        TextView tvName, tvDes, tvCount, tvPrice;
        SwipeLayout swipeLayout;
        MenuPartnerHolder(@NonNull View itemView) {
            super(itemView);

            imMenu = itemView.findViewById(R.id.im_menu);
            tvName = itemView.findViewById(R.id.tv_nameMenu);
            tvDes = itemView.findViewById(R.id.tv_desMenu);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCount = itemView.findViewById(R.id.tv_countMenu);
            swipeLayout = itemView.findViewById(R.id.swipe);

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
}
