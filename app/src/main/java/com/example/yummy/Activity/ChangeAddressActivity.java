package com.example.yummy.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yummy.Model.Addresses;
import com.example.yummy.R;
import com.example.yummy.Receive.NetworkChangeReceiver;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class ChangeAddressActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Location location;
    private String address;
    private MapFragment mapFragment;
    private EditText edtSearch;
    private Marker marker;
    private GoogleMap map;
    private NetworkChangeReceiver networkChangeReceiver;
    private int isSave = 0; // 1: nearby

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_address);

        isSave = getIntent().getIntExtra("isSave", 0);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        location = new Location("");
        location.setLatitude(Common.myLocation.getLatitude());
        location.setLongitude(Common.myLocation.getLongitude());

        edtSearch = findViewById(R.id.edt_search_address);
        Button btnSearch = findViewById(R.id.btn_search);

        btnSearch.setOnClickListener(v -> {
            if (!edtSearch.getText().toString().trim().isEmpty()) {
                DataLongOperationAsynchTask asynchTask = new DataLongOperationAsynchTask();
                asynchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                Toast.makeText(this, R.string.empty_user, Toast.LENGTH_SHORT).show();
            }
        });

        ImageView btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> {
            if(isSave == 1){
                Intent intent = new Intent(this, NearByActivity.class);
                startActivity(intent);
            }
            finish();
        });
        registerReceiver();
    }

    private void registerReceiver() {
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (networkChangeReceiver != null)
            unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setMarker(googleMap);
    }

    private void setMarker(GoogleMap googleMap) {
        googleMap.clear();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (marker != null) {
            marker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(address);

        marker = googleMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        googleMap.moveCamera(cameraUpdate);
    }

    @SuppressLint("StaticFieldLeak")
    private class DataLongOperationAsynchTask extends AsyncTask<String, Void, String[]> {
        ProgressDialog dialog = new ProgressDialog(ChangeAddressActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage(getString(R.string.please_wait));
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @SuppressLint("WrongThread")
        @Override
        protected String[] doInBackground(String... params) {
            String response;
            try {
                address = edtSearch.getText().toString().trim();
                address = address.replaceAll("\\s", "+");
                response = getLatLongByURL("https://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false&key=" + getResources().getString(R.string.api_key_map));
                return new String[]{response};
            } catch (Exception e) {
                return new String[]{"error"};
            }
        }

        @Override
        protected void onPostExecute(String... result) {
            try {
                JSONObject jsonObject = new JSONObject(result[0]);
                Addresses addressnew = new Addresses();
                double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

                if (lat != 0 && lng != 0) {
                    addressnew.setLatitude(lat);
                    addressnew.setLongitude(lng);

                    location.setLatitude(lat);
                    location.setLongitude(lng);

                    if (isSave == 0) {
                        String addresse = UtilsBottomBar.getAddressCurrent(getBaseContext(), lat, lng, false);
                        addressnew.setName(addresse);
                        Common.myLocation = addressnew;
                        if (Common.accountCurrent != null) {
                            DatabaseReference nodeRoot = FirebaseDatabase.getInstance().getReference();
                            nodeRoot.child(Node.Address).child(Common.accountCurrent.getUserId()).push().setValue(addressnew);
                        }
                    } else {
                        String addresse = UtilsBottomBar.getAddressCurrent(getBaseContext(), lat, lng, true);
                        addressnew.setName(addresse);
                        Common.nearLocation = addressnew;
                    }
                    setMarker(map);
                    Toast.makeText(ChangeAddressActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChangeAddressActivity.this, R.string.error_change_address, Toast.LENGTH_SHORT).show();
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    if (isSave == 1) {
                        Common.nearLocation = Common.basicAddress;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ChangeAddressActivity.this, R.string.error_change_address, Toast.LENGTH_SHORT).show();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Common.nearLocation = Common.basicAddress;
                UtilsBottomBar.setDistanceAll(true, Common.nearLocation);
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }


    public String getLatLongByURL(String requestURL) {
        URL url;
        StringBuilder response = new StringBuilder();
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            } else {
                response = new StringBuilder();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}
