package com.yele.hu.upgradetools.policy.ble;

import com.yele.baseapp.utils.ByteUtils;
import com.yele.hu.upgradetools.bean.CmdFlag;
import com.yele.hu.upgradetools.bean.DebugFlag;
import com.yele.hu.upgradetools.bean.DeviceBase;
import com.yele.hu.upgradetools.bean.comfig.car.AmbientLightConfig;
import com.yele.hu.upgradetools.bean.comfig.car.BleConfig;
import com.yele.hu.upgradetools.bean.comfig.car.CarConfig;
import com.yele.hu.upgradetools.bean.comfig.car.CarOutConfig;
import com.yele.hu.upgradetools.bean.comfig.car.CheckConfig;
import com.yele.hu.upgradetools.bean.comfig.car.FactoryConfig;
import com.yele.hu.upgradetools.bean.comfig.car.KeyConfig;
import com.yele.hu.upgradetools.bean.comfig.car.LedConfig;
import com.yele.hu.upgradetools.bean.comfig.car.LockConfig;
import com.yele.hu.upgradetools.bean.comfig.car.MeterStyleConfig;
import com.yele.hu.upgradetools.bean.comfig.car.PartConnectConfig;
import com.yele.hu.upgradetools.bean.comfig.car.RideConfig;
import com.yele.hu.upgradetools.bean.comfig.car.UpgradeConfig;
import com.yele.hu.upgradetools.bean.comfig.knapsack.KDisinfectControl;
import com.yele.hu.upgradetools.bean.comfig.knapsack.KFingerprintConfig;
import com.yele.hu.upgradetools.bean.comfig.knapsack.KInLedControl;
import com.yele.hu.upgradetools.bean.comfig.knapsack.KOutLedControl;
import com.yele.hu.upgradetools.bean.comfig.knapsack.KOutLedStateConfig;
import com.yele.hu.upgradetools.bean.comfig.knapsack.KTestBleConfig;
import com.yele.hu.upgradetools.bean.comfig.knapsack.KTimeSync;
import com.yele.hu.upgradetools.bean.info.car.CarConfigInfo;
import com.yele.hu.upgradetools.util.JavaAES128Encryption;
import com.yele.hu.upgradetools.util.PartColorUtils;

public class CmdPackage {

    private int cmd;
    private Object object;

    public CmdPackage(int cmd, Object object) {
        this.cmd = cmd;
        this.object = object;
    }


    /**
     * 根据车辆SN对超级密码加密
     * @return
     */
    private String getEncryptionKey(String sn) {
        String key = ByteUtils.bytesToStringByBig(JavaAES128Encryption.encryptAES128(sn!=null? sn:"000000000000000"));
        return key;
    }


    /**
     * 根据仪表SN对普通密码加密
     * @return
     */
    private String getYbEncryptionKey(String sn) {
        return ByteUtils.bytesToStringByBig(JavaAES128Encryption.encryptAES128(sn != null ? sn : "000000000000000"));
    }

    /**
     * 得到指令序列号
     * 从0x0000开始，到达0xFFFF，之后从 0x0000 回滚
     * @return 返回指令序列号
     */
    public static String getCmdNo() {
        int cmdNo = CmdFlag.CMD_NO++;
        if (cmdNo > 65535) {
            cmdNo = 0;
            CmdFlag.CMD_NO = 0;
        }
        byte[] buff = ByteUtils.longToBytesByBig(cmdNo, 2);
        return ByteUtils.bytesToStringByBig(buff);
    }

