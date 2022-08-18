package com.yele.hu.upgradetools.bean.comfig.car;

public class LockConfig {

    /**
     * 车辆锁
     */
    public int carLock;

    /**
     * 电池锁
     */
    public int batteryLock;

    /**
     * 直杆锁
     */
    public int poleLock;

    /**
     * 篮筐锁
     */
    public int rimLock;

    /**
     * 备用锁
     */
    public int spareLock;

    public LockConfig(int carLock, int batteryLock, int poleLock, int rimLock, int spareLock) {
        this.carLock = carLock;
        this.batteryLock = batteryLock;
        this.poleLock = poleLock;
        this.rimLock = rimLock;
        this.spareLock = spareLock;
    }
}
