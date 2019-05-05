package com.example.yummy.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class InfoRestaurantDetailFragment extends Fragment implements OnMapReadyCallback {
    private Restaurant restaurant;
    private Branch branch;
    private SupportMapFragment map;
    private GoogleMap mMap;

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

    private void initView(View v){
        TextView tvBranchs = v.findViewById(R.id.tv_branch);
        TextView tvType = v.findViewById(R.id.tv_typeRes);
        getChildFragmentManager();
        map = (SupportMapFragment)this.getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
        TextView tvAddressMap = v.findViewById(R.id.tvAddressMap);
        tvAddressMap.setText(branch.getAddress());
        tvBranchs.setText(restaurant.getBranchList().size()+" braches");
        String type="";
        for(String key : restaurant.getMenuIdList()){
            type += "\\"+getStringMenuList(key);
        }
        tvType.setText(type.substring(1));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
}
