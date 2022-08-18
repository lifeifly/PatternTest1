package com.yele.hu.blesdk520demo.view.activity.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.yele.hu.blesdk520demo.util.CheckPermissionUtils;


public abstract class BasePerActivity extends BaseActivity {
    /**
     * 是否有定位权限
     * @return 是否有定位权限
     */
    protected boolean hasLocationPermission() {
        return CheckPermissionUtils.hasLocationPermission(this);
    }

    /**
     * 请求定位权限
     * @return 是否请求成功
     */
    protected boolean requestLocationPermission() {
        return CheckPermissionUtils.requestLocationPermission(this);
    }

    /**
     * 用户拒绝授予定位权限
     */
    protected void refuseLocationPermission() {
    }

    /**
     * 用户授予定位权限
     */
    protected void ensureLocationPermission() {
    }

    /**
     * 显示自定义请求定位权限的弹窗
     */
    protected void showRequestLocationDialog() {

    }

    /**
     * 是否有蓝牙打开的权限
     * @return 是否有蓝牙打开权限
     */
    protected boolean hasBleOpenPermission() {
        return CheckPermissionUtils.hasBleOpenPermission(this);
    }

    /**
     * 请求蓝牙打开权限
     * @return 是否请求成功
     */
    protected boolean requestBleOpenPermission() {
        return CheckPermissionUtils.requestBluetoothOpenPermission(this);
    }

    /**
     * 用户拒绝蓝牙权限
     */
    protected void refuseBleOpenPermission() {
    }

    /**
     * 用户授权蓝牙打开权限
     */
    protected void ensureBleOpenPermission() {
    }

    /**
     * 显示自定义请求打开蓝牙的权限的弹窗
     */
    protected void showRequestBleOpenPermissionDialog() {

    }

    /**
     * 判断是否有读写权限
     *
     * @return 是否有读写权限
     */
    protected boolean hasWriteOrReadPermission() {
        return CheckPermissionUtils.hasWriteOrReadPermission(this);
    }

    /**
     * 请求读写权限
     *
     * @return 请求成功与否
     */
    protected boolean requestSysWORPermission() {
        return CheckPermissionUtils.requestSystemWOrRPermission(this);
    }



    /**
     * 被拒绝了读写权限
     */
    protected void refuseROWPermission() {
    }

    /**
     * 确认授权读写权限
     */
    protected void ensureROWPermission() {
    }

    /**
     * 请求权限结果的返回函数
     *
     * @param requestCode  请求码
     * @param permissions  对应的权限数组
     * @param grantResults 权限数组对应的请求状态数组
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CheckPermissionUtils.REQUEST_CODE_WRITE_OR_READ_PERMISSIONS:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showRequestROWPermissionDialog();
                    } else {
                        refuseROWPermission();
                    }
                } else {
                    ensureROWPermission();
                }
                break;
            case CheckPermissionUtils.REQUEST_CODE_READ_PHONE_STATE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_PHONE_STATE)) {
                        showRequestReadPhoneStatePermissionDialog();
                        return;
                    } else {
                        refuseReadPhoneStatePermission();
                    }
                } else {
                    ensureReadPhoneStatePermission();
                }
                break;
            case CheckPermissionUtils.REQUEST_CODE_AUDIO_PERMISSIONS:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.BLUETOOTH_ADMIN)) {
                        showRequestBleOpenPermissionDialog();
                        return;
                    } else {
                        refuseBleOpenPermission();
                    }
                } else {
                    ensureBleOpenPermission();
                }
                break;
            case CheckPermissionUtils.REQUEST_LOCATION_CODE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        showRequestLocationDialog();
                        return;
                    } else {
                        refuseLocationPermission();
                    }
                } else {
                    ensureLocationPermission();
                }
                break;
            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 显示自定义读或者写的权限的弹窗
     */
    protected void showRequestROWPermissionDialog() {

    }


    /**
     * 是否有读取手机状态码的权限
     *
     * @return 是否有对应权限
     */
    protected boolean hasReadPhoneStatePermission() {
        return CheckPermissionUtils.hasPhoneStatePermission(this);
    }

    /**
     * 请求手机状态码的权限
     *
     * @return 是否请求成功
     */
    protected boolean requestReadPhoneStatePermission() {
        return CheckPermissionUtils.requestPhoneStatePermission(this);
    }

    /**
     * 确认有读取手机状态的权限
     */
    protected void ensureReadPhoneStatePermission() {
    }

    /**
     * 读取手机状态权限被拒绝
     */
    protected void refuseReadPhoneStatePermission() {
    }

    /**
     * 显示自定义请求手机状态的弹窗
     */
    protected void showRequestReadPhoneStatePermissionDialog() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
