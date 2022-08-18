package com.yele.hu.blesdk520demo.bean;

import android.os.Environment;

import java.io.File;

public class FileManager {

    private static final String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();


    public static final String APP_DIR = ROOT + File.separator + "ylBleTest" + File.separator;

    public static final String UPGRADE_DIR = APP_DIR  + "upgrade"+ File.separator;

}