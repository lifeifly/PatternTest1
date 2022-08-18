package com.yele.hu.upgradetools.view.service;

import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.yele.baseapp.utils.LogUtils;
import com.yele.hu.upgradetools.bean.CmdFlag;
import com.yele.hu.upgradetools.bean.DeviceBase;
import com.yele.hu.upgradetools.bean.RevResult;
import com.yele.hu.upgradetools.bean.info.car.OkaiBleDevice;
import com.yele.hu.upgradetools.bean.info.car.ReportInfo;
import com.yele.hu.upgradetools.data.BLEUUIDs;
import com.yele.hu.upgradetools.policy.ble.CmdDeviceAnalysis;
import com.yele.hu.upgradetools.policy.ble.CmdPackage;
import com.yele.hu.upgradetools.policy.ble.UpgradeControl;
import com.yele.hu.upgradetools.policy.ble.UpgradeLengthPackage;
import com.yele.hu.upgradetools.policy.event.BleDevRequestConEvent;
import com.yele.hu.upgradetools.policy.event.BleDevScanEvent;
import com.yele.hu.upgradetools.policy.event.CmdReportEvent;
import com.yele.hu.upgradetools.policy.event.CmdResultEvent;
import com.yele.hu.upgradetools.policy.event.CmdRevEvent;
import com.yele.hu.upgradetools.policy.event.CmdSendEvent;
import com.yele.hu.upgradetools.policy.event.UpgradeActionEvent;
import com.yele.hu.upgradetools.policy.event.UpgradeResultEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 蓝牙服务
 */
public class BleService extends BaseBleService {

