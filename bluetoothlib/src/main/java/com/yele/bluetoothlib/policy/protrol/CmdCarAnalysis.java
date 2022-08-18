package com.yele.bluetoothlib.policy.protrol;

import com.yele.baseapp.utils.LogUtils;
import com.yele.bluetoothlib.bean.BLEUUIDs;
import com.yele.bluetoothlib.bean.LogDebug;
import com.yele.bluetoothlib.bean.cmd.CmdFlag;
import com.yele.bluetoothlib.bean.cmd.RevResult;
import com.yele.bluetoothlib.bean.config.ProduceConfig;
import com.yele.bluetoothlib.bean.config.ble.BluetoothConfig;
import com.yele.bluetoothlib.bean.config.ble.CarConfig;
import com.yele.bluetoothlib.bean.config.ble.DesignConfig;
import com.yele.bluetoothlib.bean.config.ble.VersionInfo;
import com.yele.bluetoothlib.bean.debug.DebugFlag;
import com.yele.bluetoothlib.bean.device.CarRunState;
import com.yele.bluetoothlib.bean.device.DevStateInfo;
import com.yele.bluetoothlib.bean.device.ErrInfo;
import com.yele.bluetoothlib.bean.device.ReportInfo;
import com.yele.bluetoothlib.bean.device.RunInfoReport;
import com.yele.bluetoothlib.bean.device.TestInfoReport;

/**
 * 指令协议解析类
 */
public class CmdCarAnalysis {

    private static final String TAG = "CmdAnalysis";

    private boolean DEBUG_LOG = true;

    private void logi(String msg) {
        if (!DEBUG_LOG || !LogDebug.IS_LOG) {
            return;
        }
        LogUtils.i(TAG, msg);
    }

    // 当前的通道
    private String channel;
    // 收到的数据
    private String mData;

    /**
     * 解析指令的构造函数
     *
     * @param data 需要解析的数据
     */
    public CmdCarAnalysis(String channel, String data) {
        this.channel = channel;
        this.mData = data;
    }

    // 收到的结果
    private RevResult revResult;

    /**
     * 开始解析数据
     */
    public RevResult analysis() {
        if (channel.equals(BLEUUIDs.CAR_COMMON_CHANNEL.toLowerCase())
                || channel.equals(BLEUUIDs.PART_COMMON_CHANNEL.toLowerCase())
                || channel.equals(BLEUUIDs.KNAP_COMMON_CHANNEL.toLowerCase())) {
            logi("收到普通数据：" + mData);
            if (mData.startsWith("+ACK")) {
                if (!dealAckData(mData)) {
                    dealUpgrade(mData);
                }
            } else {
                if (!dealReportData(mData)) {
                    logi("收到脏数据了1");
                }
            }
        } else if (channel.equals(BLEUUIDs.CAR_REPORT_CHANNEL.toLowerCase())
                || channel.equals(BLEUUIDs.PART_REPORT_CHANNEL.toLowerCase())
                || channel.equals(BLEUUIDs.KNAP_REPORT_CHANNEL.toLowerCase())) {
            logi("收到广播数据：" + mData);
            if (mData.startsWith("+ACK")) {
                if (!dealAckData(mData)) {
                    dealUpgrade(mData);
                }
            } else {
                if (!dealReportData(mData)) {
                    logi("收到脏数据了2");
                }
            }
        } else if (channel.equals(BLEUUIDs.CAR_UPGRADE_CHANNEL.toLowerCase())
                || channel.equals(BLEUUIDs.PART_UPGRADE_CHANNEL.toLowerCase())
                || channel.equals(BLEUUIDs.KNAP_UPGRADE_CHANNEL.toLowerCase())) {
            logi("收到更新数据：" + mData);
            if (mData.startsWith("+ACK")) {
                dealUpgrade(mData);
            } else {
                logi("收到脏数据3");
            }
        } else if (channel.equals(BLEUUIDs.CONFIG_CHANNEL.toLowerCase())) {
            logi("收到非通道数据");
        } else {
            logi("收到其他数据：" + mData + " channel:" + channel);
        }
        if (revResult != null) {
            revResult.srcData = mData;
        }
        return revResult;
    }

