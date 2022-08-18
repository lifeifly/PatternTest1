package com.yele.huht.bluetoothsdklib.bean;

public class CmdFlag {

    public static final String ACTION_SCAN = "com.yele.blesdk.scan";        // 扫描蓝牙
    public static final String ACTION_STOP = "com.yele.download.stop";          // 暂停下载某文件
    public static final String ACTION_END = "com.yele.download.end";            // 结束当前的下载内容
    public static final String RESULT_INIT_SUCCESS = "com.yele.download.result.init"; // 返回当前的下载情况-某文件开始下载
    public static final String RESULT_INIT_FAIL = "com.yele.download.result.init.fail"; // 返回当前的下载情况-某文件初始加载失败
    public static final String RESULT_LOAD_UPDATE = "com.yele.download.result.load.part"; // 返回当前的下载情况-某文件下载更新
    public static final String RESULT_LOAD_STOP = "com.yele.download.result.load.stop"; // 返回当前的下载情况-某文件暂停下载
    public static final String RESULT_LOAD_DELETE = "com.yele.download.result.load.delete"; // 返回当前的下载情况-某文件移除下载信息
    public static final String RESULT_LOAD_FAIL = "com.yele.download.result.load.fail"; // 返回当前的下载情况-某文件下载失败
    public static final String RESULT_FINISH = "com.yele.download.result.finish"; // 返回当前的下载情况-某文件下载完成

    public static final String[] CMD_STR = new String[]{
            "测试-蓝牙参数配置","测试-车辆参数配置","测试-开始成品测试",
            "正常-模式切换-正常模式","正常-模式切换-测试模式","正常-模式切换-恢复出厂",
            "正常-寻车指令",
            "正常-车辆锁与机械锁控制-全开","正常-车辆锁与机械锁控制-全锁",
            "正常-车辆参数配置",
            "正常-LED控制-关闭","正常-LED控制-打开",
            "正常-车辆指令密码修改","正常-读取所有配置信息","正常-修改蓝牙名称",
            "修改开机模式：0","修改开机模式：1",
            "定速巡航控制-开","定速巡航控制-关",
            "锁车模式-开","锁车模式-关",
            "助力启动","无助力启动"
    };

    public static final int[] CMD_FLAG = new int[]{
            1,2,3,
            4,4,4,
            5,
            6,6,
            7,
            8,8,
            9,10,11,
            12,12,
            13,13,
            14,14,
            15,15
    };

    public static final String[] AUTO_CMD_STR = new String[]{
            "正常-模式切换-正常模式","正常-模式切换-测试模式",
            "正常-寻车指令",
            "正常-车辆锁与机械锁控制-全开","正常-车辆锁与机械锁控制-全锁",
            "正常-LED控制-关闭","正常-LED控制-打开",
            "正常-读取所有配置信息",
            "修改开机模式：0","修改开机模式：1",
            "定速巡航控制-开","定速巡航控制-关",
            "锁车模式-开","锁车模式-关",
            "助力启动","无助力启动"
    };

    public static final int[] AUTO_CMD_FLAG = new int[]{
            4,4,
            5,
            6,6,
            8,8,
            10,
            12,12,
            13,13,
            14,14,
            15,15
    };

    public static final String[] DEMO_CMD_STR = new String[]{
            "CMD_SEARCH",
            "CMD_ALL_LOCK","CMD_LOCK_OPEN",
            "CMD_LED_CLOSE","CMD_LED_OPEN"
    };

    public static final int[] DEMO_CMD_FLAG = new int[]{
            5,
            6,6,
            8,8
    };

    public static int CMD_NO = 0X0001;
    // 测试模式下的蓝牙配置
    public static final int CMD_BLE_CONFIG = 0X01;
    // 测试模式下的车辆配置
    public static final int CMD_CAR_CONFIG = 0X02;
    // 产品测试模式
    public static final int CMD_GOOD_TEST = 0X03;
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


    public static final int CMD_REPORT_INFO = 0X51;

    public static final int CMD_REPORT_ERR = 0X52;

}
