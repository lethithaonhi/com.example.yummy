package com.example.yummy.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.yummy.Activity.LoginActivity;
import com.example.yummy.Activity.RestaurantManagePartnerActivity;
import com.example.yummy.Adapter.ImgRestaurantDetailAdapter;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class RestaurantPartnerFragment extends Fragment {
    private int TAKE_PHOTO_CODE = 1;
    private ImgRestaurantDetailAdapter adapter;
    private int CHOOSE_PHOTO_CODE = 2;

    public static RestaurantPartnerFragment newInstance() {
        Bundle args = new Bundle();
        RestaurantPartnerFragment fragment = new RestaurantPartnerFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_restaurant_home_partner, container, false);

        LinearLayout vOrder = v.findViewById(R.id.v_order);
        LinearLayout vBranch = v.findViewById(R.id.v_branch);
        LinearLayout vMenu = v.findViewById(R.id.v_menu);
        LinearLayout vImage = v.findViewById(R.id.v_image);

        vOrder.setOnClickListener(vl->{
            Intent intent = new Intent(getContext(), RestaurantManagePartnerActivity.class);
            intent.putExtra("type", 0);
            startActivity(intent);
        });

        vBranch.setOnClickListener(vl->{
            Intent intent = new Intent(getContext(), RestaurantManagePartnerActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
        });

        vMenu.setOnClickListener(vl->{
            Intent intent = new Intent(getContext(), RestaurantManagePartnerActivity.class);
            intent.putExtra("type", 2);
            startActivity(intent);
        });

        vImage.setOnClickListener(vl-> showImgMenu());
        if(Common.accountCurrent != null && Common.accountCurrent.getPartner() != null) {
            UtilsBottomBar.RestaurantPartnerAsyncTask asyncTask = new UtilsBottomBar.RestaurantPartnerAsyncTask(Common.accountCurrent.getPartner().getBoss());
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if(Common.accountCurrent == null || Common.accountCurrent.getPartner() == null) {
            if(LoginActivity.mAuth != null)
                LoginActivity.mAuth.signOut();
            Toast.makeText(getContext(), R.string.login_again, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
            if(getActivity() != null)
                getActivity().finish();
        }

        return v;
    }

    private void showImgMenu() {
        if(getContext() != null) {
            Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setTitle("");
            dialog.setContentView(R.layout.activity_imagedetail_review);
            dialog.show();

            TextView tvName = dialog.findViewById(R.id.tv_name);
            tvName.setText(Common.restaurantPartner.getName());
            TextView tvCountImg = dialog.findViewById(R.id.tv_countImg);
            tvCountImg.setText(Common.restaurantPartner.getImgList().size() + " images");
            ImageView btnAdd = dialog.findViewById(R.id.btn_add);
            btnAdd.setVisibility(View.VISIBLE);
            btnAdd.setOnClickListener(v-> createDialogChangeAvatar());

            RecyclerView rcvImRes = dialog.findViewById(R.id.rcv_image_res);
            rcvImRes.setLayoutManager(new GridLayoutManager(getContext(), 3));
            adapter = new ImgRestaurantDetailAdapter(getContext(), Common.restaurantPartner.getImgList(), Common.restaurantPartner, 1);
            rcvImRes.setAdapter(adapter);

            ImageView btnBack = dialog.findViewById(R.id.btn_back);
            btnBack.setOnClickListener(v -> dialog.dismiss());
        }else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    private void createDialogChangeAvatar() {
        if (getContext() != null) {
            Dialog dialog = new Dialog(getContext());
            dialog.setTitle("");
            dialog.setContentView(R.layout.dialog_change_avatar);
            dialog.show();

            TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
            TextView btnTake = dialog.findViewById(R.id.btn_takePho);
            TextView btnChoose = dialog.findViewById(R.id.btn_choosePho);

            btnTake.setOnClickListener(v->{
                checkPermission(false);
                dialog.dismiss();
            });

            btnChoose.setOnClickListener(v->{
                checkPermission(true);
                dialog.dismiss();
            });

            btnCancel.setOnClickListener(v->dialog.dismiss());
        }
    }

    private void checkPermission(boolean isChoose) {
        if (getActivity() != null && (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA))) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, TAKE_PHOTO_CODE);
        }else {
            if(isChoose) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_PHOTO_CODE);
            }else {
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), TAKE_PHOTO_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == TAKE_PHOTO_CODE && data.getExtras() != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if(bitmap != null)
                    uploadAvatarFB(bitmap);

            } else if (requestCode == CHOOSE_PHOTO_CODE)
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), data.getData());
                    if(bitmap != null)
                        uploadAvatarFB(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private void uploadAvatarFB(Bitmap bitmap){
        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef.child("image_menu").child(bitmap.toString()+".png");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(taskSnapshot -> uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    // Continue with the task to get the download URL
                    return mountainsRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        if(downloadUri != null) {
                            DatabaseReference nodeRoot = FirebaseDatabase.getInstance().getReference();
                            nodeRoot.child(Node.HinhAnhQuanAn).child(Common.restaurantPartner.getRes_id()).push().setValue(downloadUri.toString());
                            Toast.makeText(getContext(), R.string.success, Toast.LENGTH_SHORT).show();
                            Common.restaurantPartner.getImgList().add(downloadUri.toString());
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                }));
    }
}
