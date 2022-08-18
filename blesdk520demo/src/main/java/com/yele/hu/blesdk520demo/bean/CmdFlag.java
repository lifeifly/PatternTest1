package com.yele.hu.blesdk520demo.bean;

public class CmdFlag {

    public static final String[] CMD_STR = new String[]{
            "初始化/读取所有配置信息",
            "正常-寻车指令",
            "正常-车辆锁与机械锁控制-全开","正常-车辆锁与机械锁控制-全锁",
            "正常-车辆骑行参数配置(最大速度)","正常-车辆骑行参数配置(S档)",
            "正常-LED控制-关闭","正常-LED控制-打开",
            "正常-修改蓝牙名称",
            "修改开机模式：休眠/唤醒模式","修改开机模式：掉电/上电模式",
            "定速巡航控制-开","定速巡航控制-关",
            "锁车模式-开","锁车模式-关",
            "助力启动","无助力启动",
            "自检模式",
            "本地蓝牙升级","本地控制器升级"
    };

    public static int CMD_NO = 0X0001;
    // 测试模式下的蓝牙配置
    public static final int CMD_BLE_CONFIG = 0X01;
    // 测试模式下的车辆配置
    public static final int CMD_CAR_CONFIG = 0X02;
    // 产品测试模式
    public static final int CMD_GOOD_TEST = 0X03;
    // 模式切换-正常模式
    public static final int CMD_MODE_CHANGE = 0X04;
    // 模式切换-测试模式
    public static final int CMD_MODE_CHANGE_TEST = 0X05;
    // 模式切换-恢复出厂模式
    public static final int CMD_MODE_CHANGE_RETRY = 0X06;
    // 寻车
    public static final int CMD_FIND_CAR = 0X07;
    // 车辆锁-开
    public static final int CMD_CAR_UNLOCK = 0X08;
    // 车辆锁-关
    public static final int CMD_CAR_LOCK = 0X09;
    // 车辆模式配置
    public static final int CMD_CAR_NORMAL_CONFIG = 0X0a;
    // LED控制-开
    public static final int CMD_LED_CONTROL = 0X0b;
    // LED控制-关
    public static final int CMD_LED_CONTROL_CLOSE = 0X0c;
    // 修改蓝牙普通密码
    public static final int CMD_PWD_CHANGE = 0X0d;
    // 读取配置信息
    public static final int CMD_READ_CONFIG = 0X0e;
    // 修改名称
    public static final int CMD_BLE_NAME_CHANGE = 0X0f;
    // 开关机的模式修改-开
    public static final int CMD_OPEN_MODE = 0x10;
    // 开关机的模式修改-关
    public static final int CMD_OPEN_MODE_CLOSE = 0x11;
    // 定速巡航开关控制-开
    public static final int CMD_DLCC_CONTROL = 0x12;
    // 定速巡航开关控制-关
    public static final int CMD_DLCC_CONTROL_CLOSE = 0x13;
    // 锁车模式配置-开
    public static final int CMD_LOCK_MODE_CONFIG = 0x14;
    // 锁车模式配置-关
    public static final int CMD_LOCK_MODE_CONFIG_CLOSE = 0x15;
    // 骑行时启动模式配置-无助力
    public static final int CMD_DRIVE_MODE_CHANGE = 0x16;
    // 骑行时启动模式配置-关
    public static final int CMD_DRIVE_MODE_CHANGE_CLOSE = 0x17;


    public static final int CMD_REPORT_INFO = 0X51;

    public static final int CMD_REPORT_ERR = 0X52;

}
