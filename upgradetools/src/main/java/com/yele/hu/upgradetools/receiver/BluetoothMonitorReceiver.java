package com.yele.hu.upgradetools.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yele.baseapp.utils.LogUtils;

public class BluetoothMonitorReceiver extends BroadcastReceiver {

    private static final String TAG = "BluetoothMonitorReceive——蓝牙状态";

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

    public BluetoothMonitorReceiver(OnDeviceConnectChangeListener listener) {
        this.listener = listener;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action != null){
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            LogUtils.i(TAG,"蓝牙正在打开");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            LogUtils.i(TAG,"蓝牙已经打开");
                            if(listener != null){
                                listener.bleOpen();
                            }
//                            EventBus.getDefault().post(new BleServiceStatus(BleServiceStatus.BLUE_SERVER_CONNECT,null));
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            LogUtils.i(TAG,"蓝牙正在关闭");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            LogUtils.i(TAG,"蓝牙已经关闭");
                            if(listener != null){
                                listener.bleClose();
                            }
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    LogUtils.i(TAG,"蓝牙设备已连接");
                    BluetoothDevice deviceCon = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(listener != null){
                        listener.devConnected(deviceCon);
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    LogUtils.i(TAG,"蓝牙设备已断开");
//                    EventBus.getDefault().post(new BleDevRequestConEvent(BleDevRequestConEvent.CODE_RESULT_DISCONNECT, null));
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(listener != null){
                        listener.devDisconnected(device);
                    }
                    break;
            }

        }
    }
}