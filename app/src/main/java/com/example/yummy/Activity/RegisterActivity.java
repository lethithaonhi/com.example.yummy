package com.example.yummy.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Model.User;
import com.example.yummy.R;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity  implements View.OnClickListener{
    private EditText edtUsername, edtNewPass, edtConfirm;
    private FirebaseAuth mAuth;
    private LinearLayout viewRoot;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        initView();
    }

    public void initView(){
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
            }else {
                txtTitle.setVisibility(View.VISIBLE);
            }
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

                                User user = new User();
                                user.setPassword(password);
                                user.setUsername(email);
                                user.setRole(3);
                                user.setUserId(uid);
                                user.AddUser(user, uid);
                                updateUI();
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
    }
}
