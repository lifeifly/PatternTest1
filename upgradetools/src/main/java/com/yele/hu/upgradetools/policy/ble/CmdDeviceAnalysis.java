package com.yele.hu.upgradetools.policy.ble;

import com.yele.baseapp.utils.LogUtils;
import com.yele.baseapp.utils.StringUtils;
import com.yele.hu.upgradetools.bean.CmdFlag;
import com.yele.hu.upgradetools.bean.DebugFlag;
import com.yele.hu.upgradetools.bean.RevResult;
import com.yele.hu.upgradetools.bean.info.car.AmbientLedInfo;
import com.yele.hu.upgradetools.bean.info.car.BLEConfig;
import com.yele.hu.upgradetools.bean.info.car.BluetoothConfig;
import com.yele.hu.upgradetools.bean.info.car.CarConfigInfo;
import com.yele.hu.upgradetools.bean.info.car.CarRunState;
import com.yele.hu.upgradetools.bean.info.car.DesignConfig;
import com.yele.hu.upgradetools.bean.info.car.DevStateInfo;
import com.yele.hu.upgradetools.bean.info.car.DeviceConfig;
import com.yele.hu.upgradetools.bean.info.car.ProduceConfig;
import com.yele.hu.upgradetools.bean.info.car.ReportInfo;
import com.yele.hu.upgradetools.bean.info.car.VersionInfo;
import com.yele.hu.upgradetools.bean.info.car.YBConfig;
import com.yele.hu.upgradetools.bean.info.helmet.HReportInfo;
import com.yele.hu.upgradetools.bean.info.helmet.HStudyAck;
import com.yele.hu.upgradetools.bean.info.knapsack.KReadFingerprintInfo;
import com.yele.hu.upgradetools.bean.info.knapsack.KReadOutLedInfo;
import com.yele.hu.upgradetools.bean.info.knapsack.KReportStateInfo;
import com.yele.hu.upgradetools.data.BLEUUIDs;
import com.yele.hu.upgradetools.util.PartColorUtils;

import java.util.ArrayList;
import java.util.List;

public class CmdDeviceAnalysis {

    private static final String TAG = "CmdDeviceAnalysis";

    private boolean DEBUG_LOG = true;

    private void logi(String msg) {
        if (!DEBUG_LOG) {
            return;
        }
        LogUtils.i(TAG, msg);
    }



    private String channel;
    private String data;
    private String deviceType;   // 车辆广播型号

    public CmdDeviceAnalysis(String deviceType,String channel, String data) {
        this.channel = channel;
        this.data = data;
        this.deviceType = deviceType;
    }


    // 收到的结果
    private RevResult revResult;

    /**
     * 根据通道解析传送过来的数据
     * @return
     */
    public RevResult analysis(){
        if (channel.equals(BLEUUIDs.COMMON_CHANNEL.toLowerCase())
                || channel.equals(BLEUUIDs.KNAP_COMMON_CHANNEL.toLowerCase())
                || channel.equals(BLEUUIDs.PART_COMMON_CHANNEL.toLowerCase())) {
            if (data.startsWith("+ACK")) {
                if(!dealAckData(channel, data)){
                    dealUpgrade(channel,data);
                }
            } else {
                if (!dealReportData(channel,data)) {
                    logi("收到脏数据了1");
                }
            }
        } else if (channel.equals(BLEUUIDs.REPORT_CHANNEL.toLowerCase())
                || channel.equals(BLEUUIDs.KNAP_REPORT_CHANNEL.toLowerCase())
                || channel.equals(BLEUUIDs.PART_REPORT_CHANNEL.toLowerCase())) {
            logi("收到广播数据：" + data);
            if (data.startsWith("+ACK")) {
                if(!dealAckData(channel, data)){
                    dealUpgrade(channel,data);
                }
            } else {
                if (!dealReportData(channel, data)) {
                    logi("收到脏数据了2");
                }
            }
        } else if (channel.equals(BLEUUIDs.UPGRADE_CHANNEL.toLowerCase())
                || channel.equals(BLEUUIDs.KNAP_UPGRADE_CHANNEL.toLowerCase())
                || channel.equals(BLEUUIDs.PART_UPGRADE_CHANNEL.toLowerCase())) {
            logi("收到更新数据：" + data);
            if (data.startsWith("+ACK")) {
                dealUpgrade(channel,data);
            }else {
                logi("收到脏数据3");
            }
        } else if (channel.equals(BLEUUIDs.CONFIG_CHANNEL.toLowerCase())) {
            logi("收到非通道数据");
        } else {
            logi("收到其他数据：" + data + " channel:" + channel);
        }
        if (revResult != null) {
            revResult.srcData = data;
        }
        return revResult;
    }

