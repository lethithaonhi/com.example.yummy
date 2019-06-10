package com.example.yummy.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Activity.InfoUserActivity;
import com.example.yummy.Adapter.AddressAdapter;
import com.example.yummy.Model.Account;
import com.example.yummy.Model.Partner;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class InfoPartnerFragment extends Fragment {
    private int gender;
    private TextView tvBirth;
    private boolean isPhone = false;

    public static InfoPartnerFragment newInstance() {
        Bundle args = new Bundle();
        InfoPartnerFragment fragment = new InfoPartnerFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_info_account, container, false);
        TextView tvName = v.findViewById(R.id.tv_name);
        tvName.setText(Common.accountCurrent.getName());
        TextView tvUsername = v.findViewById(R.id.tv_username);
        tvUsername.setText(Common.accountCurrent.getUsername());
        EditText edName = v.findViewById(R.id.ed_nameinfo);
        edName.setText(Common.accountCurrent.getName());
        EditText edEmail = v.findViewById(R.id.ed_email);
        edEmail.setText(Common.accountCurrent.getEmail());
        tvBirth = v.findViewById(R.id.tv_birth);
        tvBirth.setText(Common.accountCurrent.getDatebirth());

        LinearLayout vPartner = v.findViewById(R.id.v_partner);
        EditText edCMND = v.findViewById(R.id.ed_cmnd);
        EditText edBank =v.findViewById(R.id.ed_bank);
        EditText edSTK = v.findViewById(R.id.ed_accountnum);
        ImageView imError = v.findViewById(R.id.btn_error);
        if(Common.accountCurrent.getPartner() != null) {
            edCMND.setText(Common.accountCurrent.getPartner().getCmnd());
            edCMND.setEnabled(false);
            edBank.setText(Common.accountCurrent.getPartner().getBank());
            edSTK.setText(Common.accountCurrent.getPartner().getStk());
            vPartner.setVisibility(View.VISIBLE);
        }
        Button btnSave = v.findViewById(R.id.btn_save);
        btnSave.setVisibility(View.VISIBLE);

        RadioGroup radioGroup = v.findViewById(R.id.radioGrp);
        RadioButton radioMale = v.findViewById(R.id.radio_male);
        RadioButton radioFemale = v.findViewById(R.id.radio_female);
        RadioButton radioNone = v.findViewById(R.id.radio_none);
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

        RecyclerView rcvAddress = v.findViewById(R.id.rcv_address);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rcvAddress.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvAddress.getContext(), layoutManager.getOrientation());
        rcvAddress.addItemDecoration(dividerItemDecoration);
        AddressAdapter addressAdapter = new AddressAdapter(getContext(), Common.accountCurrent.getAddressList(), 1);
        rcvAddress.setAdapter(addressAdapter);

        EditText edPhone = v.findViewById(R.id.ed_phone);
        edPhone.setText(Common.accountCurrent.getPhone());

        edPhone.setOnFocusChangeListener((vl, hasFocus) -> {
            String phone = edPhone.getText().toString().trim();
            if(!hasFocus) {
                if (phone.length() > 8 && phone.length() < 12 && phone.charAt(0) == '0') {
                    imError.setImageResource(R.drawable.ic_check_circle_24dp);
                    isPhone = true;
                } else {
                    imError.setImageResource(R.drawable.ic_error_red_24dp);
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    isPhone = false;
                }
            }
        });

        ImageView imClose = v.findViewById(R.id.im_close);
        imClose.setVisibility(View.GONE);
        btnSave.setOnClickListener(vl->{
            String name = edName.getText().toString().trim();
            String email = edEmail.getText().toString().trim();
            String phone = edPhone.getText().toString().trim();
            String date = tvBirth.getText().toString().trim();
            String bank = edBank.getText().toString().trim();
            String stk = edSTK.getText().toString().trim();

            if (phone.length() > 8 && phone.length() < 12 && phone.charAt(0) == '0') {
                imError.setImageResource(R.drawable.ic_check_circle_24dp);
                isPhone = true;
            } else {
                imError.setImageResource(R.drawable.ic_error_red_24dp);
                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                isPhone = false;
            }

            if(name.isEmpty()|| email.isEmpty() || phone.isEmpty() || date.isEmpty() || bank.isEmpty() || stk.isEmpty() || !isPhone){
                Toast.makeText(getContext(), R.string.empty_user, Toast.LENGTH_SHORT).show();
            }else{
                Account account = new Account();
                account.setUserId(Common.accountCurrent.getUserId());
                account.setUsername(Common.accountCurrent.getUsername());
                account.setAvatar(Common.accountCurrent.getAvatar());
                account.setRole(Common.accountCurrent.getRole());
                account.setPassword(Common.accountCurrent.getPassword());
                account.setName(name);
                account.setEmail(email);
                account.setPhone(phone);
                account.setDatebirth(date);
                account.setGender(gender);

                Partner partner = new Partner();
                partner.setCmnd(Common.accountCurrent.getPartner().getCmnd());
                partner.setBoss(Common.accountCurrent.getPartner().getBoss());
                partner.setStk(stk);
                partner.setBank(bank);

                DatabaseReference nodeRoot = FirebaseDatabase.getInstance().getReference();
                nodeRoot.child(Node.user).child(Common.accountCurrent.getUserId()).setValue(account);
                nodeRoot.child(Node.Partner).child(Common.accountCurrent.getUserId()).setValue(partner);
                Common.accountCurrent = account;
                Toast.makeText(getContext(), R.string.success, Toast.LENGTH_SHORT).show();
            }
        });

        tvBirth.setOnClickListener(lv-> showDatePickerDialog());

        return v;
    }

    private void showDatePickerDialog() {
        @SuppressLint("SetTextI18n") DatePickerDialog.OnDateSetListener callback= (view, year, monthOfYear, dayOfMonth) -> tvBirth.setText((dayOfMonth) +"/"+(monthOfYear+1)+"/"+year);

        String s=tvBirth.getText()+"";
        String[] strArrtmp = s.split("/");
        int ngay=Integer.parseInt(strArrtmp[0]);
        int thang=Integer.parseInt(strArrtmp[1])-1;
        int nam=Integer.parseInt(strArrtmp[2]);
        DatePickerDialog pic=new DatePickerDialog(Objects.requireNonNull(getContext()), callback, nam, thang, ngay);
        pic.setTitle(R.string.address_user);
        pic.show();
    }
}
