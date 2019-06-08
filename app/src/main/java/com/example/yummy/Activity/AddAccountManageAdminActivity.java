package com.example.yummy.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Model.Account;
import com.example.yummy.Model.Partner;
import com.example.yummy.R;
import com.example.yummy.Utils.Node;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddAccountManageAdminActivity extends AppCompatActivity {
    private boolean isShowPass = false, isShowPass1 = false;
    private int gender=0;
    private boolean isPhone = false;
    private EditText edtUsername;
    private EditText edtNewPass;
    private EditText edtConfirm;
    private EditText edName;
    private EditText edPhone;
    private TextView tvDateBirth;
    private EditText edCMND, edBank, edSTK;
    private FirebaseAuth mAuth;
    private int role=0;
    private ImageButton imError;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_register_account_admin);
        mAuth = FirebaseAuth.getInstance();
        initView();
    }

    private void initView(){
        LinearLayout vPartner = findViewById(R.id.v_partner);
        NestedScrollView vProfile = findViewById(R.id.v_profile);
        ImageView imClose = findViewById(R.id.im_close);
        List<String> list = new ArrayList<>();
        list.add(this.getResources().getString(R.string.none));
        list.add(this.getResources().getString(R.string.admin));
        list.add(this.getResources().getString(R.string.customer));
        list.add(this.getResources().getString(R.string.partner));
        Spinner spinner = findViewById(R.id.sn_role);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.item_spinner,list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0) {
                    role = i;
                    vProfile.setVisibility(View.VISIBLE);
                    if(i == 3){
                        vPartner.setVisibility(View.VISIBLE);
                    }else {
                        vPartner.setVisibility(View.GONE);
                    }
                }else {
                    vProfile.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        edtUsername = findViewById(R.id.edt_username);
        edtNewPass = findViewById(R.id.edt_pass);
        edtConfirm = findViewById(R.id.edt_passconfirm);
        edName = findViewById(R.id.ed_nameinfo);
        LinearLayout edEmail = findViewById(R.id.v_email);
        edEmail.setVisibility(View.GONE);
        tvDateBirth = findViewById(R.id.tv_birth);
        edCMND = findViewById(R.id.ed_cmnd);
        edBank = findViewById(R.id.ed_bank);
        edSTK = findViewById(R.id.ed_accountnum);

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

        imClose.setOnClickListener(v->finish());
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        tvDateBirth.setText(dateformat.format(c.getTime()));
        tvDateBirth.setOnClickListener(v->showDatePickerDialog(tvDateBirth));

        edPhone = findViewById(R.id.ed_phone);
        RadioGroup radioGroup = findViewById(R.id.radioGrp);
        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setText(R.string.create_user);
        LinearLayout vTitle = findViewById(R.id.v_title);
        imError = findViewById(R.id.btn_error);

        btnSave.setOnClickListener(v->registerAccount());

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
    }

    private void registerAccount(){
        String email = edtUsername.getText().toString().trim();
        String password = edtNewPass.getText().toString().trim();
        String passconfirm = edtConfirm.getText().toString().trim();

        if(email.length() == 0 || password.length() == 0 || passconfirm.length() == 0){
            Toast.makeText(this,R.string.empty_user,Toast.LENGTH_SHORT).show();
        }else if(password.trim().length() < 6){
            Toast.makeText(this,R.string.request_pass,Toast.LENGTH_SHORT).show();
        }else if(!passconfirm.equals(password)){
            Toast.makeText(this,R.string.wrong_pass,Toast.LENGTH_SHORT).show();
        }else if(role == 0) {
            Toast.makeText(this, R.string.choose_role, Toast.LENGTH_SHORT).show();
        }else{
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
                                account.setRole(role);

                                saveInfor(account);
                                mAuth.signOut();

                            }else{
                                Toast.makeText(AddAccountManageAdminActivity.this, R.string.failed_register,
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("notify_register", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(AddAccountManageAdminActivity.this, R.string.failed_register,
                                    Toast.LENGTH_LONG).show();
                        }
                    });

        }
    }

    private void saveInfor(Account account) {
        String name = edName.getText().toString().trim();
        String date = tvDateBirth.getText().toString().trim();
        String phone = edPhone.getText().toString().trim();
        String bank = edBank.getText().toString().trim();
        String stk = edSTK.getText().toString().trim();
        String cmnd = edCMND.getText().toString().trim();

        if (phone.length() > 0 && phone.length() < 11 && phone.charAt(0) == '0') {
            imError.setImageResource(R.drawable.ic_check_circle_24dp);
            isPhone = true;
        } else {
            imError.setImageResource(R.drawable.ic_error_red_24dp);
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            isPhone = false;
        }

        boolean isPart = false;
        Partner partner = new Partner();
        if (account.getRole() == 3) {
            if (!stk.isEmpty() && !cmnd.isEmpty() && !bank.isEmpty()) {
                partner.setCmnd(cmnd);
                partner.setStk(stk);
                partner.setBank(bank);
                isPart = true;
            }
        } else {
            isPart = true;
        }

        if (!name.isEmpty() && !date.isEmpty() && isPhone && isPart) {
            account.setName(name);
            account.setPhone(phone);
            account.setDatebirth(date);
            account.setGender(gender);

            DatabaseReference dataNode = FirebaseDatabase.getInstance().getReference();
            dataNode.child(Node.user).child(account.getUserId()).setValue(account);
            if(account.getRole() == 3) {
                dataNode.child(Node.Partner).child(account.getUserId()).setValue(partner);
                Intent intent = new Intent(this, AddRestaurantActivity.class);
                intent.putExtra("userID", account.getUserId());
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.empty_user, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog(TextView tvBirth) {
        @SuppressLint("SetTextI18n") DatePickerDialog.OnDateSetListener callback= (view, year, monthOfYear, dayOfMonth) -> tvBirth.setText((dayOfMonth) +"/"+(monthOfYear+1)+"/"+year);

        String s=tvBirth.getText()+"";
        String[] strArrtmp = s.split("/");
        int ngay=Integer.parseInt(strArrtmp[0]);
        int thang=Integer.parseInt(strArrtmp[1])-1;
        int nam=Integer.parseInt(strArrtmp[2]);

        DatePickerDialog pic=new DatePickerDialog(
                AddAccountManageAdminActivity.this,
                callback, nam, thang, ngay);
        pic.setTitle(R.string.address_user);
        pic.show();
    }
}