    /**
     * 打包当前的数据
     * 根据不同的指令打包不同的指令数据
     * 如果没有打包成功，说明指令不合规，则知己诶返回指令错误
     * @return 返回打包好的数据
     */
    public String packCmdStr() {
        String str = null;
        switch (cmd) {
            case CmdFlag.CMD_BLE_CONFIG:        // 蓝牙参数配置指令
                BleConfig bleConfig = (BleConfig) object;
                str = "CON=" + bleConfig.SN + ","
                        + bleConfig.SPACE + ","
                        + bleConfig.DELAY + ","
                        + bleConfig.MIN_SPACE + ","
                        + bleConfig.MAX_SPACE + ","
                        + bleConfig.PWD + ",,"
                        + getCmdNo();
                break;
            case CmdFlag.CMD_CAR_CONFIG:        // 车辆参数配置
                CarConfig carConfig = (CarConfig) object;
                str = "CAP=" + carConfig.SN + "," + carConfig.typeName + "," + getCmdNo();
                break;
            case CmdFlag.CMD_GOOD_TEST:         // 成品测试（只通过URT下发）
                int test = (int) object;
                str = "TET=" + test + "," + getCmdNo();
                break;
            case CmdFlag.CMD_OUT_CAR_CONFIG:    // 测试模式下的出厂车辆参数配置
                CarOutConfig carOutConfig = (CarOutConfig) object;
                str = "LFC=" + CarConfigInfo.PWD + ","
                        + carOutConfig.brakeSelect + ","
                        + carOutConfig.YB_SHOW_MODE + ","
                        + carOutConfig.gear1 + ","
                        + carOutConfig.gear2 + ","
                        + carOutConfig.gear3 + ","
                        + carOutConfig.gear4 + ","
                        + carOutConfig.batteryType + ","
                        + carOutConfig.electronicBrakeSelect + ","
                        + carOutConfig.openCarLedMode + ","
                        + carOutConfig.taillightMode + ","
                        + carOutConfig.salesLocationCode + ","
                        + carOutConfig.customerCode + ","
                        + carOutConfig.carModel + ","
                        + carOutConfig.bleModel + ","
                        + getCmdNo();
                break;
            case CmdFlag.CMD_OUT_CAR_CONFIG_BLE_SWITCH:
                CarOutConfig carOutConfig1 = (CarOutConfig) object;
                str = "LFC=" + CarConfigInfo.PWD + ","
                        + carOutConfig1.brakeSelect + ","
                        + carOutConfig1.YB_SHOW_MODE + ","
                        + carOutConfig1.gear1 + ","
                        + carOutConfig1.gear2 + ","
                        + carOutConfig1.gear3 + ","
                        + carOutConfig1.gear4 + ","
                        + carOutConfig1.batteryType + ","
                        + carOutConfig1.electronicBrakeSelect + ","
                        + carOutConfig1.openCarLedMode + ","
                        + carOutConfig1.taillightMode + ","
                        + carOutConfig1.salesLocationCode + ","
                        + carOutConfig1.customerCode + ","
                        + carOutConfig1.carModel + ","
                        + carOutConfig1.bleModel + ","
                        + carOutConfig1.bleSwitch + ","
                        + getCmdNo();
                break;
            case CmdFlag.CMD_INPUT_CODE:   // 测试模式下的写码
                String batteryCode = (String) object;
                str = "CDG=" + CarConfigInfo.PWD + "," + batteryCode + ",,," + getCmdNo();
                break;
            // 以上是测试模式的指令
            case CmdFlag.CMD_MODE_CHANGE:       // 模式切换
                int mode = (int) object;
                str = "XWM=" + CarConfigInfo.PWD + "," + mode + "," + getCmdNo();
                break;
            case CmdFlag.CMD_FIND_CAR:          // 找车指令
                str = "LOC=" + DeviceBase.PWD + "," + getCmdNo();
                break;
            case CmdFlag.CMD_CAR_LOCK:          // 开关机指令
                LockConfig lock = (LockConfig) object;
                str = "SCT=" + DeviceBase.PWD + ","
                        + lock.carLock + ","
                        + lock.batteryLock + ","
                        + lock.poleLock + ","
                        + lock.rimLock + ","
                        + lock.spareLock+ ","
                        + getCmdNo();
                break;
            case CmdFlag.CMD_CAR_NORMAL_CONFIG: // 普通模式下，车辆配置指令
                RideConfig rideConfig = (RideConfig) object;
                str = "ECP=" + DeviceBase.PWD + ","
                        + rideConfig.hasGearOpen + ","
                        + rideConfig.ADD_MODE + ","
                        + rideConfig.YB_SHOW_MODE + ","
                        + rideConfig.SPACE + ","
                        + rideConfig.WAIT_TIME + ","
                        + getCmdNo();
                break;
            case CmdFlag.CMD_LED_CONTROL:       // LED控制
                LedConfig led = (LedConfig) object;
                str = "LED=" + DeviceBase.PWD + ","+ led.type +"," + led.state + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PWD_CHANGE:        // 车辆密码修改
                String pwd = (String) object;
                str = "PWD=" + DeviceBase.PWD + ","
                        + pwd + ","
                        + DeviceBase.getCarSn()+ ","
                        + getCmdNo();
                break;
            case CmdFlag.CMD_READ_CONFIG:       // 读取当前车辆的配置指令
                str = "ALC=" + DeviceBase.PWD + "," + getCmdNo();
                break;
            case CmdFlag.CMD_BLE_NAME_CHANGE:   // 蓝牙名称的修改
                String bleName = (String) object;
                str = "NAM=" + DeviceBase.PWD + "," + bleName + "," + getCmdNo();
                break;
            case CmdFlag.CMD_OPEN_MODE:         // 开关机模式的修改
                int openMode = (int) object;
                str = "ONF=" + DeviceBase.PWD + "," + openMode + "," + getCmdNo();
                break;
            case CmdFlag.CMD_DLCC_CONTROL:      // 定速巡航开关控制
                int dlcc = (int) object;
                str = "DSX=" + DeviceBase.PWD + "," + dlcc + "," + getCmdNo();
                break;
            case CmdFlag.CMD_LOCK_MODE_CONFIG:  // 锁车模式配置
                int lockMode = (int) object;
                str = "SCM=" + DeviceBase.PWD + "," + lockMode + "," + getCmdNo();
                break;
            case CmdFlag.CMD_DRIVE_MODE_CHANGE: // 骑行时启动模式配置-无助力
                int driveMode = (int) object;
                str = "SUM=" + DeviceBase.PWD + "," + driveMode + "," + getCmdNo();
                break;
            case CmdFlag.CMD_OUT_MODE:   // 出厂模式
                FactoryConfig factoryConfig = (FactoryConfig) object;
                str = "FCG=" + (DebugFlag.debugMode?getEncryptionKey(DeviceBase.getSN()): CarConfigInfo.PWD) + "," + factoryConfig.carFlag
                        + "," + factoryConfig.outFlag + "," + factoryConfig.readFlag + "," + getCmdNo();
                break;
            case CmdFlag.CMD_WARN_CONTROL:   // 报警器控制
                int warn = (int) object;
                str = "ALS=" + DeviceBase.PWD + "," + warn + "," + getCmdNo();
                break;
            case CmdFlag.CMD_CHECK_MODE:   // 自检模式控制
                CheckConfig checkConfig = (CheckConfig) object;
                str = "SIM=" + DeviceBase.PWD + "," + checkConfig.mode + "," + checkConfig.position + "," + getCmdNo();
                break;
            case CmdFlag.CMD_SCREEN_BRIGHTNESS:   // 屏幕亮度控制（ES800）
                String screen = (String) object;
                str = "SDF=" + DeviceBase.PWD + "," + screen + ",,,," + getCmdNo();
                break;
            case CmdFlag.CMD_STATE_QUERY:   // 滑板车状态信息查询
                str = "CNF=" + DeviceBase.PWD + "," + getCmdNo();
                break;
            case CmdFlag.SMD_MODIFY_REPORT:    // 修改定时上报模式
                int report = (int) object;
                str = "CIF=" + DeviceBase.PWD + "," + report + "," +getCmdNo();
                break;
            case CmdFlag.CMD_AMBIENT_LIGHT_MODE:   // 设置氛围灯模式
                AmbientLightConfig ambientLightConfig = (AmbientLightConfig) object;
                str = "ATL=" + DeviceBase.PWD + ","
                        + ambientLightConfig.readOrWrite + ","
                        + ambientLightConfig.carMode + ","
                        + ambientLightConfig.atmosphereLightStyle + ","
                        + ambientLightConfig.rgbColor + ","
                        + ambientLightConfig.flowSpeed + ","
                        + getCmdNo();
                break;
            case CmdFlag.CMD_INTERFACE_STYLE:   // 切换仪表风格
                MeterStyleConfig meterStyleConfig = (MeterStyleConfig) object;
                // 仪表风格切换兼容ES20和ES800。
                // 如果显示模式为-1，表示没有显示模式，只下发风格和语言；如果显示模式为其他数值，表示有显示模式下发。
                str = "STY=" + DeviceBase.PWD + ","
                        +  meterStyleConfig.interfaceStyle + ","
                        + meterStyleConfig.showLanguage + ","
                        + (meterStyleConfig.mode == -1 ? getCmdNo() : (meterStyleConfig.mode + "," + getCmdNo()));
                break;
            case CmdFlag.CMD_STUDY_MODE:   // 开启学习模式
                KeyConfig keyConfig = (KeyConfig) object;
                str = "LNM=" + DeviceBase.PWD + "," + keyConfig.mode + ","+ keyConfig.num +",1," + getCmdNo();
                break;
            case CmdFlag.CMD_DRIVER_CONFIG:
                int driverMode = (int) object;
                str = "DCF=" + DeviceBase.PWD + "," + driverMode + ",,,," + getCmdNo();
                break;
            case CmdFlag.CMD_METER_STYLE_FIND:   // 查询仪表风格
                str = "STY=" + DeviceBase.PWD;
                break;
            case CmdFlag.CMD_AMBIENT_LIGHT_FIND:   // 氛围灯配置查询
                str = "ATL=" + DeviceBase.PWD;
                break;
            case CmdFlag.CMD_VERSION_FIND:   // 仪表所有版本信息查询
                str = "ASV=" + DeviceBase.PWD;
                break;
            case CmdFlag.CMD_RIDE_RECORD:   // 骑行数据记录查询
                str = "CDR=" + DeviceBase.PWD;
                break;
            case CmdFlag.CMD_DRIVE_MODE:   // 驱动模式查询
                str = "DCF=" + DeviceBase.PWD;
                break;
            case CmdFlag.CMD_LED_STATE:   // LED状态查询
                str = "LED=" + DeviceBase.PWD;
                break;
            case CmdFlag.CMD_SCREEN:   // 屏幕亮度查询
                str = "SDF=" + DeviceBase.PWD;
                break;
            case CmdFlag.CMD_HELMET_CONNECT:   // 连接/断开指定头盔
                PartConnectConfig partConnectConfig = (PartConnectConfig) object;
                str = "PSH=" + DeviceBase.PWD + "," + partConnectConfig.name + "," + partConnectConfig.sn + "," + partConnectConfig.connectState + "," + getCmdNo();
                break;
            case CmdFlag.CMD_BACKPACK_CONNECT:   // 连接/断开指定背包
                PartConnectConfig partConnectConfig1 = (PartConnectConfig) object;
                str = "PSK=" + DeviceBase.PWD + "," + partConnectConfig1.name + "," + partConnectConfig1.sn + "," + partConnectConfig1.connectState + "," + getCmdNo();
                break;
            case CmdFlag.CMD_PART_BODY_ROUND:  // 体感转向选择
                // 0:关闭体感转向功能；1：打开体感转向功能；2：左转摆头动作学习；3：右转摆头动作学习
                int hBodyTurn = (int) object;
                str = "TRL=" + DeviceBase.PWD + "," + hBodyTurn + "," + getCmdNo();
                break;
            case CmdFlag.CMD_UPGRADE_LENGTH_READY:   // 控制器升级
                UpgradeConfig upgradeInfo = (UpgradeConfig) object;
                str = "URD=" + DeviceBase.PWD + "," + upgradeInfo.code + "," + upgradeInfo.length + "," + getCmdNo();
                break;
            /********************************* 背包类控制 ***************************************/
            case CmdFlag.CMD_KNAP_TEST_BLE_CONFIG: // (配件指令-背包) 测试模式-蓝牙参数配合
                KTestBleConfig kTestBleConfig = (KTestBleConfig) object;
                str = "CON=" + kTestBleConfig.sn + "," + kTestBleConfig.name + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_TEST_SHOW_CONFIG: // (配件指令-背包) 测试模式-背包氛围灯显示状态控制
                int kShowConfig = (int) object;
                str = "LEC=" + DeviceBase.PWD + "," + kShowConfig + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_TEST_BRAKE_CONFIG: // (配件指令-背包) 测试模式-背包氛围灯刹车状态配置
                int kBrakeLed = (int) object;
                str = "BKL=" + DeviceBase.PWD + "," + kBrakeLed + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_LOCK: // (配件指令-背包) 背包锁控制
                int kLockControl = (int) object;
                str = "CLK=" + DeviceBase.PWD + "," + kLockControl + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_FIND:  // 背包寻找
                int kFind = (int) object;
                str = "LOC=" + DeviceBase.PWD + "," + kFind + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_WIRELESS: // (配件指令-背包) 无线充控制
                int wirelessControl = (int) object;
                str = "CQI=" + DeviceBase.PWD + "," + wirelessControl + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_DISINFECT: // (配件指令-背包) 紫外线消毒功能控制
                KDisinfectControl disinfect = (KDisinfectControl) object;
                str = "CUR=" + DeviceBase.PWD + "," + disinfect.control + "," + disinfect.durationTime
                        +"," + disinfect.ledOpen + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_CAR_INFO: // (配件指令-背包) 车身状态数据下发
                // todo 待删除，改指令不做配置
                break;
            case CmdFlag.CMD_KNAP_OUT_LED_CONTROL: // (配件指令-背包) 包外示廓灯闹钟配置
                KOutLedControl kOutLedControl = (KOutLedControl) object;
                str = "CLP=" + DeviceBase.PWD + ","
                        + kOutLedControl.cmd + ","
                        + kOutLedControl.num + ","
                        + kOutLedControl.openWeek + ","
                        + kOutLedControl.openHour + ","
                        + kOutLedControl.openMin + ","
                        + kOutLedControl.closeHour + ","
                        + kOutLedControl.closeMin + ","
                        + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_IN_LED_COLOR_CONFIG: // (配件指令-背包) 包内照明灯颜色配置
                String inLedColor = (String) object;
                str = "CLY=" + DeviceBase.PWD + "," + PartColorUtils.toCorrectColor(inLedColor) + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_OUT_LED_STATE: // (配件指令-背包) 示廓灯状态配置
                KOutLedStateConfig kOutLedStateConfig = (KOutLedStateConfig) object;
                str = "AML=" + DeviceBase.PWD + "," + kOutLedStateConfig.control+ "," + kOutLedStateConfig.state + "," + PartColorUtils.toCorrectColor(kOutLedStateConfig.color) + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_FINGERPRINT: // (配件指令-背包) 背包指纹配置
                KFingerprintConfig kFingerprintConfig = (KFingerprintConfig) object;
                str = "FIN=" + DeviceBase.PWD + "," + kFingerprintConfig.cmd + "," + kFingerprintConfig.no + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_IN_LED_CONTROL: // (配件指令-背包) 保内照明灯控制
                KInLedControl kInLedControl = (KInLedControl) object;
                str = "PLG=" + DeviceBase.PWD + "," + kInLedControl.cmd + "," + kInLedControl.openTime + ","
                        + kInLedControl.closeTime + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_TIME_SYNC: // (配件指令-背包) 当地时间校准
                KTimeSync kTimeSync = (KTimeSync) object;
                str = "TME=" + DeviceBase.PWD + "," + kTimeSync.week + "," + kTimeSync.hour + "," + kTimeSync.min + ","
                        + kTimeSync.sec + "," + kTimeSync.milliSec + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_WORK_MODE: // (配件指令-背包) 工作模式切换
                int kWorkMode = (int) object;
                str = "MDS=" + DeviceBase.PWD + "," + kWorkMode + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_BLE_RENAME: // (配件指令-背包) 背包蓝牙广播名称修改
                String kName = (String) object;
                str = "NAM=" + DeviceBase.PWD + "," + kName + "," + getCmdNo();
                break;
            case CmdFlag.CMD_KNAP_READ_FINGREPRINT: // (配件指令-背包) 指纹权限查询
                str = "FIN=" + DeviceBase.PWD;
                break;
            case CmdFlag.CMD_KNAP_READ_OUT_LED_STATE: // (配件指令-背包) 包外示廓灯闹钟查询
                str = "CLP=" + DeviceBase.PWD;
                break;
            default:
                break;
        }

        String data = null;
        if(str != null) {
            if (CmdFlag.isPartKnap(cmd)) {
                data = DebugFlag.SEND_KNAP_PROTOCOL + str;
            } else if (CmdFlag.isPartHead(cmd)) {
                data = DebugFlag.SEND_HEAD_PROTOCOL + str;
            } else {
                data = DebugFlag.SEND_PROTOCOL + str;
            }
        }

        if(CmdFlag.isReadCmd(cmd)){
            if (data != null) {
                data = data + "?\r\n";
            }
        }else {
            if (data != null) {
                data = data + "$\r\n";
            }
        }
        return data;
    }


}
