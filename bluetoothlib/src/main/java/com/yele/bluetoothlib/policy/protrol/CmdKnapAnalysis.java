package com.yele.bluetoothlib.policy.protrol;

import com.yele.baseapp.utils.LogUtils;
import com.yele.bluetoothlib.bean.BLEUUIDs;
import com.yele.bluetoothlib.bean.LogDebug;
import com.yele.bluetoothlib.bean.cmd.CmdFlag;
import com.yele.bluetoothlib.bean.cmd.RevResult;
import com.yele.bluetoothlib.bean.config.part.knapsack.KReadFingerprintInfo;
import com.yele.bluetoothlib.bean.config.part.knapsack.KReadOutLedInfo;
import com.yele.bluetoothlib.bean.config.part.knapsack.KReportStateInfo;
import com.yele.bluetoothlib.bean.debug.DebugFlag;
import com.yele.bluetoothlib.utils.PartColorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 指令协议解析类
 */
public class CmdKnapAnalysis {

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
    public CmdKnapAnalysis(String channel, String data) {
        this.channel = channel;
        this.mData = data;
    }

    // 收到的结果
    private RevResult revResult;

    /**
     * 开始解析数据
     */
    public RevResult analysis() {
        if (channel.equals(BLEUUIDs.KNAP_COMMON_CHANNEL.toLowerCase())) {
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
        } else if (channel.equals(BLEUUIDs.KNAP_REPORT_CHANNEL.toLowerCase())) {
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
        } else if (channel.equals(BLEUUIDs.KNAP_UPGRADE_CHANNEL.toLowerCase())) {
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
        data = data.replace(DebugFlag.PART_KNAP_ACK_PROTOCOL, "")
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
        data = data.replace(DebugFlag.PART_KNAP_RESP_PROTOCOL, "").replace("\r\n", "").replace("$", "");
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
                    readOutLedInfo.openHour = buff[index ++];
                    readOutLedInfo.openMin = buff[index ++];
                    readOutLedInfo.closeHour = buff[index ++];
                    readOutLedInfo.closeMin = buff[index ++];
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

    /**
     * 处理更新应答数据
     *
     * @param data 当前更新应答数据
     */
    private void dealUpgrade(String data) {
        data = data.replace(DebugFlag.PART_KNAP_ACK_PROTOCOL, "").replace("\r\n", "").replace("$", "");
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