    private void dealUpgrade(String channel, String data) {
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
                revResult.cmd = CmdFlag.CMD_UPGRADE_LENGTH_READY;
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
                revResult.cmd = CmdFlag.CMD_UPGRADE_CONTROL;
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
     * 处理上报数据
     * @param channel
     * @param data
     * @return
     */
    private boolean dealReportData(String channel, String data) {
        if(channel.equals(BLEUUIDs.REPORT_CHANNEL.toLowerCase()) || channel.equals(BLEUUIDs.COMMON_CHANNEL.toLowerCase())){
            return dealReportCarData(data);
        }else if(channel.equals(BLEUUIDs.PART_REPORT_CHANNEL.toLowerCase()) || channel.equals(BLEUUIDs.PART_COMMON_CHANNEL.toLowerCase())){
            return dealReportHelmetData(data);
        } else if(channel.equals(BLEUUIDs.KNAP_REPORT_CHANNEL.toLowerCase()) || channel.equals(BLEUUIDs.KNAP_COMMON_CHANNEL.toLowerCase())){
            return dealReportKnapsackData(data);
        }
        return false;
    }

    private boolean dealReportKnapsackData(String data) {
        data = data.replace(DebugFlag.RESP_KNAP_PROTOCOL, "").replace("\r\n", "").replace("$", "");
        String[] buff = data.split("=");
        if (buff.length < 2) {
            return false;
        }
        String type = buff[0];
        data = buff[1];
        buff = data.split(",");
        revResult = new RevResult();
        switch (type) {
            case "STA":   // 头盔状态定时上报
                revResult.cmd = CmdFlag.CMD_KNAP_REPORT;
                if (buff.length != 12) {
                    revResult.errMsg = "上报参数长度错误";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                KReportStateInfo reportStateInfo = new KReportStateInfo();
                reportStateInfo.openState = Integer.parseInt(buff[0]);
                reportStateInfo.inLedState = Integer.parseInt(buff[1]);
                reportStateInfo.inLedConfig = Integer.parseInt(buff[2]);
                reportStateInfo.inLedColor = PartColorUtils.toCorrectColor(buff[3]);
                reportStateInfo.disinfectState = Integer.parseInt(buff[4]);
                reportStateInfo.disinfectTime = Integer.parseInt(buff[5]);
                reportStateInfo.disinfectSurplus = Integer.parseInt(buff[6]);
                reportStateInfo.battery = Integer.parseInt(buff[7]);
                reportStateInfo.wireless = Integer.parseInt(buff[8]);
                reportStateInfo.outLedState = Integer.parseInt(buff[9]);
                reportStateInfo.outLedColor = PartColorUtils.toCorrectColor(buff[10]);
                reportStateInfo.errCode = Long.parseLong(buff[11]);
                revResult.object = reportStateInfo;
                break;
            case "FIN":   // 指纹权限查询
                revResult.cmd = CmdFlag.CMD_KNAP_READ_FINGREPRINT;
                if (buff.length != 10) {
                    revResult.errMsg = "车辆错误码长度错误";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                KReadFingerprintInfo readFingerprintInfo = new KReadFingerprintInfo();
                readFingerprintInfo.states[0] = Integer.parseInt(buff[0]);
                readFingerprintInfo.states[1] = Integer.parseInt(buff[1]);
                readFingerprintInfo.states[2] = Integer.parseInt(buff[2]);
                readFingerprintInfo.states[3] = Integer.parseInt(buff[3]);
                readFingerprintInfo.states[4] = Integer.parseInt(buff[4]);
                readFingerprintInfo.states[5] = Integer.parseInt(buff[5]);
                readFingerprintInfo.states[6] = Integer.parseInt(buff[6]);
                readFingerprintInfo.states[7] = Integer.parseInt(buff[7]);
                readFingerprintInfo.states[8] = Integer.parseInt(buff[8]);
                readFingerprintInfo.states[9] = Integer.parseInt(buff[9]);
                revResult.object = readFingerprintInfo;
                break;
            case "CLP": // 示廓灯闹钟查询
                revResult.cmd = CmdFlag.CMD_KNAP_READ_OUT_LED_STATE;
                if (buff.length != 70) {
                    revResult.errMsg = "车辆错误码长度错误";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                int index = 0;
                List<KReadOutLedInfo> ledInfos = new ArrayList<>();
                for (int i=0;i<10;i++){
                    KReadOutLedInfo readOutLedInfo = new KReadOutLedInfo();
                    readOutLedInfo.num = Integer.parseInt(buff[index ++]);
                    readOutLedInfo.state = Integer.parseInt(buff[index ++]);
                    readOutLedInfo.openWeek = buff[index ++];
                    readOutLedInfo.openHour = Integer.parseInt(buff[index ++]);
                    readOutLedInfo.openMin = Integer.parseInt(buff[index ++]);
                    readOutLedInfo.closeHour = Integer.parseInt(buff[index ++]);
                    readOutLedInfo.closeMin = Integer.parseInt(buff[index ++]);
                    ledInfos.add(readOutLedInfo);
                }
                LogUtils.i(TAG,"列表数据：" + ledInfos.toString());
                revResult.object = ledInfos;
//                revResult.setCmdNo(buff[70]);
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

    private boolean dealReportHelmetData(String data) {
        data = data.replace(DebugFlag.RESP_HEAD_PROTOCOL, "").replace("\r\n", "").replace("$", "");
        String[] buff = data.split("=");
        if (buff.length < 2) {
            return false;
        }
        String type = buff[0];
        data = buff[1];
        buff = data.split(",");
        revResult = new RevResult();
        switch (type) {
            case "STA":   // 头盔状态定时上报
                revResult.cmd = CmdFlag.CMD_PART_REPORT;
                if (buff.length != 20) {
                    revResult.errMsg = "上报参数长度错误";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                HReportInfo reportInfo = new HReportInfo();
                reportInfo.powerState = Integer.parseInt(buff[0]);
                reportInfo.ledState = Integer.parseInt(buff[1]);
                reportInfo.frontTurnState = Integer.parseInt(buff[2]);
                reportInfo.frontDriveState = Integer.parseInt(buff[3]);
                reportInfo.frontDriveColor = buff[4];
                reportInfo.rearTurnState = Integer.parseInt(buff[5]);
                reportInfo.rearDriveState = Integer.parseInt(buff[6]);
                reportInfo.rearDriveColor = PartColorUtils.toCorrectColor(buff[7]);
                reportInfo.warningState = Integer.parseInt(buff[8]);
                reportInfo.showMode = Integer.parseInt(buff[9]);
                reportInfo.bodyTurnState = Integer.parseInt(buff[11]);
                reportInfo.frontFlowSpeed = Integer.parseInt(buff[12]);
                reportInfo.frontLight = Integer.parseInt(buff[13]);
                reportInfo.rearFlowSpeed = Integer.parseInt(buff[14]);
                reportInfo.rearLight = Integer.parseInt(buff[15]);
                reportInfo.battery = Integer.parseInt(buff[16]);
                reportInfo.surplusTime = Float.parseFloat(buff[17]);
                reportInfo.fallState = Integer.parseInt(buff[18]);
                reportInfo.errCode = Integer.parseInt(buff[19]);
                revResult.object = reportInfo;
                break;
            case "MNA":   // 音频蓝牙名称查询
                revResult.cmd = CmdFlag.CMD_PART_READ_AUDIO_NAME;
                if (buff.length != 3) {
                    revResult.errMsg = "车辆错误码长度错误";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.object = buff[1];
                revResult.setCmdNo(buff[2]);
                break;
            case "MAC": // 音频蓝牙地址查询
                revResult.cmd = CmdFlag.CMD_PART_READ_AUDIO_ADDR;
                if (buff.length != 3) {
                    revResult.errMsg = "车辆错误码长度错误";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.object = buff[1];
                revResult.setCmdNo(buff[2]);
                break;
            case "FWE": // 音频蓝牙固件版本查询
                revResult.cmd = CmdFlag.CMD_PART_READ_AUDIO_SOFT;
                if (buff.length != 3) {
                    revResult.errMsg = "车辆错误码长度错误";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.object = buff[1];
                revResult.setCmdNo(buff[2]);
                break;
            case "AUC": // 音频蓝牙播放状态查询
                revResult.cmd = CmdFlag.CMD_PART_READ_AUDIO_PLAY;
                if (buff.length != 3) {
                    revResult.errMsg = "车辆错误码长度错误";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.object = Integer.parseInt(buff[1]);
                revResult.setCmdNo(buff[2]);
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
     * 处理车辆上报信息
     * 0：表示数据正常，处理陈宫
     * 1、表示数据格式不对
     * 2、数据结果长度不对
     * 3、数据各个具体内容不对
     *
     * @param data 当前需要解析的指令数据
     * @return 当前的处理结果
     */
    private boolean dealReportCarData(String data) {
        data = data.replace(DebugFlag.RESP_PROTOCOL, "").replace("\r\n", "").replace("$", "");
        String[] buff = data.split("=");
        if (buff.length < 2) {
            return false;
        }
        String type = buff[0];
        data = buff[1];
        buff = data.split(",");
        revResult = new RevResult();
        CarRunState carRunState = new CarRunState();
        int i = 0;
        switch (type) {
            case "INF":   // 蓝牙参数配置上报
                revResult.cmd = CmdFlag.CMD_REPORT_INFO;
                if (buff.length != 26 && buff.length != 30) {
                    revResult.errMsg = "蓝牙参数配置长度错误";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
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
                if (buff.length < 2) {
                    revResult.errMsg = "车辆错误码长度错误";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                DeviceConfig.errCode = buff[0];
                DeviceConfig.ybCode = buff[1];
                break;
            case "CCF":
                revResult.cmd = CmdFlag.CMD_REPORT_CCF;
                if (buff.length < 17) {
                    revResult.errMsg = "滑板车状态信息上报长度错误";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
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
                            carRunState.power = Integer.parseInt(buff[i++]);
                            carRunState.chargeFlag = Integer.parseInt(buff[i++]);
                        } else if (buff[i].equals("Meter")) {
                            i++;
                            carRunState.dlccMode = Integer.parseInt(buff[i++]);
                            carRunState.isLockOpen = Integer.parseInt(buff[i++]);
                            carRunState.driveMode = Integer.parseInt(buff[i++]);
                            carRunState.sGearMode = Integer.parseInt(buff[i++]);
                            if (deviceType.equals("S800T")){
                                carRunState.rideMode = Integer.parseInt(buff[i++]);
                                carRunState.headlightMode = Integer.parseInt(buff[i++]);
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
            case "TES":
                revResult.cmd = CmdFlag.CMD_REPORT_TEST;
                if (buff.length < 21) {
                    revResult.errMsg = "滑板车状态信息上报长度错误";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                try {
                    while (i < buff.length) {
                        if (buff[i].equals("E")) {
                            i++;
                            carRunState.rideTime = Integer.parseInt(buff[i++]);
                            carRunState.totalMileage = Double.parseDouble(buff[i++]) * 0.1f;
                            carRunState.surplusMileage = Float.parseFloat(buff[i++]) * 0.1f;
                            carRunState.curMileage = Float.parseFloat(buff[i++]) * 0.01f;
                            carRunState.speed = Float.parseFloat(buff[i++]) * 0.1f;
                            carRunState.controlTemp = Integer.parseInt(buff[i++]);
                            carRunState.motorTemp = Integer.parseInt(buff[i++]);
                            carRunState.curLimitValue = Integer.parseInt(buff[i++]);
                            carRunState.curDriveValue = Integer.parseInt(buff[i++]);
                            carRunState.curBrakeValue = Integer.parseInt(buff[i++]);
                            carRunState.controlOpen = Integer.parseInt(buff[i++]);
                            carRunState.dlccMode = Integer.parseInt(buff[i++]);
                            carRunState.batteryFail = Integer.parseInt(buff[i++]);
                        } else if (buff[i].equals("B")) {
                            i++;
                            carRunState.current = Integer.parseInt(buff[i++]);
                            carRunState.voltage = Integer.parseInt(buff[i++]);
                        } else if (buff[i].equals("Y")) {
                            i++;
                            carRunState.accelerateAD = Integer.parseInt(buff[i++]);
                            carRunState.brakeADLeft = Integer.parseInt(buff[i++]);
                            carRunState.brakeADRight = Integer.parseInt(buff[i++]);
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
            case "STY":  // 仪表风格查询
                revResult.cmd = CmdFlag.CMD_METER_STYLE_FIND;
                /*DialStyleInfo dialStyleInfo = new DialStyleInfo();
                dialStyleInfo.curStyle = Integer.parseInt(buff[0]);
                dialStyleInfo.curStyleName = buff[1];
                dialStyleInfo.styleName1 = buff[2];
                dialStyleInfo.styleName2 = buff[3];
                dialStyleInfo.styleName3 = buff[4];
                *//*dialStyleInfo.styleName4 = buff[5];
                dialStyleInfo.styleName5 = buff[6];
                dialStyleInfo.styleName6 = buff[7];
                dialStyleInfo.styleName7 = buff[8];*//*
                mCurrentCar.dialStyleInfo=dialStyleInfo;
                revResult.object = dialStyleInfo;*/
                break;
            case "ATL":
                revResult.cmd = CmdFlag.CMD_AMBIENT_LIGHT_FIND;
//                AtmosphereInfo atmosphereInfo = new AtmosphereInfo();
//                atmosphereInfo.mode = Integer.parseInt(buff[0]);
//                atmosphereInfo.color = buff[1];
//                atmosphereInfo.speed = Integer.parseInt(buff[2]);
//
//                atmosphereInfo.modeRide = Integer.parseInt(buff[3]);
//                atmosphereInfo.colorRide = buff[4];
//                atmosphereInfo.speedRide = Integer.parseInt(buff[5]);
//
//                atmosphereInfo.modeStandby = Integer.parseInt(buff[6]);
//                atmosphereInfo.colorStandby = buff[7];
//                atmosphereInfo.speedStandby = Integer.parseInt(buff[8]);
//
//                atmosphereInfo.modeRecharge = Integer.parseInt(buff[9]);
//                atmosphereInfo.colorRecharge = buff[10];
//                atmosphereInfo.speedRecharge = Integer.parseInt(buff[11]);
//
//                atmosphereInfo.modeLock = Integer.parseInt(buff[12]);
//                atmosphereInfo.colorLock = buff[13];
//                atmosphereInfo.speedLock = Integer.parseInt(buff[14]);
//
//               /* atmosphereInfo.modeError = Integer.parseInt(buff[15]);
//                atmosphereInfo.colorError = buff[16];
//                atmosphereInfo.speedError = Integer.parseInt(buff[17]);*/
//                mCurrentCar.atmosphereInfo = atmosphereInfo;
//                revResult.object = atmosphereInfo;
                break;
            case "ASV":
                revResult.cmd = CmdFlag.CMD_VERSION_FIND;


                break;
            case "CDR":   // 骑行数据记录查询
                revResult.cmd = CmdFlag.CMD_RIDE_RECORD;


                break;
            case "DCF":   // 驱动模式查询
                revResult.cmd = CmdFlag.CMD_DRIVE_MODE;


                break;
            case "LED":   // LED状态查询
                revResult.cmd = CmdFlag.CMD_LED_STATE;


                break;
            case "SDF":   // 屏幕亮度查询
                revResult.cmd = CmdFlag.CMD_SCREEN;


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
     * 接收到应答的数据：ACK作为标识
     * @param channel
     * @param data
     * @return
     */
    public boolean dealAckData(String channel, String data){
        if(channel.equals(BLEUUIDs.COMMON_CHANNEL.toLowerCase()) || channel.equals(BLEUUIDs.COMMON_CHANNEL.toLowerCase())){
            return dealAckCarData(data);
        }else if(channel.equals(BLEUUIDs.PART_COMMON_CHANNEL.toLowerCase()) || channel.equals(BLEUUIDs.PART_COMMON_CHANNEL.toLowerCase())){
            return dealAckHelmetData(data);
        } else if(channel.equals(BLEUUIDs.KNAP_COMMON_CHANNEL.toLowerCase()) || channel.equals(BLEUUIDs.KNAP_COMMON_CHANNEL.toLowerCase())){
            return dealAckKnapsackData(data);
        }
        return false;
    }

    /**
     * 处理头盔数据
     * @param data
     * @return
     */
    private boolean dealAckHelmetData(String data) {
        data = data.replace(DebugFlag.ACK_HEAD_PROTOCOL, "").replace("\r\n", "").replace("$", "");
        String[] buff = data.split("=");
        if (buff.length < 2) {
            return false;
        }
        String type = buff[0];
        data = buff[1];
        buff = data.split(",");
        revResult = new RevResult();
        switch (type) {
            case "CON":   // 测试模式-蓝牙参数配置
            revResult.cmd = CmdFlag.CMD_PART_TEST_BLE_CONFIG;
            if (buff.length != 2) {
                revResult.errMsg = "收到指令长度不对";
                revResult.result = RevResult.LENGTH_ERR;
                break;
            }
            if (buff[0].equals("0")) {
                revResult.errMsg = "密码错误";
                revResult.result = RevResult.PWD_ERR;
                break;
            } else if (buff[0].equals("2")) {
                revResult.errMsg = "指令格式错误";
                revResult.result = RevResult.FAILED;
                break;
            }
            revResult.setCmdNo(buff[1]);
            break;
            case "BDC":   // 前后灯显示状态控制
                revResult.cmd = CmdFlag.CMD_PART_TEST_LED_CONTROL;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            // 以上是测试模式下的指令应答
            case "FDC":   // 骑行状态灯选择
                revResult.cmd = CmdFlag.CMD_PART_DRIVE_LED;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "COF":   // 开关机控制
                revResult.cmd = CmdFlag.CMD_PART_POWER_CONTROL;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "SMD":   // 显示模式控制
                revResult.cmd = CmdFlag.CMD_PART_SHOW_MODE;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "LCK":   // 头盔锁控制
                revResult.cmd = CmdFlag.CMD_PART_LOCK;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "TRL":   // 体感转向功能
                revResult.cmd = CmdFlag.CMD_PART_BODY_ROUND;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "BLC":   // 灯状态配置
                revResult.cmd = CmdFlag.CMD_PART_LED_STATE;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "TUN":   // 车辆转向信息
                revResult.cmd = CmdFlag.CMD_PART_CAR_ROUND_INF0;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "DIY": // 车灯DIY显示
                revResult.cmd = CmdFlag.CMD_PART_DIY_LED;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "MNA": // 音频蓝牙名称修改
                revResult.cmd = CmdFlag.CMD_PART_AUDIO_RENAME;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "MDS": // 工作模式切换
                revResult.cmd = CmdFlag.CMD_PART_WORK_MODE;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "LNM": // 开启遥控按键学习模式
                revResult.cmd = CmdFlag.CMD_PART_REMOTE_MODE;
                if (buff.length != 6) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                HStudyAck hStudyAck = new HStudyAck();
                hStudyAck.studyFlag = Integer.parseInt(buff[1]);
                hStudyAck.keyNo = Integer.parseInt(buff[2]);
                hStudyAck.keyType = Integer.parseInt(buff[3]);
                hStudyAck.ackType = Integer.parseInt(buff[4]);
                revResult.object = hStudyAck;
                revResult.setCmdNo(buff[5]);
                break;
            case "NAM": // 头盔蓝牙广播名称修改
                revResult.cmd = CmdFlag.CMD_PART_BLE_RENAME;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "VAD": // 音量调节指令
                revResult.cmd = CmdFlag.CMD_PART_AUDIO_VOL_CONTROL;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "AUC": // 音频播放控制
                revResult.cmd = CmdFlag.CMD_PART_AUDIO_PLAY;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "TTS": // 曲目切换
                revResult.cmd = CmdFlag.CMD_PART_AUDIO_SONG;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "CCC": // 曲目切换
                revResult.cmd = CmdFlag.CMD_PART_AUDIO_SONG;
                if (buff.length != 4) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[2].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[2].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[3]);
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
     * 处理背包数据
     * @param data
     * @return
     */
    private boolean dealAckKnapsackData(String data) {
        data = data.replace(DebugFlag.ACK_KNAP_PROTOCOL, "").replace("\r\n", "").replace("$", "");
        String[] buff = data.split("=");
        if (buff.length < 2) {
            return false;
        }
        String type = buff[0];
        data = buff[1];
        buff = data.split(",");
        revResult = new RevResult();
        switch (type) {
            case "CON":   // 测试模式-蓝牙参数配置
                revResult.cmd = CmdFlag.CMD_KNAP_TEST_BLE_CONFIG;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "LEC":   // 测试模式-氛围灯显示状态控制
                revResult.cmd = CmdFlag.CMD_PART_TEST_LED_CONTROL;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            // 以上是测试模式下的指令应答
            case "BKL":   // 骑行状态灯选择
                revResult.cmd = CmdFlag.CMD_KNAP_TEST_BRAKE_CONFIG;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "CLK":   // 背包锁控制
                revResult.cmd = CmdFlag.CMD_KNAP_LOCK;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "LOC":   // 背包寻找
                revResult.cmd = CmdFlag.CMD_KNAP_FIND;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "CQI":   // 无线充控制
                revResult.cmd = CmdFlag.CMD_KNAP_WIRELESS;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "CUR":   // 紫外线消毒功能控制
                revResult.cmd = CmdFlag.CMD_KNAP_DISINFECT;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                } else if (buff[0].equals("3")) {
                    revResult.errMsg = "背包处于打开状态，无法开启";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "CLP":   // 包外示廓灯控制
                revResult.cmd = CmdFlag.CMD_KNAP_OUT_LED_CONTROL;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "CAR":   // 车身状态数据下发
                revResult.cmd = CmdFlag.CMD_KNAP_CAR_INFO;
                // todo 当前指令待定
                revResult.setCmdNo(buff[1]);
                break;
            case "CLY":   // 包内照明灯颜色配置
                revResult.cmd = CmdFlag.CMD_KNAP_IN_LED_COLOR_CONFIG;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "AML": // 示廓灯状态配置
                revResult.cmd = CmdFlag.CMD_KNAP_OUT_LED_STATE;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "FIN": // 背包指纹配置
                revResult.cmd = CmdFlag.CMD_KNAP_FINGERPRINT;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.object = Integer.parseInt(buff[0]);
                revResult.setCmdNo(buff[1]);
                break;
            case "PLG": // 包内照明灯控制
                revResult.cmd = CmdFlag.CMD_KNAP_IN_LED_CONTROL;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "TME": // 当地时间校准
                revResult.cmd = CmdFlag.CMD_KNAP_TIME_SYNC;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "MDS": // 工作模式切换
                revResult.cmd = CmdFlag.CMD_KNAP_WORK_MODE;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "NAM": // 背包蓝牙广播名称修改
                revResult.cmd = CmdFlag.CMD_KNAP_BLE_RENAME;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
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
     * 处理车辆的应答数据
     * @param data
     * @return
     */
    public boolean dealAckCarData(String data){
        data = data.replace(DebugFlag.ACK_PROTOCOL, "").replace("\r\n", "").replace("$", "");
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

                if (buff[0].equals("0")) {
                    revResult.errMsg = "蓝牙参数配置失败";
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
                revResult.object = designConfig;
                revResult.setCmdNo(buff[2]);
                break;
            case "TET":   // 开始成品测试
                revResult.cmd = CmdFlag.CMD_GOOD_TEST;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "应答失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "LFC":   // 车辆出厂参数配置
                revResult.cmd = CmdFlag.CMD_OUT_CAR_CONFIG;
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
            case "CDG":   // 写码
                revResult.cmd = CmdFlag.CMD_INPUT_CODE;
                if (buff.length != 4) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                revResult.object = buff[0];
                revResult.setCmdNo(buff[3]);
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

                BLEConfig.mode = Integer.parseInt(buff[1]);
                revResult.object = buff[1];
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
                revResult.object = buff[1];
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
                            bleConfig.minConInterval = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            revResult.errMsg = "最小连接间隔参数错误";
                            revResult.result = RevResult.FAILED;
                            break;
                        }
                        try {
                            bleConfig.maxConInterval = Integer.parseInt(buff[i++]);
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
                        CarConfigInfo carConfig = new CarConfigInfo();
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
            case "ONF":   // 开关机模式的修改
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
                    revResult.errMsg = "开关车辆锁车失败";
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
                //revResult.setCmdNo(buff[2]);
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
            case "FCG":   // 出厂模式
                revResult.cmd = CmdFlag.CMD_OUT_MODE;
                if (buff.length != 15 && buff.length != 3) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }

                if(buff.length == 3){
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
//                    revResult.setCmdNo(buff[3]);
                }else {
                    ProduceConfig produceConfig1 = new ProduceConfig();
                    produceConfig1.brakeMode = Integer.parseInt(buff[0]);
                    produceConfig1.speedUnit = Integer.parseInt(buff[1]);
                    produceConfig1.gear1 = Integer.parseInt(buff[2]);
                    produceConfig1.gear2 = Integer.parseInt(buff[3]);
                    produceConfig1.gear3 = Integer.parseInt(buff[4]);
                    produceConfig1.gear4 = Integer.parseInt(buff[5]);
                    produceConfig1.bmsType = Integer.parseInt(buff[6]);
                    produceConfig1.electronicBrake = Integer.parseInt(buff[7]);
                    produceConfig1.openFrontLed = Integer.parseInt(buff[8]);
                    produceConfig1.normalTaillight = Integer.parseInt(buff[9]);
                    produceConfig1.salesNo = Integer.parseInt(buff[10]);
                    produceConfig1.customerNo = Integer.parseInt(buff[11]);
                    produceConfig1.carTypeName = buff[12];
                    produceConfig1.bleType = Integer.parseInt(buff[13]);
                    revResult.object = produceConfig1;
                    revResult.setCmdNo(buff[14]);
                }
                break;
            case "ALS":   // 报警器控制
                revResult.cmd = CmdFlag.CMD_WARN_CONTROL;
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
                    revResult.errMsg = "报警器控制处理失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "SIM":   // 自检模式控制
                revResult.cmd = CmdFlag.CMD_CHECK_MODE;
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
                YBConfig.checkResult = buff[1];
                revResult.setCmdNo(buff[2]);
                break;
            case "SDF":   // 屏幕亮度控制（ES800）
                revResult.cmd = CmdFlag.CMD_SCREEN_BRIGHTNESS;
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
                    revResult.errMsg = "屏幕亮度控制处理失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "LNM":   // 开启学习模式
                revResult.cmd = CmdFlag.CMD_STUDY_MODE;
                if (buff.length != 6) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }

                if (buff[4].equals("0")) {
                    revResult.errMsg = "开启学习模式失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[5]);
                break;
            case "DCF":
                revResult.cmd = CmdFlag.CMD_DRIVER_CONFIG;
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
                    revResult.errMsg = "驱动模式配置失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "CNF":   // 滑板车状态信息查询
                revResult.cmd = CmdFlag.CMD_STATE_QUERY;
                if(buff.length < 12){
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                DevStateInfo devStateInfo = new DevStateInfo();
                int k = 0;
                try {
                    while (k < buff.length) {
                        if (buff[k].equals("ECU")) {
                            k++;
                            devStateInfo.chargeMos = Integer.parseInt(buff[k++]);
                            devStateInfo.dischargeMos = Integer.parseInt(buff[k++]);
                            String cmd = buff[k++];
                            if(StringUtils.isEmpty(cmd)){
                                devStateInfo.soh = Integer.parseInt(buff[k++]);
                            }else {
                                devStateInfo.soh = Integer.parseInt(cmd);
                            }
                            devStateInfo.highTemp = Integer.parseInt(buff[k++]);
                            devStateInfo.lowTemp = Integer.parseInt(buff[k++]);
                            devStateInfo.mosTemp = Integer.parseInt(buff[k++]);
                            devStateInfo.otherTemp = Integer.parseInt(buff[k++]);
                            devStateInfo.loopTimes = Integer.parseInt(buff[k++]);
                            devStateInfo.accAd = Integer.parseInt(buff[k++]);
                            devStateInfo.leftAd = Integer.parseInt(buff[k++]);
                            devStateInfo.rightAd = Integer.parseInt(buff[k++]);
                            devStateInfo.brakeState = Integer.parseInt(buff[k++]);
                        } else {
                            revResult.errMsg = "解析参数错误";
                            revResult.result = RevResult.FAILED;
                            break;
                        }
                    }
                } catch (Exception e) {
                    revResult.errMsg = "其中某一个参数配置错误：" + k;
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.object = devStateInfo;
                break;
            case "ATL":  // 设置氛围灯模式
                revResult.cmd = CmdFlag.CMD_AMBIENT_LIGHT_MODE;
                if(buff.length < 6){
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }

                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                }

                AmbientLedInfo ambientLedInfo = new AmbientLedInfo();
                ambientLedInfo.carMode = Integer.parseInt(buff[1]);
                ambientLedInfo.atmosphereLightStyle = Integer.parseInt(buff[2]);
                ambientLedInfo.rgbColor = buff[3];
                ambientLedInfo.flowSpeed = Integer.parseInt(buff[4]);
                revResult.object = ambientLedInfo;
                revResult.setCmdNo(buff[5]);
                break;
            case "STY":
                revResult.cmd = CmdFlag.CMD_INTERFACE_STYLE;
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
                    revResult.errMsg = "报警器控制处理失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[2]);
                break;
            case "PSH":
                revResult.cmd = CmdFlag.CMD_HELMET_CONNECT;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }

                if (buff[0].equals("0")) {
                    revResult.errMsg = "连接/断开指定头盔处理失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "PSK":
                revResult.cmd = CmdFlag.CMD_BACKPACK_CONNECT;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }

                if (buff[0].equals("0")) {
                    revResult.errMsg = "连接/断开指定背包处理失败";
                    revResult.result = RevResult.FAILED;
                    break;
                }
                revResult.setCmdNo(buff[1]);
                break;
            case "TRL":   // 体感转向功能
                revResult.cmd = CmdFlag.CMD_PART_BODY_ROUND;
                if (buff.length != 2) {
                    revResult.errMsg = "收到指令长度不对";
                    revResult.result = RevResult.LENGTH_ERR;
                    break;
                }
                if (buff[0].equals("0")) {
                    revResult.errMsg = "密码错误";
                    revResult.result = RevResult.PWD_ERR;
                    break;
                } else if (buff[0].equals("2")) {
                    revResult.errMsg = "指令格式错误";
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
     * 判断当前接收到的指令和具体收到的应答指令是否一致
     *
     * @param nowCmd 当前收到的指令
     * @return 指令是否一致
     */
    private boolean checkCmdNo(int nowCmd) {
        return CmdFlag.CMD_NO == nowCmd;
    }





}
