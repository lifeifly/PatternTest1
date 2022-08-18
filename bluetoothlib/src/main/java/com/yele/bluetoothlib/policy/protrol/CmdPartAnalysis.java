package com.yele.bluetoothlib.policy.protrol;

import com.yele.baseapp.utils.LogUtils;
import com.yele.bluetoothlib.bean.BLEUUIDs;
import com.yele.bluetoothlib.bean.LogDebug;
import com.yele.bluetoothlib.bean.cmd.CmdFlag;
import com.yele.bluetoothlib.bean.cmd.RevResult;
import com.yele.bluetoothlib.bean.config.part.head.HReportInfo;
import com.yele.bluetoothlib.bean.config.part.head.HStudyAck;
import com.yele.bluetoothlib.bean.debug.DebugFlag;
import com.yele.bluetoothlib.utils.PartColorUtils;

/**
 * 指令协议解析类
 */
public class CmdPartAnalysis {

    private static final String TAG = "CmdPartAnalysis";

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
    public CmdPartAnalysis(String channel, String data) {
        this.channel = channel;
        this.mData = data;
    }

    // 收到的结果
    private RevResult revResult;

    /**
     * 开始解析数据
     */
    public RevResult analysis() {
        if (channel.equals(BLEUUIDs.PART_COMMON_CHANNEL.toLowerCase())) {
            logi("收到普通数据：" + mData);
            if (mData.startsWith("+ACK")) {
                if (!dealAckData(mData)) {
                    dealUpgrade(mData);
                }else{
                    logi("普通数据解析陈宫：" + mData);
                }
            } else {
                if (!dealReportData(mData)) {
                    logi("收到脏数据了1");
                }
            }
        } else if (channel.equals(BLEUUIDs.PART_REPORT_CHANNEL.toLowerCase())) {
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
        } else if (channel.equals(BLEUUIDs.PART_UPGRADE_CHANNEL.toLowerCase())) {
            logi("收到更新数据：" + mData);
            if (mData.startsWith("+ACK")) {
                dealUpgrade(mData);
            } else {
                logi("收到脏数据3");
            }
        }  else {
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
        data = data.replace(DebugFlag.PART_ACK_PROTOCOL, "")
                .replace("\r\n", "")
                .replace("$", "");
        String[] buff = data.split("=");

        if (buff.length < 2) {
            logi("收到非应答数据");
            return false;
        }
        String type = buff[0];
        data = buff[1];
        buff = data.split(",");
        logi("开始解析应答数据 " + type);
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
            case "CAR":   // 配置信息应答
                revResult.cmd = CmdFlag.CMD_READ_CONFIG;
                // todo 当前指令待定
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
            LogUtils.i(TAG,"解析成功");
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
        data = data.replace(DebugFlag.PART_RESP_PROTOCOL, "").replace("\r\n", "").replace("$", "");
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
     * 处理更新应答数据
     *
     * @param data 当前更新应答数据
     */
    private void dealUpgrade(String data) {
        data = data.replace(DebugFlag.PART_ACK_PROTOCOL, "").replace("\r\n", "").replace("$", "");
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
