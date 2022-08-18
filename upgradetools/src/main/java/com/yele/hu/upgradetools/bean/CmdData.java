package com.yele.hu.upgradetools.bean;

import com.yele.hu.upgradetools.bean.comfig.car.CheckConfig;
import com.yele.hu.upgradetools.bean.comfig.car.FactoryConfig;
import com.yele.hu.upgradetools.bean.comfig.car.KeyConfig;
import com.yele.hu.upgradetools.bean.comfig.car.LedConfig;
import com.yele.hu.upgradetools.bean.comfig.car.PartConnectConfig;
import com.yele.hu.upgradetools.bean.comfig.helmet.HDriveLed;
import com.yele.hu.upgradetools.bean.comfig.helmet.HLedConfig;
import com.yele.hu.upgradetools.bean.comfig.helmet.HRemoteStudy;
import com.yele.hu.upgradetools.bean.comfig.helmet.HShowMode;
import com.yele.hu.upgradetools.bean.comfig.helmet.STBleUpgradeConfig;
import com.yele.hu.upgradetools.bean.comfig.knapsack.KDisinfectControl;
import com.yele.hu.upgradetools.bean.comfig.knapsack.KFingerprintConfig;
import com.yele.hu.upgradetools.bean.comfig.knapsack.KInLedControl;
import com.yele.hu.upgradetools.bean.comfig.knapsack.KOutLedControl;
import com.yele.hu.upgradetools.bean.comfig.knapsack.KOutLedStateConfig;
import com.yele.hu.upgradetools.bean.comfig.knapsack.KTestBleConfig;
import com.yele.hu.upgradetools.bean.comfig.knapsack.KTimeSync;

public class CmdData {

