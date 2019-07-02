package com.example.yummy.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yummy.Adapter.RestaurantAdapter;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class NearByActivity extends AppCompatActivity implements OnMapReadyCallback, RestaurantAdapter.OnSawMapChangeListener {
    private GoogleMap myMap;
    private List<Restaurant> restaurantList;
    private RecyclerView rcvRes;
    private Circle circle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        initView();

        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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
        TextView tvAddress = findViewById(R.id.tv_address);
        tvAddress.setText(Common.newLocation.getName());
        LinearLayout viewAddress = findViewById(R.id.view_address);
        viewAddress.setOnClickListener(v->startActivity(new Intent(this, ChangeAddressActivity.class)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        LatLng current = new LatLng(Common.newLocation.getLatitude(), Common.newLocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(current).title(getString(R.string.your_location)).icon(bitmapDescriptorFromVector(R.drawable.ic_near_me_blue_24dp)));
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
        RestaurantAdapter adapter = new RestaurantAdapter(restaurantList, this, 3);
        rcvRes.setAdapter(adapter);
        adapter.setOnSawMapChangeListener(this);
    }

    @Override
    public void onSawMap(Branch branch) {
        LatLng markerPosition = new LatLng(branch.getLatitude(), branch.getLongitude());
        Marker marker = myMap.addMarker(new MarkerOptions().position(markerPosition).title(branch.getAddress()));
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 20f));

//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(Latitude, Latitude) // Center Set
//                .zoom(11.0f)                // Zoom
//                .bearing(90)                // Orientation of the camera to east
//                .tilt(30)                   // Tilt of the camera to 30 degrees
//                .build();                   // Creates a CameraPosition from the builder
//        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


//        CameraUpdate center=
//                CameraUpdateFactory.newLatLng(new LatLng(doubleLat, doubleLon));
//        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
//
//        map.moveCamera(center);
//        map.animateCamera(zoom);

    }

    private BitmapDescriptor bitmapDescriptorFromVector(int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(this, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
