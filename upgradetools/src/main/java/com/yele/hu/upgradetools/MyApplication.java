package com.yele.hu.upgradetools;

import android.app.Application;

import com.yele.hu.upgradetools.data.DataManager;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DataManager.initData();
//        CrashReport.initCrashReport(getApplicationContext(), "fac11fc71d", false);
    }

    // 是否正在扫描
    public static boolean isScan = false;
    // 是否正在连接或连接中
    public static boolean isCon = false;

}
