package com.yele.blesdklibrary.port;

import android.bluetooth.BluetoothDevice;

import com.yele.blesdklibrary.bean.OkaiBleDevice;

public interface OnScanDevStateBack {

    void onScanSuccess(OkaiBleDevice device);

}
