package com.yele.hu.upgradetools.bean.info.car;

public class DeviceConfig {

    /**
     * 当前蓝牙的SN
     */
    public static String SN = "00000000000000";
    /**
     * 车型名称
     */
    public static String typeName = "ES200T";

    /**
     * 测试标志
     * 0：取消/停止成品测试。
     * 1：开始成品测试。
     */
    public static int goodTest = 0;

    /**
     * 单双刹选择
     * 0单刹（左）     霍尔刹
     * 1双刹           霍尔刹
     * 2单刹           开关刹
     */
    public static int brakeSelect = 0;

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
    public static int gear1 = 0;
    public static int gear2 = 0;
    public static int gear3 = 0;
    public static int gear4 = 0;

    /**
     * 电池保护板类型
     * 0：硬件板
     * 1：软件板
     */
    public static int batteryType = 0;

    /**
     * 电子刹车力度档位选择
     * 数字由0-3，电子刹车力度由弱增强
     */
    public static int electronicBrakeSelect = 0;

    /**
     * 开机车大灯模式
     * 0：开机车大灯默认熄灭
     * 1：开机车大灯默认点亮
     */
    public static int openCarLedMode = 0;

    /**
     * 正常模式下尾灯模式
     * 0：无刹车:低亮  刹车:低亮
     * 1：无刹车:高亮  刹车:高亮
     * 2：无刹车:低亮  刹车:高低亮
     * 3：无刹车:低亮  刹车:高亮
     *
     */
    public static int taillightMode = 0;

    /**
     * 车辆销售地编码
     * 由此码可查表得该车辆批次发往的销售地区
     */
    public static String salesLocationCode;

    /**
     * 客户编码
     * 可由此码查表得该车辆批次的购买客户（公司）
     */
    public static String customerCode;

    /**
     * 车辆型号
     * 用于表示当前车辆的型号
     */
    public static String carModel;

    /**
     * 开启学习模式
     * 0：取消/停止学习模式。
     * 1：开始学习模式。
     */
    public static int STUDY_MODE = 0;

    /**
     * <钥匙序号> ：
     *  0：保留
     *  1：开始学习模钥匙 1
     *  2：开始学习模钥匙 2
     */
    public static int STUDY_KEY_NUM = 0;

    /**
     * 电池码
     */
    public static String batteryCode;

    /**
     * 清数据标志
     * 1：车辆清除骑行数据。
     * 0：车辆不清除骑行数据
     */
    public static int clearData = 0;

    /**
     * 发船模式标志
     * 1：车辆切换发船模式。
     * 0：车辆不切换发船模式。
     * 上报执行结果（成功/失败）
     */
    public static int shippingMode = 0;

    /**
     * 读取配置标志
     * 0：不读取配置。
     * 1：读取配置（有OKFCG应答）。测试模式  超级密码
     */
    public static int hasReadConfig = 0;

    /**
     * 报警器控制
     * 1：打开
     * 0：关闭
     */
    public static int warnControl = 0;

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
     * 当前车辆大灯的状态开关
     */
    public static boolean isOpen = false;
    /**
     * 当前车辆的密码
     * 车辆密码就用到的了一个地方：模式切换命令中用到了该密码（其他用的都是蓝牙密码）
     */
    public static String PWD = "OKAI_CAR";

    /**
     * 当前车辆上报信息的时间间隔
     * 默认为100ms，最大9999ms,0表示不上报
     */
    public static int SPACE = 10;
    /**
     * 当前车辆允许的最大速度
     * 速度范围为0~63（单位：KM/h）
     */
    public static int MAX_SPEED = 0;
    /**
     * 车辆的加速模式
     * 0：柔和模式
     * 1：运动模式
     */
    public static int ADD_MODE = 0;
    /**
     * 待机-关机时间（0~1800）（默认为30）（单位：S）
     */
    public static int WAIT_TIME = 0;
    /**
     * 所有的锁是否都锁
     * true:所有车都锁
     * false:所有车解锁
     */
    public static boolean allLock = false;

    public static int lock_p = 0;

    /**
     * 控制器软件版本
     */
    public static String controlSV;
    /**
     * 控制器硬件版本
     */
    public static String controlWV;
    /**
     * BMS软件版本
     */
    public static String bmsSV;
    /**
     * BMS硬件版本
     */
    public static String bmsWV;
    /**
     * 当前的错误码
     */
    public static String errCode;

    public static String ybCode;

    /**
     * 是否开启S档开关
     * 1-开启S档
     * 0-关闭S档
     */
    public static int hasGearOpen = 0;

    /**
     * 读写
     * 0: 读。
     * 1：写。
     */
    public static int readOrWrite = 0;

    /**
     * <相应模式>:
     * 0x0 模式统一(各模式属于成套情况下)
     * 0x1 骑行模式
     * 0x2 待机模式
     * 0x3 充电模式41
     * 0x4 解锁模式
     * 0X5 报错模式
     */
    public static int carMode = 0x0;

    /**
     * <样式>:
     * 0x0: 关闭
     * 0x1: 开
     * 0x2: 单向循环
     * 0x3: 双向循环
     * 0x4: 下坠循环
     * 0x5: 呼吸循环
     * 0x6: 上升循环
     * 0x7: 瀑布循环
     */
    public static int atmosphereLightStyle = 0x0;

    /**
     * <RGB 颜色值>:
     * FC0003:红色
     * FFFC03:黄色
     * 03FC00:绿色
     * 03FFFC:青色
     * 0003FC:蓝色
     * FC03FF:紫色
     */
    public static String rgbColor;

    /**
     * <流速>: 0-100
     */
    public static int flowSpeed = 0;
}
