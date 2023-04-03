package com.example.genral_task.BroadcastRec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.genral_task.Services.MyWorker;

public class MyReceiver extends BroadcastReceiver {
    private String TAG = "MyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive called");

        WorkManager workManager = WorkManager.getInstance(context);
        OneTimeWorkRequest startServiceRequest = new OneTimeWorkRequest.Builder(MyWorker.class).build();
        workManager.enqueue(startServiceRequest);
    }
}
