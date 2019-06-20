package com.example.yummy.Receive;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.FrameLayout;
import com.example.yummy.R;
import com.example.yummy.Utils.Common;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private Context context;
    private Dialog dialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr != null) {
            boolean isConnected = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
            if (!isConnected) {
                showDialog();
                Common.isNetwork = false;
            } else {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Common.isNetwork = true;
            }
        }
    }

    private void showDialog() {
        dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setTitle("");
        dialog.setContentView(R.layout.dialog_network_notify);
        dialog.setCancelable(false);
        dialog.show();

        FrameLayout btnSetting = dialog.findViewById(R.id.btn_setting);
        btnSetting.setOnClickListener(v -> context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
    }
}
