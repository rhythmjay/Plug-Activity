package com.example.hostapplication;

import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.example.hostapplication.dex.LoadDexUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyApplication extends Application {
    private AssetManager mDexAM;
    private Resources mDexResources;
    private Resources.Theme mDexTheme;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public Resources getResources() {
        return mDexResources == null ? super.getResources() : mDexResources;
    }

    @Override
    public AssetManager getAssets() {
        return mDexAM == null ? super.getAssets() : mDexAM;
    }

    @Override
    public Resources.Theme getTheme() {
        return mDexTheme == null ? super.getTheme() : mDexTheme;
    }

    public void prepareResources() {
        String app = LoadDexUtils.PLUG_PACKAGE_NAME;
        try {
            mDexAM = AssetManager.class.newInstance();
            Method method = mDexAM.getClass().getDeclaredMethod("addAssetPath", String.class);
            method.setAccessible(true);
            method.invoke(mDexAM, LoadDexUtils.getPlugApkPath());

            Resources res = getResources();
            mDexResources = new Resources(mDexAM, res.getDisplayMetrics(), res.getConfiguration());

            Class resources = Class.forName("android.content.res.Resources");
            Method selectDefaultTheme = resources.getDeclaredMethod("selectDefaultTheme", int.class, int.class);
            selectDefaultTheme.setAccessible(true);
            int defaultTheme = (Integer) selectDefaultTheme.invoke(mDexResources, 0, getApplicationInfo().targetSdkVersion);
            mDexTheme = mDexResources.newTheme();
            mDexTheme.applyStyle(defaultTheme, true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

}
