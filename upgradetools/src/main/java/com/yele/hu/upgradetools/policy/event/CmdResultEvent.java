package com.yele.hu.upgradetools.policy.event;

public class CmdResultEvent {

    public int cmd;

    public boolean isSuccess = false;

    public String str;

    public CmdResultEvent(int cmd, boolean isSuccess) {
        this.cmd = cmd;
        this.isSuccess = isSuccess;
    }

    public CmdResultEvent(int cmd, boolean isSuccess, String str) {
        this.cmd = cmd;
        this.isSuccess = isSuccess;
        this.str = str;
    }
}
