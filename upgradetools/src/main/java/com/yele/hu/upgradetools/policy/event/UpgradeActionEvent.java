package com.yele.hu.upgradetools.policy.event;

public class UpgradeActionEvent {

    public static final int TYPE_CONTROL = 0;
    public static final int TYPE_BLE = 1;
    public static final int TYPE_MCU = 2;
    public static final int TYPE_BMS = 3;   // 16：电池保护板。
    public static final int TYPE_CONTROL_LENGTH = 4;

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
