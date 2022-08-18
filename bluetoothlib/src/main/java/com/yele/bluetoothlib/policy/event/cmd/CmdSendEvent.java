package com.yele.bluetoothlib.policy.event.cmd;


public class CmdSendEvent {

    public int cmd;

    public String channel;  // 0：车辆；1：头盔;2:背包
    public Object object;

    public CmdSendEvent() {
    }

    public CmdSendEvent(int cmd) {
        this.cmd = cmd;
    }

    public CmdSendEvent(int cmd, Object object) {
        this.cmd = cmd;
        this.object = object;
    }

    public CmdSendEvent(int cmd, String channel, Object object) {
        this.cmd = cmd;
        this.channel = channel;
        this.object = object;
    }
}
