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

import com.example.yummy.Model.Menu;
import com.example.yummy.R;
import com.example.yummy.Utils.UtilsBottomBar;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MenuRestaurantDetailAdapter extends RecyclerView.Adapter<MenuRestaurantDetailAdapter.MenuRestaurantDetailHolder> {
    private List<Menu> data;
    private Context context;
    private int[] countList;
    private OnCountChangeListener onCountChangeListener;
    private boolean isClose;

    public void setOnCountChangeListener(OnCountChangeListener onCountChangeListener) {
        this.onCountChangeListener = onCountChangeListener;
    }

    public interface OnCountChangeListener {
        void onCountChanged(int count, Menu menu);
    }

    public MenuRestaurantDetailAdapter (Context context, List<Menu> data, boolean isClose){
        this.context = context;
        this.data = data;
        this.isClose = isClose;
        countList = new int [data.size()];
        for (int i=0;i<data.size();i++){
            countList[i] = 0;
        }
    }


    @NonNull
    @Override
    public MenuRestaurantDetailAdapter.MenuRestaurantDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_menu_restaurantdetail, parent, false);
        return new MenuRestaurantDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuRestaurantDetailHolder holder, int i) {
        Menu menu = data.get(i);

        if(menu.getIsDelete() != 1) {
            holder.tvName.setText(menu.getName());
            holder.tvDes.setText(menu.getDescribe());
            holder.tvPrice.setText(UtilsBottomBar.convertStringToMoney(menu.getPrices()));
            if (!menu.getImage().isEmpty())
                Picasso.get().load(menu.getImage()).into(holder.imMenu);

            holder.btnAdd.setOnClickListener(v -> {
                if (!isClose) {
                    countList[i]++;
                    holder.tvCount.setText(String.valueOf(countList[i]));
                    if (holder.btnRemove.getVisibility() == View.GONE) {
                        holder.btnRemove.setVisibility(View.VISIBLE);
                        holder.tvCount.setVisibility(View.VISIBLE);
                    }
                }
                onCountChangeListener.onCountChanged(countList[i], menu);
            });

            holder.btnRemove.setOnClickListener(v -> {
                if (!isClose) {
                    if (countList[i] > 0) {
                        countList[i]--;
                        holder.tvCount.setText(String.valueOf(countList[i]));
                    }
                    if (countList[i] == 0) {
                        holder.btnRemove.setVisibility(View.GONE);
                        holder.tvCount.setVisibility(View.GONE);
                    }
                }
                onCountChangeListener.onCountChanged(countList[i], menu);
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MenuRestaurantDetailHolder extends RecyclerView.ViewHolder {
        ImageView imMenu, btnRemove, btnAdd;
        TextView tvName, tvDes, tvCount, tvPrice;

        MenuRestaurantDetailHolder(@NonNull View itemView) {
            super(itemView);
            imMenu = itemView.findViewById(R.id.im_menu);
            tvName = itemView.findViewById(R.id.tv_nameMenu);
            tvDes = itemView.findViewById(R.id.tv_desMenu);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvCount = itemView.findViewById(R.id.tv_countMenu);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            btnAdd = itemView.findViewById(R.id.btn_plus);

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