    /**
     * 处理应答数据
     * 0：表示数据正常，处理陈宫
     * 1、表示数据格式不对
     * 2、数据结果长度不对
     * 3、非可识别指令
     * 4、数据的结果不对
     *
     * @param data 当前需要处理的字符串
     * @return 处理结果
     */
    private boolean dealAckData(String data) {
        data = data.replace(DebugFlag.ACK_PROTOCOL, "")
                .replace("\r\n", "")
                .replace("$", "");
        String[] buff = data.split("=");
        if (buff.length < 2) {
            return false;
        }
        String type = buff[0];
        data = buff[1];
        buff = data.split(",");
        revResult = new RevResult();
        switch (type) {
            case "CON":   // 蓝牙参数配置应答指令
                revResult.cmd = CmdFlag.CMD_BLE_CONFIG;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (!buff[0].equals("0")) {
                    revResult.errMsg = "蓝牙SN配置失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "CAP":   // 车辆参数配置应答
                revResult.cmd = CmdFlag.CMD_CAR_CONFIG;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                DesignConfig designConfig = new DesignConfig();
                designConfig.SN = buff[0];
                designConfig.typeName = buff[1];
                revResult.setCmdNo(buff[2]);
                break;
            case "XWM":   // 模式切换
                revResult.cmd = CmdFlag.CMD_MODE_CHANGE;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                // todo 前台可以判定模式是否切换成功
                revResult.object = Integer.parseInt(buff[1]);
                revResult.setCmdNo(buff[2]);
                break;
            case "LOC":   // 寻车
                revResult.cmd = CmdFlag.CMD_FIND_CAR;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "SCT":   // 车辆锁和机械锁
                revResult.cmd = CmdFlag.CMD_CAR_LOCK;
                if (buff.length != 7) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
//                if (CarConfig.allLock) {
//                    for (int i = 1; i < buff.length - 1; i++) {
//                        if (!buff[i].equals("1")) {
//                            revResult.errMsg = "锁状态不对，应该是锁住状态：" + i;
//                            return 4;
//                        }
//                    }
//                } else {
//                    for (int i = 1; i < buff.length - 1; i++) {
//                        if (!buff[i].equals("0")) {
//                            revResult.errMsg = "锁状态不对，应该是解锁状态：" + i;
//                            return 4;
//                        }
//                    }
//                }
                revResult.setCmdNo(buff[6]);
                break;
            case "ECP":   // 骑行参数配置
                revResult.cmd = CmdFlag.CMD_CAR_NORMAL_CONFIG;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "骑行参数配置失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "LED":   // LED控制
                revResult.cmd = CmdFlag.CMD_LED_CONTROL;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "LED配置失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "PWD":   // 密码修改
                revResult.cmd = CmdFlag.CMD_PWD_CHANGE;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "密码修改失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "ALC":   // 配置信息应答
                revResult.cmd = CmdFlag.CMD_READ_CONFIG;
                if (buff.length != 27) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                int i = 0;
                ReportInfo reportInfo = new ReportInfo();
                while (i < buff.length - 1) {
                    if (buff[i].equals("CON")) {
                        i++;
                        BluetoothConfig bleConfig = new BluetoothConfig();
                        try {
                            bleConfig.sn = buff[i++];
                        } catch (NumberFormatException e) {
                            revResult.errMsg = "广播间隔参数错误";
                            revResult.result = RevResult.FAILED;
                            break;
                        }
                        try {
                            bleConfig.broadInterval = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            revResult.errMsg = "广播间隔参数错误";
                            revResult.result = RevResult.FAILED;
                            break;
                        }
                        try {
                            bleConfig.broadDuration = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            revResult.errMsg = "广播持续时间参数错误";
                            revResult.result = RevResult.FAILED;
                            break;
                        }
                        try {
                            bleConfig.broadDuration = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            revResult.errMsg = "最小连接间隔参数错误";
                            revResult.result = RevResult.FAILED;
                            break;
                        }
                        try {
                            bleConfig.broadDuration = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            revResult.errMsg = "最大连接间隔参数错误";
                            revResult.result = RevResult.FAILED;
                            break;
                        }
                        reportInfo.bluetoothConfig = bleConfig;
                        i++;
                        i++;
                    } else if (buff[i].equals("CAP")) {
                        i++;
                        DesignConfig designConfig1 = new DesignConfig();
                        designConfig1.SN = buff[i++];
                        designConfig1.typeName = buff[i++];
                        reportInfo.designConfig = designConfig1;
                    } else if (buff[i].equals("XWM")) {
                        i++;
                        try {
                            reportInfo.mode = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            revResult.errMsg = "蓝牙模式参数错误";
                            revResult.result = RevResult.FAILED;
                            break;
                        }
                    } else if (buff[i].equals("ECP")) {
                        CarConfig carConfig = new CarConfig();
                        i++;
                        try {
                            carConfig.maxLimitSpeed = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            revResult.errMsg = "最高速度参数错误";
                            revResult.result = RevResult.FAILED;
                            break;
                        }

                        try {
                            carConfig.accMode = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            revResult.errMsg = "加速模式参数错误";
                            revResult.result = RevResult.FAILED;
                            break;
                        }

                        try {
                            carConfig.ybShowMode = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            revResult.errMsg = "仪表显示单位参数错误";
                            revResult.result = RevResult.FAILED;
                            break;
                        }

                        try {
                            carConfig.reportInterval = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            revResult.errMsg = "车辆信息上报间参数错误";
                            revResult.result = RevResult.FAILED;
                            break;
                        }

                        try {
                            carConfig.standbyTime = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            revResult.errMsg = "待机关机时间参数错误";
                            revResult.result = RevResult.FAILED;
                            break;
                        }

                        reportInfo.carConfig = carConfig;
                    } else if (buff[i].equals("VER")) {
                        i++;
                        VersionInfo versionInfo = new VersionInfo();
                        versionInfo.bleSoftVersion = buff[i++];
                        versionInfo.bleWareVersion = buff[i++];
                        versionInfo.controlSoftVersion = buff[i++];
                        versionInfo.controlWareVersion = buff[i++];
                        versionInfo.bmsSoftVersion = buff[i++];
                        versionInfo.bmsWareVersion = buff[i++];
                        reportInfo.versionInfo = versionInfo;
                    } else {
                        revResult.errMsg = "解析参数错误";
                        revResult.result = RevResult.FAILED;
                        break;
                    }
                }
                revResult.object = reportInfo;
                revResult.setCmdNo(buff[i++]);
                break;
            case "NAM":   // 修改名称应答
                revResult.cmd = CmdFlag.CMD_BLE_NAME_CHANGE;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "修改蓝牙名称失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "ONF": // 修改开关机模式
                revResult.cmd = CmdFlag.CMD_OPEN_MODE;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "修改开关机模式失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "DSX": // 修改定速巡航模式失败
                revResult.cmd = CmdFlag.CMD_DLCC_CONTROL;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "修改定速巡航模式失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "SCM": // 开关车辆锁车失败
                revResult.cmd = CmdFlag.CMD_LOCK_MODE_CONFIG;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "修改定速巡航模式失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "SUM": // 切换车辆启动模式
                revResult.cmd = CmdFlag.CMD_DRIVE_MODE_CHANGE;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "切换车辆启动模式失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "NAV": // 导航
                revResult.cmd = CmdFlag.CMD_NAV_MODE_CHANGE;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "导航失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "MSG":
                revResult.cmd = CmdFlag.CMD_MSG_SEND;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "消息发送失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "TIM":
                revResult.cmd = CmdFlag.CMD_TIME_SYN;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "时间同步失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "LBS":
                revResult.cmd = CmdFlag.CMD_LBS_SYN;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "地理位置配置失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "WEA":
                revResult.cmd = CmdFlag.CMD_WEATHER_SYN;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "天气信息配置失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "FCG": // 出厂模式
                revResult.cmd = CmdFlag.CMD_FCG;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "清车辆数据配置失败，";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                if (buff[2].equals("0")) {
                    revResult.errMsg += "发船模式配置失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
//                revResult.setCmdNo(buff[3]);
                break;
            case "ALS": // 报警器模式
                revResult.cmd = CmdFlag.CMD_ALARM_CONTROL;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "报警器配置失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "SIM": // 自检模式
                revResult.cmd = CmdFlag.CMD_MANUAL_CHECK;
                if (buff.length != 4) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[2].equals("0")) {
                    revResult.errMsg = "自检配置失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[3]);
                break;
            case "STY": // 风格配置
                revResult.cmd = CmdFlag.CMD_STYLE_CONFIG;
                if (buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "风格配置失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "ATL": // 氛围灯模式
                revResult.cmd = CmdFlag.CMD_ATMOSPHERE;
                if (buff.length != 4) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }
                if (buff[1].equals("0")) {
                    revResult.errMsg = "氛围灯配置失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                if (buff[2].equals("0")) {
                    revResult.errMsg = "氛围灯速度配置失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[3]);
                break;
            case "LFC": // 车辆出厂参数配置
                revResult.cmd = CmdFlag.CMD_LFC;
                if (buff.length != 13 && buff.length != 15) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                ProduceConfig produceConfig = new ProduceConfig();
                produceConfig.brakeMode = Integer.parseInt(buff[0]);
                produceConfig.speedUnit = Integer.parseInt(buff[1]);
                produceConfig.gear1 = Integer.parseInt(buff[2]);
                produceConfig.gear2 = Integer.parseInt(buff[3]);
                produceConfig.gear3 = Integer.parseInt(buff[4]);
                produceConfig.gear4 = Integer.parseInt(buff[5]);
                produceConfig.bmsType = Integer.parseInt(buff[6]);
                produceConfig.electronicBrake = Integer.parseInt(buff[7]);
                produceConfig.openFrontLed = Integer.parseInt(buff[8]);
                produceConfig.normalTaillight = Integer.parseInt(buff[9]);
                produceConfig.salesNo = Integer.parseInt(buff[10]);
                produceConfig.customerNo = Integer.parseInt(buff[11]);
                produceConfig.carTypeName = buff[12];
                produceConfig.bleType = Integer.parseInt(buff[13]);
                revResult.object = produceConfig;
                revResult.setCmdNo(buff[14]);
                break;
            case "CNF": // 滑板车状态信息查询
                revResult.cmd = CmdFlag.CMD_CNF;
                if (buff.length != 13) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                DevStateInfo devStateInfo = new DevStateInfo();
                devStateInfo.chargeMos = Integer.parseInt(buff[0]);
                devStateInfo.dischargeMos = Integer.parseInt(buff[1]);
                devStateInfo.soh = Integer.parseInt(buff[2]) * 0.1f;
                devStateInfo.highTemp = Integer.parseInt(buff[3]);
                devStateInfo.lowTemp = Integer.parseInt(buff[4]);
                devStateInfo.mosTemp = Integer.parseInt(buff[5]);
                devStateInfo.otherTemp = Integer.parseInt(buff[6]);
                devStateInfo.loopTimes = Integer.parseInt(buff[7]);
                devStateInfo.accAd = Integer.parseInt(buff[8]);
                devStateInfo.leftAd = Integer.parseInt(buff[9]);
                devStateInfo.rightAd = Integer.parseInt(buff[10]);
                devStateInfo.brakeState = Integer.parseInt(buff[11]);
                revResult.object = devStateInfo;
                revResult.setCmdNo(buff[12]);
                break;
            case "PSK": // 连接/断开指定背包
                revResult.cmd = CmdFlag.CMD_CONNECT_KNAP;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "连接/断开指定背包失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            default:
                revResult.result = RevResult.ILLEGAL;
                break;
        }
        if (revResult.cmd != RevResult.INIT) {
            if (checkCmdNo(revResult.cmdNo)) {
                revResult.errMsg = revResult.errMsg != null ? revResult.errMsg + "应答指令不对" : "应答指令不对";
            }
            revResult.type = RevResult.TYPE_ACK;
            if (revResult.result == RevResult.INIT) {
                revResult.result = RevResult.SUCCESS;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 处理上报信息
     * 0：表示数据正常，处理陈宫
     * 1、表示数据格式不对
     * 2、数据结果长度不对
     * 3、数据各个具体内容不对
     *
     * @param data 当前需要解析的指令数据
     * @return 当前的处理结果
     */
    private boolean dealReportData(String data) {
        data = data.replace(DebugFlag.RESP_PROTOCOL, "").replace("\r\n", "").replace("$", "");
        String[] buff = data.split("=");
        if (buff.length < 2) {
            return false;
        }
        String type = buff[0];
        data = buff[1];
        buff = data.split(",");
        revResult = new RevResult();
        switch (type) {
            case "INF":   // 蓝牙参数配置上报
                revResult.cmd = CmdFlag.CMD_REPORT_INFO;
                if (buff.length != 26 && buff.length != 30) {
                    revResult.errMsg = "蓝牙参数配置长度错误";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                CarRunState carRunState = new CarRunState();
                int i = 0;
                try {
                    while (i < buff.length) {
                        if (buff[i].equals("ECU")) {
                            i++;
                            carRunState.lock = Integer.parseInt(buff[i++]);
                            carRunState.speed = Float.parseFloat(buff[i++]) * 0.1f;
                            carRunState.curMileage = Float.parseFloat(buff[i++]) * 0.01f;
                            carRunState.surplusMileage = Float.parseFloat(buff[i++]) * 0.1f;
                            carRunState.totalMileage = Double.parseDouble(buff[i++]) * 0.1f;
                            carRunState.rideTime = Integer.parseInt(buff[i++]);
                            carRunState.ledState = Integer.parseInt(buff[i++]);
                            carRunState.adMode = Integer.parseInt(buff[i++]);
                        } else if (buff[i].equals("BMS")) {
                            i++;
                            carRunState.chargeMos = Integer.parseInt(buff[i++]);
                            carRunState.dischargeMos = Integer.parseInt(buff[i++]);
                            carRunState.power = Integer.parseInt(buff[i++]);
                            carRunState.soh = Integer.parseInt(buff[i++]);
                            carRunState.eleCoreHigh = Integer.parseInt(buff[i++]);
                            carRunState.eleCoreLow = Integer.parseInt(buff[i++]);
                            carRunState.mosTemp = Integer.parseInt(buff[i++]);
                            carRunState.otherTemp = Integer.parseInt(buff[i++]);
                            carRunState.current = Integer.parseInt(buff[i++]);
                            carRunState.voltage = Integer.parseInt(buff[i++]);
                            carRunState.chargeFlag = Integer.parseInt(buff[i++]);
                            carRunState.loopTimes = Integer.parseInt(buff[i++]);
                        } else if (buff[i].equals("Meter")) {
                            i++;
                            carRunState.accelerateAD = Integer.parseInt(buff[i++]);
                            carRunState.brakeADLeft = Integer.parseInt(buff[i++]);
                            carRunState.brakeADRight = Integer.parseInt(buff[i++]);
                            if (buff.length == 30) {
                                carRunState.dlccMode = Integer.parseInt(buff[i++]);
                                carRunState.isLockOpen = Integer.parseInt(buff[i++]);
                                carRunState.driveMode = Integer.parseInt(buff[i++]);
                                carRunState.openMode = Integer.parseInt(buff[i++]);
                            }
                        } else {
                            revResult.errMsg = "解析参数错误";
                            revResult.result = RevResult.FAILED;
                            break;
                        }
                    }
                } catch (Exception e) {
                    revResult.errMsg = "其中某一个参数配置错误：" + i;
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.object = carRunState;
                break;
            case "ECO":   // 车辆错误码上报
                revResult.cmd = CmdFlag.CMD_REPORT_ERR;
                if (buff.length != 10) {
                    revResult.errMsg = "车辆错误码长度错误";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                ErrInfo errInfo = new ErrInfo();
                errInfo.errCode = buff[0];
                errInfo.errCodes = new String[8];
                for (int j = 0; j < 8; j++) {
                    errInfo.errCodes[j] = buff[j + 1];
                }
                errInfo.ybCode = buff[9];
                revResult.object = errInfo;
//                revResult.setCmdNo(buff[10]);
                break;
            case "CCF": // 滑板车状态信息上报
                revResult.cmd = CmdFlag.CMD_REPORT_CCF;
                if (buff.length != 17) {
                    revResult.errMsg = "车辆错误码长度错误";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                RunInfoReport runInfoReport = new RunInfoReport();
                int index = 0;
                try {
                    while (index < buff.length) {
                        if (buff[index].equals("ECU")) {
                            index++;
                            runInfoReport.lock = Integer.parseInt(buff[index++]);
                            runInfoReport.speed = Integer.parseInt(buff[index++]) * 0.1f;
                            runInfoReport.currentMileage = Integer.parseInt(buff[index++]) * 0.1f;
                            runInfoReport.surplusMileage = Integer.parseInt(buff[index++]) * 0.1f;
                            runInfoReport.totalMileage = Integer.parseInt(buff[index++]) * 0.1f;
                            runInfoReport.rideTime = Integer.parseInt(buff[index++]);
                            runInfoReport.ledState = Integer.parseInt(buff[index++]);
                            runInfoReport.accMode = Integer.parseInt(buff[index++]);
                        } else if (buff[index].equals("BMS")) {
                            index++;
                            runInfoReport.electricity = Integer.parseInt(buff[index++]) * 0.1f;
                            runInfoReport.chargeFlag = Integer.parseInt(buff[index++]);
                        }else if (buff[index].equals("Meter")) {
                            index++;
                            runInfoReport.accMode = Integer.parseInt(buff[index++]);
                            runInfoReport.carLock = Integer.parseInt(buff[index++]);
                            runInfoReport.startOver = Integer.parseInt(buff[index++]);
                            runInfoReport.SMode = Integer.parseInt(buff[index++]);
                        }else{
                            index++;
                        }
                    }
                } catch (Exception e) {
                    break;
                }
                revResult.object = runInfoReport;
//                revResult.setCmdNo(buff[index]);
                break;
            case "TES": // 测试状态信息上报
                revResult.cmd = CmdFlag.CMD_REPORT_TEST;
                if (buff.length != 21 && buff.length != 22) {
                    revResult.errMsg = "车辆错误码长度错误";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                TestInfoReport testInfoReport = new TestInfoReport();
                int j = 0;
                try {
                    while (j < buff.length) {
                        if (buff[j].equals("E")) {
                            j++;
                            testInfoReport.rideTime = buff[j++];
                            testInfoReport.totalMileage = buff[j++];
                            testInfoReport.surplusMileage = buff[j++];
                            testInfoReport.currentMileage = buff[j++];
                            testInfoReport.speed = buff[j++];
                            testInfoReport.controlTemp = buff[j++];
                            testInfoReport.machineryTemp = buff[j++];
                            testInfoReport.currentValue = buff[j++];
                            testInfoReport.driveValue = buff[j++];
                            testInfoReport.brakeValue = buff[j++];
                            testInfoReport.controlState = buff[j++];
                            testInfoReport.dlccValue = buff[j++];
                            testInfoReport.conFailTimes = buff[j++];
                        } else if (buff[j].equals("B")) {
                            j++;
                            testInfoReport.eleCurrent = buff[j++];
                            testInfoReport.eleVoltage = buff[j++];
                            if (buff.length == 22) {
                                testInfoReport.power = buff[j++];
                            }
                        }else if (buff[j].equals("Y")) {
                            j++;
                            testInfoReport.accAD = buff[j++];
                            testInfoReport.leftAD = buff[j++];
                            testInfoReport.rightAD = buff[j++];
                        }else{
                            j++;
                        }
                    }
                } catch (Exception e) {
                    break;
                }
                revResult.object = testInfoReport;
                break;
            default:
                revResult.result = RevResult.ILLEGAL;
                break;

        }
        if (revResult.cmd == RevResult.INIT) {
            return false;
        }else{
            revResult.type = RevResult.TYPE_REPORT;
            if (checkCmdNo(revResult.cmdNo)) {
                revResult.errMsg = revResult.errMsg != null ? revResult.errMsg + "应答指令不对" : "应答指令不对";
            }
            if (revResult.result == RevResult.INIT) {
                revResult.result = RevResult.SUCCESS;
            }
            return true;
        }
    }

    /**
     * 处理更新应答数据
     *
     * @param data 当前更新应答数据
     */
    private void dealUpgrade(String data) {
        data = data.replace(DebugFlag.ACK_PROTOCOL, "").replace("\r\n", "").replace("$", "");
        String[] buff = data.split("=");
        if (buff.length < 2) {
            return;
        }
        String type = buff[0];
        data = buff[1];
        buff = data.split(",");
        switch (type) {
            case "UDA":
                revResult.cmdType="UDA";
                revResult.type = RevResult.TYPE_UPGRADE;
                int result2 = -1;
                try {
                    result2 = Integer.parseInt(buff[0]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (result2 != -1) {
                    switch (result2) {
                        case 1: // CRC校验不通过，请求回滚
                            int back=Integer.parseInt(buff[1]);
                            int result1 = -1;
                            try {
                                result1 = Integer.parseInt(buff[0]);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            revResult.object = result1;
                            revResult.object1=back;
                            logi("zhangql=====CRC校验不通过，请求回滚,回滚index:"+back);
                            break;
                        case 2: // 待更新设备软件版本号一至
                            logi("zhangql=====待更新设备软件版本号一至");
                            break;
                        case 3: // 待更新设备硬件版本号不匹配

                            logi("zhangql=====待更新设备硬件版本号不匹配");

                            break;
                        case 4: // 通讯超时，传输失败
                            logi("zhangql=====通讯超时，传输失败");
                            int result=0;
                            try {
                                result = Integer.parseInt(buff[0]);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            revResult.object = result;
                            break;
                        case 5: // 传输成功
                            logi("zhangql=====长包传输升级准备成功");
                            try {
                                result2 = Integer.parseInt(buff[0]);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                            revResult.object = result2;

                            break;
                    }
               /*     if (code != -1) {
                        UpgradeResultEvent resultEvent = new UpgradeResultEvent(code, msg);
                        resultEvent.percent = percent;
                        EventBus.getDefault().post(resultEvent);
                    }*/
                }

                break;
            case "UAS":
                revResult.type = RevResult.TYPE_UPGRADE;
                revResult.cmd = CmdFlag.CMD_UPGRADE_READY;
                int result1 = -1;
                try {
                    result1 = Integer.parseInt(buff[0]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                revResult.object = result1;
                break;
            case "URD":
                revResult.cmdType="URD";
                revResult.type = RevResult.TYPE_UPGRADE;
                revResult.cmd = CmdFlag.CMD_UPGRADE_LENGTH_READY;
                int result3 = -1;
                try {
                    result3 = Integer.parseInt(buff[0]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                revResult.object = result3;
                break;
        }
    }



    /**
     * 判断当前接收到的指令和具体收到的应答指令是否一致
     *
     * @param nowCmd 当前收到的指令
     * @return 指令是否一致
     */
    private boolean checkCmdNo(int nowCmd) {
        return CmdFlag.CMD_NO == nowCmd;
    }
}