    public static final CmdContent[] GroupCmd = new CmdContent[]{

            new CmdContent("通用指令"),
            new CmdContent("读取配置", CmdFlag.CMD_READ_CONFIG, null),
            new CmdContent("寻车", CmdFlag.CMD_FIND_CAR, null),
            new CmdContent("切换模式", CmdFlag.CMD_MODE_CHANGE, null),

            new CmdContent("车辆测试模式指令"),
            new CmdContent("蓝牙参数配置", CmdFlag.CMD_BLE_CONFIG, null),
            new CmdContent("车辆参数配置", CmdFlag.CMD_CAR_CONFIG, null),
            new CmdContent("出厂车辆参数配置", CmdFlag.CMD_OUT_CAR_CONFIG, null),
            new CmdContent("开始成品测试", CmdFlag.CMD_GOOD_TEST, 1),
            new CmdContent("取消成品测试", CmdFlag.CMD_GOOD_TEST, 0),

            new CmdContent("控制指令"),
            new CmdContent("车辆锁与机械锁控制指令", CmdFlag.CMD_CAR_LOCK, null),
            new CmdContent("开LED", CmdFlag.CMD_LED_CONTROL, new LedConfig(0,1)),
            new CmdContent("关LED", CmdFlag.CMD_LED_CONTROL, new LedConfig(0,0)),
            new CmdContent("唤醒模式", CmdFlag.CMD_OPEN_MODE, 0),
            new CmdContent("掉电模式", CmdFlag.CMD_OPEN_MODE, 1),
            new CmdContent("助力模式", CmdFlag.CMD_DRIVE_MODE_CHANGE, 0),
            new CmdContent("直开模式", CmdFlag.CMD_DRIVE_MODE_CHANGE, 1),
            new CmdContent("定速巡航", CmdFlag.CMD_DLCC_CONTROL, 1),
            new CmdContent("巡航关闭", CmdFlag.CMD_DLCC_CONTROL, 0),
            new CmdContent("锁车开", CmdFlag.CMD_LOCK_MODE_CONFIG, 1),
            new CmdContent("锁车关", CmdFlag.CMD_LOCK_MODE_CONFIG, 0),
            new CmdContent("报警关", CmdFlag.CMD_WARN_CONTROL, 0),
            new CmdContent("报警开", CmdFlag.CMD_WARN_CONTROL, 1),
            new CmdContent("前驱", CmdFlag.CMD_DRIVER_CONFIG, 1),
            new CmdContent("后驱", CmdFlag.CMD_DRIVER_CONFIG, 2),
            new CmdContent("双驱", CmdFlag.CMD_DRIVER_CONFIG, 0),
            new CmdContent("前灯标准模式", CmdFlag.CMD_LED_CONTROL, new LedConfig(1,0)),
            new CmdContent("前灯加强模式", CmdFlag.CMD_LED_CONTROL, new LedConfig(1,1)),
            //new CmdContent("时间同步",CmdFlag.CMD_TIME_SYN,null),

            new CmdContent("配置指令"),
            new CmdContent("车辆配置", CmdFlag.CMD_CAR_NORMAL_CONFIG, null),
            new CmdContent("改密码", CmdFlag.CMD_PWD_CHANGE, null),
            new CmdContent("改名称", CmdFlag.CMD_BLE_NAME_CHANGE, null),
            new CmdContent("设置氛围灯模式", CmdFlag.CMD_AMBIENT_LIGHT_MODE, null),
            new CmdContent("切换仪表风格", CmdFlag.CMD_INTERFACE_STYLE, null),
            new CmdContent("屏幕亮度控制(ES800)", CmdFlag.CMD_SCREEN_BRIGHTNESS, null),

            new CmdContent("升级"),
            new CmdContent("蓝牙本地升级",CmdFlag.CMD_UPGRADE_BLE,null),
            new CmdContent("控制器本地升级-13",CmdFlag.CMD_UPGRADE_CONTROL,13),
            new CmdContent("电池板本地升级-16",CmdFlag.CMD_UPGRADE_CONTROL,16),
            //new CmdContent("长包传输本地升级",CmdFlag.CMD_UPGRADE_LENGTH_READY,null),

            new CmdContent("钥匙录入"),
            new CmdContent("钥匙录入1",CmdFlag.CMD_STUDY_MODE,new KeyConfig(1,1)),
            new CmdContent("钥匙录入2",CmdFlag.CMD_STUDY_MODE,new KeyConfig(1,2)),
            new CmdContent("停止钥匙录入",CmdFlag.CMD_STUDY_MODE,new KeyConfig(0,1)),

            new CmdContent("出厂模式"),
            new CmdContent("出厂全清", CmdFlag.CMD_OUT_MODE, new FactoryConfig(1,1,0)),
            new CmdContent("车辆双清", CmdFlag.CMD_OUT_MODE, new FactoryConfig(1,0,0)),
            new CmdContent("出厂发船", CmdFlag.CMD_OUT_MODE, new FactoryConfig(0,1,0)),
            new CmdContent("读取出厂",CmdFlag.CMD_OUT_MODE,new FactoryConfig(0,0,1)),

            new CmdContent("自检模式控制"),
            new CmdContent("自检速度", CmdFlag.CMD_CHECK_MODE, new CheckConfig(1,0)),
            new CmdContent("自检电量", CmdFlag.CMD_CHECK_MODE, new CheckConfig(1,1)),
            new CmdContent("自检功能", CmdFlag.CMD_CHECK_MODE, new CheckConfig(1,2)),
            new CmdContent("自检转把", CmdFlag.CMD_CHECK_MODE, new CheckConfig(1,3)),
            new CmdContent("自检左刹把", CmdFlag.CMD_CHECK_MODE, new CheckConfig(1,4)),
            new CmdContent("自检右刹把", CmdFlag.CMD_CHECK_MODE, new CheckConfig(1,5)),
            new CmdContent("自检电机", CmdFlag.CMD_CHECK_MODE, new CheckConfig(1,6)),
            new CmdContent("自检电池", CmdFlag.CMD_CHECK_MODE, new CheckConfig(1,7)),
            new CmdContent("自检关", CmdFlag.CMD_CHECK_MODE, new CheckConfig(0,0)),

            new CmdContent("上报数据控制"),
            new CmdContent("普通数据上报", CmdFlag.SMD_MODIFY_REPORT, 0),
            new CmdContent("测试数据上报", CmdFlag.SMD_MODIFY_REPORT, 1),

            new CmdContent("配件断连控制"),
            new CmdContent("连接/断开头盔", CmdFlag.CMD_HELMET_CONNECT, new PartConnectConfig("sp010","OK201912040602","2")),
            new CmdContent("连接/断开背包", CmdFlag.CMD_BACKPACK_CONNECT, new PartConnectConfig("sp010","OK201912040602","0")),

            new CmdContent("查询指令"),
            new CmdContent("滑板车状态查询", CmdFlag.CMD_STATE_QUERY, null),
            new CmdContent("仪表风格查询", CmdFlag.CMD_METER_STYLE_FIND, null),
            new CmdContent("氛围灯查询", CmdFlag.CMD_AMBIENT_LIGHT_FIND, null),
            new CmdContent("仪表所有版本查询", CmdFlag.CMD_VERSION_FIND, null),
            new CmdContent("骑行数据记录查询", CmdFlag.CMD_RIDE_RECORD, null),
            new CmdContent("驱动模式查询", CmdFlag.CMD_DRIVE_MODE, null),
            new CmdContent("LED状态查询", CmdFlag.CMD_LED_STATE, null),
            new CmdContent("屏幕亮度查询", CmdFlag.CMD_SCREEN, null),

            /********************************* 背包 ***************************************/
            new CmdContent("背包控制"),
            new CmdContent("开锁", CmdFlag.CMD_KNAP_LOCK, 1),
            new CmdContent("关锁", CmdFlag.CMD_KNAP_LOCK, 0),
            new CmdContent("寻找-关灯效", CmdFlag.CMD_KNAP_FIND, 0),
            new CmdContent("寻找-开闪烁", CmdFlag.CMD_KNAP_FIND, 1),
//            new CmdContent("无线充-关", CmdFlag.CMD_KNAP_WIRELESS, 0),
//            new CmdContent("无线充-开", CmdFlag.CMD_KNAP_WIRELESS, 1),
//            new CmdContent("消毒-关", CmdFlag.CMD_KNAP_DISINFECT, new KDisinfectControl(0,15,0)),
//            new CmdContent("消毒-开", CmdFlag.CMD_KNAP_DISINFECT, new KDisinfectControl(1,15,0)),
//            new CmdContent("消毒-开-外灯开", CmdFlag.CMD_KNAP_DISINFECT, new KDisinfectControl(0,15,0)),
//            new CmdContent("消毒-开-外灯关", CmdFlag.CMD_KNAP_DISINFECT, new KDisinfectControl(0,15,0)),
//            new CmdContent("包外灯控制-关", CmdFlag.CMD_KNAP_OUT_LED_CONTROL, new KOutLedControl(0)),
//            new CmdContent("包外灯控制-开", CmdFlag.CMD_KNAP_OUT_LED_CONTROL, new KOutLedControl(1)),
//            new CmdContent("包内照明灯颜色", CmdFlag.CMD_KNAP_IN_LED_COLOR_CONFIG, "f0f0f0"),
//            new CmdContent("示廓灯状态-关", CmdFlag.CMD_KNAP_OUT_LED_STATE, new KOutLedStateConfig(0,0,"000000")),
//            new CmdContent("示廓灯状态-1开", CmdFlag.CMD_KNAP_OUT_LED_STATE, new KOutLedStateConfig(1,1,"ff0000")),
//            new CmdContent("指纹配置-删除1", CmdFlag.CMD_KNAP_FINGERPRINT, new KFingerprintConfig(0,0)),
//            new CmdContent("指纹配置-添加1", CmdFlag.CMD_KNAP_FINGERPRINT, new KFingerprintConfig(1,0)),
//            new CmdContent("包内灯-关", CmdFlag.CMD_KNAP_IN_LED_CONTROL, new KInLedControl(0,0,0)),
//            new CmdContent("包内灯-开", CmdFlag.CMD_KNAP_IN_LED_CONTROL, new KInLedControl(1,0,0)),
//            new CmdContent("时间校准", CmdFlag.CMD_KNAP_TIME_SYNC, new KTimeSync()),
//            new CmdContent("模式-骑行", CmdFlag.CMD_KNAP_WORK_MODE, 0),
//            new CmdContent("模式-行人", CmdFlag.CMD_KNAP_WORK_MODE, 1),
//            new CmdContent("模式-测试", CmdFlag.CMD_KNAP_WORK_MODE, 2),
//            new CmdContent("修改广播名称", CmdFlag.CMD_KNAP_BLE_RENAME, "OK_KNAP-001"),
//
//            new CmdContent("查询指令"),
//            new CmdContent("指纹锁读取", CmdFlag.CMD_KNAP_READ_FINGREPRINT, null),
//            new CmdContent("包外示廓灯状态读取", CmdFlag.CMD_KNAP_READ_OUT_LED_STATE, null),
//            new CmdContent("升级指令", CmdFlag.CMD_PART_UPGRADE, null),
//
//            new CmdContent("背包测试指令"),
//            new CmdContent("蓝牙参数配置", CmdFlag.CMD_KNAP_TEST_BLE_CONFIG, new KTestBleConfig("YL0000000011", "YL_KNAP_1")),
//            new CmdContent("LED灯控制", CmdFlag.CMD_KNAP_TEST_SHOW_CONFIG, 0),
//            new CmdContent("刹车灯状态配置", CmdFlag.CMD_KNAP_TEST_BRAKE_CONFIG, 0),

            /********************************* 头盔 ***************************************/

            new CmdContent("头盔配件"),
//            new CmdContent("骑行状态灯选择", CmdFlag.CMD_PART_DRIVE_LED, new HDriveLed(0, 0, "f0f0f0", 1, 1, "0f0f0f", 1)),
//            new CmdContent("开关机控制-开", CmdFlag.CMD_PART_POWER_CONTROL, 1),
//            new CmdContent("开关机控制-关", CmdFlag.CMD_PART_POWER_CONTROL, 0),
//            new CmdContent("显示模式",CmdFlag.CMD_PART_SHOW_MODE,new HShowMode(0,0)),
//            new CmdContent("头盔锁控制-开",CmdFlag.CMD_PART_LOCK,1),
//            new CmdContent("头盔锁控制-关",CmdFlag.CMD_PART_LOCK,0),
            new CmdContent("体感转向功能-关",CmdFlag.CMD_PART_BODY_ROUND,0),
            new CmdContent("体感转向功能-开",CmdFlag.CMD_PART_BODY_ROUND,1),
            new CmdContent("体感-开-左",CmdFlag.CMD_PART_BODY_ROUND,2),
            new CmdContent("体感-开-右",CmdFlag.CMD_PART_BODY_ROUND,3),
//            new CmdContent("灯状态配置",CmdFlag.CMD_PART_LED_STATE,new HLedConfig(2,5,2,5)),
//            new CmdContent("转向控制-左开",CmdFlag.CMD_PART_CAR_ROUND_INF0,0),
//            new CmdContent("转向控制-左关",CmdFlag.CMD_PART_CAR_ROUND_INF0,1),
//            new CmdContent("转向控制-右开",CmdFlag.CMD_PART_CAR_ROUND_INF0,2),
//            new CmdContent("转向控制-右关",CmdFlag.CMD_PART_CAR_ROUND_INF0,3),
//            new CmdContent("后灯DIY显示",CmdFlag.CMD_PART_DIY_LED,null),
//            new CmdContent("音频蓝牙名称修改",CmdFlag.CMD_PART_AUDIO_RENAME,"HiOkHead"),
//            new CmdContent("工作模式切换-正常",CmdFlag.CMD_PART_WORK_MODE,0),
//            new CmdContent("工作模式切换-测试",CmdFlag.CMD_PART_WORK_MODE,1),
//            new CmdContent("开启遥控按键学习模式",CmdFlag.CMD_PART_REMOTE_MODE,new HRemoteStudy()),
//            new CmdContent("蓝牙广播名称修改",CmdFlag.CMD_PART_BLE_RENAME,"HiOkHead2"),
//
//            new CmdContent("音频控制指令"),
//            new CmdContent("音量调节指令-3",CmdFlag.CMD_PART_AUDIO_VOL_CONTROL,3),
//            new CmdContent("音量调节指令-6",CmdFlag.CMD_PART_AUDIO_VOL_CONTROL,6),
//            new CmdContent("音频播放-Pause",CmdFlag.CMD_PART_AUDIO_PLAY,0),
//            new CmdContent("音频播放-Stop",CmdFlag.CMD_PART_AUDIO_PLAY,1),
//            new CmdContent("音频播放-Start",CmdFlag.CMD_PART_AUDIO_PLAY,2),
//            new CmdContent("曲目切换-Last",CmdFlag.CMD_PART_AUDIO_SONG,0),
//            new CmdContent("曲目切换-Next",CmdFlag.CMD_PART_AUDIO_SONG,1),
//
//            new CmdContent("音频查询指令"),
//            new CmdContent("音频蓝牙名称查询",CmdFlag.CMD_PART_READ_AUDIO_NAME,null),
//            new CmdContent("音频蓝牙地址查询",CmdFlag.CMD_PART_READ_AUDIO_ADDR,null),
//            new CmdContent("音频蓝牙固件版本查询",CmdFlag.CMD_PART_READ_AUDIO_SOFT,null),
//            new CmdContent("音频蓝牙播放状态查询",CmdFlag.CMD_PART_READ_AUDIO_PLAY,null),
//            new CmdContent("升级跳转指令",CmdFlag.CMD_PART_UPGRADE,new STBleUpgradeConfig(30,3,1)),
    };



