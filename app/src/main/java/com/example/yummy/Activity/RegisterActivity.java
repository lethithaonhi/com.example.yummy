package com.example.yummy.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Model.Account;
import com.example.yummy.Model.User;
import com.example.yummy.R;
import com.example.yummy.Receive.NetworkChangeReceiver;
import com.example.yummy.Utils.Node;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity  implements View.OnClickListener{
    private EditText edtUsername, edtNewPass, edtConfirm;
    private FirebaseAuth mAuth;
    private LinearLayout viewRoot;
    private int gender = 1;
    private boolean isPhone = false;
    private boolean isShowPass = false;
    private boolean isShowPass1 = false;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        initView();
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
    public void initView() {
        edtUsername = findViewById(R.id.edt_username);
        edtNewPass = findViewById(R.id.edt_pass);
        edtConfirm = findViewById(R.id.edt_passconfirm);
        TextView txtTitle = findViewById(R.id.txt_title);
        Button btnSignUp = findViewById(R.id.btn_register);
        btnSignUp.setOnClickListener(this);
        viewRoot = findViewById(R.id.view_root);
        viewRoot.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = viewRoot.getRootView().getHeight() - viewRoot.getHeight();
            if (heightDiff > UtilsBottomBar.dpToPx(RegisterActivity.this, 20)) { // if more than 200 dp, it's probably a keyboard...
                txtTitle.setVisibility(View.GONE);
            } else {
                txtTitle.setVisibility(View.VISIBLE);
            }
        });

        ImageButton btnShowPass = findViewById(R.id.btn_password_show1);
        btnShowPass.setOnClickListener(v -> {
            isShowPass = !isShowPass;
            if (!isShowPass) {
                edtNewPass.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                edtNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }

            btnShowPass.setImageResource(isShowPass ? R.drawable.ic_remove_red_eye : R.drawable.ic_hide_pass);
        });

        ImageButton btnShowPass1 = findViewById(R.id.btn_password_show);
        btnShowPass1.setOnClickListener(v -> {
            isShowPass1 = !isShowPass1;
            if (!isShowPass1) {
                edtConfirm.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                edtConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }

            btnShowPass1.setImageResource(isShowPass1 ? R.drawable.ic_remove_red_eye : R.drawable.ic_hide_pass);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        String email = edtUsername.getText().toString().trim();
        String password = edtNewPass.getText().toString().trim();
        String passconfirm = edtConfirm.getText().toString().trim();

        if(email.length() == 0 || password.length() == 0 || passconfirm.length() == 0){
            Toast.makeText(this,R.string.empty_user,Toast.LENGTH_SHORT).show();
        }else if(password.trim().length() < 6){
            Toast.makeText(this,R.string.request_pass,Toast.LENGTH_SHORT).show();
        }else if(!passconfirm.equals(password)){
            Toast.makeText(this,R.string.wrong_pass,Toast.LENGTH_SHORT).show();
        }else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("notify_register", "createUserWithEmail:success");
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if(currentUser!= null) {
                                String uid = currentUser.getUid();

                                Account account = new Account();
                                account.setUserId(uid);
                                account.setPassword(password);
                                account.setEmail(email);
                                account.setUsername(email);
                                account.setRole(2);

                                showDialogInfor(account);

                            }else{
                                Toast.makeText(RegisterActivity.this, R.string.failed_register,
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("notify_register", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, R.string.failed_register,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }

    }

    private void updateUI(){
        Toast.makeText(this, R.string.success_register, Toast.LENGTH_LONG).show();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    private void showDialogInfor(Account account){
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setTitle("");
        dialog.setContentView(R.layout.view_enter_user_profile);
        dialog.setCancelable(false);
        dialog.show();

        EditText edName = dialog.findViewById(R.id.ed_nameinfo);
        EditText edEmail = dialog.findViewById(R.id.ed_email);
        if(account.getEmail() != null && !account.getEmail().isEmpty()) {
            edEmail.setText(account.getEmail());
            edEmail.setEnabled(false);
        }
        TextView tvDateBirth = dialog.findViewById(R.id.tv_birth);
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        tvDateBirth.setText(dateformat.format(c.getTime()));
        tvDateBirth.setOnClickListener(v->showDatePickerDialog(tvDateBirth));

        EditText edPhone = dialog.findViewById(R.id.ed_phone);
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGrp);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        LinearLayout vTitle = dialog.findViewById(R.id.v_title);
        ImageButton imError = dialog.findViewById(R.id.btn_error);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int checkedRadioId = group.getCheckedRadioButtonId();

            if(checkedRadioId== R.id.radio_male) {
                gender = 1;
            } else if(checkedRadioId== R.id.radio_female ) {
                gender = 2;
            } else if(checkedRadioId== R.id.radio_none) {
                gender = 3;
            }
        });

        edPhone.setOnFocusChangeListener((v, hasFocus) -> {
            String phone = edPhone.getText().toString().trim();
            if(!hasFocus) {
                vTitle.setVisibility(View.VISIBLE);
                if (phone.length() > 0 && phone.length() < 11 && phone.charAt(0) == '0') {
                    imError.setImageResource(R.drawable.ic_check_circle_24dp);
                    isPhone = true;
                } else {
                    imError.setImageResource(R.drawable.ic_error_red_24dp);
                    Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                    isPhone = false;
                }
            }else {
                vTitle.setVisibility(View.GONE);
            }
        });

        btnSave.setOnClickListener(v->{
            String name = edName.getText().toString().trim();
            String date = tvDateBirth.getText().toString().trim();
            String phone = edPhone.getText().toString().trim();

            if (phone.length() > 0 && phone.length() < 11 && phone.charAt(0) == '0') {
                imError.setImageResource(R.drawable.ic_check_circle_24dp);
                isPhone = true;
            } else {
                imError.setImageResource(R.drawable.ic_error_red_24dp);
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                isPhone = false;
            }

            if(!name.isEmpty() && !date.isEmpty() && isPhone){
                account.setName(name);
                account.setPhone(phone);
                account.setDatebirth(date);
                account.setGender(gender);
                account.setAvatar("");

                DatabaseReference dataNode = FirebaseDatabase.getInstance().getReference();
                dataNode.child(Node.user).child(account.getUserId()).setValue(account);

                Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
                updateUI();
            }else {
                Toast.makeText(this, R.string.empty_user, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog(TextView tvBirth) {
        @SuppressLint("SetTextI18n") DatePickerDialog.OnDateSetListener callback= (view, year, monthOfYear, dayOfMonth) -> tvBirth.setText((dayOfMonth) +"/"+(monthOfYear+1)+"/"+year);

        String s=tvBirth.getText()+"";
        String[] strArrtmp = s.split("/");
        int ngay=Integer.parseInt(strArrtmp[0]);
        int thang=Integer.parseInt(strArrtmp[1])-1;
        int nam=Integer.parseInt(strArrtmp[2]);

        DatePickerDialog pic=new DatePickerDialog(
                RegisterActivity.this,
                callback, nam, thang, ngay);
        pic.setTitle(R.string.address_user);
        pic.show();
    }
}
