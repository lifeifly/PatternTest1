package com.yele.huht.bluetoothsdklib.policy.event;


import com.yele.huht.bluetoothsdklib.bean.CmdFlag;

public class CmdSendEvent {

    public int cmd;

    public CmdSendEvent() {
        CmdFlag.CMD_NO ++;
    }

    public CmdSendEvent(int cmd) {
        this.cmd = cmd;
    }
}
