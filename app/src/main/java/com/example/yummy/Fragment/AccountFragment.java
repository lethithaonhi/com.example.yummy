package com.example.yummy.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.yummy.Activity.InforUserActivity;
import com.example.yummy.Activity.LoginActivity;
import com.example.yummy.Activity.RestaurantActivity;
import com.example.yummy.R;

public class AccountFragment extends Fragment {

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        Button btnSignIn = v.findViewById(R.id.btn_signin);
        btnSignIn.setOnClickListener(v1 -> startActivity(new Intent(getContext(), LoginActivity.class)));

        LinearLayout viewSetting = v.findViewById(R.id.view_setting);
        viewSetting.setOnClickListener(vl-> dialogSetting());
        return v;
    }

    private void dialogSetting(){
        if(getContext() != null) {
            Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setTitle("");
            dialog.setContentView(R.layout.dialog_setting_account);
            dialog.show();

            LinearLayout viewAvatar = dialog.findViewById(R.id.view_avatar);
            LinearLayout viewinfor = dialog.findViewById(R.id.view_info);
            LinearLayout viewChangePass = dialog.findViewById(R.id.view_changepass);

            viewAvatar.setOnClickListener(v -> startActivity(new Intent(getContext(), InforUserActivity.class)));
            ImageView imClose = dialog.findViewById(R.id.im_close);
            imClose.setOnClickListener(v->dialog.dismiss());
        }
    }
}
