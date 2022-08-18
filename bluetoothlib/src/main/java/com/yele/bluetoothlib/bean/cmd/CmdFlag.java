package com.yele.bluetoothlib.bean.cmd;

public class CmdFlag {

    public static int CMD_PART_NO = 0X0001;
    public static int CMD_NO = 0X0001;


    // 测试模式下的蓝牙配置
    public static final int CMD_BLE_CONFIG = 0X01;
    // 测试模式下的车辆配置
    public static final int CMD_CAR_CONFIG = 0X02;
    // 模式切换
    public static final int CMD_MODE_CHANGE = 0X04;
    // 寻车
    public static final int CMD_FIND_CAR = 0X05;
    // 车辆锁
    public static final int CMD_CAR_LOCK = 0X06;
    // 车辆模式配置
    public static final int CMD_CAR_NORMAL_CONFIG = 0X07;
    // LED控制
    public static final int CMD_LED_CONTROL = 0X08;
    // 修改蓝牙普通密码
    public static final int CMD_PWD_CHANGE = 0X09;
    // 读取配置信息
    public static final int CMD_READ_CONFIG = 0X0a;
    // 修改名称
    public static final int CMD_BLE_NAME_CHANGE = 0X0b;
    // 开关机的模式修改
    public static final int CMD_OPEN_MODE = 0x0c;
    // 定速巡航开关控制
    public static final int CMD_DLCC_CONTROL = 0x0d;
    // 锁车模式配置
    public static final int CMD_LOCK_MODE_CONFIG = 0x0e;
    // 骑行时启动模式配置
    public static final int CMD_DRIVE_MODE_CHANGE = 0x0f;
    // 导航模式
    public static final int CMD_NAV_MODE_CHANGE = 0x10;
    // 消息提醒
    public static final int CMD_MSG_SEND = 0X11;
    // 时间同步
    public static final int CMD_TIME_SYN = 0x12;
    // 地理信息
    public static final int CMD_LBS_SYN = 0x13;
    // 天气信息
    public static final int CMD_WEATHER_SYN = 0X14;
    // 出厂模式
    public static final int CMD_FCG = 0X15;
    // 报警器控制
    public static final int CMD_ALARM_CONTROL = 0X16;
    // 自检模式（手动）
    public static final int CMD_MANUAL_CHECK = 0X17;
    // 风格配置
    public static final int CMD_STYLE_CONFIG = 0x18;
    // 氛围模式配置
    public static final int CMD_ATMOSPHERE = 0x19;
    // 发送升级准备指令
    public static final int CMD_UPGRADE_READY = 0x1a;
    // 长包更新指令
    public static final int CMD_UPGRADE_LENGTH_READY = 0x1f;
    // 出厂车辆参数配置
    public static final int CMD_LFC = 0x1b;
    // 发送升级准备指令
    public static final int CMD_UPGRADE_READY_NEW = 0x1c;
    // 滑板车状态查询
    public static final int CMD_CNF = 0X1d;

    // 上报模式切换
    public static final int CMD_REPORT_TEST_MODE = 0x1e;
    // 连接/断开指定背包
    public static final int CMD_CONNECT_KNAP = 0x20;

    /********************************* 配件类控制 ***************************************/
    // ST芯片升级指令
    public static final int CMD_PART_UPGRADE = 0X2f;

    public static boolean isPartHead(int cmd) {
        return cmd >= 0x30 && cmd < 0x50;
    }
    // (配件指令-头盔) 骑行状态灯选择
    public static final int CMD_PART_DRIVE_LED = 0X30;
    // (配件指令-头盔) 开关机控制
    public static final int CMD_PART_POWER_CONTROL = 0X31;
    // (配件指令-头盔) 显示模式选择
    public static final int CMD_PART_SHOW_MODE = 0X32;
    // (配件指令-头盔) 头盔锁控制
    public static final int CMD_PART_LOCK = 0X33;
    // (配件指令-头盔) 体感转向选择
    public static final int CMD_PART_BODY_ROUND = 0X34;
    // (配件指令-头盔) 灯状态配置
    public static final int CMD_PART_LED_STATE = 0X35;
    // (配件指令-头盔) 车身状态数据下发
    public static final int CMD_PART_CAR_INFO = 0X36;
    // (配件指令-头盔) 车辆转向信息
    public static final int CMD_PART_CAR_ROUND_INF0 = 0X37;
    // (配件指令-头盔) 后灯DIY显示
    public static final int CMD_PART_DIY_LED = 0X38;
    // (配件指令-头盔) 音频蓝牙名称修改
    public static final int CMD_PART_AUDIO_RENAME = 0X39;
    // (配件指令-头盔) 工作模式切换
    public static final int CMD_PART_WORK_MODE = 0X3a;
    // (配件指令-头盔) 开启遥控按钮学习模式
    public static final int CMD_PART_REMOTE_MODE = 0X3b;
    // (配件指令-头盔) 头盔蓝牙广播名称修改
    public static final int CMD_PART_BLE_RENAME = 0X3c;
    // (配件指令-头盔) 音量调节指令
    public static final int CMD_PART_AUDIO_VOL_CONTROL = 0X3D;
    // (配件指令-头盔) 音频播放控制
    public static final int CMD_PART_AUDIO_PLAY = 0X3E;
    // (配件指令-头盔) 曲目切换
    public static final int CMD_PART_AUDIO_SONG = 0x3f;
    // (配件指令-头盔) 音频蓝牙名称查询
    public static final int CMD_PART_READ_AUDIO_NAME = 0X40;
    // (配件指令-头盔) 音频蓝牙地址查询
    public static final int CMD_PART_READ_AUDIO_ADDR = 0X41;
    // (配件指令-头盔) 音频蓝牙固件版本查询
    public static final int CMD_PART_READ_AUDIO_SOFT = 0X42;
    // (配件指令-头盔) 音频蓝牙播放状态查询
    public static final int CMD_PART_READ_AUDIO_PLAY = 0X43;
    // (配件指令-头盔) 测试模式-蓝牙参数配置
    public static final int CMD_PART_TEST_BLE_CONFIG = 0X44;
    // (配件指令-头盔) 测试模式-前后灯显示状态控制
    public static final int CMD_PART_TEST_LED_CONTROL = 0X45;

