package com.example.yummy.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Model.Account;
import com.example.yummy.Model.Addresses;
import com.example.yummy.Model.Partner;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.example.yummy.Utils.UtilsBottomBar;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private EditText edtUsername, edtPassword;
    public static FirebaseAuth mAuth;
    private RelativeLayout viewLanguage;
    private LinearLayout viewRoot;
    private ProgressBar progressBar;
    private CallbackManager callbackManager;
    private ImageButton btnShowPass;
    private boolean isShowPass = false;
    private int gender=0;
    private boolean isPhone = false;
//    private FirebaseUser user;
//    private LoginButton loginFB;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 234;
    private static final String TAG = "simplifiedcoding";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        initView();
    }

    private void initView() {
        LinearLayout btnPhoneLogin = findViewById(R.id.btn_login_phone);
        btnPhoneLogin.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, PhoneActivity.class)));
        ImageView btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v->finish());
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
        progressBar.setVisibility(View.GONE);
        LinearLayout btnFacebook = findViewById(R.id.btn_sso_fb);
        callbackManager = CallbackManager.Factory.create();
        btnFacebook.setOnClickListener(view -> {
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });
        });

        LinearLayout btnGoogleSigin = findViewById(R.id.btn_sso_gg);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btnGoogleSigin.setOnClickListener(view -> signInGG());

        TextView txtForgetPass = findViewById(R.id.txtForgetPass);
        txtForgetPass.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ResetPassActivity.class)));

        TextView txtRegister = findViewById(R.id.txtRegister);
        Button btnLogin = findViewById(R.id.btn_register);
        btnLogin.setOnClickListener(v -> signinWithEmail());
        txtRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        edtUsername = findViewById(R.id.cred_username);
        edtPassword = findViewById(R.id.cred_password);
        viewLanguage = findViewById(R.id.layout_language);
        viewRoot = findViewById(R.id.root_view);
        btnShowPass = findViewById(R.id.btn_password_show);
        btnShowPass.setOnClickListener(v->{
            isShowPass = !isShowPass;
            if(!isShowPass){
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            }else {
                edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }

            btnShowPass.setImageResource(isShowPass ? R.drawable.ic_remove_red_eye : R.drawable.ic_hide_pass);
        });

        viewRoot.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = viewRoot.getRootView().getHeight() - viewRoot.getHeight();
            if (heightDiff > UtilsBottomBar.dpToPx(LoginActivity.this, 20)) { // if more than 200 dp, it's probably a keyboard...
                viewLanguage.setVisibility(View.GONE);
            } else {
                viewLanguage.setVisibility(View.VISIBLE);
            }
        });

        TextView tv = findViewById(R.id.txt_policy);
        tv.setOnClickListener(v-> {
            startActivity(new Intent(this, HomePartnerActivity.class));
            finish();
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void handleFacebookToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                Intent intent = new Intent(LoginActivity.this, BottomBarActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(getApplicationContext(), "Login failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {
            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                if(account != null)
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        getInfoAccount();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void signInGG() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signinWithEmail() {
        progressBar.setVisibility(View.VISIBLE);
        String email = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("notifySignInEmail", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, R.string.success,
                                    Toast.LENGTH_SHORT).show();
                            getInfoAccount();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("notifySignInEmail", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, R.string.empty_user, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            getInfoAccount();
        }
    }

    private void getInfoAccount() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        if (mAuth.getUid() != null) {
            mDatabase.child(Node.user).child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Common.accountCurrent = dataSnapshot.getValue(Account.class);
                    if(Common.accountCurrent != null) {
                        if(Common.accountCurrent.getIsClose() == 0) {
                            if (Common.accountCurrent.getRole() == 2)
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
                                            if (Common.myLocation != null && address.getName().equals(Common.myLocation.getName())) {
                                                isShow = true;
                                            }
                                            addresses.add(address);
                                        }
                                    }
                                    if (!isShow) {
                                        mDatabase.child(Node.Address).child(mAuth.getUid()).push().setValue(Common.myLocation);
                                        addresses.add(Common.myLocation);
                                    }
                                    Common.accountCurrent.setAddressList(addresses);

                                    if (Common.accountCurrent != null && Common.accountCurrent.getRole() == 2) {
                                        Intent intent = new Intent(LoginActivity.this, BottomBarActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                        progressBar.setVisibility(View.GONE);
                                    } else if (Common.accountCurrent != null && Common.accountCurrent.getRole() == 3) {
                                        mDatabase.child(Node.Partner).child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Partner partner = dataSnapshot.getValue(Partner.class);
                                                Common.accountCurrent.setPartner(partner);
                                                Intent intent = new Intent(LoginActivity.this, HomePartnerActivity.class);
                                                startActivity(intent);
                                                finish();
                                                progressBar.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, HomePartnerActivity.class);
                                        startActivity(intent);
                                        finish();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(LoginActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
                                    finish();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }else {
                            Toast.makeText(LoginActivity.this, R.string.account_lock, Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            progressBar.setVisibility(View.GONE);
                        }
                    }else {
                        if(mAuth != null) {
                            progressBar.setVisibility(View.GONE);
                            Account account = new Account();
                            account.setUserId(mAuth.getUid());
                            if (mAuth.getCurrentUser() != null) {
                                account.setEmail(mAuth.getCurrentUser().getEmail());
                                account.setRole(2);
                            }
                            showDialogInfor(account);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    mAuth.signOut();
                    startActivity(new Intent(LoginActivity.this, BottomBarActivity.class));
                    finish();
                    Toast.makeText(LoginActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
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
        edEmail.setText(account.getEmail());
        edEmail.setEnabled(false);
        EditText edName = dialog.findViewById(R.id.ed_nameinfo);
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

            if (phone.length() > 8 && phone.length() < 12 && phone.charAt(0) == '0') {
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

        DatePickerDialog pic=new DatePickerDialog(LoginActivity.this, callback, nam, thang, ngay);
        pic.setTitle(R.string.address_user);
        pic.show();
    }
}
