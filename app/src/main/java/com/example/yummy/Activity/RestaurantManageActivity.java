package com.example.yummy.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.example.yummy.Adapter.MenuPartnerAdapter;
import com.example.yummy.Model.Account;
import com.example.yummy.Model.Menu;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class RestaurantManageActivity extends AppCompatActivity {
    private int type;
    private String typeMenu;
    private ImageView imgMenu;
    private Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_manage);

        type = getIntent().getIntExtra("type", 0);
        initView();
    }

    private void initView() {
        TextView tvType = findViewById(R.id.tv_type);
        ImageView imAdd = findViewById(R.id.btn_add);
        RecyclerView rcv = findViewById(R.id.rcv_partner);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcv.getContext(), layoutManager.getOrientation());
        rcv.addItemDecoration(dividerItemDecoration);

        String name;
        if (type == 0) {
            name = getResources().getString(R.string.order);
        } else if (type == 1) {
            name = getResources().getString(R.string.branch);
        } else if (type == 2) {
            if (Common.restaurantListCurrent.size() == 0) {
                UtilsBottomBar.RestaurantPartnerAsyncTask asyncTask = new UtilsBottomBar.RestaurantPartnerAsyncTask("quan1");
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            name = getResources().getString(R.string.menu);
            rcv.setItemAnimator(new FadeInLeftAnimator());
            MenuPartnerAdapter adapter = new MenuPartnerAdapter(this, Common.restaurantListCurrent.get(0).getMenuList());
            adapter.setMode(Attributes.Mode.Single);
            rcv.setAdapter(adapter);
        } else {
            name = getResources().getString(R.string.image);
        }
        tvType.setText(name);
        imAdd.setOnClickListener(v -> {
            if (type == 2) {
                showAddMenu();
            }
        });
    }

    private void showAddMenu() {
        Dialog dialog = new Dialog(this);
        dialog.setTitle("");
        dialog.setContentView(R.layout.view_add_menu_part);
        dialog.show();

        EditText edName = dialog.findViewById(R.id.ed_name);
        EditText edPrice = dialog.findViewById(R.id.ed_price);
        EditText edDes = dialog.findViewById(R.id.ed_des);
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnEdit = dialog.findViewById(R.id.btn_edit);
        Spinner spnMenu = dialog.findViewById(R.id.spn_menu);
        EditText edType = dialog.findViewById(R.id.ed_type);
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
                    Toast.makeText(RestaurantManageActivity.this, spnMenu.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
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
        btnEdit.setOnClickListener(v -> {
            String name = edName.getText().toString().trim();
            String prices = edPrice.getText().toString().trim();
            String des = edDes.getText().toString().trim();
            if (edType.getVisibility() == View.VISIBLE)
                typeMenu = edType.getText().toString().trim();

            if (!name.isEmpty() && !prices.isEmpty() && !typeMenu.isEmpty() && menu.getImage() != null) {
                menu.setName(name);
                menu.setPrices(Integer.parseInt(prices));
                menu.setDescribe(des);
                menu.setIsDelete(0);
                menu.setType(typeMenu);

                DatabaseReference nodeRoot = FirebaseDatabase.getInstance().getReference();
                nodeRoot.child(Node.ThucDonQuanAn).child(Common.restaurantListCurrent.get(0).getRes_id()).child(menu.getType()).push().setValue(menu);
                Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
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
                        uploadImgMenu(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(requestCode == 2){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        }
    }

    private void uploadImgMenu(Bitmap bitmap){
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        if(menu.getType() != null || menu.getName() != null) {
            StorageReference mountainsRef = storageRef.child("menu").child(menu.getType() + "_" + menu.getName() + ".png");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnFailureListener(exception -> Toast.makeText(this, R.string.error_change_avatar, Toast.LENGTH_SHORT).show())
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
                            }
                        } else {
                            Toast.makeText(this, R.string.error_change_avatar, Toast.LENGTH_SHORT).show();
                        }
                    }));
        }else {
            Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
        }
    }
}