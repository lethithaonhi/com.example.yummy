package com.example.yummy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.yummy.Model.Account;
import com.example.yummy.Model.Address;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private EditText edtUsername, edtPassword;
    private FirebaseAuth mAuth;
    private RelativeLayout viewLanguage;
    private LinearLayout viewRoot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        initView();
    }

    private void initView(){
        LinearLayout btnPhoneLogin = findViewById(R.id.btn_login_phone);
        btnPhoneLogin.setOnClickListener(v-> startActivity(new Intent(LoginActivity.this, PhoneActivity.class)));

        TextView txtRegister = findViewById(R.id.txtRegister);
        Button btnLogin = findViewById(R.id.btn_register);
        btnLogin.setOnClickListener(v -> signinWithEmail());
        txtRegister.setOnClickListener(v-> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        edtUsername = findViewById(R.id.cred_username);
        edtPassword = findViewById(R.id.cred_password);
        viewLanguage = findViewById(R.id.layout_language);
        viewRoot = findViewById(R.id.root_view);

        viewRoot.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = viewRoot.getRootView().getHeight() - viewRoot.getHeight();
            if (heightDiff > UtilsBottomBar.dpToPx(LoginActivity.this, 20)) { // if more than 200 dp, it's probably a keyboard...
                viewLanguage.setVisibility(View.GONE);
            }else {
                viewLanguage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void signinWithEmail(){
        String email = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if(!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("notifySignInEmail", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            getInfoAccount();
                            startActivity(new Intent(LoginActivity.this, BottomBarActivity.class));
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("notifySignInEmail", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    });
        }else{
            Toast.makeText(this, R.string.empty_user, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            getInfoAccount();
            startActivity(new Intent(LoginActivity.this, BottomBarActivity.class));
            finish();
        }
    }

    private void getInfoAccount(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        if(mAuth.getUid() != null)
        mDatabase.child(Node.user).child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Common.accountCurrent = dataSnapshot.getValue(Account.class);
                mDatabase.child(Node.Address).child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Address> addresses = new ArrayList<>();
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            Address address = dataSnapshot1.getValue(Address.class);
                            addresses.add(address);
                        }

                        Common.accountCurrent.setAddressList(addresses);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
