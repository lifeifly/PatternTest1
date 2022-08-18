package com.yele.huht.bluetoothsdklib.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yele.huht.bluetoothsdklib.policy.event.BleDevRequestConEvent;

import org.greenrobot.eventbus.EventBus;

public class BluetoothMonitorReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action != null){
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.i("蓝牙状态","蓝牙正在打开");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.i("蓝牙状态","蓝牙已经打开");
//                            EventBus.getDefault().post(new BleServiceStatus(BleServiceStatus.BLUE_SERVER_CONNECT,null));
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.i("蓝牙状态","蓝牙正在关闭");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Log.i("蓝牙状态","蓝牙已经关闭");
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Log.i("蓝牙状态","蓝牙设备已连接");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Log.i("蓝牙状态","蓝牙设备已断开");
                    EventBus.getDefault().post(new BleDevRequestConEvent(BleDevRequestConEvent.CODE_RESULT_DISCONNECT, null));
                    break;
            }

        }
    }
}