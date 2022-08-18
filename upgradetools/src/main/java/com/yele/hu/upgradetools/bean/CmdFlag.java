package com.yele.hu.upgradetools.bean;

public class CmdFlag {

    public static int CMD_NO = 0X0001;



    /********************************* 仪表类 ***************************************/

    public static boolean isReadCmd(int cmd) {
        return cmd == CMD_PART_READ_AUDIO_NAME
                || cmd == CMD_PART_READ_AUDIO_ADDR
                || cmd == CMD_PART_READ_AUDIO_SOFT
                || cmd == CMD_PART_READ_AUDIO_PLAY
                || cmd == CMD_KNAP_READ_FINGREPRINT
                || cmd == CMD_KNAP_READ_OUT_LED_STATE
                || cmd == CMD_METER_STYLE_FIND
                || cmd == CMD_AMBIENT_LIGHT_FIND
                || cmd == CMD_VERSION_FIND
                || cmd == CMD_RIDE_RECORD
                || cmd == CMD_DRIVE_MODE
                || cmd == CMD_LED_STATE
                || cmd == CMD_SCREEN;
    }

    /********************************* 测试模式 ***************************************/

    // 蓝牙参数配置
    public static final int CMD_BLE_CONFIG = 0X01;
    // 车辆参数配置
    public static final int CMD_CAR_CONFIG = 0X02;
    // 成品测试
    public static final int CMD_GOOD_TEST = 0X03;
    // 出厂车辆参数配置
    public static final int CMD_OUT_CAR_CONFIG = 0x05;
    // 写码
    public static final int CMD_INPUT_CODE = 0x04;
    // 出厂车辆参数配置（500B）
    public static final int CMD_OUT_CAR_CONFIG_BLE_SWITCH = 0x06;

    /****************************************** 正常模式 ******************************************/

    // 模式切换
    public static final int CMD_MODE_CHANGE = 0X07;
    // 寻车
    public static final int CMD_FIND_CAR = 0X08;
    // 车辆锁
    public static final int CMD_CAR_LOCK = 0X09;
    // 车辆模式配置
    public static final int CMD_CAR_NORMAL_CONFIG = 0X0a;
    // LED控制
    public static final int CMD_LED_CONTROL = 0X0b;
    // 修改蓝牙普通密码
    public static final int CMD_PWD_CHANGE = 0X0c;
    // 读取配置信息
    public static final int CMD_READ_CONFIG = 0X0d;
    // 修改名称
    public static final int CMD_BLE_NAME_CHANGE = 0X0e;
    // 开关机的模式修改-开
    public static final int CMD_OPEN_MODE = 0x0f;
    // 定速巡航开关控制-开
    public static final int CMD_DLCC_CONTROL = 0x10;
    // 锁车模式配置-开
    public static final int CMD_LOCK_MODE_CONFIG = 0x11;
    // 骑行时启动模式配置
    public static final int CMD_DRIVE_MODE_CHANGE = 0x12;
    // 开启学习模式（ES20、EA10）
    public static final int CMD_STUDY_MODE = 0x13;
    // 导航模式（ES800）
    public static final int CMD_NAV_MODE_CHANGE = 0x14;
    // 消息提醒（ES800）
    public static final int CMD_MSG_SEND = 0X15;
    // 时间同步（ES800）
    public static final int CMD_TIME_SYN = 0x16;
    // 地理信息（ES800）
    public static final int CMD_LBS_SYN = 0x17;
    // 天气信息（ES800）
    public static final int CMD_WEATHER_SYN = 0X18;
    // 出厂模式
    public static final int CMD_OUT_MODE = 0x19;
    // 报警器控制（EA10）
    public static final int CMD_WARN_CONTROL = 0x1a;
    // 自检模式控制
    public static final int CMD_CHECK_MODE = 0x1b;
    // 修改定时上报模式
    public static final int SMD_MODIFY_REPORT = 0x1c;
    // 设置氛围灯模式（ES800、ES20）
    public static final int CMD_AMBIENT_LIGHT_MODE = 0x1d;
    // 切换风格（ES800、ES20）
    public static final int CMD_INTERFACE_STYLE = 0x1e;
    // 界面显示内容配置（ES800、ES20）
    public static final int CMD_INTERFACE_CONTENT = 0x1f;
    // 电池充电参数配置（ES20）
    public static final int CMD_BATTERY_CONFIG = 0x20;
    // 骑行刹车力度配置
    public static final int CMD_RIDE_BRAKE_LEVEL = 0x21;
    // 驱动模式配置（ES800）
    public static final int CMD_DRIVER_CONFIG = 0x22;
    // 屏幕亮度控制（ES800）
    public static final int CMD_SCREEN_BRIGHTNESS = 0x23;