    private static final String TAG = "BleService";

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i(TAG,"onCreate");
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public class MyBinder extends Binder {
        public String getData() {
            return "onBind Service";
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        LogUtils.i(TAG,"onDestroy");
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    /**
     * 蓝牙扫描功能的动作
     * 主要由请求扫描，以及停止扫描的动作
     *
     * @param event 具体动作
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleDevScanEvent(BleDevScanEvent event) {
        if (event == null) {
            return;
        }
        switch (event.code) {
            case BleDevScanEvent.REQUEST_SCAN_DEV:
                setAimScanDevName((String) event.obj);
                startScanDev();
                break;
            case BleDevScanEvent.REQUEST_STOP_SCAN_DEV:
                stopScanDev();
                break;
        }
    }


    /**
     * 蓝牙设备的请求动作，主要是连接、断开功能
     *
     * @param event 具体的请求动作
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleDevRequestConEvent(BleDevRequestConEvent event) {
        if (event == null) {
            return;
        }
        switch (event.code) {
            case BleDevRequestConEvent.CODE_REQUEST_CONNECT:
                OkaiBleDevice device = (OkaiBleDevice) event.obj;
                if (conGatt != null){
                    conGatt.close();
                }
                if(device.model == 1){
                    COMMON_CHANNEL = BLEUUIDs.PART_COMMON_CHANNEL;
                }else if(device.model == 2){
                    COMMON_CHANNEL = BLEUUIDs.KNAP_COMMON_CHANNEL;
                }else {
                    COMMON_CHANNEL = BLEUUIDs.COMMON_CHANNEL;
                }

                BluetoothGatt gatt = connectDevice(device);
                if (gatt == null) {
                    LogUtils.i(TAG,  "连接的通讯接口失败");
                    return;
                }
                if (gatt.discoverServices()) {
                    LogUtils.i(TAG,  "发现服务了");
                    boolean state = gatt.connect();
                    LogUtils.i(TAG,  "发现服务中: " + state);
                }
                break;
            case BleDevRequestConEvent.CODE_REQUEST_DISCONNECT:
                if (conDevice == null) {
                    LogUtils.i(TAG, "当前无设备连接");
                    if(conGatt != null){
                        conGatt.disconnect();
                    }
                    return;
                }
                if(conGatt != null){
                    conGatt.disconnect();
                    LogUtils.i(TAG, "通讯接口以及断开连接了");
                    return;
                }
                LogUtils.i(TAG,"断开连接");
                break;
        }
    }

    @Override
    protected void deviceDisConnectFailed() {
        okaiBleDevice.isConnected = true;
        EventBus.getDefault().post(
                new BleDevRequestConEvent(BleDevRequestConEvent.CODE_RESULT_DISCONNECT_FAIL, null));
    }

    @Override
    protected void deviceConnectFailed() {
        okaiBleDevice.isConnected = false;
        EventBus.getDefault().post(
                new BleDevRequestConEvent(BleDevRequestConEvent.CODE_RESULT_CONNECT_FAIL, null));
    }

    @Override
    protected void deviceDisconnect() {
        okaiBleDevice.isConnected = false;
        EventBus.getDefault().post(
                new BleDevRequestConEvent(BleDevRequestConEvent.CODE_RESULT_DISCONNECT, null));
    }

    @Override
    protected void deviceConnected() {
        okaiBleDevice.isConnected = true;
        EventBus.getDefault().post(
                new BleDevRequestConEvent(BleDevRequestConEvent.CODE_RESULT_CONNECT, null));
    }

    @Override
    protected void discoverNewDevice(OkaiBleDevice device) {
        EventBus.getDefault().post(new BleDevScanEvent(BleDevScanEvent.RESULT_DISCOVER_LIST_DEV, device));
    }

    @Override
    protected void discoverAimDevice(OkaiBleDevice device) {
        EventBus.getDefault().post(new BleDevScanEvent(BleDevScanEvent.RESULT_DISCOVER_AIM_DEV, device));
    }

    @Override
    protected void deviceConnectTimeOut() {
        EventBus.getDefault().post(
                new BleDevRequestConEvent(BleDevRequestConEvent.CODE_RESULT_TIME_OUT, null));
    }

    @Override
    protected void deviceChannelSuccess() {
        EventBus.getDefault().post(
                new BleDevRequestConEvent(BleDevRequestConEvent.CODE_RESULT_CHANNEL, null));
    }



    /**
     * 发送指令
     * @param event 指令
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCmdSendEvent(CmdSendEvent event) {
        if (event == null) {
            return;
        }
        String str, channel;
        int cmd = event.cmd;
        CmdPackage cmdPackage = new CmdPackage(event.cmd,event.object);
        str = cmdPackage.packCmdStr();
        if(CmdFlag.isPartHead(cmd)){
            channel = BLEUUIDs.PART_COMMON_CHANNEL;
        } else if(CmdFlag.isPartKnap(cmd)){
            channel = BLEUUIDs.KNAP_COMMON_CHANNEL;
        } else {
            channel = BLEUUIDs.COMMON_CHANNEL;
        }
        if (str != null && sendCmdStr(channel, str)) {
            EventBus.getDefault().post(new CmdResultEvent(event.cmd, true,str));
        } else {
            EventBus.getDefault().post(new CmdResultEvent(event.cmd, false));
        }
    }


    /**
     * 根据通道发送指令
     *
     * @param channel 通道号
     * @param cmd     具体的指令内容
     * @return 是否发送成功
     */
    private boolean sendCmdStr(String channel, String cmd) {
        return sendSCmd(channel, cmd.getBytes());
    }

    /**
     * 具体的处理数据
     *
     * @param channel 具体的通道号
     * @param data    具体的数据
     */
    @Override
    protected void dealRevData(String channel, String data) {
        data = data.replace("\r\n", "");
        CmdDeviceAnalysis deviceAnalysis = new CmdDeviceAnalysis(okaiBleDevice.type,channel,data);
        RevResult revResult = deviceAnalysis.analysis();
        if (revResult == null) {
            return;
        }
        if (revResult.result == RevResult.INIT) {
            return;
        }
        if (revResult.type == RevResult.TYPE_ACK) {
            // 当前ACK的数据应答处理
            /*if (dataChangeListener != null) {
                dataChangeListener.revACKData(revResult);
            }*/
            if (revResult.cmd == CmdFlag.CMD_READ_CONFIG) {
                ReportInfo reportInfo = (ReportInfo) revResult.object;
                if(reportInfo == null){
                    return;
                }
                DeviceBase.setSN(reportInfo.bluetoothConfig.sn);
                DeviceBase.setCarSn(reportInfo.designConfig.SN);
            } else if (revResult.cmd == CmdFlag.CMD_UPGRADE_LENGTH_READY) {
                dealLengthUpdate(revResult);
            } else if (revResult.cmd == CmdFlag.CMD_UPGRADE_CONTROL) {
                dealUpgrade(revResult);
            }
            EventBus.getDefault().post(new CmdRevEvent(revResult.srcData, revResult));
        } else if (revResult.type == RevResult.TYPE_REPORT) {
            // 当前上报指令的更新处理
            /*if (dataChangeListener != null) {
                dataChangeListener.revReportData(revResult);
            }*/
            EventBus.getDefault().post(new CmdReportEvent(revResult.srcData, revResult));
        } else if (revResult.type == RevResult.TYPE_UPGRADE) {
            LogUtils.i(TAG,"升级指令应答 ");
            /*if (dataChangeListener != null) {
                dataChangeListener.revUpgradeData(revResult);
            }*/
            if (revResult.cmd == CmdFlag.CMD_UPGRADE_LENGTH_READY) {
                dealLengthUpdate(revResult);
            } else if (revResult.cmd == CmdFlag.CMD_UPGRADE_CONTROL) {
                dealUpgrade(revResult);
            }
        }


    }



    /**
     * 具体的固件升级类型
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upgradeActionEvent(UpgradeActionEvent event) {
        if (event == null) {
            return;
        }
        if (event.action == UpgradeActionEvent.ACTION_START) {
            if (event.type == UpgradeActionEvent.TYPE_CONTROL) {
                startUpgrade(event.type, event.path);
            } else if (event.type == UpgradeActionEvent.TYPE_BMS) {
                startUpgrade(event.type, event.path);
            } else if (event.type == UpgradeActionEvent.TYPE_MCU) {
                startUpgrade(event.type, event.path);
            }else if (event.type == UpgradeActionEvent.TYPE_CONTROL_LENGTH) {
                startUpdateLengthPackage(event.type, event.path);
            } else {
                LogUtils.i(TAG,"非法指令");
            }
        } else {
            if (event.type == UpgradeActionEvent.TYPE_CONTROL) {
                if (upgradeControl != null) {
                    upgradeControl.stopUpdate();
                    upgradeControl = null;
                }
            } else if (event.type == UpgradeActionEvent.TYPE_BMS) {
                if (upgradeControl != null) {
                    upgradeControl.stopUpdate();
                    upgradeControl = null;
                }
            } else if (event.type == UpgradeActionEvent.TYPE_MCU) {
                if (upgradeControl != null) {
                    upgradeControl.stopUpdate();
                    upgradeControl = null;
                }
            } else if (event.type == UpgradeActionEvent.TYPE_CONTROL_LENGTH) {
                if (upgradeLengthPackage != null) {
                    upgradeLengthPackage.stopUpdate();
                    upgradeLengthPackage = null;
                }
            } else {
                LogUtils.i(TAG,"非法指令");
            }
        }
    }



    private UpgradeLengthPackage upgradeLengthPackage;

    /**
     * 开始发送长包数据
     *
     * @param path
     */
    private void startUpdateLengthPackage(int type, String path) {
        if (upgradeLengthPackage == null) {
            upgradeLengthPackage = new UpgradeLengthPackage(path, new UpgradeLengthPackage.OnControlUpgradeListener() {
                @Override
                public void upgradeSuccess() {
                    EventBus.getDefault().post(new UpgradeResultEvent(UpgradeResultEvent.SUCCESS, "数据更新成功"));
                }

                @Override
                public void updateProgress(int percent) {
                    Log.i("percent", percent + "");
                    UpgradeResultEvent upgradeResultEvent = new UpgradeResultEvent(UpgradeResultEvent.UPDATE, "更新数据");
                    upgradeResultEvent.percent = percent;
                    EventBus.getDefault().post(upgradeResultEvent);
                }

                @Override
                public void upgradeFailed(int err, String msg) {
                    if (upgradeLengthPackage != null) {
                        upgradeLengthPackage.stopUpdate();
                        upgradeLengthPackage = null;
                    }
                    EventBus.getDefault().post(new UpgradeResultEvent(UpgradeResultEvent.FAILED, "数据发送失败"));
                }

                @Override
                public boolean sendData(byte[] data) {
                    return sendSCmd(BLEUUIDs.UPGRADE_CHANNEL, data);
                }
            });
        }
        upgradeLengthPackage.startUpdate(type);
    }

    /**
     * 处理长包升级的程序
     *
     * @param revResult
     */
    private void dealLengthUpdate(RevResult revResult) {
        if (upgradeLengthPackage != null) {
            upgradeLengthPackage.dealRevResult(revResult);
        }
    }



    // 普通蓝牙的升级办法
    private UpgradeControl upgradeControl;

    private void startUpgrade(int type, String path) {
        if (upgradeControl == null) {
            upgradeControl = new UpgradeControl(this,path, new UpgradeControl.OnControlUpgradeListener() {
                @Override
                public void upgradeSuccess() {
                    EventBus.getDefault().post(new UpgradeResultEvent(UpgradeResultEvent.SUCCESS, "数据更新成功"));
                }

                @Override
                public void updateProgress(int percent) {
                    UpgradeResultEvent upgradeResultEvent = new UpgradeResultEvent(UpgradeResultEvent.UPDATE, "更新数据");
                    upgradeResultEvent.percent = percent;
                    EventBus.getDefault().post(upgradeResultEvent);
                }

                @Override
                public void upgradeFailed(int err, String msg) {
                    if (upgradeControl != null) {
                        upgradeControl.stopUpdate();
                        upgradeControl = null;
                    }

                    UpgradeResultEvent resultEvent = new UpgradeResultEvent(UpgradeResultEvent.FAILED, msg);
                    EventBus.getDefault().post(resultEvent);
//                    EventBus.getDefault().post(new UpgradeResultEvent(UpgradeResultEvent.FAILED, "数据发送失败"));
                }

                @Override
                public boolean sendData(byte[] data) {
                    return sendSCmd(BLEUUIDs.UPGRADE_CHANNEL, data);
                }
            });
        }
        upgradeControl.startUpdate(type);
    }


    /**
     * 处理控制器固件升级指令
     * @param revResult
     */
    private void dealUpgrade(RevResult revResult) {
        if (upgradeControl != null) {
            upgradeControl.dealRevResult(revResult);
        }
    }


}
