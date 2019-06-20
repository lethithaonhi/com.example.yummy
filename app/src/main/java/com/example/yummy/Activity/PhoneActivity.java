package com.example.yummy.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Model.Account;
import com.example.yummy.Model.Addresses;
import com.example.yummy.R;
import com.example.yummy.Receive.NetworkChangeReceiver;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {
    private static final String TAG = "PhoneAuth";
    private EditText edtCodeText;
    private Button btnVerifyCode;
    private Button btnSendCode;
    private Button btnResendCode;
    private String number;
    private int gender = 1;

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth mAuth;
    private CountryCodePicker ccp;
    private FirebaseUser user;
    private LinearLayout vPhone, vCode;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        EditText edtPhoneNumber = findViewById(R.id.phoneText);
        edtCodeText =  findViewById(R.id.codeText);
        btnVerifyCode = findViewById(R.id.verifyButton);
        btnSendCode = findViewById(R.id.sendButton);
        btnResendCode = findViewById(R.id.resendButton);
        vPhone = findViewById(R.id.v_send);
        vCode = findViewById(R.id.v_code);

        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(edtPhoneNumber);

        mAuth = FirebaseAuth.getInstance();

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
    public void sendCode(View view) {
        vCode.setVisibility(View.VISIBLE);
        vPhone.setVisibility(View.GONE);
        number = ccp.getFullNumberWithPlus();

        setUpVerificatonCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                verificationCallbacks);
    }

    private void setUpVerificatonCallbacks() {
        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential) {
                        btnResendCode.setEnabled(false);
                        btnVerifyCode.setEnabled(false);
                        edtCodeText.setText("");
                        signInWithPhoneAuthCredential(credential);
                    }
                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Log.d(TAG, "Invalid credential: "
                                    + e.getLocalizedMessage());
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            Log.d(TAG, "SMS Quota exceeded.");
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        phoneVerificationId = verificationId;
                        resendToken = token;
                        btnVerifyCode.setEnabled(true);
                        btnSendCode.setEnabled(false);
                        btnResendCode.setEnabled(true);
                    }
                };
    }

    public void verifyCode(View view) {

        String code = edtCodeText.getText().toString();
        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        edtCodeText.setText("");
                        if(task.getResult() != null) {
                            user = task.getResult().getUser();
                            getInfoAccount();
                        }else {
                            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void resendCode(View view) {
        vPhone.setVisibility(View.VISIBLE);
        vCode.setVisibility(View.GONE);
        number = ccp.getFullNumberWithPlus();
        setUpVerificatonCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                resendToken);
    }

    private void getInfoAccount() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        if (mAuth.getUid() != null) {
            mDatabase.child(Node.user).child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Common.accountCurrent = dataSnapshot.getValue(Account.class);
                    if(Common.accountCurrent != null) {
                        UtilsBottomBar.getOrderCurrent();
                        mDatabase.child(Node.Address).child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean isShow = false;
                                List<Addresses> addresses = new ArrayList<>();
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    Addresses address = dataSnapshot1.getValue(Addresses.class);
                                    if (address != null) {
                                        address.setId(dataSnapshot1.getKey());
                                        if (address.getLongitude() == Common.myLocation.getLongitude() && address.getLatitude() == Common.myLocation.getLatitude()) {
                                            isShow = true;
                                        }
                                        if(address.getName() == null || address.getName().isEmpty()){
                                            isShow = true;
                                        }else {
                                            addresses.add(address);
                                        }
                                    }
                                }
                                if (!isShow) {
                                    mDatabase.child(Node.Address).child(user.getUid()).push().setValue(Common.myLocation);
                                    addresses.add(Common.myLocation);
                                }
                                Common.accountCurrent.setAddressList(addresses);

                                if (Common.accountCurrent != null && Common.accountCurrent.getRole() != 1) {
                                    startActivity(new Intent(PhoneActivity.this, BottomBarActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(PhoneActivity.this, HomePartnerActivity.class));
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(PhoneActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PhoneActivity.this, WelcomeActivity.class));
                                finish();
                            }
                        });
                    }else {
                        if(mAuth != null) {
                            Account account = new Account();
                            account.setUserId(user.getUid());
                            if (mAuth.getCurrentUser() != null) {
                                account.setPhone(user.getPhoneNumber());
                                account.setRole(2);
                            }
                            showDialogInfor(account);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if(mAuth != null) {
                        Account account = new Account();
                        account.setUserId(mAuth.getUid());
                        if (mAuth.getCurrentUser() != null) {
                            account.setPhone(mAuth.getCurrentUser().getPhoneNumber());
                            account.setRole(2);
                        }
                        showDialogInfor(account);
                    }
                }
            });
        }
    }

    private void showDialogInfor(Account account){
        Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setTitle("");
        dialog.setContentView(R.layout.view_enter_user_profile);
        dialog.setCancelable(false);
        dialog.show();

        LinearLayout vEmail = dialog.findViewById(R.id.v_email);
        EditText edEmail = dialog.findViewById(R.id.ed_email);
        vEmail.setVisibility(View.VISIBLE);
        EditText edName = dialog.findViewById(R.id.ed_nameinfo);
        TextView tvDateBirth = dialog.findViewById(R.id.tv_birth);
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
        tvDateBirth.setText(dateformat.format(c.getTime()));
        tvDateBirth.setOnClickListener(v->showDatePickerDialog(tvDateBirth));

        EditText edPhone = dialog.findViewById(R.id.ed_phone);
        edPhone.setText(account.getPhone());
        edPhone.setEnabled(false);
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGrp);
        Button btnSave = dialog.findViewById(R.id.btn_save);

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

        btnSave.setOnClickListener(v->{
            String name = edName.getText().toString().trim();
            String date = tvDateBirth.getText().toString().trim();

            if(!name.isEmpty() && !date.isEmpty() && !edEmail.getText().toString().trim().isEmpty()){
                account.setName(name);
                account.setDatebirth(date);
                account.setGender(gender);
                account.setEmail(edEmail.getText().toString().trim());

                DatabaseReference dataNode = FirebaseDatabase.getInstance().getReference();
                dataNode.child(Node.user).child(account.getUserId()).setValue(account);

                Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
                updateUI();
            }else {
                Toast.makeText(this, R.string.empty_user, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(){
        Toast.makeText(this, R.string.success_register, Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void showDatePickerDialog(TextView tvBirth) {
        @SuppressLint("SetTextI18n") DatePickerDialog.OnDateSetListener callback= (view, year, monthOfYear, dayOfMonth) -> tvBirth.setText((dayOfMonth) +"/"+(monthOfYear+1)+"/"+year);

        String s=tvBirth.getText()+"";
        String[] strArrtmp = s.split("/");
        int ngay=Integer.parseInt(strArrtmp[0]);
        int thang=Integer.parseInt(strArrtmp[1])-1;
        int nam=Integer.parseInt(strArrtmp[2]);

        DatePickerDialog pic=new DatePickerDialog(PhoneActivity.this, callback, nam, thang, ngay);
        pic.setTitle(R.string.address_user);
        pic.show();
    }
}