    /****************************************** 车辆查询指令 ******************************************/

    // 滑板车状态信息查询
    public static final int CMD_STATE_QUERY = 0x40;
    // 仪表风格查询
    public static final int CMD_METER_STYLE_FIND = 0x41;
    // 氛围灯配置查询
    public static final int CMD_AMBIENT_LIGHT_FIND = 0x42;
    // 仪表所有版本信息查询
    public static final int CMD_VERSION_FIND = 0x43;
    // 骑行数据记录查询
    public static final int CMD_RIDE_RECORD = 0x44;
    // 驱动模式查询
    public static final int CMD_DRIVE_MODE = 0x45;
    // LED状态查询
    public static final int CMD_LED_STATE = 0x46;
    // 屏幕亮度查询
    public static final int CMD_SCREEN = 0x47;

    /****************************************** 配件连接 ******************************************/

    // 连接/断开指定头盔
    public static final int CMD_HELMET_CONNECT = 0x24;
    // 连接/断开指定背包
    public static final int CMD_BACKPACK_CONNECT = 0x25;

    /****************************************** 升级 ******************************************/

    // 控制器升级
    public static final int CMD_UPGRADE_CONTROL = 0x26;
    // 蓝牙升级
    public static final int CMD_UPGRADE_BLE = 0x27;
    // 长包传输
    public static final int CMD_UPGRADE_LENGTH_READY = 0x28;
    // ST芯片升级指令
    public static final int CMD_PART_UPGRADE = 0X29;

    /********************************* 配件类控制 ***************************************/

    public static boolean isPartHead(int cmd) {
        return cmd >= 0x50 && cmd < 0x70;
    }

    public static boolean isPartKnap(int cmd) {
        return cmd >= 0x80 && cmd < 0xA0;
    }

    /********************************* 头盔类 ***************************************/

    // (配件指令-头盔) 骑行状态灯选择
    public static final int CMD_PART_DRIVE_LED = 0X50;
    // (配件指令-头盔) 开关机控制
    public static final int CMD_PART_POWER_CONTROL = 0X51;
    // (配件指令-头盔) 显示模式选择
    public static final int CMD_PART_SHOW_MODE = 0X52;
    // (配件指令-头盔) 头盔锁控制
    public static final int CMD_PART_LOCK = 0X53;
    // (配件指令-头盔) 体感转向选择
    public static final int CMD_PART_BODY_ROUND = 0X54;
    // (配件指令-头盔) 灯状态配置
    public static final int CMD_PART_LED_STATE = 0X55;
    // (配件指令-头盔) 车身状态数据下发
    public static final int CMD_PART_CAR_INFO = 0X56;
    // (配件指令-头盔) 车辆转向信息
    public static final int CMD_PART_CAR_ROUND_INF0 = 0X57;
    // (配件指令-头盔) 后灯DIY显示
    public static final int CMD_PART_DIY_LED = 0X58;
    // (配件指令-头盔) 音频蓝牙名称修改
    public static final int CMD_PART_AUDIO_RENAME = 0X59;
    // (配件指令-头盔) 工作模式切换
    public static final int CMD_PART_WORK_MODE = 0X5a;
    // (配件指令-头盔) 开启遥控按钮学习模式
    public static final int CMD_PART_REMOTE_MODE = 0X5b;
    // (配件指令-头盔) 头盔蓝牙广播名称修改
    public static final int CMD_PART_BLE_RENAME = 0X5c;
    // (配件指令-头盔) 音量调节指令
    public static final int CMD_PART_AUDIO_VOL_CONTROL = 0X5D;
    // (配件指令-头盔) 音频播放控制
    public static final int CMD_PART_AUDIO_PLAY = 0X5E;
    // (配件指令-头盔) 曲目切换
    public static final int CMD_PART_AUDIO_SONG = 0x5f;
    // (配件指令-头盔) 音频蓝牙名称查询
    public static final int CMD_PART_READ_AUDIO_NAME = 0X60;
    // (配件指令-头盔) 音频蓝牙地址查询
    public static final int CMD_PART_READ_AUDIO_ADDR = 0X61;
    // (配件指令-头盔) 音频蓝牙固件版本查询
    public static final int CMD_PART_READ_AUDIO_SOFT = 0X62;
    // (配件指令-头盔) 音频蓝牙播放状态查询
    public static final int CMD_PART_READ_AUDIO_PLAY = 0X63;
    // (配件指令-头盔) 测试模式-蓝牙参数配置
    public static final int CMD_PART_TEST_BLE_CONFIG = 0X64;
    // (配件指令-头盔) 测试模式-前后灯显示状态控制
    public static final int CMD_PART_TEST_LED_CONTROL = 0X65;

