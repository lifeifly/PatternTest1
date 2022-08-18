package com.yele.bluetoothlib.policy.event.cmd.update;

public class UpgradeActionEvent {

    public static final int TYPE_CONTROL = 0;
    public static final int TYPE_BLE = 1;
    public static final int TYPE_BMS = 2;
    public static final int TYPE_MCU = 3;
    public static final int TYPE_IOT_BLE = 4;
    public static final int TYPE_IOT_HARDWIRE = 5;
    public static final int TYPE_MCU_LOCK = 6;


    public int type;

    public static final int ACTION_START = 0;
    public static final int ACTION_STOP = 1;

    public int action;

    public String path;

    public UpgradeActionEvent(int type, int action, String path) {
        this.type = type;
        this.action = action;
        this.path = path;
    }
}
