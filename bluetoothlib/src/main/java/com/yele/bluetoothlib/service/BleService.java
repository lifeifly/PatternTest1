package com.yele.bluetoothlib.service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yele.baseapp.utils.DateUtils;
import com.yele.baseapp.utils.LogUtils;
import com.yele.baseapp.utils.StringUtils;
import com.yele.bluetoothlib.bean.BLEUUIDs;
import com.yele.bluetoothlib.bean.LogDebug;
import com.yele.bluetoothlib.bean.UpgradeFlag;
import com.yele.bluetoothlib.bean.cmd.CmdFlag;
import com.yele.bluetoothlib.bean.cmd.RevResult;
import com.yele.bluetoothlib.bean.config.ble.DeviceBase;
import com.yele.bluetoothlib.bean.device.OkDevice;
import com.yele.bluetoothlib.bean.device.ReportInfo;
import com.yele.bluetoothlib.policy.event.cmd.CmdReportEvent;
import com.yele.bluetoothlib.policy.event.cmd.CmdResultEvent;
import com.yele.bluetoothlib.policy.event.cmd.CmdRevEvent;
import com.yele.bluetoothlib.policy.event.cmd.CmdSendEvent;
import com.yele.bluetoothlib.policy.event.cmd.update.UpgradeActionEvent;
import com.yele.bluetoothlib.policy.event.cmd.update.UpgradeResultEvent;
import com.yele.bluetoothlib.policy.event.device.BleDevConActionEvent;
import com.yele.bluetoothlib.policy.event.device.BleDevConResultEvent;
import com.yele.bluetoothlib.policy.event.device.BleDevScanEvent;
import com.yele.bluetoothlib.policy.protrol.CmdCarPackage;
import com.yele.bluetoothlib.policy.protrol.UpgradeControl;
import com.yele.bluetoothlib.policy.protrol.UpgradeLengthPackage;
import com.yele.bluetoothlib.policy.protrol.control.CarControlPresenter;
import com.yele.bluetoothlib.policy.protrol.control.CarPartControlPresenter;
import com.yele.bluetoothlib.policy.protrol.control.INormalSet;
import com.yele.bluetoothlib.policy.protrol.control.KnapControlPresenter;
import com.yele.bluetoothlib.policy.protrol.control.OnBleConnectListener;
import com.yele.bluetoothlib.policy.protrol.control.OnBleDataChangeListener;
import com.yele.bluetoothlib.policy.protrol.control.PartControlPresenter;
import com.yele.bluetoothlib.utils.LogFileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 蓝牙服务
 */
public abstract class BleService extends BaseBleService {

    private static final String TAG = "BleService";

    private boolean DEBUG_LOG = true;

