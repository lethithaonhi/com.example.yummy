package com.example.yummy.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yummy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassActivity extends AppCompatActivity {
    private EditText edtResetPass;
    private Button btnSend;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        mAuth = FirebaseAuth.getInstance();

        initView();

        btnSend.setOnClickListener(view -> {
            String userEmail = edtResetPass.getText().toString();
            if(TextUtils.isEmpty(userEmail)){
                Toast.makeText(ResetPassActivity.this,"Vui lòng nhập email của bạn",Toast.LENGTH_SHORT).show();
            }
            else {
                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ResetPassActivity.this,"Gửi email thành công!",Toast.LENGTH_SHORT).show();
                            Intent iLogin = new Intent(ResetPassActivity.this,LoginActivity.class);
                            startActivity(iLogin);
                        }
                        else{
                            Toast.makeText(ResetPassActivity.this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initView(){
        edtResetPass = findViewById(R.id.txt_pass_reset);
        btnSend = findViewById(R.id.btn_send_pass_reset);
    }
}
