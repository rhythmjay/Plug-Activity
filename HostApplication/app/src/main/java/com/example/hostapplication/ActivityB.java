package com.example.hostapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

public class ActivityB extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText("ActivityB");
    }
}
