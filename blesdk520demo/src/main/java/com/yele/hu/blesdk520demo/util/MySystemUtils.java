package com.yele.hu.blesdk520demo.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.List;
import java.util.Properties;

import static android.content.Context.ACTIVITY_SERVICE;

public class MySystemUtils {

	/**
	 * 判断当前应用是否在后台（需要权限）
	 *
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean isApplicationBroughtToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取md5指纹信息(签名文件不同,md5指纹也不同;不同包名app可以具有相同的md5指纹信息)
	 * 示例输出:
	 * sign: 55:2e:ba:e6:b4:7e:ac:e3:02:58:64:9a:db:82:87:b6
	 */
	public static void checkCertificate(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			Signature sig = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures[0];
			String md5Fingerprint = doFingerprint(sig.toByteArray(), "MD5");
			Log.d("sign:", md5Fingerprint);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param certificateBytes 获取到应用的signature值
	 * @param algorithm        在上文指定MD5算法
	 * @return md5签名
	 */
	private static String doFingerprint(byte[] certificateBytes, String algorithm) throws Exception {
		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(certificateBytes);
		byte[] digest = md.digest();

		String toRet = "";
		for (int i = 0; i < digest.length; i++) {
			if (i != 0) {
				toRet += ":";
			}
			int b = digest[i] & 0xff;
			String hex = Integer.toHexString(b);
			if (hex.length() == 1) {
				toRet += "0";
			}
			toRet += hex;
		}
		return toRet;
	}


	/**
	 * 判断某个包是否打开
	 *
	 * @param context
	 * @param packageName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean isPackageOpen(Context context, String packageName) {
		boolean isInBackground = false;
		ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
			List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
			for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
				// 前台程序
				if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					for (String activeProcess : processInfo.pkgList) {
						if (activeProcess.equals(packageName)) {
							isInBackground = true;
						}
					}
				}
			}
		} else {
			List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
			ComponentName componentInfo = taskInfo.get(0).topActivity;
			if (componentInfo.getPackageName().equals(packageName)) {
				isInBackground = true;
			}
		}
		return isInBackground;
	}
	/**
	 * 判断当前手机是否安装了制定的包名
	 * @param context
	 * @param pkgName
	 * @return
	 */
	public static boolean isPkgInstalled(Context context, String pkgName) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 获取当前APP版本信息
	 * @param context
	 * @return
     */
	public static int getAppVersion(Context context){
		int version = 1;
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(),0);
			version = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * 获取当前APP版本信息
	 * @param context 当前的上下文
	 * @return 当前版本的名称
	 */
	public static String getAppVersionName(Context context){
		String versionName = null;
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(),0);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 判断当前Service是否运行
	 * @param context 当前的上下文
	 * @param serviceName 当前服务的名称
     * @return
     */
	public static boolean isServiceRunning(Context context, String serviceName) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(200);
		if (runningServiceInfos.size() <= 0) {
			return false;
		}
		for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
			if (serviceInfo.service.getClassName().equals(serviceName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断当前屏幕是否亮着
	 * @param context
	 * @return
     */
	public static boolean isScreenOn(Context context){
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		boolean isScreenOn = pm.isScreenOn();//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
		return isScreenOn;
	}

	public static final int SYS_INIT = 0;		//原生系统
	public static final int SYS_EMUI = 1;		//华为阉割版
	public static final int SYS_MIUI = 2;		//小米太监版
	public static final int SYS_FLYME = 3;		//魅族阉割版
	private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
	private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
	private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
	private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
	private static final String KEY_EMUI_VERSION = "ro.build.version.emui";
	private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";

	public static int getSystem(){
		int SYS = SYS_INIT;
		try {
			Properties prop= new Properties();
			prop.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
			if(prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
					|| prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
					|| prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null){
				SYS = SYS_MIUI;//小米
			}else if(prop.getProperty(KEY_EMUI_API_LEVEL, null) != null
					||prop.getProperty(KEY_EMUI_VERSION, null) != null
					||prop.getProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, null) != null){
				SYS = SYS_EMUI;//华为
			}else if(getMeizuFlymeOSFlag().toLowerCase().contains("flyme")){
				SYS = SYS_FLYME;//魅族
			};
		} catch (IOException e){
			e.printStackTrace();
			return SYS;
		}
		return SYS;
	}

	public static String getMeizuFlymeOSFlag() {
		return getSystemProperty("ro.build.display.id", "");
	}

	private static String getSystemProperty(String key, String defaultValue) {
		try {
			Class<?> clz = Class.forName("android.os.SystemProperties");
			Method get = clz.getMethod("get", String.class, String.class);
			return (String)get.invoke(clz, key, defaultValue);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	/**
	 * 当前正在运行的服务
	 * @param context
	 * @param pkName
	 * @return
	 */
	public static boolean isServiceProcessRunning(Context context, String pkName) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> list = activityManager.getRunningServices(100);
		for (ActivityManager.RunningServiceInfo info : list) {
			if (info.process.equals(pkName)) {
				return true;
			}
		}
		return false;
	}

	@SuppressLint("MissingPermission")
	public static String getPhoneImei(Context context) {
		String imeiId = null;
		try {
			//实例化TelephonyManager对象
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			imeiId = telephonyManager.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imeiId;
	}

}
