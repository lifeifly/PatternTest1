package com.yele.bluetoothlib.bean.device;

public class RunInfoReport {
    // 锁状态
    public int lock;
    // 速度 .1km/h
    public float speed;
    // 当前里程、剩余里程、总里程 .1km
    public float currentMileage,surplusMileage,totalMileage;
    // 骑行时间 s  大灯状态  加速模式
    public int rideTime,ledState,accMode;
    // 电池电量
    public float electricity;
    // 充电标志
    public int chargeFlag;
    // 定速巡航状态
    public int dlccFlag;
    // 锁车状态
    public int carLock;
    // 启动状态
    public int startOver;
    // S档开关
    public int SMode;

}
