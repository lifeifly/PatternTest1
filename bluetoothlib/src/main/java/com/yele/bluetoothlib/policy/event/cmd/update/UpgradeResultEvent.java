package com.yele.bluetoothlib.policy.event.cmd.update;

public class UpgradeResultEvent {

    public static final int SUCCESS = 0;
    public static final int FAILED = 1;
    public static final int UPDATE = 2;

    public int result;

    public String msg;

    public int percent;

    public UpgradeResultEvent(int result, String msg) {
        this.result = result;
        this.msg = msg;
    }
}
