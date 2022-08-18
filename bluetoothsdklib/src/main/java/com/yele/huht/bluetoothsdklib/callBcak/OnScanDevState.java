package com.yele.huht.bluetoothsdklib.callBcak;


import com.yele.huht.bluetoothsdklib.bean.OkaiBleDevice;

public interface OnScanDevState {

    void onScanSuccess(OkaiBleDevice device);

    void onScanFail(String msg);

}
