package com.yele.hu.upgradetools.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

public class PhoneUtils {

    /**
     * 获取手机的IMEI码
     * @param context 上下文
     * @return
     */
    public static String getIMEI(Context context) {
        String imei = null;
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMEI号
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            imei = telephonyManager.getDeviceId();
            //在次做个验证，也不是什么时候都能获取到的啊
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }
}
