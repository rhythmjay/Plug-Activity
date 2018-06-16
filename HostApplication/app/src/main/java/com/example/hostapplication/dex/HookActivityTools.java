package com.example.hostapplication.dex;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HookActivityTools {
    private Context mContext;
    private Class mForkActivity;
    private static final String TAG = "PLUG_HookActivityTools";

    public HookActivityTools(Context context, Class fork) {
        mContext = context;
        mForkActivity = fork;
    }

    public void hookActivityManager() {
        try {
            Class activityManager = Class.forName("android.app.ActivityManager");
            Field singleton = activityManager.getDeclaredField("IActivityManagerSingleton");
            singleton.setAccessible(true);
            Object object = singleton.get(null);
            Class single = Class.forName("android.util.Singleton");
            Field instance = single.getDeclaredField("mInstance");
            instance.setAccessible(true);
            Object am = instance.get(object);
            Class<?> IActivityInterface = Class.forName("android.app.IActivityManager");
            Object proxy = Proxy.newProxyInstance(mContext.getClassLoader(), new Class<?>[]{IActivityInterface}, new ActivityInvokeHanlder(am));
            instance.set(object, proxy);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private class ActivityInvokeHanlder implements InvocationHandler {
        private Object mBase;

        public ActivityInvokeHanlder(Object object) {
            mBase = object;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("startActivity")) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        Intent intent = (Intent) args[i];
                        if (intent.getComponent() == null) {
                            break;
                        }
                        if (LoadDexUtils.PLUG_ACTIVITY_NAME[0].equals(intent.getComponent().getClassName())
                                || LoadDexUtils.PLUG_ACTIVITY_NAME[1].equals(intent.getComponent().getClassName())) {
                            Log.i(TAG, "invoke = " + intent.getComponent().getClassName());
                            String plugActivity = intent.getComponent().getClassName();
                            intent = new Intent(mContext, mForkActivity);
                            intent.putExtra(LoadDexUtils.EXTRA_PLUG_ACTIVITY, plugActivity);
                            args[i] = intent;
                        }
                    }
                }
            }
            return method.invoke(mBase, args);
        }
    }

    public void invokeActivityThread() {
        try {
            Class clazz = Class.forName("android.app.ActivityThread");
            Method method = clazz.getMethod("currentActivityThread");
            Object object = method.invoke(null);
            Field field = clazz.getDeclaredField("mH");
            field.setAccessible(true);
            Object handler = field.get(object);
            Class handlerClass = Class.forName("android.os.Handler");
            Field callback = handlerClass.getDeclaredField("mCallback");
            callback.setAccessible(true);
            callback.set(handler, mCallBack);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    private Handler.Callback mCallBack = new Handler.Callback() {
        private static final int LAUNCH_ACTIVITY = 100;

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == LAUNCH_ACTIVITY) {
                Log.i(TAG, "LAUNCH_ACTIVITY");
                try {
                    Class clazz = Class.forName("android.app.ActivityThread$ActivityClientRecord");
                    Field field = clazz.getDeclaredField("intent");
                    field.setAccessible(true);
                    Intent orig = (Intent) field.get(msg.obj);
                    String className = orig.getComponent().getClassName();
                    Log.i(TAG, "className = " + className);
                    Log.i(TAG, "mForkActivity.getName() = " + mForkActivity.getName());
                    if (TextUtils.equals(className, mForkActivity.getName())) {
                        String plugActivity = orig.getStringExtra(LoadDexUtils.EXTRA_PLUG_ACTIVITY);
                        Log.i(TAG, "plugActivity = " + plugActivity);
                        Intent intent = new Intent();
                        ComponentName componentName = new ComponentName(LoadDexUtils.PLUG_PACKAGE_NAME, plugActivity);
                        intent.setComponent(componentName);
                        field.set(msg.obj, intent);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    };
}
