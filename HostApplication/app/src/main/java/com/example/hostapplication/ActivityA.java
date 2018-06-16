package com.example.hostapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.example.hostapplication.dex.LoadDexUtils;

public class ActivityA extends Activity {
    private static final String TAG = "MainTest_Host";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn1 = (Button) findViewById(R.id.button1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                ComponentName cm = new ComponentName(LoadDexUtils.PLUG_PACKAGE_NAME, LoadDexUtils.PLUG_ACTIVITY_NAME[0]);
                intent.setComponent(cm);
                startActivity(intent);
            }
        });

        Button btn2 = (Button) findViewById(R.id.button2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                ComponentName cm = new ComponentName(LoadDexUtils.PLUG_PACKAGE_NAME, LoadDexUtils.PLUG_ACTIVITY_NAME[1]);
                intent.setComponent(cm);
                startActivity(intent);
            }
        });
        Log.i(TAG, "Process = " + Process.myPid());
    }
}
