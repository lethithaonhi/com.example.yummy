package com.example.yummy.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.daimajia.numberprogressbar.OnProgressBarListener;
import com.example.yummy.R;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity implements OnProgressBarListener {
    private NumberProgressBar progressBar;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        progressBar = findViewById(R.id.number_progress_bar);
        progressBar.setOnProgressBarListener(this);
        progressBar.setProgress(0);
        registerService();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> progressBar.incrementProgressBy(1));
            }
        }, 1000, 100);
    }

    @Override
    public void onProgressChange(int current, int max) {
        if(current == max) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            timer.cancel();
        }
    }

    private void registerService(){
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, intentFilter);

    }

    private class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connMgr != null) {
                NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                boolean isConnected = wifi != null && wifi.isConnectedOrConnecting();
                if (isConnected) {

                } else {

                }
            }
        }
    }


}

