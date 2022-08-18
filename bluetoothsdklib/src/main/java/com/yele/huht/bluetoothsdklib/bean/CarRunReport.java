package com.yele.huht.bluetoothsdklib.bean;

/**
 * 蓝牙参数配置类
 */
public class CarRunReport {
    /**
     * 锁状态
     * 0：解锁
     * 1：锁定
     */
    public int lock;
    /**
     * 当前车辆速度（00.0~99.9）（单位：.1km/h）
     */
    public float speed;
    /**
     * 当前里程（00.0~99.9）（单位：.1KM）
     */
    public float curMileage;
    /**
     * 剩余里程（00.0~99.9）（单位：.1KM）
     */
    public float surplusMileage;
    /**
     * 总里程（00.0 ~ 1677721.5）（单位：.1KM）
     */
    public double totalMileage;
    /**
     * 骑行时间(00000 - 36000)(单位：S)
     */
    public int rideTime;
    /**
     * 车灯状态,0关1开
     */
    public int ledState;
    /**
     * 车辆加速模式,0柔和加速模式，1运动加速模式
     */
    public int addMode;
    /**
     * 充电MOS状态
     * 0：关闭
     * 1：打开
     */
    public int chargeMos;
    /**
     * 放电MOS状态
     * 0：关闭
     * 1：打开
     */
    public int dischargeMos;
    /**
     * 电池电量（0~100.0）（单位：0.1%）
     */
    public int power;
    /**
     * 电池健康度（0~100.0）（单位：0.1%）
     */
    public int soh;
    /**
     * 电芯最高温度(±0 - 255)(单位：℃）
     */
    public int eleCoreHigh;
    /**
     * 电芯最低温度(±0 - 255)(单位：℃）
     */
    public int eleCoreLow;
    /**
     * MOS管温度(±0 - 255)(单位：℃）
     */
    public int mosTemp;
    /**
     * 其他温度(±0 - 255)(单位：℃）
     */
    public int otherTemp;
    /**
     * 电池电流（±0000000 - 1000000）（单位：ma)
     */
    public long current;
    /**
     * 电池电压(000000 - 100000)(单位：mV)
     */
    public long voltage;
    /**
     * 电池充电标志
     * 0：没有链接充电器
     * 1：充电器已连接
     * 2：正在充电
     * 3：充电完成
     */
    public int chargeFlag;
    /**
     * 电池循环次数
     */
    public long cycleIndex;
    /**
     * 加速转把AD值（0-10000）
     */
    public int accelerateAD;
    /**
     * 刹把AD（0-10000）
     */
    public int brakeADLeft;

    public int brakeADRight;


    /**
     * 开关车身定速巡航
     * 0：关闭车辆定速巡航模式。
     * 1：开启车辆定速巡航模式。
     */
    public static int carCruise = 0;

    /**
     * 开关车身锁车
     * 0：关闭车辆锁车模式。
     * 1：开启车辆锁车模式。
     */
    public static int carLockMode = 0;

    /**
     * 车辆启动模式
     * 0：车辆助力启动模式。
     * 1：车辆无助力启动模式
     */
    public static int carOpenMode = 0;

    /**
     * 开关机模式
     * 0：开关机模式修改为芯片休眠/唤醒模式，蓝牙广播不停止
     * 1：开关机模式修改为芯片掉电/上电模式，蓝牙广播停止
     */
    public static int carSwitch = 0;

    /**
     * 车辆加速模式
     */
    public static int adMode = 0;

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


    @Override
    public String toString() {
        return "CarRunReport{" +
                "lock=" + lock +
                ", speed=" + speed +
                ", curMileage=" + curMileage +
                ", surplusMileage=" + surplusMileage +
                ", totalMileage=" + totalMileage +
                ", rideTime=" + rideTime +
                ", ledState=" + ledState +
                ", addMode=" + addMode +
                ", chargeMos=" + chargeMos +
                ", dischargeMos=" + dischargeMos +
                ", power=" + power +
                ", soh=" + soh +
                ", eleCoreHigh=" + eleCoreHigh +
                ", eleCoreLow=" + eleCoreLow +
                ", mosTemp=" + mosTemp +
                ", otherTemp=" + otherTemp +
                ", current=" + current +
                ", voltage=" + voltage +
                ", chargeFlag=" + chargeFlag +
                ", cycleIndex=" + cycleIndex +
                ", accelerateAD=" + accelerateAD +
                ", brakeADLeft=" + brakeADLeft +
                ", brakeADRight=" + brakeADRight +
                '}';
    }
}
