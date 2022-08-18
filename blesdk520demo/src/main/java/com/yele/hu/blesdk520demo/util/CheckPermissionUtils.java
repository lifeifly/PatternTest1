package com.yele.hu.blesdk520demo.util;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;

/**
 * 检查权限的工具类
 * Created by cheng on 2017/1/19.
 */

public class CheckPermissionUtils {

    private static final String TAG = "CheckPermissionUtils";

    /**
     * 判断当前应用是否有悬浮窗权限
     * 6.0及以上用6.0以上的方法判断，否则就用正常的方法
     *
     * @param context 当前的上下文
     * @return 是否有悬浮窗权限
     */
    public static boolean hasAlertWindowPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                return false;
            } else {
                return true;
            }
        } else {
            if (!hasAlertWindowPermission(context, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 当前是低版本的情况下
     * 判断是否有权限
     *
     * @param context    当前应用的上下文
     * @param permission 当前需要判断的权限
     * @return 是否有指定的权限
     */
    public static boolean hasAlertWindowPermission(Context context, String permission) {
        if (!permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            return false;
        }
        switch (MySystemUtils.getSystem()) {
            case MySystemUtils.SYS_INIT:
                return true;
//                PackageManager pm = context.getPackageManager();
//                return PackageManager.PERMISSION_GRANTED ==
//                        pm.checkPermission("android.permission.RECORD_AUDIO", permission);
            case MySystemUtils.SYS_EMUI:
                return checkHuaWeiFloatWindowPermission(context);
            case MySystemUtils.SYS_MIUI:
                return checkMiuiFloatWindowPermission(context);
            case MySystemUtils.SYS_FLYME:
                return checkMeiZuFloatWindowPermission(context);
        }
        return true;
    }

    /**
     * 检测 miui 悬浮窗权限
     */
    public static boolean checkMiuiFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkMiuiOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        } else {
            return true;
        }
    }

    /**
     * 检测小米MIUI系统是否有该权限
     *
     * @param context
     * @param op
     * @return
     */
    private static boolean checkMiuiOp(Context context, int op) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {

            }
        } else {
            LogUtils.e(TAG, "Below API 19 cannot invoke!");
            return true;
        }
        return false;
    }

    /**
     * 检测 meizu 悬浮窗权限
     */
    public static boolean checkMeiZuFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkMeiZuOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        }
        return true;
    }

    /**
     * 检测魅族系统的权限，需要>=19才有必要检测，否则是直接拥有的该权限的
     *
     * @param context
     * @param op
     * @return
     */
    private static boolean checkMeiZuOp(Context context, int op) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {

            }
        } else {
            LogUtils.e(TAG, "Below API 19 cannot invoke!");
            return true;
        }
        return false;
    }

    /**
     * 检测 Huawei 悬浮窗权限
     */
    public static boolean checkHuaWeiFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkHuaWeiOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        }
        return true;
    }

    private static boolean checkHuaWeiOp(Context context, int op) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
            }
        } else {
            LogUtils.e(TAG, "Below API 19 cannot invoke!");
            return true;
        }
        return false;
    }


    /**
     * 当前录音权限的对应的文字内容
     */
    public static final String STR_AUDIO_PERMISSION = "android.permission.RECORD_AUDIO";

    /**
     * 判断当前是否有多媒体的权限（多媒体权限主要是）
     *
     * @param context 当前上下文
     * @return 是否有权限
     */
    public static boolean hasAudioPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkHighVersionAudio(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 6.0以前只要在xml清单中标示出当前的权限就默认会有，所以上面方法不适用了
            return checkLowVersionAudio();
        } else {
            // 5.0 以下是直接给该权限的
            return true;
        }
    }

    /**
     * 检测6.0版本以及以上是否有录音权限的方法(非常规的)
     *
     * @return 是否有录音权限
     */
    private static boolean checkHighVersionAudioOther() {
        // 6.0以后用这个方法是没有问题的
        int audioSource = MediaRecorder.AudioSource.MIC;
        // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
        int sampleRateInHz = 44100;
        // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
        int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        // 缓冲区字节大小
        int bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        //开始录制音频
        try {
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        // 根据开始录音判断是否有录音权限
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            return false;
        }
        audioRecord.stop();
        audioRecord.release();

        return true;
    }

    /**
     * 检测6.0版本以及以上是否有录音权限的方法（官方推荐的）
     *
     * @return 是否有录音权限
     */
    private static boolean checkHighVersionAudio(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    // 音频获取源
    public static int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    public static int sampleRateInHz = 44100;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    public static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    public static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    public static int bufferSizeInBytes = 0;

    /**
     * 检测低版本是否有录音权限，当前是6.0以下的版本
     * （其实该方法是兼容6.0以及上下的）
     *
     * @return
     */
    private static boolean checkLowVersionAudio() {
        bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        //开始录制音频
        try {
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        /**
         * 根据开始录音判断是否有录音权限
         */
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            return false;
        }
        audioRecord.stop();
        audioRecord.release();

        return true;
    }

    public final static int REQUEST_CODE_AUDIO_PERMISSIONS = 125;

    /**
     * 请求获取录音的权限
     *
     * @param activity
     * @return
     */
    public static boolean requestSystemAudioPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!checkHighVersionAudio(activity)) {
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_AUDIO_PERMISSIONS);
                return true;
            }
        }
        return false;
    }

    /**
     * 跳转到系统的权限设置界面
     *
     * @param activity
     */
    public static void requestJumpToPermissionSetView(Activity activity) {
        Uri packageURI = Uri.parse("package:" + activity.getPackageName());
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        activity.startActivity(intent);
    }

    /**
     * 是否有读取文件的权限
     *
     * @param context
     * @return
     */
    public static boolean hasWriteOrReadPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkHighVersionROrW(context);
        } else {
            // 6.0以前只要在xml清单中标示出当前的权限就默认会有，所以上面方法不适用了
            return true;
        }
    }

    public final static int REQUEST_CODE_WRITE_OR_READ_PERMISSIONS = 126;

    /**
     * 当前录音权限的对应的文字内容
     */
    public static final String STR_WRITE_OR_READ_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    /**
     * 高版本判断是否有读写权限
     *
     * @param context 上下文
     * @return 返回权限
     */
    private static boolean checkHighVersionROrW(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 请求获取读写的权限
     *
     * @param activity
     * @return
     */
    public static boolean requestSystemWOrRPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkHighVersionROrW(activity)) {
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_OR_READ_PERMISSIONS);
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否有手机状态权限
     *
     * @param context 上下文
     * @return 是否有对应的权限
     */
    public static boolean hasPhoneStatePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 请求手机状态的请求码
     */
    public static final int REQUEST_CODE_READ_PHONE_STATE = 127;

    /**
     * 请求手机状态权限
     *
     * @param activity 上下文
     * @return 是否请求成功
     */
    public static boolean requestPhoneStatePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPhoneStatePermission(activity)) {
                ActivityCompat.requestPermissions(activity, new String[]{
                        Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_READ_PHONE_STATE);
                return true;
            }
        }
        return true;
    }

    /**
     * 判断手机是否有蓝牙权限
     *
     * @param context 上下文
     * @return 是否有蓝牙权限
     */
    public static boolean hasBleOpenPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;
    }

    public static final int REQUEST_BLUETOOTH_OPEN_CODE = 128;

    /**
     * 请求蓝牙打开的权限
     *
     * @param activity 当前界面
     * @return 是否请求成功
     */
    public static boolean requestBluetoothOpenPermission(Activity activity) {
        if (!hasBleOpenPermission(activity)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_PRIVILEGED,Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH_OPEN_CODE);
            return true;
        }
        return true;
    }

    /**
     * 判断是否有定位权限
     *
     * @param context 上下文
     * @return 是否授予权限
     */
    public static boolean hasLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static final int REQUEST_LOCATION_CODE = 129;

    /**
     * 请求定位权限
     *
     * @param activity 界面
     * @return 是否请求权限成功
     */
    public static boolean requestLocationPermission(Activity activity) {
        if (!hasLocationPermission(activity)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            return true;
        }
        return true;
    }
}
