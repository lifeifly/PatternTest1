package com.yele.bluetoothlib.bean.config;

public class ProduceConfig {

    // 单杀双选择  1:单刹  2：双刹
    public int brakeMode = 0;

    // 速度单位  0:mph  1:km/h
    public int speedUnit = 0;

    // 档位1,2,3,4
    public int gear1 = 0,gear2,gear3,gear4;

    // 电池保护板类型  0:硬件版  1：软件版
    public int bmsType = 0;

    // 电子刹力度档位
    public int electronicBrake = 0;

    // 开机车大灯模式  0：默认熄灭  1：默认电量
    public int openFrontLed = 0;

    // 正常模式尾灯  0：无刹车/刹车 低亮 1:都高亮  2：无低亮 有高低亮 3：无低 有高
    public int normalTaillight = 0;

    // 车辆销售地编码
    public int salesNo = 0;

    // 客户编码
    public int customerNo = 0;

    public String carTypeName = null;

    public int bleType = 0;

    public ProduceConfig(int brakeMode, int speedUnit, int gear1, int gear2, int gear3, int gear4, int bmsType,
                         int electronicBrake, int openFrontLed, int normalTaillight, int salesNo, int customerNo) {
        this.brakeMode = brakeMode;
        this.speedUnit = speedUnit;
        this.gear1 = gear1;
        this.gear2 = gear2;
        this.gear3 = gear3;
        this.gear4 = gear4;
        this.bmsType = bmsType;
        this.electronicBrake = electronicBrake;
        this.openFrontLed = openFrontLed;
        this.normalTaillight = normalTaillight;
        this.salesNo = salesNo;
        this.customerNo = customerNo;
    }

    public ProduceConfig(int brakeMode, int speedUnit, int gear1, int gear2, int gear3, int gear4, int bmsType,
                         int electronicBrake, int openFrontLed, int normalTaillight, int salesNo, int customerNo, String carTypeName, int bleType) {
        this(brakeMode,speedUnit,gear1,gear2,gear3,gear4,bmsType,electronicBrake,openFrontLed,normalTaillight,salesNo,customerNo);
        this.carTypeName = carTypeName;
        this.bleType = bleType;
    }

    public ProduceConfig() {
    }

    public String toCmdString() {
        String str = brakeMode +
                "," + speedUnit +
                "," + gear1 +
                "," + gear2 +
                "," + gear3 +
                "," + gear4 +
                "," + bmsType +
                "," + electronicBrake +
                "," + openFrontLed +
                "," + normalTaillight +
                "," + salesNo +
                "," + customerNo;
        if (carTypeName != null && !"".equals(carTypeName)) {
            str += "," + carTypeName + "," + bleType;
        }
        return  str;
    }
}
