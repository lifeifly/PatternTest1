package com.yele.hu.upgradetools.policy.event;


import com.yele.hu.upgradetools.bean.CmdFlag;

public class CmdSendEvent {

    public int cmd;

    public Object object;

    public CmdSendEvent() {
        CmdFlag.CMD_NO ++;
    }

    public CmdSendEvent(int cmd) {
        this.cmd = cmd;
    }

    public CmdSendEvent(int cmd, Object object) {
        this.cmd = cmd;
        this.object = object;
    }
}
