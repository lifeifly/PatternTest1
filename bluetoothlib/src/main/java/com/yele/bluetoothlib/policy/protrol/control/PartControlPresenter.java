package com.yele.bluetoothlib.policy.protrol.control;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.os.Build;

import com.yele.baseapp.utils.LogUtils;
import com.yele.bluetoothlib.bean.BLEUUIDs;
import com.yele.bluetoothlib.bean.cmd.RevResult;
import com.yele.bluetoothlib.bean.device.OkDevice;
import com.yele.bluetoothlib.policy.protrol.CmdPartAnalysis;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: PartControlPresenter
 *
 * @Author: Chenxc
 * @Date: 2021/7/21 15:14
 * @Description: 配件通讯控制类
 * History:
 * <author> <time><version><desc>
 */
public class PartControlPresenter extends BaseBleControl implements INormalSet {

    public PartControlPresenter(Context context, OkDevice okDevice) {
        this.mContext = context;
        this.okDevice = okDevice;
        init();
    }

    private void init() {
        COMMON_CHANNEL = BLEUUIDs.PART_COMMON_CHANNEL;
        REPORT_CHANNEL = BLEUUIDs.PART_REPORT_CHANNEL;
        UPGRADE_CHANNEL = BLEUUIDs.PART_UPGRADE_CHANNEL;
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
                    logi("连接的通讯接口失败");
                    return;
                }
                if (gatt.discoverServices()) {
                    logi("发现服务了");
                    boolean state = gatt.connect();
                    logi("发现服务中: " + state);
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
    protected void dealRevData(String channel, String data) {
        data = data.replace("\r\n", "");
        CmdPartAnalysis analysis = new CmdPartAnalysis(channel, data);
        RevResult revResult = analysis.analysis();
        if (revResult == null) {
            loge("数据解析为空");
            return;
        }
        if (revResult.type == RevResult.TYPE_ACK) {
            if (dataChangeListener != null) {
                dataChangeListener.revACKData(revResult);
            }
        } else if (revResult.type == RevResult.TYPE_REPORT) {
            if (dataChangeListener != null) {
                dataChangeListener.revReportData(revResult);
            }
        } else if (revResult.type == RevResult.TYPE_UPGRADE) {
            if (dataChangeListener != null) {
                dataChangeListener.revUpgradeData(revResult);
            }
        }
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
    public boolean sendCmd(byte[] data) {
        return sendSCmd(COMMON_CHANNEL, data);
    }

    @Override
    public boolean sendCmd(String channel, byte[] data) {
        return false;
    }

    @Override
    public boolean updateData(byte[] data) {
        return sendSCmd(UPGRADE_CHANNEL,data);
    }

    @Override
    public boolean updateData(String channel, byte[] data) {
        return false;
    }
}
