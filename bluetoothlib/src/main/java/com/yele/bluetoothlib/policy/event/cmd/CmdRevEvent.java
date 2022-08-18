package com.yele.bluetoothlib.policy.event.cmd;

import com.yele.bluetoothlib.bean.cmd.RevResult;

public class CmdRevEvent {

    public String data;

    public RevResult object;

    public CmdRevEvent(String data, RevResult object) {
        this.data = data;
        this.object = object;
    }
}
