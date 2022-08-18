package com.yele.bluetoothlib.policy.protrol.control;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.os.Build;

import com.yele.baseapp.utils.LogUtils;
import com.yele.bluetoothlib.bean.BLEUUIDs;
import com.yele.bluetoothlib.bean.cmd.CmdFlag;
import com.yele.bluetoothlib.bean.cmd.RevResult;
import com.yele.bluetoothlib.bean.config.ble.DeviceBase;
import com.yele.bluetoothlib.bean.debug.DebugFlag;
import com.yele.bluetoothlib.bean.device.OkDevice;
import com.yele.bluetoothlib.bean.device.ReportInfo;
import com.yele.bluetoothlib.policy.event.cmd.CmdReportEvent;
import com.yele.bluetoothlib.policy.event.cmd.CmdRevEvent;
import com.yele.bluetoothlib.policy.protrol.CmdCarAnalysis;
import com.yele.bluetoothlib.policy.protrol.CmdKnapAnalysis;
import com.yele.bluetoothlib.policy.protrol.CmdPartAnalysis;

import org.greenrobot.eventbus.EventBus;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: CarControlPresenter
 *
 * @Author: Chenxc
 * @Date: 2021/7/21 9:14
 * @Description: 常规车辆的通讯控制类
 * History:
 * <author> <time><version><desc>
 */
public class CarPartControlPresenter extends BaseBleControl implements INormalSet{

    private static final String TAG = "CarControlPresenter";
    /**
     * 单独打印开关
     */
    protected boolean DEBUG_LOG = false;


    public CarPartControlPresenter(Context context, OkDevice device) {
        this.mContext = context;
        this.okDevice = device;
        init();
    }

    private void init() {
        COMMON_CHANNEL = BLEUUIDs.CAR_COMMON_CHANNEL;
        REPORT_CHANNEL = BLEUUIDs.CAR_REPORT_CHANNEL;
        UPGRADE_CHANNEL = BLEUUIDs.CAR_UPGRADE_CHANNEL;
        COMMON_HEAD_CHANNEL = BLEUUIDs.PART_COMMON_CHANNEL;
        REPORT_HEAD_CHANNEL = BLEUUIDs.PART_REPORT_CHANNEL;
        UPGRADE_HEAD_CHANNEL = BLEUUIDs.PART_UPGRADE_CHANNEL;
        COMMON_KNAP_CHANNEL = BLEUUIDs.KNAP_COMMON_CHANNEL;
        REPORT_KNAP_CHANNEL = BLEUUIDs.KNAP_REPORT_CHANNEL;
        UPGRADE_KNAP_CHANNEL = BLEUUIDs.KNAP_UPGRADE_CHANNEL;
    }

