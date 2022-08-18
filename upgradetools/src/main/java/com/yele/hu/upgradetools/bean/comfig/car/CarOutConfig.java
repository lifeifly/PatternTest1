package com.yele.hu.upgradetools.bean.comfig.car;

public class CarOutConfig {

    /**
     * 单双刹选择
     * 0单刹（左）     霍尔刹
     * 1双刹           霍尔刹
     * 2单刹           开关刹
     */
    public int brakeSelect = 0;

    /**
     * 仪表的显示模式
     * 0：为YD
     * 1：为KM
     */
    public int YB_SHOW_MODE = 0;

    /**
     * 档位X
     * 档位1（E） 0：无此档位
     *           1：限速5
     * 档位2（L）0：无此档位
     *          1：限速15
     * 档位3（H）0：无此档位
     *          1：限速20
     *          1：限速25
     *          1：限速63
     * 加速曲线累加：0：L,H加速曲线分别为0，1
     *              1：L,H加速曲线分别为1，2
     *              2：L,H加速曲线分别为2，3........
     *
     * 0：无此档位
     * 1—6：选择相应加速曲线
     */
    public int gear1 = 0;
    public int gear2 = 0;
    public int gear3 = 0;
    public int gear4 = 0;

    /**
     * 电池保护板类型
     * 0：硬件板
     * 1：软件板
     */
    public int batteryType = 0;

    /**
     * 电子刹车力度档位选择
     * 数字由0-3，电子刹车力度由弱增强
     */
    public int electronicBrakeSelect = 0;

    /**
     * 开机车大灯模式
     * 0：开机车大灯默认熄灭
     * 1：开机车大灯默认点亮
     */
    public int openCarLedMode = 0;

    /**
     * 正常模式下尾灯模式
     * 0：无刹车:低亮  刹车:低亮
     * 1：无刹车:高亮  刹车:高亮
     * 2：无刹车:低亮  刹车:高低亮
     * 3：无刹车:低亮  刹车:高亮
     *
     */
    public int taillightMode = 0;

    /**
     * 车辆销售地编码
     * 由此码可查表得该车辆批次发往的销售地区
     */
    public String salesLocationCode;

    /**
     * 客户编码
     * 可由此码查表得该车辆批次的购买客户（公司）
     */
    public String customerCode;

    /**
     * 车辆型号
     * 用于表示当前车辆的型号
     */
    public String carModel;

    /**
     * 蓝牙缩写
     * 广播包中的型号缩写，用于区分不同车型
     * 0：S052T
     * 1：S521T
     * 2：S522T
     */
    public int bleModel;

    /**
     * 蓝牙开关(目前仅ES500B有此功能)
     * 0：开启蓝牙
     * 1：关闭蓝牙
     */
    public int bleSwitch = 0;

    public CarOutConfig() {
    }

    public CarOutConfig(int brakeSelect, int gear1, int gear2, int gear3, int gear4, int batteryType, int electronicBrakeSelect, int openCarLedMode, int taillightMode, String salesLocationCode, String customerCode, String carModel, int bleModel) {
        this.brakeSelect = brakeSelect;
        this.gear1 = gear1;
        this.gear2 = gear2;
        this.gear3 = gear3;
        this.gear4 = gear4;
        this.batteryType = batteryType;
        this.electronicBrakeSelect = electronicBrakeSelect;
        this.openCarLedMode = openCarLedMode;
        this.taillightMode = taillightMode;
        this.salesLocationCode = salesLocationCode;
        this.customerCode = customerCode;
        this.carModel = carModel;
        this.bleModel = bleModel;
    }


    public CarOutConfig(int brakeSelect, int gear1, int gear2, int gear3, int gear4, int batteryType, int electronicBrakeSelect, int openCarLedMode, int taillightMode, String salesLocationCode, String customerCode, String carModel, int bleModel, int bleSwitch) {
        this.brakeSelect = brakeSelect;
        this.gear1 = gear1;
        this.gear2 = gear2;
        this.gear3 = gear3;
        this.gear4 = gear4;
        this.batteryType = batteryType;
        this.electronicBrakeSelect = electronicBrakeSelect;
        this.openCarLedMode = openCarLedMode;
        this.taillightMode = taillightMode;
        this.salesLocationCode = salesLocationCode;
        this.customerCode = customerCode;
        this.carModel = carModel;
        this.bleModel = bleModel;
        this.bleSwitch = bleSwitch;
    }
}
