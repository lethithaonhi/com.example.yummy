package com.example.yummy.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.example.yummy.Activity.ManageAccountAdminActivity;
import com.example.yummy.R;

public class ManageAdminFragment extends Fragment {

    public static ManageAdminFragment newInstance() {
        Bundle args = new Bundle();
        ManageAdminFragment fragment = new ManageAdminFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manage_home_admin, container, false);

        initView(v);
        return v;
    }

    private void initView(View v){
        LinearLayout vAccount = v.findViewById(R.id.v_account);
        LinearLayout vRestaurant = v.findViewById(R.id.v_branch);
        LinearLayout vBlog = v.findViewById(R.id.v_blog);
        LinearLayout vStatistic = v.findViewById(R.id.v_statistic);

        vAccount.setOnClickListener(vl->startActivity(new Intent(getContext(), ManageAccountAdminActivity.class)));
    }
}