    private void logi(String msg) {
        if (!DEBUG_LOG || !LogDebug.IS_LOG) {
            return;
        }
        LogUtils.i(TAG, msg);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logi("onCreate");
        EventBus.getDefault().register(this);
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
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        logi("onDestroy");
        if (logWriteThread != null) {
            if (handler != null) {
                handler.sendEmptyMessage(0x02);
            }
        }
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    private INormalSet iNormalSet;

    /**
     * 蓝牙设备的请求动作，主要是连接、断开功能
     *
     * @param event 具体的请求动作
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleDevRequestConEvent(BleDevConActionEvent event) {
        if (event == null) {
            return;
        }
        switch (event.code) {
            case BleDevConActionEvent.CODE_CONNECT:
                OkDevice device = (OkDevice) event.obj;
                if (device.model == 1) {
                    iNormalSet = new PartControlPresenter(BleService.this, device);
                } else if(device.model == 2){
                    iNormalSet = new KnapControlPresenter(BleService.this, device);
                }else{
//                    iNormalSet = new CarControlPresenter(BleService.this, device);
                    iNormalSet = new CarPartControlPresenter(BleService.this, device);
                }
                iNormalSet.setOnConnectListener(new OnBleConnectListener() {
                    @Override
                    public void connected(int code) {

                    }

                    @Override
                    public void disConnected(int code) {

                    }
                });
                iNormalSet.setOnBleDataChangeListener(new OnBleDataChangeListener() {
                    @Override
                    public void revACKData(RevResult result) {
                        logi("解析数据成功了");
                        dealACKData(result);
                    }

                    @Override
                    public void revReportData(RevResult result) {
                        dealReportData(result);
                    }

                    @Override
                    public void revUpgradeData(RevResult result) {
                        dealUpdateData(result);
                    }
                });
                iNormalSet.connectDevice();
                break;
            case BleDevConActionEvent.CODE_DISCONNECT:
                if (iNormalSet != null) {
                    iNormalSet.disconnect();
                }
                break;

        }
    }

    @Override
    protected void deviceDisConnectFailed() {
        EventBus.getDefault().post(
                new BleDevConResultEvent(BleDevConResultEvent.CODE_RESULT_DISCONNECT_FAIL, null));
    }

    @Override
    protected void deviceConnectFailed() {
        EventBus.getDefault().post(
                new BleDevConResultEvent(BleDevConResultEvent.CODE_RESULT_CONNECT_FAIL, null));
    }

    @Override
    protected void deviceDisconnect() {
        EventBus.getDefault().post(
                new BleDevConResultEvent(BleDevConResultEvent.CODE_RESULT_DISCONNECT, null));
    }

    @Override
    protected void deviceConnected() {
        EventBus.getDefault().post(
                new BleDevConResultEvent(BleDevConResultEvent.CODE_RESULT_CONNECT, null));
    }

    @Override
    protected void discoverNewDevice(OkDevice device) {
        EventBus.getDefault().post(new BleDevScanEvent(BleDevScanEvent.RESULT_DISCOVER_LIST_DEV, device));
    }

    @Override
    protected void discoverAimDevice(BluetoothDevice device) {
        EventBus.getDefault().post(new BleDevScanEvent(BleDevScanEvent.RESULT_DISCOVER_AIM_DEV, device));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCmdSendEvent(CmdSendEvent event) {
        if (event == null) {
            return;
        }
        String channel;
        channel = BLEUUIDs.CAR_COMMON_CHANNEL;
        CmdCarPackage cmdPackage = new CmdCarPackage(event.cmd, event.object);
        byte[] data = cmdPackage.packageData();
        if (data != null && iNormalSet != null) {
            if(StringUtils.isEmpty(event.channel)){
                if(iNormalSet.sendCmd(data)){
                    EventBus.getDefault().post(new CmdResultEvent(event.cmd, true));
                }else {
                    EventBus.getDefault().post(new CmdResultEvent(event.cmd, false));
                }
            }else {
                LogUtils.i(TAG,"当前发送的通道数据：" + event.channel);
                if(iNormalSet.sendCmd(event.channel,data)){
                    EventBus.getDefault().post(new CmdResultEvent(event.cmd, true));
                }else {
                    EventBus.getDefault().post(new CmdResultEvent(event.cmd, false));
                }
            }
        } else {
            EventBus.getDefault().post(new CmdResultEvent(event.cmd, false));
        }
    }

    /**
     * 处理更新数据的数据结构包
     *
     * @param revResult 当前的处理结果
     */
    protected void dealUpdateData(RevResult revResult) {
        if (type == 13) {
            dealNormalUpdate(revResult);
//            dealLengthUpdate(revResult);
        } else if (type == 15) {
            dealUpdateMCU(revResult);
        }
    }

    /**
     * 更新上报指令数据
     *
     * @param revResult 数据处理的结果
     */
    protected void dealReportData( RevResult revResult) {
        if (revResult.cmd == CmdFlag.CMD_REPORT_TEST) {
            //
            if (logWriteThread == null) {
                logWriteThread = new LogWriteThread();
                logWriteThread.start();
            }
            Message msg = new Message();
            msg.what = 0x01;
            msg.obj = revResult.srcData;
            if (handler != null) {
                handler.sendMessage(msg);
            }
        }
        EventBus.getDefault().post(new CmdReportEvent(revResult.srcData, revResult));
    }

    /**
     * 处理应答指令数据
     * @param revResult 数据的处理结果
     */
    protected void dealACKData(RevResult revResult) {
        if (revResult.cmd == CmdFlag.CMD_READ_CONFIG) {
            ReportInfo reportInfo = (ReportInfo) revResult.object;
            DeviceBase.setSN(reportInfo.bluetoothConfig.sn);
        } else if (revResult.cmd == CmdFlag.CMD_UPGRADE_LENGTH_READY) {
            dealLengthUpdate(revResult);
        } else if (revResult.cmd == CmdFlag.CMD_UPGRADE_READY) {
            dealNormalUpdate(revResult);
        }
        EventBus.getDefault().post(new CmdRevEvent(revResult.srcData, revResult));
    }


    private Handler handler;
    private LogWriteThread logWriteThread;

    /**
     * 测试数据记录线程
     */
    class LogWriteThread extends Thread {

        public LogWriteThread() {
        }

        @Override
        public void run() {
            super.run();
            Looper.prepare();
            LogUtils.i(TAG, "开始记录log");
            handler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 0x01:
                            saveLog((String) msg.obj);
                            break;
                        case 0x02:
                            Looper.myLooper().quit();
                            break;
                    }
                }
            };
            Looper.loop();
        }

        private void saveLog(String data) {
            String dateStr = LogFileUtils.getLogFiles() + DateUtils.getTimeStrByStyle("yyyy-MM-dd");
            dateStr += ".txt";
            LogFileUtils.saveData(dateStr, data);
        }
    }

    private int type = 0;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upgradeActionEvent(UpgradeActionEvent event) {
        if (event == null) {
            return;
        }
//        startUpdateLengthPackage(event.type,event.path);
        if (event.action == UpgradeActionEvent.ACTION_START) {
            if (event.type == UpgradeActionEvent.TYPE_CONTROL) {
//                startUpdateLengthPackage(event.type,event.path);
                startUpdateControl(event.path);
            } else if (event.type == UpgradeActionEvent.TYPE_BMS) {
                startUpdateLengthPackage(event.type, event.path);
            } else if (event.type == UpgradeActionEvent.TYPE_BLE) {
                startUpdateLengthPackage(event.type, event.path);
            } else if (event.type == UpgradeActionEvent.TYPE_MCU) {
                startUpdateLengthPackage(event.type, event.path);
            } else {
                logi("非法指令");
            }
        } else {
            if (event.type == UpgradeActionEvent.TYPE_CONTROL) {
                type = 13;
                stopUpdateControl();
            } else if (event.type == UpgradeActionEvent.TYPE_BMS) {
//                stopUpgrade();
            } else if (event.type == UpgradeActionEvent.TYPE_BLE) {
//                stopUpgrade();
            } else if (event.type == UpgradeActionEvent.TYPE_MCU) {
                type = 15;
                stopUpdateMCU();
            } else {
                logi("非法指令");
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
                    if (upgradeControl != null) {
                        upgradeControl.stopUpdate();
                        upgradeControl = null;
                    }
                    EventBus.getDefault().post(new UpgradeResultEvent(UpgradeResultEvent.FAILED, "数据发送失败"));
                }

                @Override
                public boolean sendData(byte[] data) {
                    if (iNormalSet != null) {
                        return iNormalSet.sendCmd(data);
                    }
                    return false;
                }
            });
        }
        upgradeLengthPackage.startUpdate(UpgradeFlag.isPlanA);
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

    /**
     * 开始进行蓝牙的升级功能
     *
     * @param path 当前的路径
     */
    private void startUpdateControl(String path) {
        type = 13;
        if (upgradeControl == null) {
            upgradeControl = new UpgradeControl(path, new UpgradeControl.OnControlUpgradeListener() {
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
                    EventBus.getDefault().post(new UpgradeResultEvent(UpgradeResultEvent.FAILED, "数据发送失败"));
                }

                @Override
                public boolean sendData(byte[] data) {
                    if (iNormalSet != null) {
                        return iNormalSet.updateData(data);
                    }
                    return false;
                }
            });
        }
        upgradeControl.startUpdate(UpgradeFlag.isPlanA);
    }

    /**
     * 处理普通仪表的升级功能
     *
     * @param revResult 当前收到的关于升级的数据
     */
    private void dealNormalUpdate(RevResult revResult) {
        if (upgradeControl != null) {
            upgradeControl.dealRevResult(revResult);
        }
    }


    /**
     * 暂停蓝牙的升级功能
     */
    private void stopUpdateControl() {
        if (upgradeControl != null) {
            upgradeControl.stopUpdate();
        }
    }

    private UpgradeControl upgradeMCU;

    /**
     * 开始MCU的更新
     *
     * @param path 当前的更新文件的路径
     */
    private void startUpdateMCU(String path) {
        if (upgradeMCU == null) {
            upgradeMCU = new UpgradeControl(path, new UpgradeControl.OnControlUpgradeListener() {
                @Override
                public void upgradeSuccess() {
                    EventBus.getDefault().post(new UpgradeResultEvent(UpgradeResultEvent.SUCCESS, "数据更新成功"));
                    upgradeMCU = null;
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
                    EventBus.getDefault().post(new UpgradeResultEvent(UpgradeResultEvent.FAILED, "数据发送失败"));
                    upgradeMCU = null;
                }

                @Override
                public boolean sendData(byte[] data) {
                    if (iNormalSet != null) {
                        return iNormalSet.updateData(data);
                    }
                    return false;
                }
            });
        }
        upgradeMCU.startUpdate(false);
    }

    /**
     * 处理MCU的更新应答
     *
     * @param revResult 应答数据
     */
    private void dealUpdateMCU(RevResult revResult) {
        if (upgradeMCU != null) {
            upgradeMCU.dealRevResult(revResult);
        }
    }

    /**
     * 停止更新MCU
     */
    private void stopUpdateMCU() {
        if (upgradeMCU != null) {
            upgradeMCU.stopUpdate();
        }
    }
}
