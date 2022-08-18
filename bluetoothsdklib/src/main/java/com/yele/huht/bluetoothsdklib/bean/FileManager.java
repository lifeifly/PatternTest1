package com.yele.huht.bluetoothsdklib.bean;

import android.os.Environment;

import java.io.File;

// 文件下载路径
public class FileManager {

    private static final String RO = System.getenv("EXTERNAL_STORAGE");;
    private static final String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();


    public static final String APP_DIR = ROOT + File.separator + "ylBleTest" + File.separator;

    public static final String UPGRADE_DIR = APP_DIR  + "upgrade"+ File.separator;

}