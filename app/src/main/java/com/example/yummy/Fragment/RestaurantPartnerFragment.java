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
import android.widget.Toast;
import com.example.yummy.Activity.LoginActivity;
import com.example.yummy.Activity.RestaurantManageActivity;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.UtilsBottomBar;

public class RestaurantPartnerFragment extends Fragment {

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

        if(Common.accountCurrent != null && Common.accountCurrent.getPartner() != null) {
            UtilsBottomBar.RestaurantPartnerAsyncTask asyncTask = new UtilsBottomBar.RestaurantPartnerAsyncTask(Common.accountCurrent.getPartner().getBoss());
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else {
            Toast.makeText(getContext(), R.string.login_again, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
            LoginActivity.mAuth.signOut();
            if(getActivity() != null)
                getActivity().finish();
        }

        return v;
    }
}
