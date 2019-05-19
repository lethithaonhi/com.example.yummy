package com.example.yummy.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.yummy.Model.Addresses;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.R;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.example.yummy.Activity.WelcomeActivity.gac;

public class UtilsBottomBar {
    public static void startFragment(FragmentManager manager, Fragment fragment) {
        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment).commit();
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public static List<Map> getMenuList() {
        List<Map> menuList = new ArrayList<>();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(Node.ThucDon).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> hashMap = (Map<String, Object>) dataSnapshot.getValue();
                menuList.add(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return menuList;
    }

    public static float getDistanceBranch(Branch branch) {
        float[] distance = new float[1];
        Location.distanceBetween(branch.getLatitude(), branch.getLongitude(),
                Common.myLocation.getLatitude(), Common.myLocation.getLongitude(), distance);
        Common.db.updateBranch(distance[0], branch.getId_db());
        return distance[0];
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getAddressCurrent(Context context, double latitude, double longitude) {
        List<Address> addresses;
        String address = "";
        if (latitude == 0 || longitude == 0) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location location = LocationServices.FusedLocationApi.getLastLocation(gac);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Addresses address1 = new Addresses();
                address1.setLatitude(latitude);
                address1.setLongitude(longitude);
                Common.myLocation = address1;

                for (Restaurant restaurant : Common.restaurantListCurrent){
                    for (Branch branch : restaurant.getBranchList()){
                        branch.setDistance(getDistanceBranch(branch));
                    }
                }
            }
        }
        Geocoder geocoder;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            String knownName = addresses.get(0).getAdminArea();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
}
