package com.yele.bluetoothlib.policy.protrol;

import com.yele.baseapp.utils.ByteUtils;
import com.yele.bluetoothlib.bean.cmd.CmdFlag;
import com.yele.bluetoothlib.bean.config.AtmosphereConfig;
import com.yele.bluetoothlib.bean.config.CheckConfig;
import com.yele.bluetoothlib.bean.config.FactoryConfig;
import com.yele.bluetoothlib.bean.config.LBSConfig;
import com.yele.bluetoothlib.bean.config.MsgConfig;
import com.yele.bluetoothlib.bean.config.NavConfig;
import com.yele.bluetoothlib.bean.config.ProduceConfig;
import com.yele.bluetoothlib.bean.config.TimeConfig;
import com.yele.bluetoothlib.bean.config.WeatherConfig;
import com.yele.bluetoothlib.bean.config.ble.BluetoothConfig;
import com.yele.bluetoothlib.bean.config.ble.CarConfig;
import com.yele.bluetoothlib.bean.config.ble.DesignConfig;
import com.yele.bluetoothlib.bean.config.ble.DeviceBase;
import com.yele.bluetoothlib.bean.config.ble.PartKnapsackConfig;
import com.yele.bluetoothlib.bean.config.part.head.HCarInfoConfig;
import com.yele.bluetoothlib.bean.config.part.head.HDriveLed;
import com.yele.bluetoothlib.bean.config.part.head.HLedConfig;
import com.yele.bluetoothlib.bean.config.part.head.HRemoteStudy;
import com.yele.bluetoothlib.bean.config.part.head.HShowMode;
import com.yele.bluetoothlib.bean.config.part.head.HTestBleConfig;
import com.yele.bluetoothlib.bean.config.part.head.HTestLedControl;
import com.yele.bluetoothlib.bean.config.part.head.STBleUpgradeConfig;
import com.yele.bluetoothlib.bean.config.part.knapsack.KDisinfectControl;
import com.yele.bluetoothlib.bean.config.part.knapsack.KFingerprintConfig;
import com.yele.bluetoothlib.bean.config.part.knapsack.KInLedControl;
import com.yele.bluetoothlib.bean.config.part.knapsack.KOutLedControl;
import com.yele.bluetoothlib.bean.config.part.knapsack.KOutLedStateConfig;
import com.yele.bluetoothlib.bean.config.part.knapsack.KTestBleConfig;
import com.yele.bluetoothlib.bean.config.part.knapsack.KTimeSync;
import com.yele.bluetoothlib.bean.config.upgrade.UpgradeInfo;
import com.yele.bluetoothlib.bean.debug.DebugFlag;
import com.yele.bluetoothlib.utils.CodeUtils;
import com.yele.bluetoothlib.utils.JavaAES128Encryption;
import com.yele.bluetoothlib.utils.PartColorUtils;

public class CmdCarPackage {

    // 当前需要解析的指令
    private int cmd = -1;

    // 当前需要解析的数据
    private Object obj;

    public CmdCarPackage(int cmd, Object object) {
        this.cmd = cmd;
        this.obj = object;
    }

