package com.yele.blesdklibrary.bean;

/**
 * 蓝牙参数配置类
 */
public class CarRunState {
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
    public float totalMileage;
    /**
     * 骑行时间(00000 - 36000)(单位：S)
     */
    //private int rideTime;
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
    //private int chargeMos;
    /**
     * 放电MOS状态
     * 0：关闭
     * 1：打开
     */
    //private int dischargeMos;
    /**
     * 电池电量（0~100.0）（单位：0.1%）
     */
    public float power;
    /**
     * 电池健康度（0~100.0）（单位：0.1%）
     */
    //private float soh;
    /**
     * 电芯最高温度(±0 - 255)(单位：℃）
     */
    //private int eleCoreHigh;
    /**
     * 电芯最低温度(±0 - 255)(单位：℃）
     */
    //private int eleCoreLow;
    /**
     * MOS管温度(±0 - 255)(单位：℃）
     */
    //private int mosTemp;
    /**
     * 其他温度(±0 - 255)(单位：℃）
     */
    //private int otherTemp;
    /**
     * 电池电流（±0000000 - 1000000）（单位：ma)
     */
    //private long current;
    /**
     * 电池电压(000000 - 100000)(单位：mV)
     */
    //private long voltage;
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
    //private long cycleIndex;
    /**
     * 加速转把AD值（0-10000）
     */
    //private int accelerateAD;
    /**
     * 刹把AD（0-10000）
     */
    //private int brakeADLeft;
    //private int brakeADRight;


    /**
     * 开关车身定速巡航
     * 0：关闭车辆定速巡航模式。
     * 1：开启车辆定速巡航模式。
     */
    public int carCruise;

    /**
     * 开关车身锁车
     * 0：关闭车辆锁车模式。
     * 1：开启车辆锁车模式。
     */
    public int carLockMode;

    /**
     * 车辆启动模式
     * 0：车辆助力启动模式。
     * 1：车辆无助力启动模式
     */
    public int carOpenMode;

    /**
     * 开关机模式
     * 0：开关机模式修改为芯片休眠/唤醒模式，蓝牙广播不停止
     * 1：开关机模式修改为芯片掉电/上电模式，蓝牙广播停止
     */
    public int carSwitch;


    @Override
    public String toString() {
        return "CarRunState{" +
                "lock=" + lock +
                ", speed=" + speed +
                ", curMileage=" + curMileage +
                ", surplusMileage=" + surplusMileage +
                ", totalMileage=" + totalMileage +
                ", ledState=" + ledState +
                ", addMode=" + addMode +
                ", power=" + power +
                ", chargeFlag=" + chargeFlag +
                ", carCruise=" + carCruise +
                ", carLockMode=" + carLockMode +
                ", carOpenMode=" + carOpenMode +
                ", carSwitch=" + carSwitch +
                '}';
    }
}
