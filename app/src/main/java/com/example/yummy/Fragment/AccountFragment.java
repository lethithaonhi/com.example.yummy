package com.example.yummy.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yummy.Activity.BottomBarActivity;
import com.example.yummy.Activity.InfoUserActivity;
import com.example.yummy.Activity.LoginActivity;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class AccountFragment extends Fragment {
    private SharedPreferences sharedPreferences;

    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        if(getContext() != null)
        mContext = getContext();

        Button btnSignIn = v.findViewById(R.id.btn_signin);
        btnSignIn.setOnClickListener(v1 -> startActivity(new Intent(mContext, LoginActivity.class)));

        LinearLayout viewSetting = v.findViewById(R.id.view_setting);
        viewSetting.setOnClickListener(vl-> dialogSetting());

        ImageView imAvatar = v.findViewById(R.id.im_avatar);
        TextView tvInfoAccount = v.findViewById(R.id.view_infoaccount);
        TextView tvName = v.findViewById(R.id.tv_name);
        if (Common.accountCurrent != null){
            btnSignIn.setVisibility(View.GONE);
            tvName.setText(Common.accountCurrent.getName());
            tvInfoAccount.setText(R.string.info_account);
            if(Common.accountCurrent.getAvatar() != null){
                Picasso.get().load(Common.accountCurrent.getAvatar()).into(imAvatar);
            }
        }
        return v;
    }

    private void dialogSetting(){
        if(getContext() != null) {
            Dialog dialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setTitle("");
            dialog.setContentView(R.layout.dialog_setting_account);
            dialog.show();

            LinearLayout viewAvatar = dialog.findViewById(R.id.view_avatar);
            LinearLayout viewinfor = dialog.findViewById(R.id.view_info);
            LinearLayout viewChangePass = dialog.findViewById(R.id.view_changepass);
            LinearLayout viewChangLang = dialog.findViewById(R.id.view_changelang);

            viewChangLang.setOnClickListener(v-> createDialogLang());

            viewAvatar.setOnClickListener(v->{

            });

            viewChangePass.setOnClickListener(v->{

            });

            viewinfor.setOnClickListener(v -> {
                if(Common.accountCurrent != null && mContext != null) {
                    Intent intent = new Intent(mContext, InfoUserActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                }else {
                    Toast.makeText(mContext, R.string.login_first, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            });

            ImageView imClose = dialog.findViewById(R.id.im_close);
            imClose.setOnClickListener(v->dialog.dismiss());

            ImageView imgAvatar = dialog.findViewById(R.id.img_avatar);
            if(Common.accountCurrent != null && Common.accountCurrent.getAvatar() != null){
                Picasso.get().load(Common.accountCurrent.getAvatar()).into(imgAvatar);
            }
        }
    }

    // Initialise it from onAttach()
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void createDialogLang() {
        if (mContext != null) {
            sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("changeLang", Context.MODE_PRIVATE);
            Dialog dialog = new Dialog(mContext);
            dialog.setTitle("");
            dialog.setContentView(R.layout.dialog_changelanguage);
            dialog.show();

            RadioButton radioEng = dialog.findViewById(R.id.radio_english);
            RadioButton radioViet = dialog.findViewById(R.id.radio_vietnamese);
            getSharedLang();

            if (Common.language.equals("en")) {
                radioEng.setChecked(true);
            } else {
                radioViet.setChecked(true);
            }

            TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(v -> dialog.dismiss());

            RadioGroup radioGroup = dialog.findViewById(R.id.radiogroup);
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                Locale locale;
                int checkedRadioId = group.getCheckedRadioButtonId();
                if (checkedRadioId == R.id.radio_english) {
                    Common.language = "en";
                    locale = new Locale("en");
                } else {
                    Common.language = "vi";
                    locale = new Locale("vi");
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("lang", Common.language);
                editor.apply();

                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = locale;
                res.updateConfiguration(conf, dm);
                Intent refresh = new Intent(getContext(), BottomBarActivity.class);
                startActivity(refresh);
                if (getActivity() != null)
                    getActivity().finish();
            });
        }
    }

    private void getSharedLang(){
        if(sharedPreferences!= null) {
            Common.language = sharedPreferences.getString("lang", "en");
        }else {
            Common.language = "en";
        }

    }
}
