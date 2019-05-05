package com.example.yummy.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.daimajia.numberprogressbar.OnProgressBarListener;
import com.example.yummy.Database.MyDatabaseHelper;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Menu;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleProgressBar progressBar;
    private NetworkChangeReceiver broadcastReceiver;
    private GoogleApiClient gac;
    private DatabaseReference mDatabase;
    private MyDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Common.restaurantList = new ArrayList<>();
        progressBar = findViewById(R.id.number_progress_bar);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Common.listResId = new ArrayList<>();
        registerService();
        db = new MyDatabaseHelper(this);
        Common.db = db;
    }

    private void getRestaurant(){
        mDatabase.child(Node.DiaDiem).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    String address = data.getKey();
                    if(address != null)
                    mDatabase.child(Node.DiaDiem).child(address).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshotRoot) {
                            for (DataSnapshot postSnapshot: dataSnapshotRoot.getChildren()) {
                                String resID = postSnapshot.getValue(String.class);
                                if(resID != null){
                                    if(address.equals(Common.myAddress)){
                                        Common.listResId.add(resID);
                                    }
                                    mDatabase.child(Node.QuanAn).child(resID).addValueEventListener(new ValueEventListener(){
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                                            if(restaurant != null) {
                                                restaurant.setCity(address);
                                                restaurant.setRes_id(dataSnapshot.getKey());

                                                List<String> imgList = new ArrayList<>();
                                                mDatabase.child(Node.HinhAnhQuanAn).child(resID).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for(DataSnapshot data : dataSnapshot.getChildren())
                                                            imgList.add(data.getValue(String.class));
                                                        restaurant.setImgList(imgList);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                                mDatabase.child(Node.QuanAn).child(resID).child(Node.Menu_QuanAn).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        List<String> menuList = new ArrayList<>();
                                                        for (DataSnapshot menuSnapshot : dataSnapshot.getChildren()) {
                                                            menuList.add(menuSnapshot.getValue(String.class));
                                                            restaurant.setMenuIdList(menuList);
                                                        }

                                                        List<Branch> branchList = new ArrayList<>();
                                                        mDatabase.child(Node.Branch).child(resID).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for(DataSnapshot data : dataSnapshot.getChildren()) {
                                                                    Branch branch = data.getValue(Branch.class);
                                                                    if (branch != null)
                                                                        branch.setId(data.getKey());
                                                                    branchList.add(branch);
                                                                    db.addBranch(branch, resID, address);
                                                                }

                                                                restaurant.setBranchList(branchList);

                                                                List<Menu> menuList1 = new ArrayList<>();
                                                                mDatabase.child(Node.ThucDonQuanAn).child(resID).addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        for (DataSnapshot menuIDSnap : dataSnapshot.getChildren()) {
                                                                            for (DataSnapshot data : menuIDSnap.getChildren()) {
                                                                                Menu menu = data.getValue(Menu.class);
                                                                                if (menu != null) {
                                                                                    menu.setType(menuIDSnap.getKey());
                                                                                    menu.setMenu_id(data.getKey());
                                                                                }
                                                                                menuList1.add(menu);
                                                                                db.addMenu(menu, resID, address);
                                                                            }
                                                                        }
                                                                        restaurant.setMenuList(menuList1);
                                                                        Common.restaurantList.add(restaurant);
                                                                        db.addRestaurant(restaurant);
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
            }



    private void registerService(){
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        if(gac != null)
            gac.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if(gac != null)
            gac.disconnect();
        super.onStop();
    }

    private class NetworkChangeReceiver extends BroadcastReceiver {
        private Dialog dialog;

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connMgr != null) {
                NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                boolean isConnected = wifi != null && wifi.isConnectedOrConnecting();
                if (!isConnected) {
                    showDialog();
                } else {
                    if (dialog!= null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    checkServiceGG();
                    //setTimer();
                }
            }
        }

        private void showDialog() {
            dialog = new Dialog(WelcomeActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setTitle("");
            dialog.setContentView(R.layout.dialog_network_notify);
            dialog.setCancelable(true);
            dialog.show();

            FrameLayout btnSetting = dialog.findViewById(R.id.btn_setting);
            btnSetting.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
        }
    }

    private void checkServiceGG(){
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            gac.connect();
        }else{
            gac.connect();
        }
    }

    /**
     * Phương thức này dùng để hiển thị trên UI
     * */
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Kiểm tra quyền hạn
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        } else {
            Location location = LocationServices.FusedLocationApi.getLastLocation(gac);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Common.myLocation = location;
                getCityLocation(latitude, longitude);
            } else {
                Toast.makeText(this, "Falied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * Tạo đối tượng google api client
     * */
    protected synchronized void buildGoogleApiClient() {
        if (gac == null) {
            gac = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
        }
    }
    /**
     * Phương thức kiểm chứng google play services trên thiết bị
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1000).show();
            } else {
                Toast.makeText(this, "Thiết bị này không hỗ trợ.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Đã kết nối với google api, lấy vị trí
        getLocation();
    }
    @Override
    public void onConnectionSuspended(int i) {
        gac.connect();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Lỗi kết nối: " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }


    private void getCityLocation(double lat, double lng){
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> address = geoCoder.getFromLocation(lat, lng, 1);
            for (int i=0; i<address.size(); i++) {
                Common.myAddress = address.get(0).getAdminArea();
                List<String>list = new ArrayList<>();
                list.add("quan1");
                if(Common.db.getRestaurant(list, Common.myAddress).size() > 0){
                    Common.db.clearData();
                }
                getRestaurant();
            }
        } catch (IOException e) {
            // Handle IOException
        } catch (NullPointerException e) {
            // Handle NullPointerException
        }
    }
}

