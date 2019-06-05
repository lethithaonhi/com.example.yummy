package com.example.yummy.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.example.yummy.Adapter.BranchAdapter;
import com.example.yummy.Adapter.HistoryMenuAdapter;
import com.example.yummy.Adapter.MenuPartnerAdapter;
import com.example.yummy.Model.Addresses;
import com.example.yummy.Model.Branch;
import com.example.yummy.Model.Discounts;
import com.example.yummy.Model.Menu;
import com.example.yummy.Model.Order;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class RestaurantManagePartnerActivity extends AppCompatActivity {
    private int type;
    private String typeMenu;
    private ImageView imgMenu, imBranch, imCheck;
    private Menu menu;
    private EditText edName, edType, edAddress;
    private Discounts discounts;
    private TextView tvDiscount, tvFreeship;
    private String address;
    private Location location;
    private Branch branch;
    private BranchAdapter branchAdapter;
    private HistoryMenuAdapter historyMenuAdapter;
    private List<Order> dataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_manage);

        type = getIntent().getIntExtra("type", 0);
        dataList = new ArrayList<>();
        initView();
    }

    private void initView() {
        TextView tvType = findViewById(R.id.tv_type);
        LinearLayout imAdd = findViewById(R.id.btn_add);
        ImageView imClose = findViewById(R.id.im_close);
        RecyclerView rcv = findViewById(R.id.rcv_partner);
        LinearLayout vBranch = findViewById(R.id.v_branch);
        imClose.setOnClickListener(v -> finish());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcv.getContext(), layoutManager.getOrientation());
        rcv.addItemDecoration(dividerItemDecoration);

        if (Common.restaurantListCurrent.size() == 0) {
            UtilsBottomBar.RestaurantPartnerAsyncTask asyncTask = new UtilsBottomBar.RestaurantPartnerAsyncTask("quan1");
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        String name;
        if (type == 0) {
            name = getResources().getString(R.string.order);

            List<Order> dataList = getOrder();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            Collections.sort(dataList, (obj1, obj2) -> {
                try {
                    return dateFormat.parse(obj1.getDate()+" "+obj1.getTime()).compareTo(dateFormat.parse(obj2.getTime()+" "+obj2.getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return -1;
            });

            Collections.reverse(dataList);
            imAdd.setVisibility(View.VISIBLE);
            historyMenuAdapter = new HistoryMenuAdapter(this,  dataList, true);
            rcv.setAdapter(historyMenuAdapter);
        } else if (type == 1) {
            vBranch.setVisibility(View.VISIBLE);
            name = getResources().getString(R.string.restaurant);
            branchAdapter = new BranchAdapter(this, Common.restaurantListCurrent.get(0).getBranchList());
            rcv.setAdapter(branchAdapter);

            tvDiscount = findViewById(R.id.tv_discount);
            tvFreeship = findViewById(R.id.tv_freeship);
            setDiscounts();
            ImageView imEdit = findViewById(R.id.im_edit);
            imEdit.setOnClickListener(v -> showEditDiscount());

        } else {
            name = getResources().getString(R.string.menu);
            rcv.setItemAnimator(new FadeInLeftAnimator());
            MenuPartnerAdapter adapter = new MenuPartnerAdapter(this, Common.restaurantListCurrent.get(0).getMenuList());
            adapter.setMode(Attributes.Mode.Single);
            rcv.setAdapter(adapter);
        }

        tvType.setText(name);
        imAdd.setOnClickListener(v -> {
            if (type == 1) {
                showAddBranch();
            } else {
                showAddMenu();
            }
        });
    }

    private List<Order> getOrder(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(Node.Order).child(Common.accountCurrent.getPartner().getBoss()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataList.size() > 0)
                    dataList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Order order = dataSnapshot1.getValue(Order.class);
                    if (order != null) {
                        order.setId(dataSnapshot1.getKey());
                        HashMap<Menu, Integer> menuList = new HashMap<>();
                        mDatabase.child(Node.Order_Menu).child(Common.accountCurrent.getPartner().getBoss()).child(order.getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataRoot) {
                                for (DataSnapshot dataSnapshot2 : dataRoot.getChildren()) {
                                    Menu menu = dataSnapshot2.getValue(Menu.class);
                                    if (menu != null && dataSnapshot2.getKey()!= null) {
                                        mDatabase.child(Node.Order_Menu).child(Common.accountCurrent.getPartner().getBoss()).child(order.getId()).child(dataSnapshot2.getKey()).child(Node.count).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                int count = dataSnapshot.getValue(Integer.class);
                                                menuList.put(menu, count);
                                                order.setMenuList(menuList);
                                                if (menuList.size() == dataRoot.getChildrenCount() && !dataList.contains(order)) {
                                                    dataList.add(order);
                                                    historyMenuAdapter.notifyDataSetChanged();
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
        return  dataList;
    }

    private void setDiscounts() {
        String discount = "Discount: " + Common.restaurantListCurrent.get(0).getDiscounts().getDiscount() + "% - Code: " + Common.restaurantListCurrent.get(0).getDiscounts().getCode() +
                "\nMin order: " + Common.restaurantListCurrent.get(0).getDiscounts().getMin_order() + "VND - Max discount: " + Common.restaurantListCurrent.get(0).getDiscounts().getMax_discount() + "VND";
        tvDiscount.setText(discount);

        String freeship = "";
        if (Common.restaurantListCurrent.get(0).getFreeship() == 0)
            freeship = "FreeShip under 2km, only 15000VND for 2km - 5km\n";
        freeship = freeship + "Verify: " + Common.restaurantListCurrent.get(0).getFreeship() + "VND/Km";
        tvFreeship.setText(freeship);
    }

    private void showAddMenu() {
        Dialog dialog = new Dialog(this);
        dialog.setTitle("");
        dialog.setContentView(R.layout.view_add_menu_part);
        dialog.show();

        edName = dialog.findViewById(R.id.ed_name);
        EditText edPrice = dialog.findViewById(R.id.ed_price);
        EditText edDes = dialog.findViewById(R.id.ed_des);
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnAdd = dialog.findViewById(R.id.btn_edit);
        Spinner spnMenu = dialog.findViewById(R.id.spn_menu);
        edType = dialog.findViewById(R.id.ed_type);
        LinearLayout btnChoosePhoto = dialog.findViewById(R.id.btn_choosePho);
        imgMenu = dialog.findViewById(R.id.im_menu_add);
        List<String> typeList = getTypeMenu();
        typeList.add("+ Other");
        menu = new Menu();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, typeList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spnMenu.setAdapter(adapter);
        spnMenu.setSelection(0);

        spnMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos < typeList.size() - 1) {
                    edType.setVisibility(View.GONE);
                    typeMenu = spnMenu.getSelectedItem().toString();
                } else {
                    edType.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnChoosePhoto.setOnClickListener(v -> checkPermission());

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnAdd.setOnClickListener(v -> {
            String name = edName.getText().toString().trim();
            String prices = edPrice.getText().toString().trim();
            String des = edDes.getText().toString().trim();

            if (!name.isEmpty() && !prices.isEmpty() && !typeMenu.isEmpty() && menu.getImage() != null) {
                menu.setName(name);
                menu.setPrices(Integer.parseInt(prices));
                menu.setDescribe(des);
                menu.setIsDelete(0);
                menu.setType(typeMenu);

                DatabaseReference nodeRoot = FirebaseDatabase.getInstance().getReference();
                String key = nodeRoot.child(Node.ThucDonQuanAn).child(Common.restaurantListCurrent.get(0).getRes_id()).child(menu.getType()).push().getKey();
                nodeRoot.child(Node.ThucDonQuanAn).child(Common.restaurantListCurrent.get(0).getRes_id()).child(menu.getType()).push().setValue(menu);
                menu.setMenu_id(key);
                Common.restaurantListCurrent.get(0).getMenuList().add(menu);
                Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            } else {
                Toast.makeText(this, R.string.empty_user, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> getTypeMenu() {
        List<String> typeList = new ArrayList<>();
        for (Menu menu : Common.restaurantListCurrent.get(0).getMenuList()) {
            if (!typeList.contains(menu.getType())) {
                typeList.add(menu.getType());
            }
        }

        return typeList;
    }

    private void checkPermission() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

            } else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == 1) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    if (bitmap != null)
                        if (type == 2)
                            uploadImgMenu(bitmap);
                        else if (type == 1) {
                            uploadImgBranch(bitmap);
                        }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        }
    }

    private void uploadImgMenu(Bitmap bitmap) {
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        if (typeMenu.equals("+ Other")) {
            typeMenu = edType.getText().toString().trim();
        }

        if (!edName.getText().toString().trim().isEmpty() && !typeMenu.isEmpty()) {
            StorageReference mountainsRef = storageRef.child("menu").child(edName.getText().toString().trim() + "_" + typeMenu + ".png");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnFailureListener(exception -> Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> uploadTask.continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }

                        // Continue with the task to get the download URL
                        return mountainsRef.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            if (downloadUri != null) {
                                runOnUiThread(() -> Picasso.get().load(downloadUri).into(imgMenu));
                                menu.setImage(downloadUri.toString());
                                Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, R.string.error_change_avatar, Toast.LENGTH_SHORT).show();
                        }
                    }));
        } else {
            Toast.makeText(this, R.string.img_first, Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditDiscount() {
        Dialog dialog = new Dialog(this);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_edit_discount);
        dialog.show();

        EditText edDiscount = dialog.findViewById(R.id.ed_discount);
        EditText edCode = dialog.findViewById(R.id.ed_code);
        EditText edMax = dialog.findViewById(R.id.ed_max);
        EditText edMin = dialog.findViewById(R.id.ed_min);
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnAdd = dialog.findViewById(R.id.btn_edit);

        discounts = Common.restaurantListCurrent.get(0).getDiscounts();
        edCode.setText(discounts.getCode());
        edDiscount.setText(discounts.getDiscount() + "");
        edMax.setText(discounts.getMax_discount() + "");
        edMin.setText(discounts.getMin_order() + "");

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnAdd.setOnClickListener(v -> {
            String dis = edDiscount.getText().toString().trim();
            String code = edCode.getText().toString().trim();
            String max = edMax.getText().toString().trim();
            String min = edMin.getText().toString().trim();

            if (!dis.isEmpty() && !code.isEmpty() && !max.isEmpty() && !min.isEmpty()) {
                discounts = new Discounts();
                discounts.setDiscount(Integer.parseInt(dis));
                discounts.setCode(code);
                discounts.setMax_discount(Integer.parseInt(max));
                discounts.setMin_order(Integer.parseInt(min));

                DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
                mData.child(Node.KhuyenMai).child(Common.restaurantListCurrent.get(0).getRes_id()).setValue(discounts);
                Common.restaurantListCurrent.get(0).setDiscounts(discounts);
                Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
                setDiscounts();
                dialog.dismiss();
            } else {
                Toast.makeText(this, R.string.empty_user, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddBranch() {
        Dialog dialog = new Dialog(this);
        dialog.setTitle("");
        dialog.setContentView(R.layout.view_add_branch_cus);
        dialog.show();

        edAddress = dialog.findViewById(R.id.ed_address);
        imCheck = dialog.findViewById(R.id.im_check);
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnAdd = dialog.findViewById(R.id.btn_edit);
        LinearLayout btnChoosePhoto = dialog.findViewById(R.id.btn_choosePho);
        imBranch = dialog.findViewById(R.id.im_branch_add);
        branch = new Branch();
        location = new Location("");
        address = "";
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        imCheck.setOnClickListener(v -> {
            address = edAddress.getText().toString().trim();
            if (!address.isEmpty()) {
                DataLongOperationAsynchTask asynchTask = new DataLongOperationAsynchTask();
                asynchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                Toast.makeText(this, R.string.empty_user, Toast.LENGTH_SHORT).show();
            }
        });

        btnAdd.setOnClickListener(v -> {
            address = edAddress.getText().toString().trim();
            if(!address.isEmpty() && location != null && location.getLatitude() != 0 && location.getLongitude() != 0 && !branch.getAvatar().isEmpty()){
                branch.setAddress(address);
                branch.setLatitude(location.getLatitude());
                branch.setLongitude(location.getLongitude());

                DatabaseReference nodeRoot = FirebaseDatabase.getInstance().getReference();
                String key = nodeRoot.child(Node.Branch).child(Common.restaurantListCurrent.get(0).getRes_id()).push().getKey();
                if(key != null) {
                    nodeRoot.child(Node.Branch).child(Common.restaurantListCurrent.get(0).getRes_id()).push().setValue(menu);
                    branch.setId(key);
                    Common.restaurantListCurrent.get(0).getBranchList().add(branch);
                    Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
                    branchAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }else {
                    Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, R.string.empty_user, Toast.LENGTH_SHORT).show();
            }
        });

        btnChoosePhoto.setOnClickListener(v -> checkPermission());
    }

    @SuppressLint("StaticFieldLeak")
    private class DataLongOperationAsynchTask extends AsyncTask<String, Void, String[]> {
        ProgressDialog dialog = new ProgressDialog(RestaurantManagePartnerActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String response;
            try {
                address = address.replaceAll("\\s", "+");
                response = getLatLongByURL("https://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false&key=" + getResources().getString(R.string.Your_API_KEY));
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
                    String addresse = UtilsBottomBar.getAddressCurrent(getBaseContext(), lat, lng);
                    addressnew.setName(addresse);

                    location.setLatitude(lat);
                    location.setLongitude(lng);
                    imCheck.setImageResource(R.drawable.ic_check_circle_24dp);

                } else {
                    Toast.makeText(RestaurantManagePartnerActivity.this, R.string.error_change_address, Toast.LENGTH_SHORT).show();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                imCheck.setImageResource(R.drawable.click);
                Toast.makeText(RestaurantManagePartnerActivity.this, R.string.error_change_address, Toast.LENGTH_SHORT).show();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
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

    private void uploadImgBranch(Bitmap bitmap) {
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        long time= System.currentTimeMillis();
        StorageReference mountainsRef = storageRef.child("branch").child(time + ".png");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(taskSnapshot -> uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    // Continue with the task to get the download URL
                    return mountainsRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        if (downloadUri != null) {
                            runOnUiThread(() -> Picasso.get().load(downloadUri).into(imBranch));
                            branch.setAvatar(downloadUri.toString());
                        }
                    } else {
                        Toast.makeText(this, R.string.error_change_avatar, Toast.LENGTH_SHORT).show();
                    }
                }));
    }
}