package com.yele.bluetoothlib.policy.protrol;

import com.yele.baseapp.utils.ByteUtils;
import com.yele.bluetoothlib.bean.cmd.CmdFlag;
import com.yele.bluetoothlib.bean.config.ble.DeviceBase;
import com.yele.bluetoothlib.bean.config.part.head.HCarInfoConfig;
import com.yele.bluetoothlib.bean.config.part.head.HDriveLed;
import com.yele.bluetoothlib.bean.config.part.head.HLedConfig;
import com.yele.bluetoothlib.bean.config.part.head.HRemoteStudy;
import com.yele.bluetoothlib.bean.config.part.head.HShowMode;
import com.yele.bluetoothlib.bean.config.part.head.HTestBleConfig;
import com.yele.bluetoothlib.bean.config.part.head.HTestLedControl;
import com.yele.bluetoothlib.bean.debug.DebugFlag;
import com.yele.bluetoothlib.utils.JavaAES128Encryption;

public class CmdPartPackage {

    private static final String TAG = "CmdPartPackage";

    // 当前需要解析的指令
    private int cmd = -1;

    // 当前需要解析的数据
    private Object obj;

    public CmdPartPackage(int cmd, Object object) {
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
                str = "FDC=" + DeviceBase.PWD + "," + hDriveLed.frontTurnState + "," + hDriveLed.frontDriveState + "," + hDriveLed.frontDriveColor + ","
                        + hDriveLed.rearTurnState + "," + hDriveLed.rearDriveState + "," + hDriveLed.rearDriveColor + "," + getCmdNo();
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
        }
        if (str != null) {
            str = DebugFlag.PART_SEND_PROTOCOL + str + "$\r\n";
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
        int cmdNo = CmdFlag.CMD_PART_NO++;
        if (cmdNo > 65535) {
            cmdNo = 0;
            CmdFlag.CMD_PART_NO = 0;
        }
        byte[] buff = ByteUtils.longToBytesByBig(cmdNo, 2);
        return ByteUtils.bytesToStringByBig(buff);
    }
}
