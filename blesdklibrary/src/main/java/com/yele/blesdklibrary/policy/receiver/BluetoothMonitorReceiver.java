package com.yele.blesdklibrary.policy.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yele.blesdklibrary.bean.DebugInfo;
import com.yele.blesdklibrary.util.LogUtils;

public class BluetoothMonitorReceiver extends BroadcastReceiver {

    private BleConnectBack back;


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action != null){
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            logI("蓝牙状态","蓝牙正在打开");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            logI("蓝牙状态","蓝牙已经打开");
//                            EventBus.getDefault().post(new BleServiceStatus(BleServiceStatus.BLUE_SERVER_CONNECT,null));
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            logI("蓝牙状态","蓝牙正在关闭");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            logI("蓝牙状态","蓝牙已经关闭");
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    logI("蓝牙状态","蓝牙设备已连接");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    logI("蓝牙状态","蓝牙设备已断开");
                    if (back != null) {
                        back.disconnected();
                    }
                    break;
            }

        }
    }

    public void setBack(BleConnectBack back) {
        this.back = back;
    }


    private void logI(String tag,String msg){
        if(DebugInfo.IS_DEBUG){
            LogUtils.i(tag,msg);
        }
    }
}