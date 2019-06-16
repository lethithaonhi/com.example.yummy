package com.example.yummy.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.yummy.Model.Addresses;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Menu;
import com.example.yummy.Model.Order;
import com.example.yummy.Model.Restaurant;
import com.example.yummy.Model.Review;
import com.example.yummy.R;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.yummy.Activity.WelcomeActivity.gac;

public class UtilsBottomBar {
    public static void startFragment(FragmentManager manager, Fragment fragment) {
        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment).commit();
    }

    public static String convertStringToMoney(int result){
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        return format.format(result);
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public static List<Map<String, String>> getMenuList() {
        List<Map<String, String>> menuList = new ArrayList<>();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(Node.ThucDon).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> hashMap = (Map<String, String>) dataSnapshot.getValue();
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

    public static void getReview(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        for (Restaurant restaurant : Common.restaurantListCurrent){
            List<Review> reviewList = new ArrayList<>();
            mDatabase.child(Node.Review).child(restaurant.getRes_id()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        Review review = dataSnapshot1.getValue(Review.class);
                        if (review != null){
                            review.setId(dataSnapshot1.getKey());
                            review.setId_res(restaurant.getRes_id());
                            reviewList.add(review);
                        }
                    }
                    restaurant.setReviewList(reviewList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public static void getOrderCurrent(){
        if(Common.listResId != null && Common.listResId.size() > 0 && Common.accountCurrent != null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            Common.orderListCurrent = new ArrayList<>();
            for (String resID : Common.listResId) {
                mDatabase.child(Node.Order).child(resID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(Common.orderListCurrent.size() < dataSnapshot.getChildrenCount()) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                Order order = dataSnapshot1.getValue(Order.class);
                                if (order != null && order.getId_user().equals(Common.accountCurrent.getUserId())) {
                                    order.setId(dataSnapshot1.getKey());
                                    order.setId_res(resID);
                                    mDatabase.child(Node.Order_Menu).child(resID).child(order.getId()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataRoor) {
                                            HashMap<Menu, Integer> menuIntegerHashMap = new HashMap<>();
                                            for (DataSnapshot dataSnapshot2 : dataRoor.getChildren()) {
                                                Menu menu = dataSnapshot2.getValue(Menu.class);
                                                if (menu != null && dataSnapshot2.getKey() != null) {
                                                    mDatabase.child(Node.Order_Menu).child(resID).child(order.getId()).child(dataSnapshot2.getKey()).child("count").addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.getValue(Integer.class) != null) {
                                                                int count = dataSnapshot.getValue(Integer.class);
                                                                menuIntegerHashMap.put(menu, count);
                                                                order.setMenuList(menuIntegerHashMap);

                                                                if (menuIntegerHashMap.size() == dataRoor.getChildrenCount() && !isExistOrder(order, Common.orderListCurrent)) {
                                                                    Common.orderListCurrent.add(order);
                                                                }
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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private static boolean isExistOrder(Order order, List<Order> orderList){
        for (Order order1 : orderList){
            if(order1.getTime().equals(order.getTime()) && order.getDate().equals(order1.getDate())){
                return true;
            }
        }
        return false;
    }

    public static void getOrderRes(String resID){
        if(Common.accountCurrent != null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child(Node.Order).child(resID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Common.orderListRes = new ArrayList<>();
                        if(Common.orderListRes.size() < dataSnapshot.getChildrenCount()) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                Order order = dataSnapshot1.getValue(Order.class);
                                if (order != null) {
                                    order.setId(dataSnapshot1.getKey());
                                    order.setId_res(resID);
                                    mDatabase.child(Node.Order_Menu).child(resID).child(order.getId()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataRoor) {
                                            HashMap<Menu, Integer> menuIntegerHashMap = new HashMap<>();
                                            for (DataSnapshot dataSnapshot2 : dataRoor.getChildren()) {
                                                Menu menu = dataSnapshot2.getValue(Menu.class);
                                                if (menu != null && dataSnapshot2.getKey() != null) {
                                                    mDatabase.child(Node.Order_Menu).child(resID).child(order.getId()).child(dataSnapshot2.getKey()).child("count").addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.getValue(Integer.class) != null) {
                                                                int count = dataSnapshot.getValue(Integer.class);
                                                                menuIntegerHashMap.put(menu, count);
                                                                order.setMenuList(menuIntegerHashMap);

                                                                if (menuIntegerHashMap.size() == dataRoor.getChildrenCount() && !isExistOrder(order, Common.orderListRes)) {
                                                                    Common.orderListRes.add(order);
                                                                }
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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        }
    }

    private void getRestaurantPartner(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(Common.accountCurrent.getPartner().getBoss()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                if(restaurant != null ){
                    restaurant.setRes_id(dataSnapshot.getKey());
                    List<Branch> branchList = new ArrayList<>();
                    mDatabase.child(Node.Branch).child(restaurant.getRes_id()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Branch branch = data.getValue(Branch.class);
                                if (branch != null) {
                                    branch.setId(data.getKey());
                                    branchList.add(branch);
                                }
                            }

                            restaurant.setBranchList(branchList);

                            //getMenu(resID, address, restaurant, dataSnapshotRoot);
                            List<Menu> menuList1 = new ArrayList<>();
                            mDatabase.child(Node.ThucDonQuanAn).child(restaurant.getRes_id()).addValueEventListener(new ValueEventListener() {
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
                                        }
                                    }
                                    restaurant.setMenuList(menuList1);
                                    Common.restaurantPartner = restaurant;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("databaseError FireBase", databaseError.getDetails());
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("databaseError FireBase", databaseError.getDetails());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    public static class RestaurantPartnerAsyncTask extends AsyncTask<Void, Void, Void> {
        private String resID;
        public RestaurantPartnerAsyncTask (String resId){
            this.resID = resId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Common.restaurantPartner = new Restaurant();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(Common.db != null) {
                Common.restaurantPartner = Common.db.getRestaurantPartner(resID, Common.myAddress);
                if(Common.restaurantPartner != null && Common.restaurantPartner.getBranchList() != null) {
                    for (Branch branch : Common.restaurantPartner.getBranchList()) {
                        branch.setDistance(UtilsBottomBar.getDistanceBranch(branch));
                    }
                }
                Common.menuList = UtilsBottomBar.getMenuList();
            }
            return null;
        }
    }

    public static void getOrderListAll() {
        if (Common.listResId != null && Common.listResId.size() > 0 && Common.accountCurrent != null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            for (String resID : Common.listResId) {
                mDatabase.child(Node.Order).child(resID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Common.orderListAll = new ArrayList<>();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Order order = dataSnapshot1.getValue(Order.class);
                            if (order != null) {
                                order.setId(dataSnapshot1.getKey());
                                order.setId_res(resID);
                                mDatabase.child(Node.Order_Menu).child(resID).child(order.getId()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataRoor) {
                                        HashMap<Menu, Integer> menuIntegerHashMap = new HashMap<>();
                                        for (DataSnapshot dataSnapshot2 : dataRoor.getChildren()) {
                                            Menu menu = dataSnapshot2.getValue(Menu.class);
                                            if (menu != null && dataSnapshot2.getKey() != null) {
                                                mDatabase.child(Node.Order_Menu).child(resID).child(order.getId()).child(dataSnapshot2.getKey()).child("count").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.getValue(Integer.class) != null) {
                                                            int count = dataSnapshot.getValue(Integer.class);
                                                            menuIntegerHashMap.put(menu, count);
                                                            order.setMenuList(menuIntegerHashMap);

                                                            if (menuIntegerHashMap.size() == dataRoor.getChildrenCount() && !isExistOrder(order, Common.orderListAll)) {
                                                                Common.orderListAll.add(order);
                                                            }
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    public static void showSuccessView(Context context, String mess, boolean isFinish){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_success);
        dialog.show();

        TextView tvMess = dialog.findViewById(R.id.tv_mess);
        tvMess.setText(mess);
        Button btOkay = dialog.findViewById(R.id.btn_ok);
        btOkay.setOnClickListener(v->{
            dialog.dismiss();
            if(isFinish){
                ((Activity)context).finish();
            }
        });

       if(isFinish){
           Handler handler = new Handler();
           handler.postDelayed(((Activity) context)::finish, 5000);
       }
    }
}
