package com.example.yummy.Fragment;

import android.annotation.SuppressLint;
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
import com.example.yummy.Adapter.RestaurantHorizontalAdapter;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.UtilsBottomBar;

import java.util.ArrayList;

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

        RestaurantPartnerAsyncTask asyncTask = new RestaurantPartnerAsyncTask();
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return v;
    }

    @SuppressLint("StaticFieldLeak")
    private class RestaurantPartnerAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Common.restaurantListCurrent = new ArrayList<>();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(Common.db != null) {
                Common.restaurantListCurrent = Common.db.getRestaurantPartner(resID, Common.myAddress);
                for (Restaurant restaurant : Common.restaurantListCurrent) {
                    for (Branch branch : restaurant.getBranchList()) {
                        branch.setDistance(UtilsBottomBar.getDistanceBranch(branch));
                    }
                }
                Common.menuList = UtilsBottomBar.getMenuList();
            }
            return null;
        }
    }
}
