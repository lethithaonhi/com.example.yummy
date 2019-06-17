package com.example.yummy.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.yummy.Model.Account;
import com.example.yummy.Model.Partner;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.example.yummy.Utils.Node;
import com.example.yummy.Utils.UtilsBottomBar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class AccountAdminAdapter  extends RecyclerSwipeAdapter<AccountAdminAdapter.AccountAdminHolder> {
    private Context context;
    private List<Account> data;

    public AccountAdminAdapter(Context context, List<Account> data){
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public AccountAdminHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_admin_manage, parent, false);
        return new AccountAdminHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountAdminHolder holder, int position) {
        Account account = data.get(position);
        holder.tvName.setText(account.getName());
        holder.tvUserName.setText(account.getUsername());
        holder.tvPhone.setText(account.getPhone());
        holder.tvPass.setText(account.getPhone() != null ? account.getPhone(): "Hide");
        if(account.getAvatar() != null && !account.getAvatar().isEmpty())
            Picasso.get().load(account.getAvatar()).into(holder.imAvatar);
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        holder.btnClose.setOnClickListener(v->showDialogClose(account));
        if(account.getIsClose() == 0) {
            holder.imClose.setVisibility(View.GONE);
            holder.btnClose.setBackgroundColor(context.getResources().getColor(R.color.red));
            holder.imDelete.setImageResource(R.drawable.ic_lock);
        }else {
            holder.btnClose.setBackgroundColor(context.getResources().getColor(R.color.green));
            holder.imDelete.setImageResource(R.drawable.unlock);
            holder.imClose.setVisibility(View.VISIBLE);
        }

        holder.btnEdit.setOnClickListener(v->{
            showDialogEdit(account);
        });

        if(account.getGender() == 1){
            holder.imSex.setVisibility(View.VISIBLE);
            holder.imSex.setImageResource(R.drawable.boy);
        }else if(account.getGender() == 2){
            holder.imSex.setVisibility(View.VISIBLE);
            holder.imSex.setImageResource(R.drawable.girl);
        }else {
            holder.imSex.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size():0;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    class AccountAdminHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        ImageView imAvatar, imDelete, imClose, imSex;
        TextView tvName, tvUserName, tvPhone, tvPass;
        LinearLayout btnClose, btnEdit;
        AccountAdminHolder(@NonNull View itemView) {
            super(itemView);

            swipeLayout = itemView.findViewById(R.id.swipe);
            imAvatar = itemView.findViewById(R.id.im_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvUserName = itemView.findViewById(R.id.tv_username);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvPass = itemView.findViewById(R.id.tv_pass);
            btnClose = itemView.findViewById(R.id.btn_delete);
            imDelete = itemView.findViewById(R.id.im_delete);
            imClose = itemView.findViewById(R.id.im_close);
            imSex = itemView.findViewById(R.id.im_sex);
            btnEdit = itemView.findViewById(R.id.btn_edit);
        }
    }

    private void showDialogClose(Account account) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(account.getIsClose() == 1 ? context.getResources().getString(R.string.mess_open) : context.getResources().getString(R.string.mess_close))
                .setCancelable(false)
                .setPositiveButton(account.getIsClose()== 1 ? context.getResources().getString(R.string.yes) : context.getResources().getString(R.string.close), ((dialog, which) -> {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                    int isClose = account.getIsClose();
                    if(isClose == 0)
                        isClose = 1;
                    else
                        isClose = 0;

                    mDatabase.child(Node.user).child(account.getUserId()).child(Node.isClose).setValue(isClose);

                    account.setIsClose(isClose);
                    dialog.dismiss();
                    notifyDataSetChanged();
                    Toast.makeText(context, R.string.success, Toast.LENGTH_SHORT).show();
                }))
                .setNegativeButton(context.getResources().getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private int gender;
    private TextView tvBirth;
    private boolean isPhone = false;
    private void showDialogEdit(Account account){
        Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setTitle("");
        dialog.setContentView(R.layout.activity_info_account);
        dialog.show();

        TextView tvName = dialog.findViewById(R.id.tv_name);
        tvName.setText(account.getName());
        TextView tvUsername = dialog.findViewById(R.id.tv_username);
        tvUsername.setText(account.getUsername());
        EditText edName = dialog.findViewById(R.id.ed_nameinfo);
        edName.setText(account.getName());
        EditText edEmail = dialog.findViewById(R.id.ed_email);
        edEmail.setText(account.getEmail());
        tvBirth = dialog.findViewById(R.id.tv_birth);
        tvBirth.setText(account.getDatebirth());
        LinearLayout vPartner = dialog.findViewById(R.id.v_partner);
        EditText edCMND = dialog.findViewById(R.id.ed_cmnd);
        EditText edBank = dialog.findViewById(R.id.ed_bank);
        EditText edSTK = dialog.findViewById(R.id.ed_accountnum);
        if(account.getRole() == 3) {
            vPartner.setVisibility(View.VISIBLE);
            if(account.getPartner() != null ) {
                edCMND.setText(account.getPartner().getCmnd());
                edCMND.setEnabled(false);
                edBank.setText(account.getPartner().getBank());
                edSTK.setText(account.getPartner().getStk());
            }
        }else {
            vPartner.setVisibility(View.GONE);
        }

        ImageView imError = dialog.findViewById(R.id.btn_error);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        btnSave.setVisibility(View.VISIBLE);

        RadioGroup radioGroup = dialog.findViewById(R.id.radioGrp);
        RadioButton radioMale = dialog.findViewById(R.id.radio_male);
        RadioButton radioFemale = dialog.findViewById(R.id.radio_female);
        RadioButton radioNone = dialog.findViewById(R.id.radio_none);
        gender = account.getGender();

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

        RecyclerView rcvAddress = dialog.findViewById(R.id.rcv_address);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rcvAddress.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvAddress.getContext(), layoutManager.getOrientation());
        rcvAddress.addItemDecoration(dividerItemDecoration);
        AddressAdapter addressAdapter = new AddressAdapter(context, account.getAddressList(), 1);
        rcvAddress.setAdapter(addressAdapter);

        EditText edPhone = dialog.findViewById(R.id.ed_phone);
        edPhone.setText(account.getPhone());

        edPhone.setOnFocusChangeListener((vl, hasFocus) -> {
            String phone = edPhone.getText().toString().trim();
            if(!hasFocus) {
                if (phone.length() > 8 && phone.length() < 12 && phone.charAt(0) == '0') {
                    imError.setImageResource(R.drawable.ic_check_circle_24dp);
                    isPhone = true;
                } else {
                    imError.setImageResource(R.drawable.ic_error_red_24dp);
                    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                    isPhone = false;
                }
            }
        });

        ImageView imClose = dialog.findViewById(R.id.im_close);
        imClose.setVisibility(View.GONE);
        btnSave.setOnClickListener(vl->{
            String name = edName.getText().toString().trim();
            String email = edEmail.getText().toString().trim();
            String phone = edPhone.getText().toString().trim();
            String date = tvBirth.getText().toString().trim();

            if (phone.length() > 8 && phone.length() < 13) {
                imError.setImageResource(R.drawable.ic_check_circle_24dp);
                isPhone = true;
            } else {
                imError.setImageResource(R.drawable.ic_error_red_24dp);
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                isPhone = false;
            }
            if(account.getRole() == 3 && (edBank.getText().toString().trim().isEmpty() || edSTK.getText().toString().trim().isEmpty())){
                Toast.makeText(context, R.string.empty_user, Toast.LENGTH_SHORT).show();
            } if(name.isEmpty()|| email.isEmpty() || phone.isEmpty() || date.isEmpty() || !isPhone){
                Toast.makeText(context, R.string.empty_user, Toast.LENGTH_SHORT).show();
            }else {
                Partner partner = new Partner();
                if(account.getRole() == 3) {
                    String bank = edBank.getText().toString().trim();
                    String stk = edSTK.getText().toString().trim();
                    partner.setCmnd(account.getPartner().getCmnd());
                    partner.setBoss(account.getPartner().getBoss());
                    partner.setStk(stk);
                    partner.setBank(bank);
                }

                account.setUserId(account.getUserId());
                account.setUsername(account.getUsername());
                account.setAvatar(account.getAvatar());
                account.setRole(account.getRole());
                account.setPassword(account.getPassword());
                account.setName(name);
                account.setEmail(email);
                account.setPhone(phone);
                account.setDatebirth(date);
                account.setGender(gender);

                DatabaseReference nodeRoot = FirebaseDatabase.getInstance().getReference();
                nodeRoot.child(Node.user).child(account.getUserId()).setValue(account);
                if(account.getRole() == 3) {
                    nodeRoot.child(Node.Partner).child(account.getUserId()).setValue(partner);
                }

                if(account.getUserId().equals(Common.accountCurrent.getUserId()))
                    Common.accountCurrent = account;
                UtilsBottomBar.showSuccessView(context, context.getString(R.string.success), false);
                dialog.dismiss();
            }
        });

        tvBirth.setOnClickListener(lv-> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        @SuppressLint("SetTextI18n") DatePickerDialog.OnDateSetListener callback= (view, year, monthOfYear, dayOfMonth) -> tvBirth.setText((dayOfMonth) +"/"+(monthOfYear+1)+"/"+year);

        String s=tvBirth.getText()+"";
        String[] strArrtmp = s.split("/");
        int ngay=Integer.parseInt(strArrtmp[0]);
        int thang=Integer.parseInt(strArrtmp[1])-1;
        int nam=Integer.parseInt(strArrtmp[2]);
        DatePickerDialog pic=new DatePickerDialog(context, callback, nam, thang, ngay);
        pic.setTitle(R.string.address_user);
        pic.show();
    }
}
