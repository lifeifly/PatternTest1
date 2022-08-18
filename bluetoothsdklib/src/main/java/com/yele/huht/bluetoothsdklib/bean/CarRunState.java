package com.yele.huht.bluetoothsdklib.bean;

/**
 * 上报数据
 */
public class CarRunState {
    /**
     * 锁状态
     * 0：解锁
     * 1：锁定
     */
    public static int lock = 0;
    /**
     * 当前车辆速度（00.0~99.9）（单位：.1km/h）
     */
    public static float speed = 0.0f;
    /**
     * 当前里程（00.0~99.9）（单位：.1KM）
     */
    public static float curMileage = 0.0f;
    /**
     * 剩余里程（00.0~99.9）（单位：.1KM）
     */
    public static float surplusMileage = 0.0f;
    /**
     * 总里程（00.0 ~ 1677721.5）（单位：.1KM）
     */
    public static double totalMileage = 0.0f;
    /**
     * 骑行时间(00000 - 36000)(单位：S)
     */
    public static int rideTime = 0;
    /**
     * 车灯状态
     */
    public static int ledState = 0;
    /**
     * 车辆加速模式
     */
    public static int adMode = 0;
    /**
     * 充电MOS状态
     * 0：关闭
     * 1：打开
     */
    public static int chargeMos = 0;
    /**
     * 放电MOS状态
     * 0：关闭
     * 1：打开
     */
    public static int dischargeMos = 0;
    /**
     * 电池电量（0~100.0）（单位：0.1%）
     */
    public static int power = 0;
    /**
     * 电池健康度（0~100.0）（单位：0.1%）
     */
    public static int soh = 0;
    /**
     * 电芯最高温度(±0 - 255)(单位：℃）
     */
    public static int eleCoreHigh = 0;
    /**
     * 电芯最低温度(±0 - 255)(单位：℃）
     */
    public static int eleCoreLow = 0;
    /**
     * MOS管温度(±0 - 255)(单位：℃）
     */
    public static int mosTemp = 0;
    /**
     * 其他温度(±0 - 255)(单位：℃）
     */
    public static int otherTemp = 0;
    /**
     * 电池电流（±0000000 - 1000000）（单位：ma)
     */
    public static long current = 0;
    /**
     * 电池电压(000000 - 100000)(单位：mV)
     */
    public static long voltage = 0;
    /**
     * 电池充电标志
     * 0：没有链接充电器
     * 1：充电器已连接
     * 2：正在充电
     * 3：充电完成
     */
    public static int chargeFlag = 0;
    /**
     * 电池循环次数
     */
    public static int loopTimes = 0;
    /**
     * 加速转把AD值（0-10000）
     */
    public static int accelerateAD = 0;
    /**
     * 刹把AD（0-10000）
     */
    public static int brakeADLeft = 0;

    public static int brakeADRight = 0;


    /**
     * 开关机模式
     * 0：芯片休眠/唤醒模式，蓝牙广播不停止
     * 1：芯片掉电/上电模式，蓝牙广播停止
     */
    public static int openMode = 0;
    /**
     * 定速巡航模式
     * 0：关闭车辆定速巡航模式
     * 1：开启车辆定速巡航模式
     */
    public static int dlccMode = 0;
    /**
     * 车身锁的开关状态
     * 0：关闭车辆锁模式
     * 1：打开车辆锁模式
     */
    public static int isLockOpen = 0;
    /**
     * 车辆骑行时的启动模式
     * 0：车辆助力模式启动
     * 1：无助力模式
     */
    public static int driveMode = 0;

    /**
     * S档开关模式
     */
    public static int sGearMode = 0;

    /**
     * 当前控制器温度
     */
    public static int controlTemp = 0;

    /**
     * 电机温度
     */
    public static int motorTemp = 0;

    /**
     * 当前限流值
     */
    public static int curLimitValue = 0;

    /**
     * 当前驱动值
     */
    public static int curDriveValue = 0;

    /**
     * 当前刹车值
     */
    public static int curBrakeValue = 0;

    /**
     * 控制器开关状态
     */
    public static int controlOpen = 0;

    /**
     * 电池控制器通讯失败次数
     */
    public static int batteryFail = 0;
}