    /**
     * 打包数据
     *
     * @return 当前的数据
     */
    public byte[] packageData() {
        String str = DebugFlag.SEND_PROTOCOL;
        byte[] data = null;
        switch (cmd) {
            case CmdFlag.CMD_BLE_CONFIG:        // 蓝牙参数配置指令
                BluetoothConfig bleConfig = (BluetoothConfig) obj;
                str = "CON=" + bleConfig.sn + "," + bleConfig.broadInterval + "," + bleConfig.broadDuration + "," +
                        bleConfig.minConInterval + "," + bleConfig.maxConInterval + "," + bleConfig.PWD + ",," + getCmdNo();
                break;
            case CmdFlag.CMD_CAR_CONFIG:        // 车辆参数配置
                DesignConfig designConfig = (DesignConfig) obj;
                str = "CAP=" + designConfig.SN + "," + (designConfig.typeName == null ? "" : designConfig.typeName) + "," + getCmdNo();
                break;
            // 以上是测试模式的指令
            case CmdFlag.CMD_MODE_CHANGE:       // 模式切换
                int mode = (int) obj;
                str = "XWM=" + (DebugFlag.debugMode ? getEncryptionKey(DeviceBase.getSN()) : CarConfig.PWD) + "," + mode + "," + getCmdNo();
                break;
            case CmdFlag.CMD_LFC:               // 车辆出厂参数配置
                ProduceConfig produceConfig = (ProduceConfig) obj;
                str = "LFC=OKAI_CAR," + produceConfig.toCmdString() + "," + getCmdNo();
                break;
            case CmdFlag.CMD_FIND_CAR:          // 找车指令
                str = "LOC=" + DeviceBase.PWD + "," + getCmdNo();
                break;
            case CmdFlag.CMD_CAR_LOCK:          // 锁车指令
                int lock = (int) obj;
                str = "SCT=" + DeviceBase.PWD + "," + (lock == 1 ? "1,1,1,1,1" : "0,0,0,0,0") + "," + getCmdNo();
                break;
            case CmdFlag.CMD_CAR_NORMAL_CONFIG: // 普通模式下，车辆配置指令
                CarConfig carConfig = (CarConfig) obj;
                str = "ECP=" + DeviceBase.PWD + "," + carConfig.maxLimitSpeed + "," + carConfig.accMode + ","
                        + carConfig.ybShowMode + "," + carConfig.reportInterval + "," + carConfig.standbyTime + "," + getCmdNo();
                break;
            case CmdFlag.CMD_LED_CONTROL:       // LED控制
                int ledControl = (int) obj;
                str = "LED=" + DeviceBase.PWD + ",0," + (ledControl == 1 ? "1" : "0") + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PWD_CHANGE:        // 车辆密码修改
                // todo 前端页面需要修改
                String newPassword = (String) obj;
                str = "PWD=" + DeviceBase.PWD + "," + newPassword + "," + newPassword + "," + getCmdNo();
                break;
            case CmdFlag.CMD_READ_CONFIG:       // 读取当前车辆的配置指令
                str = "ALC=" + DeviceBase.PWD + "," + getCmdNo();
                break;
            case CmdFlag.CMD_BLE_NAME_CHANGE:   // 蓝牙名称的修改
                // todo 前端页面需要修改
                String rename = (String) obj;
                str = "NAM=" + DeviceBase.PWD + "," + rename + "," + getCmdNo();
                break;
            case CmdFlag.CMD_OPEN_MODE:         // 开关机模式的修改
                int openMode = (int) obj;
                str = "ONF=" + DeviceBase.PWD + "," + openMode + "," + getCmdNo();
                break;
            case CmdFlag.CMD_DLCC_CONTROL:      // 定速巡航开关控制
                int dlccMode = (int) obj;
                str = "DSX=" + DeviceBase.PWD + "," + dlccMode + "," + getCmdNo();
                break;
            case CmdFlag.CMD_LOCK_MODE_CONFIG:  // 锁车模式配置
                int lockMode = (int) obj;
                str = "SCM=" + DeviceBase.PWD + "," + lockMode + "," + getCmdNo();
                break;
            case CmdFlag.CMD_DRIVE_MODE_CHANGE: // 骑行时启动模式配置
                int driveMode = (int) obj;
                str = "SUM=" + DeviceBase.PWD + "," + driveMode + "," + getCmdNo();
                break;
            case CmdFlag.CMD_NAV_MODE_CHANGE:   // 进入导航模式
                NavConfig navConfig = (NavConfig) obj;
                // todo 需要结合二进制
                String str1 = DebugFlag.SEND_PROTOCOL + "NAV=" + DeviceBase.PWD + "," + navConfig.mode + "," + navConfig.aimAdd + "," +
                        navConfig.aimDis + "," + navConfig.navTime + ",";
                String str2 = CodeUtils.toUNICODE(navConfig.nextStreet);
                String str3 = "," + navConfig.nextTime + "," + navConfig.nextAction + "," + getCmdNo() + "$\r\n";
                data = mergeBytes(str1.getBytes(), ByteUtils.strToBytesByBig(str2), str3.getBytes());
                break;
            case CmdFlag.CMD_MSG_SEND:      // 消息同步
                MsgConfig msgConfig = (MsgConfig) obj;
                // todo 需要结合二进制
                String str4 = DebugFlag.SEND_PROTOCOL + "MSG=" + DeviceBase.PWD + "," + msgConfig.msgType + "," + msgConfig.getNameLen() + ",";
                String str5 = CodeUtils.toUNICODE(msgConfig.resName);
                String str6 = "," + msgConfig.getContentLen() + ",";
                String str7 = CodeUtils.toUNICODE(msgConfig.content);
                String str8 = "," + getCmdNo() + "$\r\n";
                data = mergeBytes(str4.getBytes(), ByteUtils.strToBytesByBig(str5), str6.getBytes(), ByteUtils.strToBytesByBig(str7), str8.getBytes());
                break;
            case CmdFlag.CMD_TIME_SYN:      // 时间同步
                TimeConfig timeConfig = (TimeConfig) obj;
                str = "TIM=" + DeviceBase.PWD + "," + timeConfig.year + "," + timeConfig.month + "," +
                        timeConfig.day + "," + timeConfig.hour + "," + timeConfig.min + ","
                        + timeConfig.sec + "," + timeConfig.week + "," + getCmdNo();
                break;
            case CmdFlag.CMD_LBS_SYN:      // 地理同步
                LBSConfig lbsConfig = (LBSConfig) obj;
                // todo 需要结合二进制
                String str9 = DebugFlag.SEND_PROTOCOL + "LBS=" + DeviceBase.PWD + "," + lbsConfig.altitude + "," + lbsConfig.latitude + "," + lbsConfig.longitude + ",";
                String str10 = CodeUtils.toUNICODE(lbsConfig.address);
                String str11 = "," + getCmdNo() + "$\r\n";
                data = mergeBytes(str9.getBytes(), ByteUtils.strToBytesByBig(str10), str11.getBytes());
                break;
            case CmdFlag.CMD_WEATHER_SYN:      // 天气同步
                WeatherConfig weatherConfig = (WeatherConfig) obj;
                str = "WEA=" + DeviceBase.PWD + "," + weatherConfig.now + "," + weatherConfig.next1 + "," +
                        weatherConfig.next2 + "," + weatherConfig.next3 + "," + weatherConfig.next4 + "," + weatherConfig.next5 + "," +
                        weatherConfig.temp + "," + weatherConfig.lowTemp + "," + weatherConfig.highTemp + "," + getCmdNo();
                break;
            case CmdFlag.CMD_FCG:   // 出厂模式
                FactoryConfig factoryConfig = (FactoryConfig) obj;
                str = "FCG=" + DeviceBase.PWD + "," + factoryConfig.carFlag + "," + factoryConfig.outFlag + "," + getCmdNo();
                break;
            case CmdFlag.CMD_ALARM_CONTROL:   // 报警器控制
                int alarmControl = (int) obj;
                str = "ALS=" + DeviceBase.PWD + "," + alarmControl + "," + getCmdNo();
                break;
            case CmdFlag.CMD_MANUAL_CHECK:   // 自检模式
                CheckConfig checkConfig = (CheckConfig) obj;
                str = "SIM=" + DeviceBase.PWD + "," + checkConfig.mode + "," + checkConfig.position + "," + getCmdNo();
                break;
            case CmdFlag.CMD_STYLE_CONFIG:  // 风格
                int style = (int) obj;
                str = "STY=" + DeviceBase.PWD + "," + style + "," + getCmdNo();
                break;
            case CmdFlag.CMD_ATMOSPHERE:    // 氛围灯
                AtmosphereConfig atmosphere = (AtmosphereConfig) obj;
                str = "ATL=" + DeviceBase.PWD + "," + atmosphere.mode + "," + atmosphere.speed + "," + getCmdNo();
                break;
            case CmdFlag.CMD_UPGRADE_READY: // 升级准备指令
                UpgradeInfo upgradeInfo = (UpgradeInfo) obj;
                str = "URD=" + DeviceBase.PWD + "," + upgradeInfo.code + "," + upgradeInfo.length + "," + getCmdNo();
                break;
            case CmdFlag.CMD_CNF:       // 滑板车状态查询
                str = "CNF=" + DeviceBase.PWD + "," + getCmdNo();
                break;
            case CmdFlag.CMD_REPORT_TEST_MODE:  // 定时上报模式的切换
                int reportMode = (int) obj;
                str = "CIF=" + DeviceBase.PWD + "," + reportMode + "," + getCmdNo();
                break;
            case CmdFlag.CMD_CONNECT_KNAP:  // 连接/断开指定背包
                PartKnapsackConfig config = (PartKnapsackConfig) obj;
                str = "PSK=" + DeviceBase.PWD + "," + config.name + "," + config.sn + "," + config.connectState + "," + getCmdNo();
                break;
            /********************************* 配件类控制 ***************************************/
            case CmdFlag.CMD_PART_TEST_BLE_CONFIG:  // 测试模式-蓝牙参数配置
                HTestBleConfig hBleConfig = (HTestBleConfig) obj;
                str = "CON=" + hBleConfig.sn + "," + hBleConfig.name + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_TEST_LED_CONTROL:  // 测试模式-前后灯显示状态控制
                HTestLedControl hLedControl = (HTestLedControl) obj;
                str = "BDC=" + DeviceBase.PWD + "," + hLedControl.frontLed + "," + hLedControl.rearLed + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_DRIVE_LED:  // 骑行状态灯选择
                HDriveLed hDriveLed = (HDriveLed) obj;
                String frontColor = hDriveLed.frontDriveColor;
                String rearColor = PartColorUtils.toCorrectColor(hDriveLed.rearDriveColor);
                str = "FDC=" + DeviceBase.PWD + "," + hDriveLed.frontTurnState + "," + hDriveLed.frontDriveState + "," + frontColor + ","
                        + hDriveLed.rearTurnState + "," + hDriveLed.rearDriveState + "," + rearColor + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_POWER_CONTROL:  // 开关机控制
                // 0:关机；1：开机
                int hOpen = (int) obj;
                str = "COF=" + DeviceBase.PWD + "," + hOpen + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_SHOW_MODE:  // 显示模式选择
                HShowMode hShowMode = (HShowMode) obj;
                str = "SMD=" + DeviceBase.PWD + "," + hShowMode.cmd + "," + hShowMode.turnMode + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_LOCK:  // 头盔锁控制
                // 0:锁定头盔锁；1：打开头盔锁
                int hLock = (int) obj;
                str = "LCK=" + DeviceBase.PWD + "," + hLock + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_BODY_ROUND:  // 体感转向选择
                // 0:关闭体感转向功能；1：打开体感转向功能；2：左转摆头动作学习；3：右转摆头动作学习
                int hBodyTurn = (int) obj;
                str = "TRL=" + DeviceBase.PWD + "," + hBodyTurn + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_LED_STATE:  // 灯状态配置
                HLedConfig hLedConfig = (HLedConfig) obj;
                str = "BLC=" + DeviceBase.PWD + "," + hLedConfig.frontFlowingSpeed + "," + hLedConfig.frontLight + "," +
                        hLedConfig.rearFlowingSpeed + "," + hLedConfig.rearLight + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_CAR_INFO:  // 车身状态数据下发
                HCarInfoConfig hCarInfoConfig = (HCarInfoConfig) obj;
                str = "CAR=" + DeviceBase.PWD + "," + hCarInfoConfig.carState + "," + hCarInfoConfig.accValue + "," +
                        hCarInfoConfig.leftBrake + "," + hCarInfoConfig.rightBrake + "," + hCarInfoConfig.speedValue + "," + hCarInfoConfig.speedUnit + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_CAR_ROUND_INF0:  // 车辆转向信息
                // 0：左转向灯开；1：左转向灯关；2：右转向灯开；3：右转向灯关；
                int hTurnInfo = (int) obj;
                str = "TUN=" + DeviceBase.PWD + "," + hTurnInfo + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_DIY_LED:  // 后灯DIY显示
                // todo DIY待定
                break;
            case CmdFlag.CMD_PART_AUDIO_RENAME:  // 音频蓝牙名称修改
                String hAudioName = (String) obj;
                str = "MNA=" + DeviceBase.PWD + "," + hAudioName + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_WORK_MODE:  // 工作模式切换
                int hWorkMode = (int) obj;
                str = "MDS=" + DeviceBase.PWD + "," + hWorkMode + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_REMOTE_MODE:  // 开启遥控按钮学习模式
                HRemoteStudy hRemoteStudy = (HRemoteStudy) obj;
                str = "LNM=" + DeviceBase.PWD + "," + hRemoteStudy.studyMode + "," + hRemoteStudy.keyNo + "," + hRemoteStudy.keyType + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_BLE_RENAME:  // 头盔蓝牙广播名称修改
                String hBleName = (String) obj;
                str = "NAM=" + DeviceBase.PWD + "," + hBleName + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_AUDIO_VOL_CONTROL:  // 音量调节指令 0~10
                int hVolValue = (int) obj;
                str = "VAD=" + DeviceBase.PWD + "," + hVolValue + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_AUDIO_PLAY:  // 音频播放控制 0:暂停；1：停止；2：播放
                int hAudioPlay = (int) obj;
                str = "AUC=" + DeviceBase.PWD + "," + hAudioPlay + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_AUDIO_SONG:  // 曲目切换 0:上一曲；1：下一曲
                int hSongAction = (int) obj;
                str = "TTS=" + DeviceBase.PWD + "," + hSongAction + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_READ_AUDIO_NAME:  // 音频蓝牙名称查询
                str = "MNA=" + DeviceBase.PWD;
                break;
            case CmdFlag.CMD_PART_READ_AUDIO_ADDR:  // 音频蓝牙地址查询
                str = "MAC=" + DeviceBase.PWD;
                break;
            case CmdFlag.CMD_PART_READ_AUDIO_SOFT:  // 音频蓝牙固件版本查询
                str = "FWE=" + DeviceBase.PWD;
                break;
            case CmdFlag.CMD_PART_READ_AUDIO_PLAY:  // 音频蓝牙播放状态查询
                str = "AUC=" + DeviceBase.PWD;
                break;
            case CmdFlag.CMD_PART_UPGRADE:          // ST芯片升级功能
                STBleUpgradeConfig hUpgradeConfig = (STBleUpgradeConfig) obj;
                str = "CCC=" + DeviceBase.PWD  + "," + hUpgradeConfig.type + "," + hUpgradeConfig.softVersion + "," + hUpgradeConfig.wareVersion;
                break;
            /********************************* 背包类控制 ***************************************/
            case CmdFlag.CMD_KNAP_TEST_BLE_CONFIG: // (配件指令-背包) 测试模式-蓝牙参数配合
                KTestBleConfig kTestBleConfig = (KTestBleConfig) obj;
                str = "CON=" + kTestBleConfig.sn + "," + kTestBleConfig.name + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_TEST_SHOW_CONFIG: // (配件指令-背包) 测试模式-背包氛围灯显示状态控制
                int kShowConfig = (int) obj;
                str = "LEC=" + DeviceBase.PWD + "," + kShowConfig + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_TEST_BRAKE_CONFIG: // (配件指令-背包) 测试模式-背包氛围灯刹车状态配置
                int kBrakeLed = (int) obj;
                str = "BKL=" + DeviceBase.PWD + "," + kBrakeLed + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_LOCK: // (配件指令-背包) 背包锁控制
                int kLockControl = (int) obj;
                str = "CLK=" + DeviceBase.PWD + "," + kLockControl + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_FIND: // (配件指令-背包) 背包寻找
                int kFind = (int) obj;
                str = "LOC=" + DeviceBase.PWD + "," + kFind + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_WIRELESS: // (配件指令-背包) 无线充控制
                int wirelessControl = (int) obj;
                str = "CQI=" + DeviceBase.PWD + "," + wirelessControl + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_DISINFECT: // (配件指令-背包) 紫外线消毒功能控制
                KDisinfectControl disinfect = (KDisinfectControl) obj;
                str = "CUR=" + DeviceBase.PWD + "," + disinfect.control + "," + disinfect.durationTime
                        +"," + disinfect.ledOpen + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_CAR_INFO: // (配件指令-背包) 车身状态数据下发
                // todo 待删除，改指令不做配置
                break;
            case CmdFlag.CMD_KNAP_OUT_LED_CONTROL: // (配件指令-背包) 包外示廓灯闹钟配置
                KOutLedControl kOutLedControl = (KOutLedControl) obj;
                str = "CLP=" + DeviceBase.PWD + "," + kOutLedControl.cmd + "," + kOutLedControl.num + "," + kOutLedControl.openWeek + "," + kOutLedControl.openHour +
                        "," + kOutLedControl.openMin + "," + kOutLedControl.closeHour + "," + kOutLedControl.closeMin + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_IN_LED_COLOR_CONFIG: // (配件指令-背包) 包内照明灯颜色配置
                String inLedColor = (String) obj;
                str = "CLY=" + DeviceBase.PWD + "," + PartColorUtils.toCorrectColor(inLedColor) + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_OUT_LED_STATE: // (配件指令-背包) 示廓灯状态配置
                KOutLedStateConfig kOutLedStateConfig = (KOutLedStateConfig) obj;
                str = "AML=" + DeviceBase.PWD + "," + kOutLedStateConfig.control+ "," + kOutLedStateConfig.state + "," + PartColorUtils.toCorrectColor(kOutLedStateConfig.color) + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_FINGERPRINT: // (配件指令-背包) 背包指纹配置
                KFingerprintConfig kFingerprintConfig = (KFingerprintConfig) obj;
                str = "FIN=" + DeviceBase.PWD + "," + kFingerprintConfig.cmd + "," + kFingerprintConfig.no + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_IN_LED_CONTROL: // (配件指令-背包) 保内照明灯控制
                KInLedControl kInLedControl = (KInLedControl) obj;
                str = "PLG=" + DeviceBase.PWD + "," + kInLedControl.cmd + "," + kInLedControl.openTime + ","
                        + kInLedControl.closeTime + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_TIME_SYNC: // (配件指令-背包) 当地时间校准
                KTimeSync kTimeSync = (KTimeSync) obj;
                str = "TME=" + DeviceBase.PWD + "," + kTimeSync.week + "," + kTimeSync.hour + "," + kTimeSync.min + ","
                        + kTimeSync.sec + "," + kTimeSync.milliSec + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_WORK_MODE: // (配件指令-背包) 工作模式切换
                int kWorkMode = (int) obj;
                str = "MDS=" + DeviceBase.PWD + "," + kWorkMode + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_BLE_RENAME: // (配件指令-背包) 背包蓝牙广播名称修改
                String kName = (String) obj;
                str = "NAM=" + DeviceBase.PWD + "," + kName + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_READ_FINGREPRINT: // (配件指令-背包) 指纹权限查询
                str = "FIN=" + DeviceBase.PWD;
                break;
            case CmdFlag.CMD_KNAP_READ_OUT_LED_STATE: // (配件指令-背包) 包外示廓灯闹钟查询
                str = "CLP=" + DeviceBase.PWD;
                break;
        }
        if (data == null && str != null) {
            if (CmdFlag.isPartHead(cmd)) {
                str = DebugFlag.PART_SEND_PROTOCOL + str;
            } else if (CmdFlag.isPartKnap(cmd)) {
                str = DebugFlag.PART_KNAP_SEND_PROTOCOL + str;
            }else{
                str = DebugFlag.SEND_PROTOCOL + str;
            }
            if (CmdFlag.isReadCmd(cmd)) {
                str += "?\r\n";
            } else {
                str += "$\r\n";
            }
            data = str.getBytes();
        }
        return data;
    }

    /**
     * 合并数组
     *
     * @param bytes 数组队列
     * @return 合并号的数据
     */
    private byte[] mergeBytes(byte[]... bytes) {
        int len = 0;
        for (byte[] data : bytes) {
            len += data.length;
        }
        byte[] end = new byte[len];
        int index = 0;
        for (byte[] data : bytes) {
            System.arraycopy(data, 0, end, index, data.length);
            index += data.length;
        }
        return end;
    }

    /**
     * 获取加密秘钥
     *
     * @return 当前的加密秘钥
     */
    private String getEncryptionKey(String sn) {
        return ByteUtils.bytesToStringByBig(JavaAES128Encryption.encryptAES128(sn != null ? sn : "000000000000000"));
    }

    /**
     * 获取当前的指令序号
     *
     * @return 当前的指令序号
     */
    private String getCmdNo() {
        int cmdNo = CmdFlag.CMD_NO++;
        if (cmdNo > 65535) {
            cmdNo = 0;
            CmdFlag.CMD_NO = 0;
        }
        byte[] buff = ByteUtils.longToBytesByBig(cmdNo, 2);
        return ByteUtils.bytesToStringByBig(buff);
    }
}
