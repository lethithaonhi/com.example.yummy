package com.example.yummy.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Activity.AddressHistoryActivity;
import com.example.yummy.Activity.BottomBarActivity;
import com.example.yummy.Activity.HomePartnerActivity;
import com.example.yummy.Activity.InfoUserActivity;
import com.example.yummy.Activity.LoginActivity;
import com.example.yummy.Adapter.HistoryOrderAdapter;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private int TAKE_PHOTO_CODE = 1;
    private int CHOOSE_PHOTO_CODE = 2;
    private ImageView imgAvatar, imAvatarMain;
    private  LinearLayout layoutSetting;

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        if(getContext() != null)

        mContext = getContext();

        Button btnSignIn = v.findViewById(R.id.btn_signin);
        btnSignIn.setOnClickListener(v1 -> startActivity(new Intent(mContext, LoginActivity.class)));
        LinearLayout viewSignOut = v.findViewById(R.id.view_signout);
        LinearLayout viewSetting = v.findViewById(R.id.view_setting);
        LinearLayout viewAddress = v.findViewById(R.id.view_address_setting);
        LinearLayout viewHistory = v.findViewById(R.id.view_history);
        LinearLayout viewManage = v.findViewById(R.id.view_manage);

        if(Common.accountCurrent != null && Common.accountCurrent.getRole() != 2){
                viewSetting.setVisibility(View.GONE);
                viewManage.setVisibility(View.VISIBLE);
        }

        viewManage.setOnClickListener(vl->startActivity(new Intent(getContext(), HomePartnerActivity.class)));
        layoutSetting = v.findViewById(R.id.layout_setting);
        viewSetting.setOnClickListener(vl-> dialogSetting(v));
        viewSignOut.setOnClickListener(vl->createDialogSignOut());
        viewAddress.setOnClickListener(v1 -> {
            if(Common.accountCurrent == null) {
                Toast.makeText(getContext(), R.string.login_first, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }else {
                startActivity(new Intent(getContext(), AddressHistoryActivity.class));
            }
        });

        imAvatarMain = v.findViewById(R.id.im_avatar);
        TextView tvInfoAccount = v.findViewById(R.id.view_infoaccount);
        TextView tvName = v.findViewById(R.id.tv_name);
        if (Common.accountCurrent != null){
            viewSignOut.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.GONE);
            tvName.setText(Common.accountCurrent.getName());
            tvInfoAccount.setText(R.string.info_account);
            if(!Common.accountCurrent.getAvatar().isEmpty()){
                Picasso.get().load(Common.accountCurrent.getAvatar()).into(imAvatarMain);
            }
        }

        v.setOnKeyListener((v12, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if(layoutSetting.getVisibility() == View.VISIBLE){
                        layoutSetting.setVisibility(View.GONE);
                    }else {
                        return true;
                    }
                }
            }
            return false;
        });

        viewHistory.setOnClickListener(v13 -> {
            if(Common.accountCurrent == null && mContext != null) {
                Toast.makeText(mContext, R.string.login_first, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }else {
                createDialogHistory();
            }
        });

        return v;
    }

    private void dialogSetting(View v){
        if(getContext() != null) {
            layoutSetting.setVisibility(View.VISIBLE);

            LinearLayout viewAvatar = v.findViewById(R.id.view_avatar);
            LinearLayout viewInfo = v.findViewById(R.id.view_info);
            LinearLayout viewChangePass = v.findViewById(R.id.view_changepass);
            LinearLayout viewChangLang = v.findViewById(R.id.view_changelang);

            viewChangLang.setOnClickListener(vl-> createDialogLang());

            if(Common.accountCurrent != null) {
                if (Common.accountCurrent.getPassword() != null && !Common.accountCurrent.getPassword().isEmpty()) {
                    viewChangePass.setVisibility(View.VISIBLE);
                }
                 viewAvatar.setVisibility(View.VISIBLE);
                viewInfo.setVisibility(View.VISIBLE);
            }


            viewAvatar.setOnClickListener(vl->{
                if(Common.accountCurrent == null && mContext != null) {
                    Toast.makeText(mContext, R.string.login_first, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }else {
                    createDialogChangeAvatar();
                }
            });

            viewChangePass.setOnClickListener(vl-> {
                if(Common.accountCurrent.getPassword() != null && !Common.accountCurrent.getPassword().isEmpty())
                    createDialogChangePass();
                else {
                    Toast.makeText(mContext, R.string.not_change_pass, Toast.LENGTH_SHORT).show();
                }
            });

            viewInfo.setOnClickListener(vl -> {
                if(Common.accountCurrent != null && mContext != null) {
                    Intent intent = new Intent(mContext, InfoUserActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(mContext, R.string.login_first, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            });

            ImageView imClose = v.findViewById(R.id.im_close);
            imClose.setOnClickListener(vl->layoutSetting.setVisibility(View.GONE));

            imgAvatar = v.findViewById(R.id.img_avatar);
            if(Common.accountCurrent != null && Common.accountCurrent.getAvatar() != null &&  !Common.accountCurrent.getAvatar().isEmpty()){
                Picasso.get().load(Common.accountCurrent.getAvatar()).into(imgAvatar);
            }
        }
    }

    // Initialise it from onAttach()
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void createDialogLang() {
        if (mContext != null) {
            sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("changeLang", Context.MODE_PRIVATE);
            Dialog dialog = new Dialog(mContext);
            dialog.setTitle("");
            dialog.setContentView(R.layout.dialog_changelanguage);
            dialog.show();

            RadioButton radioEng = dialog.findViewById(R.id.radio_english);
            RadioButton radioViet = dialog.findViewById(R.id.radio_vietnamese);
            getSharedLang();

            if (Common.language.equals("en")) {
                radioEng.setChecked(true);
            } else {
                radioViet.setChecked(true);
            }

            TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(v -> dialog.dismiss());

            RadioGroup radioGroup = dialog.findViewById(R.id.radiogroup);
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                Locale locale;
                int checkedRadioId = group.getCheckedRadioButtonId();
                if (checkedRadioId == R.id.radio_english) {
                    Common.language = "en";
                    locale = new Locale("en");
                } else {
                    Common.language = "vi";
                    locale = new Locale("vi");
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("lang", Common.language);
                editor.apply();

                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = locale;
                res.updateConfiguration(conf, dm);

                Intent refresh = new Intent(getContext(), BottomBarActivity.class);
                startActivity(refresh);
                dialog.dismiss();
                if (getActivity() != null)
                    getActivity().finish();
            });
        }
    }

    private void getSharedLang(){
        if(sharedPreferences!= null) {
            Common.language = sharedPreferences.getString("lang", "en");
        }else {
            Common.language = "en";
        }
    }

    private void createDialogChangePass(){
        if (mContext != null) {
            Dialog dialog = new Dialog(mContext,  android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setTitle("");
            dialog.setContentView(R.layout.dialog_change_pass);
            dialog.show();

            EditText edCurrentPass = dialog.findViewById(R.id.ed_curent_pass);
            EditText edNewPass = dialog.findViewById(R.id.ed_new_pass);
            EditText edRePass = dialog.findViewById(R.id.ed_re_pass);
            TextView tvEmail = dialog.findViewById(R.id.tv_email);
            TextView tvSave = dialog.findViewById(R.id.tv_save);
            ImageView imClose = dialog.findViewById(R.id.close_dialog);
            imClose.setOnClickListener(v-> dialog.dismiss());

            if(Common.accountCurrent != null) {
                tvEmail.setText(Common.accountCurrent.getEmail());
            }else{
                Toast.makeText(mContext, R.string.login_first, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
            tvSave.setOnClickListener(v->{
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String newPass= edNewPass.getText().toString().trim();
                String rePass = edRePass.getText().toString().trim();
                String curentPass = edCurrentPass.getText().toString().trim();
                ProgressBar progressBar = dialog.findViewById(R.id.progress_circular);
                progressBar.setIndeterminate(true);
                if(currentUser != null) {
                    if(!newPass.isEmpty() && !rePass.isEmpty() && !curentPass.isEmpty()) {
                        if(!curentPass.equals(Common.accountCurrent.getPassword())){
                            Toast.makeText(mContext, R.string.error_change_pass, Toast.LENGTH_SHORT).show();
                        }else if(!newPass.equals(rePass)){
                            Toast.makeText(mContext, R.string.wrong_pass, Toast.LENGTH_SHORT).show();
                        }else {
                            progressBar.setVisibility(View.VISIBLE);
                            currentUser.updatePassword(newPass).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(mContext, R.string.change_pass_suc, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    Common.accountCurrent.setPassword(newPass);
                                    DatabaseReference nodeRoot = FirebaseDatabase.getInstance().getReference();
                                    nodeRoot.child(Node.user).child(Common.accountCurrent.getUserId()).child("password").setValue(newPass);
                                } else {
                                    Toast.makeText(mContext, R.string.change_pass_fail, Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            });
                        }
                    }else {
                        Toast.makeText(mContext, R.string.empty_user, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(mContext, R.string.login_first, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
            });
        }
    }

    private void createDialogSignOut(){
        if (mContext != null) {
            Dialog dialog = new Dialog(mContext);
            dialog.setTitle("");
            dialog.setContentView(R.layout.dialog_signout);
            dialog.show();

            TextView btnSignOut = dialog.findViewById(R.id.btn_signout);
            TextView btnNo = dialog.findViewById(R.id.btn_no);

            btnSignOut.setOnClickListener(v->{
                FirebaseAuth.getInstance().signOut();
                if(getActivity() != null)
                    getActivity().finish();
                Common.accountCurrent = null;
                startActivity(new Intent(mContext, BottomBarActivity.class));
            });

            btnNo.setOnClickListener(v-> dialog.dismiss());
        }
    }

    private void createDialogChangeAvatar() {
        if (mContext != null) {
            Dialog dialog = new Dialog(mContext);
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
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), data.getData());
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
        StorageReference mountainsRef = storageRef.child("avatar").child(Common.accountCurrent.getUserId()+".png");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> Toast.makeText(mContext, R.string.error_change_avatar, Toast.LENGTH_SHORT).show())
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
                            ((Activity) mContext).runOnUiThread(() -> {
                                Picasso.get().load(downloadUri).into(imgAvatar);
                                Picasso.get().load(downloadUri).into(imAvatarMain);
                            });
                            DatabaseReference nodeRoot = FirebaseDatabase.getInstance().getReference();
                            nodeRoot.child(Node.user).child(Common.accountCurrent.getUserId()).child("avatar").setValue(downloadUri.toString());
                        }
                    } else {
                        Toast.makeText(mContext, R.string.error_change_avatar, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void createDialogHistory() {
        if (mContext != null) {
            Dialog dialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setTitle("");
            dialog.setContentView(R.layout.fragment_history);
            dialog.show();

            LinearLayout viewNoOrder = dialog.findViewById(R.id.view_no_order);
            LinearLayout viewBack = dialog.findViewById(R.id.v_back);
            viewBack.setVisibility(View.VISIBLE);
            ImageView imBack = dialog.findViewById(R.id.im_back);
            RecyclerView rcvOrderList = dialog.findViewById(R.id.rcv_order_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            rcvOrderList.setLayoutManager(layoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvOrderList.getContext(), layoutManager.getOrientation());
            rcvOrderList.addItemDecoration(dividerItemDecoration);

            imBack.setOnClickListener(v-> dialog.dismiss());

            if(Common.orderListCurrent != null && Common.orderListCurrent.size() > 0) {
                viewNoOrder.setVisibility(View.GONE);
                rcvOrderList.setVisibility(View.VISIBLE);
                HistoryOrderAdapter adapter = new HistoryOrderAdapter(getContext(), Common.orderListCurrent, false);
                rcvOrderList.setAdapter(adapter);
            }
        }
    }
}