    public static final CmdContent[] GroupKnapsackCmd = new CmdContent[]{

            /********************************* 背包 ***************************************/
            new CmdContent("背包控制"),
            new CmdContent("开锁", CmdFlag.CMD_KNAP_LOCK, 1),
            new CmdContent("关锁", CmdFlag.CMD_KNAP_LOCK, 0),
            new CmdContent("寻找-关灯效", CmdFlag.CMD_KNAP_FIND, 0),
            new CmdContent("寻找-开闪烁", CmdFlag.CMD_KNAP_FIND, 1),
            new CmdContent("无线充-关", CmdFlag.CMD_KNAP_WIRELESS, 0),
            new CmdContent("无线充-开", CmdFlag.CMD_KNAP_WIRELESS, 1),
            new CmdContent("消毒-关", CmdFlag.CMD_KNAP_DISINFECT, new KDisinfectControl(0,15,0)),
            new CmdContent("消毒-开", CmdFlag.CMD_KNAP_DISINFECT, new KDisinfectControl(1,15,0)),
            new CmdContent("消毒-开-外灯开", CmdFlag.CMD_KNAP_DISINFECT, new KDisinfectControl(0,15,0)),
            new CmdContent("消毒-开-外灯关", CmdFlag.CMD_KNAP_DISINFECT, new KDisinfectControl(0,15,0)),
            new CmdContent("包外灯控制-关", CmdFlag.CMD_KNAP_OUT_LED_CONTROL, new KOutLedControl(0)),
            new CmdContent("包外灯控制-开", CmdFlag.CMD_KNAP_OUT_LED_CONTROL, new KOutLedControl(1)),
            new CmdContent("包内照明灯颜色", CmdFlag.CMD_KNAP_IN_LED_COLOR_CONFIG, "f0f0f0"),
            new CmdContent("示廓灯状态-关", CmdFlag.CMD_KNAP_OUT_LED_STATE, new KOutLedStateConfig(0,0,"000000")),
            new CmdContent("示廓灯状态-1开", CmdFlag.CMD_KNAP_OUT_LED_STATE, new KOutLedStateConfig(1,1,"ff0000")),
            new CmdContent("指纹配置-删除1", CmdFlag.CMD_KNAP_FINGERPRINT, new KFingerprintConfig(0,0)),
            new CmdContent("指纹配置-添加1", CmdFlag.CMD_KNAP_FINGERPRINT, new KFingerprintConfig(1,0)),
            new CmdContent("包内灯-关", CmdFlag.CMD_KNAP_IN_LED_CONTROL, new KInLedControl(0,0,0)),
            new CmdContent("包内灯-开", CmdFlag.CMD_KNAP_IN_LED_CONTROL, new KInLedControl(1,0,0)),
            new CmdContent("时间校准", CmdFlag.CMD_KNAP_TIME_SYNC, new KTimeSync()),
            new CmdContent("模式-骑行", CmdFlag.CMD_KNAP_WORK_MODE, 0),
            new CmdContent("模式-行人", CmdFlag.CMD_KNAP_WORK_MODE, 1),
            new CmdContent("模式-测试", CmdFlag.CMD_KNAP_WORK_MODE, 2),
            new CmdContent("修改广播名称", CmdFlag.CMD_KNAP_BLE_RENAME, "OK_KNAP-001"),

            new CmdContent("查询指令"),
            new CmdContent("指纹锁读取", CmdFlag.CMD_KNAP_READ_FINGREPRINT, null),
            new CmdContent("包外示廓灯状态读取", CmdFlag.CMD_KNAP_READ_OUT_LED_STATE, null),
            new CmdContent("升级指令", CmdFlag.CMD_PART_UPGRADE, null),

            new CmdContent("背包测试指令"),
            new CmdContent("蓝牙参数配置", CmdFlag.CMD_KNAP_TEST_BLE_CONFIG, new KTestBleConfig("YL0000000011", "YL_KNAP_1")),
            new CmdContent("LED灯控制", CmdFlag.CMD_KNAP_TEST_SHOW_CONFIG, 0),
            new CmdContent("刹车灯状态配置", CmdFlag.CMD_KNAP_TEST_BRAKE_CONFIG, 0),

    };



