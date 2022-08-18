package com.yele.huht.bluetoothsdklib.bean;


/**
 * 锁状态类
 */
public class LockStateInfo {

    // 车辆锁
    public int vehicleLock;
    // 电池锁
    public int batteryLock;
    // 直干锁
    public int straightLock;
    // 篮筐锁
    public int basketLock;
    // 备用锁
    public int spareLock;


    @Override
    public String toString() {
        return "LockStateInfo{" +
                "vehicleLock=" + vehicleLock +
                ", batteryLock=" + batteryLock +
                ", straightLock=" + straightLock +
                ", basketLock=" + basketLock +
                ", spareLock=" + spareLock +
                '}';
    }
}
