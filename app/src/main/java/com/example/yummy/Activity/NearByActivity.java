package com.example.yummy.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class NearByActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap myMap;
    private ProgressDialog myProgress;
    private SupportMapFragment map;
    private List<Marker> markerList;
    private LatLngBounds.Builder builder;
    private CameraUpdate cu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        markerList = new ArrayList<>();
//        // Tạo Progress Bar
//        myProgress = new ProgressDialog(this);
//        myProgress.setTitle("Map Loading ...");
//        myProgress.setMessage("Please wait...");
//        myProgress.setCancelable(true);
//
//        // Hiển thị Progress Bar
//        myProgress.show();


        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (map != null) {
            map.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        getMarkerList();
        myMap.animateCamera(cu);
    }

    private void getMarkerList(){
        for(Restaurant restaurant : Common.restaurantListCurrent){
            for(Branch branch : restaurant.getBranchList()){
                Marker marker = myMap.addMarker(new MarkerOptions()
                        .position(new LatLng(branch.getLatitude(), branch.getLongitude()))
                        .title(restaurant.getName()+" - "+branch.getAddress()));

                markerList.add(marker);
            }
        }

        builder = new LatLngBounds.Builder();
        for (Marker m : markerList) {
            builder.include(m.getPosition());
        }
        /**initialize the padding for map boundary*/
        int padding = 50;
        /**create the bounds from latlngBuilder to set into map camera*/
        LatLngBounds bounds = builder.build();
        /**create the camera with bounds and padding to set into map*/
        cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

    }
}
