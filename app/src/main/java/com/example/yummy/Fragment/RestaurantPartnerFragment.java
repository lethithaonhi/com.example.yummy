package com.example.yummy.Fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.yummy.Activity.RestaurantManageActivity;
import com.example.yummy.R;
import com.example.yummy.Utils.UtilsBottomBar;

public class RestaurantPartnerFragment extends Fragment {
    private String resID = "quan1";

    public static RestaurantPartnerFragment newInstance() {
        Bundle args = new Bundle();
        RestaurantPartnerFragment fragment = new RestaurantPartnerFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_restaurant_home_partner, container, false);

        LinearLayout vOrder = v.findViewById(R.id.v_order);
        LinearLayout vBranch = v.findViewById(R.id.v_branch);
        LinearLayout vMenu = v.findViewById(R.id.v_menu);
        LinearLayout vImage = v.findViewById(R.id.v_image);

        vOrder.setOnClickListener(vl->{
            Intent intent = new Intent(getContext(), RestaurantManageActivity.class);
            intent.putExtra("type", 0);
            startActivity(intent);
        });

        vBranch.setOnClickListener(vl->{
            Intent intent = new Intent(getContext(), RestaurantManageActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
        });

        vMenu.setOnClickListener(vl->{
            Intent intent = new Intent(getContext(), RestaurantManageActivity.class);
            intent.putExtra("type", 2);
            startActivity(intent);
        });

        vImage.setOnClickListener(vl->{
            Intent intent = new Intent(getContext(), RestaurantManageActivity.class);
            intent.putExtra("type", 3);
            startActivity(intent);
        });

        UtilsBottomBar.RestaurantPartnerAsyncTask asyncTask = new UtilsBottomBar.RestaurantPartnerAsyncTask(resID);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return v;
    }
}
