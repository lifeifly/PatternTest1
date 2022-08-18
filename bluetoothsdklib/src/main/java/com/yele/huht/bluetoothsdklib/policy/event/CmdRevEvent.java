package com.yele.huht.bluetoothsdklib.policy.event;


import com.yele.huht.bluetoothsdklib.bean.RevResult;
import com.yele.huht.bluetoothsdklib.service.BleService;

public class CmdRevEvent {

    public String data;

    public RevResult object;

    public CmdRevEvent(String data,RevResult object) {
        this.data = data;
        this.object = object;
    }
}
