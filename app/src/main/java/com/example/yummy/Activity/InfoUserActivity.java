package com.example.yummy.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Model.Account;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InfoUserActivity extends AppCompatActivity {
    private TextView tvBirth;
    private int gender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_account);

        TextView tvName = findViewById(R.id.tv_name);
        tvName.setText(Common.accountCurrent.getName());
        TextView tvUsername = findViewById(R.id.tv_username);
        tvUsername.setText(Common.accountCurrent.getUsername());
        EditText edName = findViewById(R.id.ed_nameinfo);
        edName.setText(Common.accountCurrent.getName());
        EditText edEmail = findViewById(R.id.ed_email);
        edEmail.setText(Common.accountCurrent.getEmail());
        tvBirth = findViewById(R.id.tv_birth);
        tvBirth.setText(Common.accountCurrent.getDatebirth());

        RadioGroup radioGroup = findViewById(R.id.radioGrp);
        RadioButton radioMale = findViewById(R.id.radio_male);
        RadioButton radioFemale = findViewById(R.id.radio_female);
        RadioButton radioNone = findViewById(R.id.radio_none);
        gender = Common.accountCurrent.getGender();

        if(gender == 1){
            radioMale.setChecked(true);
        }else if(gender == 2){
            radioFemale.setChecked(true);
        }else {
            radioNone.setChecked(true);
        }

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

        EditText tvPhone = findViewById(R.id.ed_phone);
        tvPhone.setText(Common.accountCurrent.getPhone());

        ImageView imClose = findViewById(R.id.im_close);
        imClose.setOnClickListener(v->finish());

        TextView btnSave = findViewById(R.id.tv_save);
        btnSave.setOnClickListener(v->{
            String name = edName.getText().toString().trim();
            String email = edEmail.getText().toString().trim();
            String phone = tvPhone.getText().toString().trim();
            String date = tvBirth.getText().toString().trim();

            if(name.isEmpty()|| email.isEmpty() || phone.isEmpty() || date.isEmpty()){
                Toast.makeText(this, R.string.empty_user, Toast.LENGTH_SHORT).show();
            }else{
                Account account = Common.accountCurrent;
                account.setName(name);
                account.setEmail(email);
                account.setPhone(phone);
                account.setDatebirth(date);
                account.setGender(gender);
                DatabaseReference nodeRoot = FirebaseDatabase.getInstance().getReference();
                nodeRoot.child(Node.user).child(Common.accountCurrent.getUserId()).setValue(account);
                Common.accountCurrent = account;
                finish();
            }
        });

        tvBirth.setOnClickListener(v-> showDatePickerDialog());
    }

    public void showDatePickerDialog() {
        @SuppressLint("SetTextI18n") DatePickerDialog.OnDateSetListener callback= (view, year, monthOfYear, dayOfMonth) -> {
            //Mỗi lần thay đổi ngày tháng năm thì cập nhật lại TextView Date
            tvBirth.setText((dayOfMonth) +"/"+(monthOfYear+1)+"/"+year);
        };
        //các lệnh dưới này xử lý ngày giờ trong DatePickerDialog
        //sẽ giống với trên TextView khi mở nó lên
        String s=tvBirth.getText()+"";
        String[] strArrtmp = s.split("/");
        int ngay=Integer.parseInt(strArrtmp[0]);
        int thang=Integer.parseInt(strArrtmp[1])-1;
        int nam=Integer.parseInt(strArrtmp[2]);
        DatePickerDialog pic=new DatePickerDialog(
                InfoUserActivity.this,
                callback, nam, thang, ngay);
        pic.setTitle("Chọn ngày hoàn thành");
        pic.show();
    }
}
