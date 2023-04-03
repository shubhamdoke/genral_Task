package com.example.genral_task;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genral_task.Services.MyService;
import com.example.genral_task.Services.MyWorker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    TextView tv_battery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_battery = (TextView) this.findViewById(R.id.tv_battery);
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


        isNetworkAvailable();
        startServiceViaWorker();

        ActvieRun();

        sendSms();


    }

    private void sendSms() {
        String phoneNumber = "8889818200";
        String message = "Hello Shubham  charge your phone";
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage(message);
        smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
    }

    private void ActvieRun() {
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "I'm Active !!!", Toast.LENGTH_SHORT).show();
                ActvieRun();

            }
        },5000);
    }

    private void startServiceViaWorker() {
        Log.d("TAG", "startServiceViaWorker called");
        String UNIQUE_WORK_NAME = "StartMyServiceViaWorker";
        WorkManager workManager = WorkManager.getInstance(this);


        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(MyWorker.class,16, TimeUnit.MINUTES)
                        .build();

        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);
    }
    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level * 100 / (float) scale;
            tv_battery.setText(String.valueOf(batteryPct) + "%");


            if (batteryPct <= 70) {
                sendSms();
            }
        }
    };
    public void isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
             Toast.makeText(this, "Internet is connected", Toast.LENGTH_SHORT).show();
            isAvailable = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("OnStartBG", "startService called");
        if (!MyService.isServiceRunning) {
            Intent serviceIntent = new Intent(this, MyService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("OnDesBG", "onDestroy called");
        stopService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("OnStopBG", "stopService called");
        if (MyService.isServiceRunning) {
            Intent serviceIntent = new Intent(this, MyService.class);
            stopService(serviceIntent);
        }
    }

    public void stopService() {
        Log.d("OnRe-RunBG", "stopService called");
        if (MyService.isServiceRunning) {
            Intent serviceIntent = new Intent(this, MyService.class);
            stopService(serviceIntent);
        }
    }


}