    /********************************* 背包类上报 ***************************************/

    public static boolean isPartKnap(int cmd) {
        return cmd >= 0x60 && cmd < 0x80;
    }
    // (配件指令-背包) 测试模式-蓝牙参数配合
    public static final int CMD_KNAP_TEST_BLE_CONFIG = 0X60;
    // (配件指令-背包) 测试模式-背包氛围灯显示状态控制
    public static final int CMD_KNAP_TEST_SHOW_CONFIG = 0X61;
    // (配件指令-背包) 测试模式-背包氛围灯刹车状态配置
    public static final int CMD_KNAP_TEST_BRAKE_CONFIG = 0X62;
    // (配件指令-背包) 背包锁控制
    public static final int CMD_KNAP_LOCK = 0X63;
    // (配件指令-背包) 背包寻找
    public static final int CMD_KNAP_FIND = 0X64;
    // (配件指令-背包) 无线充控制
    public static final int CMD_KNAP_WIRELESS = 0x65;
    // (配件指令-背包) 紫外线消毒功能控制
    public static final int CMD_KNAP_DISINFECT = 0x66;
    // (配件指令-背包) 车身状态数据下发
    public static final int CMD_KNAP_CAR_INFO = 0X67;
    // (配件指令-背包) 包外示廓灯闹钟配置
    public static final int CMD_KNAP_OUT_LED_CONTROL = 0X68;
    // (配件指令-背包) 包内照明灯颜色配置
    public static final int CMD_KNAP_IN_LED_COLOR_CONFIG = 0X69;
    // (配件指令-背包) 示廓灯状态配置
    public static final int CMD_KNAP_OUT_LED_STATE = 0X6A;
    // (配件指令-背包) 背包指纹配置
    public static final int CMD_KNAP_FINGERPRINT = 0X6B;
    // (配件指令-背包) 保内照明灯控制
    public static final int CMD_KNAP_IN_LED_CONTROL = 0X6C;
    // (配件指令-背包) 当地时间校准
    public static final int CMD_KNAP_TIME_SYNC = 0X6D;
    // (配件指令-背包) 工作模式切换
    public static final int CMD_KNAP_WORK_MODE = 0X6E;
    // (配件指令-背包) 背包蓝牙广播名称修改
    public static final int CMD_KNAP_BLE_RENAME = 0X6F;
    // (配件指令-背包) 指纹权限查询
    public static final int CMD_KNAP_READ_FINGREPRINT = 0X70;
    // (配件指令-背包) 包外示廓灯闹钟查询
    public static final int CMD_KNAP_READ_OUT_LED_STATE = 0X71;

    public static boolean isReadCmd(int cmd) {
        return cmd == CMD_PART_READ_AUDIO_NAME
                || cmd == CMD_PART_READ_AUDIO_ADDR
                || cmd == CMD_PART_READ_AUDIO_SOFT
                || cmd == CMD_PART_READ_AUDIO_PLAY
                || cmd == CMD_KNAP_READ_FINGREPRINT
                || cmd == CMD_KNAP_READ_OUT_LED_STATE;
    }

    /***********************************************************************************
     *                                                                                  *
     *                              上报类指令                                            *
     *                                                                                  *
     ***********************************************************************************/

    /********************************* 仪表类上报 ***************************************/
    // 上报信息
    public static final int CMD_REPORT_INFO = 0X51;
    // 错误上报
    public static final int CMD_REPORT_ERR = 0X52;
    // 滑板车状态信息上报
    public static final int CMD_REPORT_CCF = 0x53;
    // 滑板车测试状态上报
    public static final int CMD_REPORT_TEST = 0X54;

    /********************************* 头盔类上报 ***************************************/
    // (配件指令-头盔) 头盔状态定时上报指令
    public static final int CMD_PART_REPORT = 0X58;

    /********************************* 背包类上报 ***************************************/
    // (配件指令-背包) 背包状态定时上报指令
    public static final int CMD_KNAP_REPORT = 0X5A;

}