    public static final CmdContent[] GroupHelmetCmd = new CmdContent[]{

            /********************************* 头盔 ***************************************/
            new CmdContent("头盔配件"),
            new CmdContent("骑行状态灯选择", CmdFlag.CMD_PART_DRIVE_LED, new HDriveLed(0, 0, "f0f0f0", 1, 1, "0f0f0f", 1)),
            new CmdContent("开关机控制-开", CmdFlag.CMD_PART_POWER_CONTROL, 1),
            new CmdContent("开关机控制-关", CmdFlag.CMD_PART_POWER_CONTROL, 0),
            new CmdContent("显示模式",CmdFlag.CMD_PART_SHOW_MODE,new HShowMode(0,0)),
            new CmdContent("头盔锁控制-开",CmdFlag.CMD_PART_LOCK,1),
            new CmdContent("头盔锁控制-关",CmdFlag.CMD_PART_LOCK,0),
            new CmdContent("体感转向功能-关",CmdFlag.CMD_PART_BODY_ROUND,0),
            new CmdContent("体感转向功能-开",CmdFlag.CMD_PART_BODY_ROUND,1),
            new CmdContent("体感-开-左",CmdFlag.CMD_PART_BODY_ROUND,2),
            new CmdContent("体感-开-右",CmdFlag.CMD_PART_BODY_ROUND,3),
            new CmdContent("灯状态配置",CmdFlag.CMD_PART_LED_STATE,new HLedConfig(2,5,2,5)),
            new CmdContent("转向控制-左开",CmdFlag.CMD_PART_CAR_ROUND_INF0,0),
            new CmdContent("转向控制-左关",CmdFlag.CMD_PART_CAR_ROUND_INF0,1),
            new CmdContent("转向控制-右开",CmdFlag.CMD_PART_CAR_ROUND_INF0,2),
            new CmdContent("转向控制-右关",CmdFlag.CMD_PART_CAR_ROUND_INF0,3),
            new CmdContent("后灯DIY显示",CmdFlag.CMD_PART_DIY_LED,null),
            new CmdContent("音频蓝牙名称修改",CmdFlag.CMD_PART_AUDIO_RENAME,"HiOkHead"),
            new CmdContent("工作模式切换-正常",CmdFlag.CMD_PART_WORK_MODE,0),
            new CmdContent("工作模式切换-测试",CmdFlag.CMD_PART_WORK_MODE,1),
            new CmdContent("开启遥控按键学习模式",CmdFlag.CMD_PART_REMOTE_MODE,new HRemoteStudy()),
            new CmdContent("蓝牙广播名称修改",CmdFlag.CMD_PART_BLE_RENAME,"HiOkHead2"),

            new CmdContent("音频控制指令"),
            new CmdContent("音量调节指令-3",CmdFlag.CMD_PART_AUDIO_VOL_CONTROL,3),
            new CmdContent("音量调节指令-6",CmdFlag.CMD_PART_AUDIO_VOL_CONTROL,6),
            new CmdContent("音频播放-Pause",CmdFlag.CMD_PART_AUDIO_PLAY,0),
            new CmdContent("音频播放-Stop",CmdFlag.CMD_PART_AUDIO_PLAY,1),
            new CmdContent("音频播放-Start",CmdFlag.CMD_PART_AUDIO_PLAY,2),
            new CmdContent("曲目切换-Last",CmdFlag.CMD_PART_AUDIO_SONG,0),
            new CmdContent("曲目切换-Next",CmdFlag.CMD_PART_AUDIO_SONG,1),

            new CmdContent("音频查询指令"),
            new CmdContent("音频蓝牙名称查询",CmdFlag.CMD_PART_READ_AUDIO_NAME,null),
            new CmdContent("音频蓝牙地址查询",CmdFlag.CMD_PART_READ_AUDIO_ADDR,null),
            new CmdContent("音频蓝牙固件版本查询",CmdFlag.CMD_PART_READ_AUDIO_SOFT,null),
            new CmdContent("音频蓝牙播放状态查询",CmdFlag.CMD_PART_READ_AUDIO_PLAY,null),
            new CmdContent("升级跳转指令",CmdFlag.CMD_PART_UPGRADE,new STBleUpgradeConfig(30,3,1)),
    };


}
