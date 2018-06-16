package com.example.myplug;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MainTest_Plug", "Process = " + Process.myPid());
        getActionBar().setTitle(R.string.app_name);
        setContentView(R.layout.content_main);
        TextView textView = (TextView) findViewById(R.id.textview);
        textView.setText("Plug MainActivity");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
