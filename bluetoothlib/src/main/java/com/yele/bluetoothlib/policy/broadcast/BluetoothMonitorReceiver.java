package com.yele.bluetoothlib.policy.broadcast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.yele.baseapp.utils.LogUtils;
import com.yele.bluetoothlib.bean.LogDebug;

public class BluetoothMonitorReceiver extends BroadcastReceiver {

    private static final String TAG = "BluetoothMonitorReceive";

    private boolean DEBUG = true;

    private void logw(String msg) {
        if (!DEBUG || !LogDebug.IS_LOG) {
            return;
        }
        LogUtils.w(TAG,msg);
    }

    public BluetoothMonitorReceiver(OnDeviceConnectChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            logw("蓝牙正在打开");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Toast.makeText(context, "蓝牙已经打开", Toast.LENGTH_SHORT).show();
                            if (listener != null) {
                                listener.bleOpen();
                            }
                            logw("蓝牙已经打开");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            logw("蓝牙正在关闭");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            if (listener != null) {
                                listener.bleClose();
                            }
                            logw("蓝牙已经关闭");
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    if (listener != null) {
                        BluetoothDevice deviceCon = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        listener.devConnected(deviceCon);
                    }
                    logw("蓝牙设备已连接");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (listener != null) {
                        String name = device.getName();
                        listener.devDisconnected(device);
                    }
                    logw("蓝牙设备已断开");
                    break;
            }
        }
    }

    private OnDeviceConnectChangeListener listener;

    public interface OnDeviceConnectChangeListener{
        /**
         * 蓝牙已打开
         */
        void bleOpen();

        /**
         * 蓝牙已关闭
         */
        void bleClose();

        /**
         * 设备连接成功
         * @param device 连接上的设备
         */
        void devConnected(BluetoothDevice device);

        /**
         * 设备断开连接
         * @param device 断开的设备
         */
        void devDisconnected(BluetoothDevice device);
    }
}
