package com.example.yummy.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yummy.R;

public class InfoPartnerFragment extends Fragment {

    public static InfoPartnerFragment newInstance() {
        Bundle args = new Bundle();
        InfoPartnerFragment fragment = new InfoPartnerFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        return rootView;

    }
}
