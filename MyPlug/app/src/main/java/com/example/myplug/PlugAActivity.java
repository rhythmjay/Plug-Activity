package com.example.myplug;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

public class PlugAActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("PlugAActivity");
        setContentView(R.layout.content_main);
        getWindow().getDecorView().setBackgroundColor(Color.GREEN);
        TextView textView = (TextView) findViewById(R.id.textview);
        textView.setText("Plug PluActivity");
    }

    @Override
    public Resources getResources() {
        return getApplication() == null ? super.getResources() : getApplication().getResources();
    }

    @Override
    public Resources.Theme getTheme() {
        return getApplication() == null ? super.getTheme() : getApplication().getTheme();
    }

    @Override
    public AssetManager getAssets() {
        return getApplication() == null ? super.getAssets() : getApplication().getAssets();
    }
}
