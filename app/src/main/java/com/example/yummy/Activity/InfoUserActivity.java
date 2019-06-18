package com.example.yummy.Activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Adapter.AddressAdapter;
import com.example.yummy.Model.Account;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InfoUserActivity extends AppCompatActivity {
    private TextView tvBirth;
    private int gender = 1;
    private boolean isPhone = false;

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
        ImageView imError = findViewById(R.id.btn_error);

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

        RecyclerView rcvAddress = findViewById(R.id.rcv_address);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcvAddress.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvAddress.getContext(), layoutManager.getOrientation());
        rcvAddress.addItemDecoration(dividerItemDecoration);
        AddressAdapter addressAdapter = new AddressAdapter(this, Common.accountCurrent.getAddressList(), 1);
        rcvAddress.setAdapter(addressAdapter);

        EditText edPhone = findViewById(R.id.ed_phone);
        edPhone.setText(Common.accountCurrent.getPhone());

        ImageView imClose = findViewById(R.id.im_close);
        imClose.setOnClickListener(v->finish());

        TextView btnSave = findViewById(R.id.tv_save);
        btnSave.setOnClickListener(v->{
            String name = edName.getText().toString().trim();
            String email = edEmail.getText().toString().trim();
            String phone = edPhone.getText().toString().trim();
            String date = tvBirth.getText().toString().trim();

            if (phone.length() > 8 && phone.length() < 13) {
                imError.setImageResource(R.drawable.ic_check_circle_24dp);
                isPhone = true;
            } else {
                imError.setImageResource(R.drawable.ic_error_red_24dp);
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                isPhone = false;
            }

            if(name.isEmpty()|| email.isEmpty() || phone.isEmpty() || date.isEmpty() || !isPhone){
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

        edPhone.setOnFocusChangeListener((vl, hasFocus) -> {
            String phone = edPhone.getText().toString().trim();
            if(!hasFocus) {
                if (phone.length() > 8 && phone.length() < 12) {
                    imError.setImageResource(R.drawable.ic_check_circle_24dp);
                    isPhone = true;
                } else {
                    imError.setImageResource(R.drawable.ic_error_red_24dp);
                    Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                    isPhone = false;
                }
            }
        });

        tvBirth.setOnClickListener(v-> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        @SuppressLint("SetTextI18n") DatePickerDialog.OnDateSetListener callback= (view, year, monthOfYear, dayOfMonth) -> tvBirth.setText((dayOfMonth) +"/"+(monthOfYear+1)+"/"+year);

        String s=tvBirth.getText()+"";
        String[] strArrtmp = s.split("/");
        int ngay=Integer.parseInt(strArrtmp[0]);
        int thang=Integer.parseInt(strArrtmp[1])-1;
        int nam=Integer.parseInt(strArrtmp[2]);
        DatePickerDialog pic=new DatePickerDialog(
                InfoUserActivity.this,
                callback, nam, thang, ngay);
        pic.setTitle(R.string.address_user);
        pic.show();
    }
}