    /********************************* 背包类 ***************************************/

    // (配件指令-背包) 测试模式-蓝牙参数配合
    public static final int CMD_KNAP_TEST_BLE_CONFIG = 0X80;
    // (配件指令-背包) 测试模式-背包氛围灯显示状态控制
    public static final int CMD_KNAP_TEST_SHOW_CONFIG = 0X81;
    // (配件指令-背包) 测试模式-背包氛围灯刹车状态配置
    public static final int CMD_KNAP_TEST_BRAKE_CONFIG = 0X82;
    // (配件指令-背包) 背包锁控制
    public static final int CMD_KNAP_LOCK = 0X83;
    // (配件指令-背包) 背包寻找
    public static final int CMD_KNAP_FIND = 0X84;
    // (配件指令-背包) 无线充控制
    public static final int CMD_KNAP_WIRELESS = 0x85;
    // (配件指令-背包) 紫外线消毒功能控制
    public static final int CMD_KNAP_DISINFECT = 0x86;
    // (配件指令-背包) 车身状态数据下发
    public static final int CMD_KNAP_CAR_INFO = 0X87;
    // (配件指令-背包) 包外示廓灯闹钟配置
    public static final int CMD_KNAP_OUT_LED_CONTROL = 0X88;
    // (配件指令-背包) 包内照明灯颜色配置
    public static final int CMD_KNAP_IN_LED_COLOR_CONFIG = 0X89;
    // (配件指令-背包) 示廓灯状态配置
    public static final int CMD_KNAP_OUT_LED_STATE = 0X8A;
    // (配件指令-背包) 背包指纹配置
    public static final int CMD_KNAP_FINGERPRINT = 0X8B;
    // (配件指令-背包) 保内照明灯控制
    public static final int CMD_KNAP_IN_LED_CONTROL = 0X8C;
    // (配件指令-背包) 当地时间校准
    public static final int CMD_KNAP_TIME_SYNC = 0X8D;
    // (配件指令-背包) 工作模式切换
    public static final int CMD_KNAP_WORK_MODE = 0X8E;
    // (配件指令-背包) 背包蓝牙广播名称修改
    public static final int CMD_KNAP_BLE_RENAME = 0X8F;
    // (配件指令-背包) 指纹权限查询
    public static final int CMD_KNAP_READ_FINGREPRINT = 0X90;
    // (配件指令-背包) 包外示廓灯闹钟查询
    public static final int CMD_KNAP_READ_OUT_LED_STATE = 0X91;

    /***********************************************************************************
     *                                                                                 *
     *                              上报类指令                                          *
     *                                                                                 *
     ***********************************************************************************/

    /********************************* 仪表类上报 ***************************************/
    // 上报信息
    public static final int CMD_REPORT_INFO = 0X71;
    // 错误上报
    public static final int CMD_REPORT_ERR = 0X72;
    // 滑板车状态信息上报
    public static final int CMD_REPORT_CCF = 0x73;
    // 滑板车测试状态上报
    public static final int CMD_REPORT_TEST = 0X74;

    /********************************* 头盔类上报 ***************************************/
    // (配件指令-头盔) 头盔状态定时上报指令
    public static final int CMD_PART_REPORT = 0X78;

    /********************************* 背包类上报 ***************************************/
    // (配件指令-背包) 背包状态定时上报指令
    public static final int CMD_KNAP_REPORT = 0X7A;

}
