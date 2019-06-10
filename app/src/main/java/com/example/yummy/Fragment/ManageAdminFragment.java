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
import com.example.yummy.Activity.ManageBlogAdminActivity;
import com.example.yummy.Activity.ManageRestaurantAdminActivity;
import com.example.yummy.Activity.ManageStatisticAdminActivity;
import com.example.yummy.Model.Account;
import com.example.yummy.Model.Partner;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

        getAccount();
        initView(v);
        return v;
    }

    private void initView(View v){
        LinearLayout vAccount = v.findViewById(R.id.v_account);
        LinearLayout vRestaurant = v.findViewById(R.id.v_branch);
        LinearLayout vBlog = v.findViewById(R.id.v_blog);
        LinearLayout vStatistic = v.findViewById(R.id.v_statistic);

        if(Common.orderListAll == null || Common.orderListAll.size() == 0){
            UtilsBottomBar.getOrderListAll();
        }

        vAccount.setOnClickListener(vl->startActivity(new Intent(getContext(), ManageAccountAdminActivity.class)));
        vRestaurant.setOnClickListener(vl->startActivity(new Intent(getContext(), ManageRestaurantAdminActivity.class)));
        vBlog.setOnClickListener(vl->startActivity(new Intent(getContext(), ManageBlogAdminActivity.class)));
        vStatistic.setOnClickListener(vl->startActivity(new Intent(getContext(), ManageStatisticAdminActivity.class)));
    }

    private void getAccount(){
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
        mData.child(Node.user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Common.accountList = new ArrayList<>();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Account account = dataSnapshot1.getValue(Account.class);
                    if(account != null){
                        if(account.getRole() == 3){
                            mData.child(Node.Partner).child(account.getUserId()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Partner partner = dataSnapshot.getValue(Partner.class);
                                        if (partner != null) {
                                            account.setPartner(partner);
                                            Common.accountList.add(account);
                                        }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else{
                            Common.accountList.add(account);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
