package com.yele.bluetoothlib.policy.event.cmd;

public class CmdResultEvent {

    public int cmd;

    public boolean isSuccess = false;

    public CmdResultEvent(int cmd, boolean isSuccess) {
        this.cmd = cmd;
        this.isSuccess = isSuccess;
    }
}
