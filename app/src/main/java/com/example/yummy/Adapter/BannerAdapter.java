package com.example.yummy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.yummy.Activity.RestaurantActivity;
import com.example.yummy.R;

public class BannerAdapter extends PagerAdapter {
    private Context context;
    private int[] image1s = {R.drawable.banner1, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4, R.drawable.banner5};
    private int[] image2s = {R.drawable.banner6, R.drawable.banner7, R.drawable.banner8, R.drawable.banner9, R.drawable.banner10};
    private int type;
    private LayoutInflater layoutInflater;


    public BannerAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return type == 1 ? image1s.length : image2s.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view ==  object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.item_banner, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        if (type == 1)
            imageView.setImageResource(image1s[position]);
        else
            imageView.setImageResource(image2s[position]);

        container.addView(itemView);
        imageView.setOnClickListener(v->{
            Intent intent = new Intent(context, RestaurantActivity.class);
            intent.putExtra("type", 3);
            context.startActivity(intent);
        });

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
