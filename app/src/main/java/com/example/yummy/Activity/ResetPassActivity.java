package com.example.yummy.Activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yummy.R;
import com.example.yummy.Receive.NetworkChangeReceiver;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassActivity extends AppCompatActivity {
    private EditText edtResetPass;
    private Button btnSend;
    private FirebaseAuth mAuth;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        mAuth = FirebaseAuth.getInstance();

        initView();

        btnSend.setOnClickListener(view -> {
            String userEmail = edtResetPass.getText().toString();
            if(TextUtils.isEmpty(userEmail)){
                Toast.makeText(ResetPassActivity.this,R.string.empty_user,Toast.LENGTH_SHORT).show();
            }
            else {
                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(ResetPassActivity.this,R.string.reset_pass_succ,Toast.LENGTH_SHORT).show();
                        Intent iLogin = new Intent(ResetPassActivity.this, LoginActivity.class);
                        startActivity(iLogin);
                    }
                    else{
                        Toast.makeText(ResetPassActivity.this, R.string.reset_pass_fail, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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
    private void initView(){
        edtResetPass = findViewById(R.id.txt_pass_reset);
        btnSend = findViewById(R.id.btn_send_pass_reset);
        ImageView imBack = findViewById(R.id.btn_back);
        imBack.setOnClickListener(v->finish());
    }
}
