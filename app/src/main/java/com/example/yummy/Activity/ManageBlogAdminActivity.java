package com.example.yummy.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Adapter.NotificationAdapter;
import com.example.yummy.Model.Blog;
import com.example.yummy.R;
import com.example.yummy.Receive.NetworkChangeReceiver;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class ManageBlogAdminActivity extends AppCompatActivity {
    private ImageView imBlog;
    private Blog blog;
    private NotificationAdapter adapter;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_admin_detail);
        initView();
    }

    private void initView(){
        TextView tvType = findViewById(R.id.tv_type);
        tvType.setText(R.string.blog);
        LinearLayout vAccount = findViewById(R.id.v_account);
        vAccount.setVisibility(View.GONE);
        RecyclerView rcv = findViewById(R.id.rcv_manage);
        rcv.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcv.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcv.getContext(), layoutManager.getOrientation());
        rcv.addItemDecoration(dividerItemDecoration);

        LinearLayout btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(v->showDialogAddBlog());

        adapter = new NotificationAdapter(this, Common.blogList, true);
        rcv.setAdapter(adapter);
        ImageView imClose = findViewById(R.id.im_close);
        imClose.setOnClickListener(v->finish());
        registerReceiver();
    }

    private void registerReceiver(){
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(networkChangeReceiver != null)
            unregisterReceiver(networkChangeReceiver);
    }

    private void showDialogAddBlog(){
        Dialog dialog = new Dialog(this);
        dialog.setTitle("");
        dialog.setContentView(R.layout.view_add_notification);
        dialog.show();

        EditText edTitle = dialog.findViewById(R.id.ed_title);
        EditText edDes = dialog.findViewById(R.id.ed_content);
        EditText edUrl = dialog.findViewById(R.id.ed_url);
        LinearLayout btnChoosePhoto = dialog.findViewById(R.id.btn_choosePho);
        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnAdd = dialog.findViewById(R.id.btn_add);
        imBlog = dialog.findViewById(R.id.im_blog);
        blog = new Blog();

        btnChoosePhoto.setOnClickListener(v -> checkPermission());

        btnCancel.setOnClickListener(v->dialog.dismiss());
        btnAdd.setOnClickListener(v->{
            String title = edTitle.getText().toString().trim();
            String des = edDes.getText().toString().trim();
            String url = edUrl.getText().toString().trim();
            Calendar c = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String strDate = sdf.format(c.getTime());

            if(!title.isEmpty() && !url.isEmpty() && !des.isEmpty() && blog.getImage() != null && !blog.getImage().isEmpty()){
                blog.setTitle(title);
                blog.setContent(des);
                blog.setUrl(url);
                blog.setTime(strDate);

                DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
                mData.child(Node.Blog).push().setValue(blog);
                Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
                Common.blogList.add(blog);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }else {
                Toast.makeText(this, R.string.empty_user, Toast.LENGTH_SHORT).show();
            }
        });
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
                        uploadImgBlog(bitmap);
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

    private void uploadImgBlog(Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        long millis = System.currentTimeMillis();
        StorageReference mountainsRef = storageRef.child("blog").child(millis + ".png");

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
                            runOnUiThread(() -> Picasso.get().load(downloadUri).into(imBlog));
                            blog.setImage(downloadUri.toString());
                            Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, R.string.error_change_avatar, Toast.LENGTH_SHORT).show();
                    }
                }));
    }
}