    @Override
    public void connectDevice() {
        String address = okDevice.device.getAddress();
        final BluetoothDevice device = okDevice.device;
        logi("连接目标蓝牙：" + device.getName() + " mac Address:" + address);
        conAction = CON_ACTION_CONNECT;
        int transport = -1;
        try {
            transport = device.getClass().getDeclaredField("TRANSPORT_LE").getInt(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        final int aimTransport = transport;
        new Thread() {
            @Override
            public void run() {
                super.run();
                connectCarDevice(aimTransport);
            }

            /**
             * 连接车辆设备
             * @param transport 设备的通讯方式
             */
            private void connectCarDevice(int transport) {
                BluetoothGatt gatt;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (transport == -1) {
                        gatt = device.connectGatt(mContext, false, gattConCallback, BluetoothDevice.TRANSPORT_LE);
                    } else {
                        gatt = device.connectGatt(mContext, false, gattConCallback, transport);
                    }
                } else {
                    gatt = device.connectGatt(mContext, false, gattConCallback);
                }
                if (gatt == null) {
                    logi( "连接的通讯接口失败");
                    return;
                }
                if (gatt.discoverServices()) {
                    logi( "发现服务了");
                    boolean state = gatt.connect();
                    logi(  "发现服务中: " + state);
                }
            }
        }.start();
    }

    @Override
    public void disconnect() {
        if (conDevice == null) {
            LogUtils.i(TAG, "当前无设备连接");
            return;
        }
        if (conGatt == null) {
            LogUtils.i(TAG, "通讯接口以及断开连接了");
            return;
        }
        LogUtils.i(TAG, "断开设备");
        conGatt.disconnect();
    }

    @Override
    public void sendNormalData(byte[] dates) {

    }

    @Override
    public void setOnConnectListener(OnBleConnectListener listener) {
        this.connectListener = listener;
    }


    @Override
    public void setOnBleDataChangeListener(OnBleDataChangeListener listener) {
        this.dataChangeListener = listener;
    }

    @Override
    public void upgradeMCU(String path) {

    }

    @Override
    public boolean sendCmd( byte[] data) {
        return sendSCmd(COMMON_CHANNEL,data);
    }

    @Override
    public boolean sendCmd(String channel, byte[] data) {
        // channel    0：车辆；1：头盔;2:背包
        if(channel.equals("1")){
            return sendSCmd(COMMON_HEAD_CHANNEL,data);
        }else if(channel.equals("2")){
            return sendSCmd(COMMON_KNAP_CHANNEL,data);
        }else {
            return sendSCmd(COMMON_CHANNEL,data);
        }
    }


    @Override
    public boolean updateData(byte[] data) {
        return sendSCmd(UPGRADE_CHANNEL,data);
    }

    @Override
    public boolean updateData(String channel, byte[] data) {
        // channel    0：车辆；1：头盔;2:背包
        if(channel.equals("1")){
            return sendSCmd(UPGRADE_HEAD_CHANNEL,data);
        }else if(channel.equals("2")){
            return sendSCmd(UPGRADE_KNAP_CHANNEL,data);
        }else {
            return sendSCmd(UPGRADE_CHANNEL,data);
        }
    }

    @Override
    protected void dealRevData(String channel, String data) {
        data = data.replace("\r\n", "");

        RevResult revResult = null;
        if(channel.equals(BLEUUIDs.KNAP_UPGRADE_CHANNEL.toLowerCase())
                || channel.equals(BLEUUIDs.KNAP_REPORT_CHANNEL.toLowerCase())
                || channel.equals(BLEUUIDs.KNAP_COMMON_CHANNEL.toLowerCase())){
            CmdKnapAnalysis analysis = new CmdKnapAnalysis(channel, data);
            revResult = analysis.analysis();
        }else if(channel.equals(BLEUUIDs.PART_COMMON_CHANNEL.toLowerCase())
                || channel.equals(BLEUUIDs.PART_REPORT_CHANNEL.toLowerCase())
                || channel.equals(BLEUUIDs.PART_UPGRADE_CHANNEL.toLowerCase())){
            CmdPartAnalysis analysis = new CmdPartAnalysis(channel, data);
            revResult = analysis.analysis();
        }else {
            CmdCarAnalysis analysis = new CmdCarAnalysis(channel, data);
            revResult = analysis.analysis();
        }

        if (revResult == null) {
            return;
        }
        if (revResult.result == RevResult.INIT) {
            return;
        }
        if (revResult.type == RevResult.TYPE_ACK) {
            // 当前ACK的数据应答处理
            if (dataChangeListener != null) {
                dataChangeListener.revACKData(revResult);
            }
        } else if (revResult.type == RevResult.TYPE_REPORT) {
            // 当前上报指令的更新处理
            if (dataChangeListener != null) {
                dataChangeListener.revReportData(revResult);
            }
        } else if (revResult.type == RevResult.TYPE_UPGRADE) {
            logi("升级指令应答 ");
            if (dataChangeListener != null) {
                dataChangeListener.revUpgradeData(revResult);
            }
        }
    }

    /**
     * 处理更新数据的数据结构包
     * @param revResult 当前的处理结果
     */
    protected void dealUpdateData(RevResult revResult) {
//        if (type == 13) {
//            dealNormalUpdate(revResult);
////            dealLengthUpdate(revResult);
//        } else if (type == 15) {
//            dealUpdateMCU(revResult);
//        }
    }

    /**
     * 更新上报指令数据
     * @param data      当前收到的数据
     * @param revResult 数据处理的结果
     */
    protected void dealReportData(String data, RevResult revResult) {
        if (revResult.cmd == CmdFlag.CMD_REPORT_TEST) {
            //
        }
        EventBus.getDefault().post(new CmdReportEvent(data, revResult));
    }

    /**
     * 处理应答指令数据
     * @param data      当前收到的数据
     * @param revResult 数据的处理结果
     */
    protected void dealACKData(String data, RevResult revResult) {
        if (revResult.cmd == CmdFlag.CMD_READ_CONFIG) {
            ReportInfo reportInfo = (ReportInfo) revResult.object;
            DeviceBase.setSN(reportInfo.bluetoothConfig.sn);
        } else if (revResult.cmd == CmdFlag.CMD_UPGRADE_LENGTH_READY) {
//            dealLengthUpdate(revResult);
        } else if (revResult.cmd == CmdFlag.CMD_UPGRADE_READY) {
//            dealNormalUpdate(revResult);
        }
        EventBus.getDefault().post(new CmdRevEvent(data, revResult));
    }
}
