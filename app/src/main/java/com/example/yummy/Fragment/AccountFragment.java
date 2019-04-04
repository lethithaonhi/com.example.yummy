package com.example.yummy.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.yummy.Activity.LoginActivity;
import com.example.yummy.R;

public class AccountFragment extends Fragment {

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);

        Button btnSignIn = v.findViewById(R.id.btn_signin);
        btnSignIn.setOnClickListener(v1 -> {
          startActivity(new Intent(getContext(), LoginActivity.class));
        });
        return v;
    }
}
