package com.example.yummy.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Adapter.BranchAdapter;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import java.util.Map;

public class InfoRestaurantDetailFragment extends Fragment implements OnMapReadyCallback, YouTubePlayer.OnInitializedListener {
    private Restaurant restaurant;
    private Branch branch;

    public static InfoRestaurantDetailFragment getInstance(Restaurant restaurant, Branch branch) {
        InfoRestaurantDetailFragment fragment = new InfoRestaurantDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("restaurant", restaurant);
        bundle.putParcelable("branch", branch);
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_retaurantdetial_info, container, false);

        if (getArguments() != null) {
            restaurant = getArguments().getParcelable("restaurant");
            branch = getArguments().getParcelable("branch");
        }

        initView(v);

        return v;
    }

    @SuppressLint("SetTextI18n")
    private void initView(View v){
        TextView tvBranchs = v.findViewById(R.id.tv_branch);
        TextView tvType = v.findViewById(R.id.tv_typeRes);
        getChildFragmentManager();
        SupportMapFragment map = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        if (map != null) {
            map.getMapAsync(this);
        }
        TextView tvAddressMap = v.findViewById(R.id.tvAddressMap);
        tvAddressMap.setText(branch.getAddress());
        tvBranchs.setText(restaurant.getBranchList().size()+" "+getResources().getString(R.string.branch));
        StringBuilder type= new StringBuilder();
        if(restaurant.getMenuIdList() != null && Common.menuList != null) {
            for (String key : restaurant.getMenuIdList()) {
                type.append("\\").append(getStringMenuList(key));
            }
            tvType.setText(type.substring(1));
        }else{
            tvType.setVisibility(View.GONE);
        }

        YouTubePlayerSupportFragment youTubePlayerView = (YouTubePlayerSupportFragment) this.getChildFragmentManager().findFragmentById(R.id.videoTrailer);
        if (youTubePlayerView != null) {
            youTubePlayerView.initialize(getResources().getString(R.string.Your_API_KEY), this);
        }

        RecyclerView rcvBranch= v.findViewById(R.id.rcv_branch);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvBranch.setLayoutManager(layoutManager);

        BranchAdapter adapter = new BranchAdapter(getContext(), restaurant.getBranchList(), false);
        rcvBranch.setAdapter(adapter);

        ImageView btnHide = v.findViewById(R.id.btn_hide);
        btnHide.setOnClickListener(vl->{
            boolean isHide = rcvBranch.getVisibility() == View.GONE;
            rcvBranch.setVisibility(isHide ? View.VISIBLE:View.GONE);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(branch.getLatitude(), branch.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(restaurant.getName());

        googleMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        googleMap.moveCamera(cameraUpdate);
    }

    private String getStringMenuList(String key){
        for(Map map: Common.menuList){
            if(map.get(key) != null){
                return (String) map.get(key);
            }
        }
        return "";
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b && restaurant.getVideo() != null) {
            youTubePlayer.cueVideo(restaurant.getVideo());
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        if (result.isUserRecoverableError() && getActivity() != null) {
            result.getErrorDialog(getActivity(), 1).show();
        } else {
            String error = String.format("Error initializing YouTube player", result.toString());
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
    }
}
