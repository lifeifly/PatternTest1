package com.yele.huht.bluetoothsdklib.policy.event;

public class CmdResultEvent {

    public int cmd;

    public boolean isSuccess = false;

    public CmdResultEvent(int cmd, boolean isSuccess) {
        this.cmd = cmd;
        this.isSuccess = isSuccess;
    }
}
