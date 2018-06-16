package com.example.hostapplication.dex;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class LoadDexUtils {
    public static final String PLUG_PACKAGE_NAME = "com.example.myplug";
    public static final String[] PLUG_ACTIVITY_NAME = new String[2];
    public static final String EXTRA_PLUG_ACTIVITY = "plug_activity";
    private static final String FIELD_PATH_LIST = "pathList";
    private static final String FIELD_DEX_ELEMENTS = "dexElements";
    private static final String PLUG_APK = "/myplug.apk";
    private static final String TAG = "PLUG_LoadDexUtils";

    static{
        PLUG_ACTIVITY_NAME[0] = "com.example.myplug.MainActivity";
        PLUG_ACTIVITY_NAME[1] = "com.example.myplug.PlugAActivity";
    }

    public static void CombineDex(Context context) {
        String pkg = getPlugApkPath();
        String path = context.getFilesDir().getAbsolutePath();
        String libPath = null;
        DexClassLoader dexLoader = new DexClassLoader(pkg, path, libPath, context.getClassLoader());
        PathClassLoader myLoader = (PathClassLoader) context.getClassLoader();
        mergeDexElements(myLoader, dexLoader);
    }

    public static String getPlugApkPath() {
        String path = null;
        boolean isSdcardExits = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        if (isSdcardExits) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + PLUG_APK;
        }
        return path;
    }


    private static Object getField(Object srcObject, String className, String fieldName) {
        Object object = null;
        try {
            Field field = getField(className, fieldName);
            object = field.get(srcObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return object;
    }

    private static Field getField(String className, String fieldName) {
        Field field = null;
        try {
            Class clazz = Class.forName(className);
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return field;
    }

    private static Object getPathList(Object srcObject) {
        return  getField(srcObject, "dalvik.system.BaseDexClassLoader", FIELD_PATH_LIST);
    }

    private static Object getDexElements(Object srcObject) {
        Class clazz = srcObject.getClass();
        Object object = null;
        try {
            Field field = clazz.getDeclaredField(FIELD_DEX_ELEMENTS);
            field.setAccessible(true);
            object = field.get(srcObject);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return object;
    }

    private static void mergeDexElements(ClassLoader src, ClassLoader des) {
        Object srcPathList = getPathList(src);
        Object desPathList = getPathList(des);
        Object srcElements = getDexElements(srcPathList);
        Object desElements = getDexElements(desPathList);
        int srcLen = Array.getLength(srcElements);
        int desLen = Array.getLength(desElements);
        Log.i(TAG, "desLen = " + desLen);
        Class compoment = srcElements.getClass().getComponentType();
        Object value = Array.newInstance(compoment, srcLen + desLen);
        for (int i = 0; i < srcLen + desLen; i++) {
            if (i < srcLen) {
                Array.set(value, i, Array.get(srcElements, i));
            } else {
                Array.set(value, i, Array.get(desElements,i - srcLen));
            }
        }
        try {
            Field field = srcPathList.getClass().getDeclaredField(FIELD_DEX_ELEMENTS);
            field.setAccessible(true);
            field.set(srcPathList, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
