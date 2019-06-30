package com.example.yummy.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.example.yummy.Adapter.RestaurantAdapter;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class NearByActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap myMap;
    private List<Restaurant> restaurantList;
    private RecyclerView rcvRes;
    private ProgressDialog myProgress;
//    private List<Marker> markerList;
//    private LatLngBounds.Builder builder;
//    private CameraUpdate cu;
    private LatLng current;
    private Circle circle;
    private SupportMapFragment map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        initView();

        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (map != null) {
            map.getMapAsync(this);
        }
    }

    private void initView(){
        rcvRes = findViewById(R.id.rcv_restaurant);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvRes.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvRes.getContext(), layoutManager.getOrientation());
        rcvRes.addItemDecoration(dividerItemDecoration);
        ImageView imPlus = findViewById(R.id.imgplusRadius);
        imPlus.setOnClickListener(v->{
            circle.setRadius(circle.getRadius() + 1000);
            getMarkerList();
            setAdapter();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        current = new LatLng(Common.myLocation.getLatitude(), Common.myLocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(current).title(getString(R.string.your_location)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_near_me_blue_24dp)));
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, (float) 14));

        //tạo vòng tròn bao quanh
        circle = myMap.addCircle(new CircleOptions()
                .center(current)
                .radius(1000)
                .strokeColor(R.color.maps)
                .fillColor(R.color.mapsfill));

        getMarkerList();
        setAdapter();
    }

    private void getMarkerList(){
        double radius = circle.getRadius();
        restaurantList = new ArrayList<>();
        for(Restaurant restaurant : Common.restaurantListCurrent){
            for(Branch branch : restaurant.getBranchList()){
                if(branch.getDistance() < radius){
                    if(!restaurantList.contains(restaurant)){
                        restaurantList.add(restaurant);
                    }
                    LatLng resPos = new LatLng(branch.getLatitude(), branch.getLongitude());
                    myMap.addMarker(new MarkerOptions().position(resPos).title(restaurant.getName()));
                }
            }
        }
    }

    private void setAdapter(){
        RestaurantAdapter adapter = new RestaurantAdapter(restaurantList, this, 1);
        rcvRes.setAdapter(adapter);
    }
}
