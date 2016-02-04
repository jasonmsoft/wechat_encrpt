package com.jasonmsoft.wechat_encrpt;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


/**
 * Created by cdmaji1 on 2016/2/4.
 */
public class AppConfig {

        private static String preference_name = "wechat_encrypt";
        private static Context mAppContext = null;
        private static int mode = 0;
        public static void initconfig(Context ctx)
        {
            mAppContext = ctx;
        }

        public static boolean getFirstRun() {
            SharedPreferences setting = mAppContext.getSharedPreferences(preference_name, 0);
            Boolean firstRun = setting.getBoolean("firstRun", true);
            if (firstRun) {
                setting.edit().putBoolean("firstRun", false).commit();
            }
            return firstRun;
        }

        public static void remove( String key) {
            SharedPreferences.Editor localEditor = mAppContext.getSharedPreferences(preference_name, 0).edit();
            localEditor.remove(key);
            localEditor.commit();
        }


        //---------------------------------------------

        public static void putString(String key,String value) {
            if (null == key) return ;
            SharedPreferences.Editor localEditor = mAppContext.getSharedPreferences(preference_name, 0).edit();
            localEditor.putString(key, value);
            localEditor.commit();
        }

        public static String getString(String key,String defaultValue) {
            return mAppContext.getSharedPreferences(preference_name, mode).getString(key, defaultValue);
        }
        public static String getString(String key) {
            return getString(key,null);
        }

        public static void putBoolean(String key,boolean value) {
            if (null == key) return ;
            SharedPreferences.Editor localEditor = mAppContext.getSharedPreferences(preference_name, 0).edit();
            localEditor.putBoolean(key, value);
            localEditor.commit();
        }

        public static boolean getBoolean(String key,boolean defaultValue) {
            return mAppContext.getSharedPreferences(preference_name, mode).getBoolean(key, defaultValue);
        }

        public static boolean getBoolean(String key) {
            return getBoolean(key,false);
        }

        public static void putInt(String key,int value) {
            if (null == key) return ;
            SharedPreferences.Editor localEditor = mAppContext.getSharedPreferences(preference_name, 0).edit();
            localEditor.putInt(key, value);
            localEditor.commit();
        }

        public static int getInt(String key,int defaultValue) {
            return mAppContext.getSharedPreferences(preference_name, mode).getInt(key, defaultValue);
        }

        public static int getInt(String key) {
            return getInt(key,-1);
        }

        public static void putLong(String key,long value) {
            if (null == key) return ;
            SharedPreferences.Editor localEditor = mAppContext.getSharedPreferences(preference_name, 0).edit();
            localEditor.putLong(key, value);
            localEditor.commit();
        }

        public static long getLong(String key,long defaultValue) {
            return mAppContext.getSharedPreferences(preference_name, mode).getLong(key, defaultValue);
        }




}
