package com.hairol2k.vcoresmsbulk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Preference {

    public static String getServerName(Context ctx) {

        SharedPreferences settings = ctx.getSharedPreferences("ServerName", 0);
        return settings.getString("ServerName", "");
    }

    public static void setServerName(Context ctx, String ServerName) {

        SharedPreferences settings = ctx.getSharedPreferences("ServerName", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("ServerName",ServerName);
        editor.commit();
    }

    public static String getServerUser(Context ctx) {

        SharedPreferences settings = ctx.getSharedPreferences("ServerUser", 0);
        return settings.getString("ServerUser", "");
    }

    public static void setServerUser(Context ctx, String ServerUser) {

        SharedPreferences settings = ctx.getSharedPreferences("ServerUser", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("ServerUser",ServerUser);
        editor.commit();
    }

    public static String getServerPass(Context ctx) {

        SharedPreferences settings = ctx.getSharedPreferences("ServerPass", 0);
        return settings.getString("ServerPass", "");
    }

    public static void setServerPass(Context ctx, String ServerPass) {

        SharedPreferences settings = ctx.getSharedPreferences("ServerPass", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("ServerPass",ServerPass);
        editor.commit();
    }

    public static String getQueueName(Context ctx) {

        SharedPreferences settings = ctx.getSharedPreferences("QueueName", 0);
        return settings.getString("QueueName", "");
    }

    public static void setQueueName(Context ctx, String QueueName) {

        SharedPreferences settings = ctx.getSharedPreferences("QueueName", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("QueueName",QueueName);
        editor.commit();
    }

    public static int getDelay(Context ctx) {

        SharedPreferences settings = ctx.getSharedPreferences("Delay", 0);
        return settings.getInt("Delay", 0);
    }

    public static void setDelay(Context ctx, int Delay) {

        SharedPreferences settings = ctx.getSharedPreferences("Delay", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("Delay",Delay);
        editor.commit();
    }
}
