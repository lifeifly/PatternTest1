package com.yele.blesdklibrary.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.yele.blesdklibrary.bean.CarConfig;
import com.yele.blesdklibrary.bean.CarRunState;
import com.yele.blesdklibrary.bean.ErrorInfo;
import com.yele.blesdklibrary.bean.InitInfo;
import com.yele.blesdklibrary.bean.LockStateInfo;
import com.yele.blesdklibrary.bean.OkaiBleDevice;
import com.yele.blesdklibrary.bean.UpgradeAction;
import com.yele.blesdklibrary.bean.UpgradeResult;
import com.yele.blesdklibrary.data.BLEUUIDs;
import com.yele.blesdklibrary.data.BindData;
import com.yele.blesdklibrary.data.LockStateEnum;
import com.yele.blesdklibrary.data.StateEnum;
import com.yele.blesdklibrary.data.SwitchStatusEnum;
import com.yele.blesdklibrary.policy.frame.UpgradeFrame;
import com.yele.blesdklibrary.policy.receiver.BleConnectBack;
import com.yele.blesdklibrary.policy.receiver.BluetoothMonitorReceiver;
import com.yele.blesdklibrary.port.OnBleConnectBack;
import com.yele.blesdklibrary.port.OnCmdDataBack;
import com.yele.blesdklibrary.port.OnCmdErrorCodeBack;
import com.yele.blesdklibrary.port.OnCmdInitInfoResultBack;
import com.yele.blesdklibrary.port.OnCmdReportBack;
import com.yele.blesdklibrary.port.OnCmdResultBack;
import com.yele.blesdklibrary.port.OnConnectDevStateBack;
import com.yele.blesdklibrary.port.OnDevicePermissionBack;
import com.yele.blesdklibrary.port.OnScanDevStateBack;
import com.yele.blesdklibrary.port.OnUpdateResultBack;
import com.yele.blesdklibrary.service.DfuService;
import com.yele.blesdklibrary.util.ByteUtils;
import com.yele.blesdklibrary.util.CarCodeUtils;
import com.yele.blesdklibrary.util.JavaAES128Encryption;
import com.yele.blesdklibrary.util.StringUtils;

import java.io.File;
import java.io.IOException;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class BleControl extends BaseBleService {

    private static final String TAG = "AndroidBle";

    private boolean isHead = true;

    /**
     * 写入的头信息
     * @return
     */
    private String getHeadInput(){
        String headInfo;
        if(!isHead){
            headInfo = "AT+BK";
        }else {
            headInfo = "AT+OK";
        }
        return headInfo;
    }

    /**
     * 读取的头信息
     * @return
     */
    private String getHeadConfig(){
        String headInfo;
        if(!isHead){
            headInfo = "BK";
        }else {
            headInfo = "OK";
        }
        return headInfo;
    }



    private static BleControl bleService;

    private BleControl(Context context) {
        this.context = context;
        initBleReceiver(context);
    }

    public static BleControl getBleControl(Context context){
        if(bleService == null){
            synchronized (BleControl.class){
                if (bleService == null) {
                    bleService = new BleControl(context);
                }
            }
        }

        return bleService;
    }

    private BluetoothMonitorReceiver receiver;

    private void initBleReceiver(Context context) {
        receiver = new BluetoothMonitorReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        context.registerReceiver(receiver, filter);
    }


    protected OnDevicePermissionBack devicePermissionBack;

    public void queryDevicePermission(OkaiBleDevice device, OnDevicePermissionBack back){
        this.devicePermissionBack = back;
    }



    private OnCmdDataBack cmdData;

    public void getCmdConfig(OnCmdDataBack data){
        this.cmdData = data;
    }

    /**
     * 扫描设备
     * 根据蓝牙名称搜索设备
     */
    public void deviceStartScan(String devName, OnScanDevStateBack state){
        this.scanState = state;
        setAimScanDevName(devName);
        startScanDev();
    }

    /**
     * 扫描设备列表
     */
    public void deviceStartScan(OnScanDevStateBack state){
        this.scanState = state;
        setAimScanDevName(null);
        startScanDev();
    }

    /**
     * 停止扫描
     */
    public void deviceStopScan(){
        stopScanDev();
    }

    /**
     * 蓝牙连接
     * @param id mac地址
     */
    public void deviceConnect(int id, OnConnectDevStateBack state){
        this.connState = state;
        OkaiBleDevice device = listScanDevice.get(id);
        if (!CarCodeUtils.switchCarType(device.type)) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(connState != null) {
                        connState.connectState(false, "当前车辆没有权限");
                    }
                }
            });
            return;
        }

        if(!CarCodeUtils.switchCarCode(device.sn)){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(connState != null) {
                        connState.connectState(false, "当前车辆没有权限");
                    }
                }
            });
            return;
        }

        String macAddress = listScanDevice.get(id).device.getAddress();
        if (conGatt != null){
            conGatt.close();
        }
        BluetoothGatt gatt = connectDevice(macAddress);
        if(gatt == null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(connState != null) {
                        connState.connectState(false, "连接的通讯接口失败");
                    }
                }
            });
            return;
        }
        if (gatt.discoverServices()) {
            logI(TAG,"发现服务了");
            gatt.connect();
        }
    }

    /**
     * 蓝牙断开连接
     *
     */
    public void deviceDisConnect(){
        initInfo = null;
        carRunState = null;
        lockInfo = null;
        if (conDevice == null) {
            logI(TAG,"conDevice is null");
            if (conGatt != null) {
                conGatt.close();
            }
            return;
        }
        if (conGatt == null) {
            logI(TAG,"conGatt is null");
            return;
        }
        conGatt.disconnect();
        conGatt.close();
        logI(TAG,"disconnect");
    }

    // 读取配置接口
    private OnCmdInitInfoResultBack infoResult;
    /*// 车辆锁状态接口
    private OnCmdLockStateBack cmdLockState;*/
    // 发送数据结果接口
    private OnCmdResultBack result;
    // 报告信道接口
    private OnCmdReportBack report;
    // 错误码信息
    private OnCmdErrorCodeBack code;
    // 所有配置信息类
    private InitInfo initInfo = new InitInfo();
    // 错误返回信息类
    private ErrorInfo errorInfo = new ErrorInfo();
    // 蓝牙配置类
    private CarRunState carRunState = new CarRunState();
    // 车辆锁
    private LockStateInfo lockInfo = new LockStateInfo();

    /**
     * 设置车辆的通讯密码
     * @param pwd 当前的密码
     */
    public void setPassword(String pwd) {
        if(StringUtils.isEmpty(pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            return;
        }
        if (initInfo == null) {
            initInfo = new InitInfo();
        }
        initInfo.pwd = pwd;
        initInfo.NameNew = conDevice.getName();
    }


    /**
     * 读取所有配置信息、初始化
     * @param cmdResult
     */
    public void sendReadConfig( OnCmdInitInfoResultBack cmdResult){
        this.infoResult = cmdResult;
        if(cmdResult == null) {
            return;
        }
        if (initInfo == null) {
            return;
        }
        final String channel = BLEUUIDs.COMMON_CHANNEL;
        String str = getHeadInput()+"ALC=" + initInfo.pwd + "," + getCmdNo();
//        String str = readConfig(initInfo.pwd,getCmdNo());

        if (str != null) {
            str += "$\r\n";
        }
        if (sendCmdStr(channel, str)) {
            logI(TAG,"发送成功");
        } else {
            errorInfo.errorCode = 117;
            errorInfo.errorDescription = "发送数据失败";
            if(cmdResult != null) {
                cmdResult.CmdInitInfo(errorInfo,null);
            }
        }
    }


    /**
     * 车辆参数配置--测试模式
     * @param name 车型名称
     * @param sn 车辆Sn
     */
    public void sendConfigCar(String sn, String name, OnCmdResultBack cmdResult){
        this.result = cmdResult;
        // 当前车辆的SN
        initInfo.carSn = sn;
        // 车型名称
//        initInfo.typeName = name;

        String str = getHeadInput()+"CAP=" + sn + "," + name + "," + getCmdNo();
        // 发送指令
        sendConfig(str);
    }


    /**
     * 蓝牙参数配置--测试模式
     * @param pwd 蓝牙密码
     * @param sn 仪表SN
     * @param broadcastSpace  广播间隔
     * @param broadcastTime 广播持续时间
     * @param minSpace 最小连接间隔
     * @param maxSpace 最大时间间隔范围
     */
    public void sendConfigBle(String pwd, String sn, int broadcastSpace, int broadcastTime, int minSpace, int maxSpace, OnCmdResultBack cmdResult){
        this.result = cmdResult;
        // 仪表SN
        initInfo.SN = sn;
        // 广播间隔
        initInfo.broadcastSpace = broadcastSpace;
        // 广播持续时间
        initInfo.broadcastTime = broadcastTime;
        // 最小连接间隔
        initInfo.minConnectSpace = minSpace;
        // 最大时间间隔范围
        initInfo.maxConnectSpace = maxSpace;
        // 蓝牙密码
        initInfo.pwd = pwd;

        String str = getHeadInput()+"CON=" + sn + "," + broadcastSpace + "," + broadcastTime + "," +
                minSpace + "," + maxSpace + "," + pwd + ",," + getCmdNo();
        // 发送指令
        sendConfig(str);
    }


    /**
     * 成品下发--测试模式
     * 测试标志 0：取消/停止成品测试，1：开始成品测试
     */
    public void sendTestProduct(StateEnum stateEnum, OnCmdResultBack cmdResult){
        this.result = cmdResult;
        int testSign;
        if(stateEnum == StateEnum.ON){
            testSign = 1;
        }else {
            testSign = 0;
        }
        String str = getHeadInput()+"TET=" + testSign + "," + getCmdNo();
        // 发送指令
        sendConfig(str);
    }


    /**
     * 骑行参数
     * @param maxSpeed 当前车辆允许的最大速度 0~63
     * @param speedMode 车辆的加速模式 0：柔和模式，1：运动模式
     * @param showModel 仪表的显示模式 0：YD,1：KM
     * @param reportSpace 上报时间间隔，默认 100ms,最大9999ms,0不上报
     * @param time 待机关机时间，默认 30s,0不自动关机
     */
    public void sendNormalBikeConfig(int maxSpeed, int speedMode, int showModel, int reportSpace, int time, OnCmdResultBack cmdResult){
        this.result = cmdResult;
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            if(cmdResult != null) {
                cmdResult.CmdResultEvent(false, errorInfo);
            }
            return;
        }

        if(maxSpeed < 0){
            errorInfo.errorCode = 124;
            errorInfo.errorDescription = "当前允许最大速度不小于0";
            if(cmdResult != null) {
                cmdResult.CmdResultEvent(false, errorInfo);
            }
            return;
        }else if(maxSpeed > 63){
            errorInfo.errorCode = 125;
            errorInfo.errorDescription = "当前允许最大速度不大于63km/h";
            if(cmdResult != null) {
                cmdResult.CmdResultEvent(false, errorInfo);
            }
            return;
        }

        if(reportSpace < 0){
            errorInfo.errorCode = 126;
            errorInfo.errorDescription = "上报时间间隔不小于0";
            if(cmdResult != null) {
                cmdResult.CmdResultEvent(false, errorInfo);
            }
        }else if(reportSpace > 9999){
            errorInfo.errorCode = 127;
            errorInfo.errorDescription = "上报时间间隔不大于9999ms";
            if(cmdResult != null) {
                cmdResult.CmdResultEvent(false, errorInfo);
            }
        }

        // 当前车辆允许的最大速度
        initInfo.MaxSpeed = maxSpeed;
        // 车辆的加速模式  0：柔和模式,1：运动模式
        initInfo.addMode = speedMode;
        // 仪表的显示模式  0：为YD,1：为KM
        initInfo.showMode = showModel;
        // 当前车辆上报信息的时间间隔
        initInfo.reportSpace = reportSpace;
        // 待机-关机时间
        initInfo.standbyTime = time;

        String str = getHeadInput()+"ECP=" + initInfo.pwd + "," + maxSpeed + "," + speedMode + ","
                + showModel + "," + reportSpace + "," + time + "," + getCmdNo();
//        String str = rideConfig(initInfo.pwd,maxSpeed,speedMode,showModel,reportSpace,time,getCmdNo());
        // 发送指令
        sendConfig(str);
    }

    /**
     * 骑行参数
     * @param gears  S档开关  1：开启S档 0：关闭S档。
     * @param speedMode 车辆的加速模式 0：柔和模式，1：运动模式
     * @param showModel 仪表的显示模式 0：YD,1：KM
     * @param reportSpace 上报时间间隔，默认 100ms,最大9999ms,0不上报
     * @param time 待机关机时间，默认 30s,0不自动关机
     */
    public void sendGearSBikeConfig(SwitchStatusEnum gears, int speedMode, int showModel, int reportSpace, int time, OnCmdResultBack cmdResult){
        this.result = cmdResult;
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            if(cmdResult != null) {
                cmdResult.CmdResultEvent(false, errorInfo);
            }
            return;
        }

        if(reportSpace < 0){
            errorInfo.errorCode = 126;
            errorInfo.errorDescription = "上报时间间隔不小于0";
            if(cmdResult != null) {
                cmdResult.CmdResultEvent(false, errorInfo);
            }
        }else if(reportSpace > 9999){
            errorInfo.errorCode = 127;
            errorInfo.errorDescription = "上报时间间隔不大于9999ms";
            if(cmdResult != null) {
                cmdResult.CmdResultEvent(false, errorInfo);
            }
        }

        int state = 0;
        if(gears == SwitchStatusEnum.END){
            state = 0;
        }else if(gears == SwitchStatusEnum.START) {
            state = 1;
        }

        // 当前车辆允许的最大速度
//        initInfo.MaxSpeed = gears;
        // 车辆的加速模式  0：柔和模式,1：运动模式
        initInfo.addMode = speedMode;
        // 仪表的显示模式  0：为YD,1：为KM
        initInfo.showMode = showModel;
        // 当前车辆上报信息的时间间隔
        initInfo.reportSpace = reportSpace;
        // 待机-关机时间
        initInfo.standbyTime = time;

        String str = getHeadInput()+"ECP=" + initInfo.pwd + "," + state + "," + speedMode + ","
                + showModel + "," + reportSpace + "," + time + "," + getCmdNo();
        sendConfig(str);
    }


    /**
     * 切换模式
     * @param cmd
     * @param cmdResult
     */
    public void sendNormalChangeMode(int cmd, final OnCmdResultBack cmdResult){
        this.result = cmdResult;


        byte[] bytes = JavaAES128Encryption.encryptAES128(initInfo.carSn);
        String carPwd;
        if(cmd == 11){
            carPwd = "OKAI_CAR";
            cmd = 0;
        }else  if(cmd == 12){
            carPwd = "OKAI_CAR";
            cmd = 2;
        }else  if(cmd == 13){
            carPwd = "OKAI_CAR";
            cmd = 1;
        } else {
            carPwd = ByteUtils.bytesToStringByBig(bytes);
        }
        initInfo.mode = cmd;

        String str = getHeadInput()+"XWM=" + carPwd + "," + cmd + "," + getCmdNo();
        // 发送指令
        sendConfig(str);

    }

    /**
     * 寻车
     * @param cmdResult
     */
    public void sendSearchCar(OnCmdResultBack cmdResult){
        this.result = cmdResult;
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            if(cmdResult != null) {
                cmdResult.CmdResultEvent(false, errorInfo);
            }
            return;
        }
        String str = getHeadInput()+"LOC=" + initInfo.pwd + "," + getCmdNo();
//        String str = findDevice(initInfo.pwd,getCmdNo());
        // 发送指令
        sendConfig(str);
    }

    /**
     * 车辆锁控制
     * @param lockState
     */
    public void sendAllLock(final LockStateEnum state, final OnCmdResultBack lockState){
        this.result = lockState;
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            if(lockState != null) {
                lockState.CmdResultEvent(false, errorInfo);
            }
            return;
        }
        lockInfo = new LockStateInfo();
        String cmd = null;
        if(state == LockStateEnum.UN_LOCK){
            cmd = "0,0,0,0,0";
            lockInfo.vehicleLock = 0;
            lockInfo.basketLock = 0;
            lockInfo.batteryLock = 0;
            lockInfo.spareLock = 0;
            lockInfo.straightLock = 0;
        }else if(state == LockStateEnum.LOCK){
            cmd = "1,1,1,1,1";
            lockInfo.vehicleLock = 1;
            lockInfo.basketLock = 1;
            lockInfo.batteryLock = 1;
            lockInfo.spareLock = 1;
            lockInfo.straightLock = 1;
        }/*else if(state == LockStateEnum.UN_SUPPORT){
            cmd = ",2,2,2,2";
        }*/
        final String channel = BLEUUIDs.COMMON_CHANNEL;
        String str = getHeadInput()+"SCT=" + initInfo.pwd + "," + cmd + "," + getCmdNo();
//        String str = lockAllDevice(initInfo.pwd,cmd,getCmdNo());
        if (str != null) {
            str += "$\r\n";
        }

        if (sendCmdStr(channel, str)) {
            logI(TAG,"发送成功");
            if(state == LockStateEnum.LOCK){
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            result.CmdResultEvent(true,null);
                        }
                    }
                });
            }
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorCode = 117;
                    errorInfo.errorDescription = "数据发送失败";
                    if(lockState != null) {
                        lockState.CmdResultEvent(false, errorInfo);
                    }
                }
            });
        }
    }

    /**
     * led控制
     * @param cmd 0：关闭，1：打开
     * @param cmdResult
     */
    public void sendLed(int cmd, OnCmdResultBack cmdResult){
        this.result = cmdResult;

        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            if(cmdResult != null) {
                cmdResult.CmdResultEvent(false, errorInfo);
            }
            return;
        }

        String str = getHeadInput()+"LED=" + initInfo.pwd + ",0," + cmd + "," + getCmdNo();
//        String str = ledControl(initInfo.pwd,cmd,getCmdNo());
        sendConfig(str);
    }

    /**
     * 修改密码
     * @param newPwd 新密码
     * @param cmdResult
     */
    public void sendChangePwd(String oldPwd, final String newPwd, String vehicleSN, final OnCmdResultBack cmdResult){
        this.result = cmdResult;

        if(!oldPwd.equals(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            if(cmdResult != null) {
                cmdResult.CmdResultEvent(false, errorInfo);
            }
            return;
        }
        String str = getHeadInput()+"PWD=" + oldPwd + "," + newPwd + "," + vehicleSN + "," + getCmdNo();
        final String channel = BLEUUIDs.COMMON_CHANNEL;
        if (str != null) {
            str += "$\r\n";
        }
        if (sendCmdStr(channel, str)) {
            logI(TAG,"发送成功");
            initInfo.pwd = newPwd;
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorCode = 117;
                    errorInfo.errorDescription = "数据发送失败";
                    if(cmdResult != null) {
                        cmdResult.CmdResultEvent(false, errorInfo);
                    }
                }
            });
        }
    }


    /**
     * 修改蓝牙名称
     * @param bleName 新的蓝牙名称
     * @param cmdResult
     */
    public void sendChangeBleName(String bleName, OnCmdResultBack cmdResult){
        this.result = cmdResult;

        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            if(cmdResult != null) {
                cmdResult.CmdResultEvent(false, errorInfo);
            }
            return;
        }
        initInfo.NameNew = bleName;
        String str = getHeadInput()+"NAM=" + initInfo.pwd + "," + bleName + "," + getCmdNo();
//        String str = modifyBleName(initInfo.pwd,bleName,getCmdNo());
        // 发送指令
        sendConfig(str);
    }


    /**
     * 各个锁的开关
     * @param vehicleLock 车辆锁
     * @param batteryLock 电池锁
     * @param straightLock 直杆锁
     * @param basketLock 篮筐锁
     * @param spareLock 备用锁
     * @param cmdResult
     */
    public void sendLockState(int vehicleLock, int batteryLock, int straightLock, int basketLock, int spareLock, OnCmdResultBack cmdResult){
        this.result = cmdResult;
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            if(cmdResult != null) {
                cmdResult.CmdResultEvent(false, errorInfo);
            }
            return;
        }
        String str = getHeadInput()+"SCT=" + initInfo.pwd + "," + vehicleLock + ","+  batteryLock + ","+  straightLock + ","+  basketLock + ","+  spareLock + "," + getCmdNo();
        // 发送指令
        sendConfig(str);
    }


    /**
     * 开关定速巡航
     * @param cruise
     * 0：关闭车辆定速巡航模式。
     * 1：开启车辆定速巡航模式。
     * @param cmdResult
     */
    public void sendCarCruise(int cruise, OnCmdResultBack cmdResult){
        this.result = cmdResult;
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            if (cmdResult != null) {
                cmdResult.CmdResultEvent(false, errorInfo);
            }
            return;
        }

        String str = getHeadInput()+"DSX=" + initInfo.pwd + "," + cruise + "," + getCmdNo();
//        String str = deviceCruise(initInfo.pwd,cruise,getCmdNo());
        // 发送指令
        sendConfig(str);
    }


    /**
     * 开关车辆锁车模式
     * @param mode
     * 0：关闭车辆锁车模式。
     * 1：开启车辆锁车模式。
     * @param cmdResult
     */
    public void sendCarLockMode(int mode, OnCmdResultBack cmdResult){
        this.result = cmdResult;
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            if (cmdResult != null) {
                cmdResult.CmdResultEvent(false, errorInfo);
            }
            return;
        }

        String str = getHeadInput()+"SCM=" + initInfo.pwd + "," + mode + "," + getCmdNo();
//        String str = carLockMode(initInfo.pwd,mode,getCmdNo());
        // 发送指令
        sendConfig(str);
    }


    /**
     * 修改开关机模式
     * @param mode
     * 0：开关机模式修改为芯片休眠/唤醒模式，蓝牙广播不停止
     * 1：开关机模式修改为芯片掉电/上电模式，蓝牙广播停止
     * @param cmdResult
     */
    public void sendOpenCar(int mode, OnCmdResultBack cmdResult){
        this.result = cmdResult;
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            if(cmdResult != null) {
                cmdResult.CmdResultEvent(false, errorInfo);
            }
            return;
        }

        String str = getHeadInput()+"ONF=" + initInfo.pwd + "," + mode + "," + getCmdNo();
//        String str = modifyOpenCar(initInfo.pwd,mode,getCmdNo());
        // 发送指令
        sendConfig(str);
    }

    /**
     * 切换车辆启动模式
     * @param mode
     * 0：车辆助力启动模式。
     * 1：车辆无助力启动模式。
     * @param cmdResult
     */
    public void sendCarOpenMode(int mode, OnCmdResultBack cmdResult){
        this.result = cmdResult;
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            if(cmdResult != null) {
                cmdResult.CmdResultEvent(false,errorInfo);
            }
            return;
        }

        String str = getHeadInput()+"SUM=" + initInfo.pwd + "," + mode + "," + getCmdNo();
//        String str = carOpenMode(initInfo.pwd,mode,getCmdNo());
        // 发送指令
        sendConfig(str);
    }

    /**
     * 自检模式
     * @param status 自检状态，是否开启自检模式
     * @param position  自检部位
     * @param cmdResult
     */
    public void sendDeviceCheckMode(int status,int position,OnCmdResultBack cmdResult){
        this.result = cmdResult;
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            if(cmdResult != null) {
                cmdResult.CmdResultEvent(false,errorInfo);
            }
            return;
        }

        String str = getHeadInput()+"SIM=" + initInfo.pwd + "," + status + "," + position + "," + getCmdNo();
        sendConfig(str);
    }

    /**
     * 发送数据
     * @param str
     */
    public void sendConfig(String str){
        final String channel = BLEUUIDs.COMMON_CHANNEL;
        if (str != null) {
            str += "$\r\n";
        }
        if (str != null && sendCmdStr(channel, str)) {
            logI(TAG,"发送成功");
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorCode = 117;
                    errorInfo.errorDescription = "数据发送失败";
                    if(result != null) {
                        result.CmdResultEvent(false, errorInfo);
                    }
                }
            });
        }
    }

    /**
     * 报告信道
     * @param cmdReport
     */
    public void getReportConfigRev(OnCmdReportBack cmdReport){
        this.report = cmdReport;
    }

    /**
     * 滑板车错误码信息
     * @param errorCode
     */
    public void getErrorCode(OnCmdErrorCodeBack errorCode){
        this.code = errorCode;
    }


    // 升级返回结果接口
    private OnUpdateResultBack updateResult;

    /**
     * 本地升级蓝牙
     * @param localPath
     * @param result
     */
    public void upGradeBle(int sign, String localPath, OnUpdateResultBack result){
        this.updateResult = result;
        if(sign == 0) {
            updateBle(localPath);
        }else {
            upgradeActionEvent(UpgradeAction.ACTION_START, UpgradeAction.TYPE_CONTROL,localPath);
        }
    }


    /**
     * 升级蓝牙
     * @param localPath
     */
    private void updateBle(String localPath){
        DfuServiceListenerHelper.registerProgressListener(context, mDfuProgressListener);
        // 开始升级
        startUpgrade(localPath);
    }


    /**
     * 升级指令
     * @param action 升级动作
     * @param type 升级指令类型
     * @param path 升级包路径
     */
    public void upgradeActionEvent(int action,int type,String path){
        if(action == UpgradeAction.ACTION_START) {
            if (type == UpgradeAction.TYPE_CONTROL) {
                startUpgrade(type, path);
            } else if (type == UpgradeAction.TYPE_BMS) {
                startUpgrade(type, path);
            } else if (type == UpgradeAction.TYPE_MCU) {
                startUpgrade(type,path);
            } else if (type == UpgradeAction.TYPE_ELECTRIC) {
                startUpgrade(type,path);
            }
        } else {
            if (type == UpgradeAction.TYPE_CONTROL) {
                stopUpgrade();
            } else if (type == UpgradeAction.TYPE_BMS) {
                stopUpgrade();
            } else if (type == UpgradeAction.TYPE_MCU) {
                stopUpgrade();
            }else if (type == UpgradeAction.TYPE_ELECTRIC) {
                stopUpgrade();
            }
        }
    }


    /**
     * 设备断开连接失败
     */
    @Override
    protected void deviceDisConnectFailed() {
        if(disConnState != null) {
            disConnState.disConnectState(false);
        }
    }

    /**
     * 设备连接失败
     */
    @Override
    protected void deviceConnectFailed() {
        if(connState != null) {
            connState.connectState(false, "连接失败");
        }
    }

    /**
     * 设备断开连接
     */
    @Override
    protected void deviceDisconnect() {
        if(disConnState != null) {
            disConnState.disConnectState(true);
        }
    }

    /**
     * 设备连接蓝牙
     */
    @Override
    protected void deviceConnected() {
        if(connState != null) {
            connState.connectState(true, "连接成功");
        }
    }


    /**
     * 发现新的设备
     * @param device 蓝牙扫描到的新设备
     */
    @Override
    protected void discoverNewDevice(OkaiBleDevice device) {
        if(scanState != null) {
            scanState.onScanSuccess(device);
        }
    }

    /**
     * 发现具体设备
     * @param device 具体的设备信息
     */
    @Override
    protected void discoverAimDevice(OkaiBleDevice device) {
        if(scanState != null) {
            scanState.onScanSuccess(device);
        }
    }


    /**
     * 指令序列号
     * @return
     */
    private String getCmdNo() {
        int cmdNo = BindData.CMD_NO++;
        if (cmdNo > 65535) {
            cmdNo = 0;
            BindData.CMD_NO = 0;
        }
        byte[] buff = ByteUtils.longToBytesByBig(cmdNo, 2);
        return ByteUtils.bytesToStringByBig(buff);
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


    public int cmd;
    public int cmdNo;
    // 判断指令数据是否正确
    public static final int ERR = 1;
    public static final int SUCCESS = 0;
    public int res = ERR;
    // 错误提示
    public String errMsg = null;
    // 指令序列号
    public void setCmdNo(String cmdNoStr) {
        byte[] buff = ByteUtils.strToBytes(cmdNoStr);
        this.cmdNo = ByteUtils.bytesToIntByBig(buff, 2);
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
//        carRunState = new CarRunState();
        if (channel.equals(BLEUUIDs.COMMON_CHANNEL.toLowerCase())) {
            if (data.startsWith("+ACK")) {
                res = ERR;
                final int code = dealAckData(data);
                if (code != 3) {
                    logI(TAG,"收到的数据：" + data);
                } else {
                    dealUpgrade(data);
                }
            } else {
                dealReportData(data);
            }
        } else if (channel.equals(BLEUUIDs.REPORT_CHANNEL.toLowerCase())) {
            logI(TAG,"收到广播数据：" + data);
            dealReportData(data);
        } else if (channel.equals(BLEUUIDs.UPGRADE_CHANNEL.toLowerCase())) {
            logI(TAG,"收到更新数据：" + data);
            if (data.startsWith("+ACK")) {
                dealUpgrade(data);
            }
        } else if (channel.equals(BLEUUIDs.CONFIG_CHANNEL.toLowerCase())) {
            logI(TAG,"收到更新固件数据：" + data);
        } else {
            logI(TAG,"收到其他数据：" + data + " channel:" + channel);
        }
    }

    /**
     * 判断当前接收到的指令和具体收到的应答指令是否一致
     *
     * @param nowCmd 当前收到的指令
     * @return 指令是否一致
     */
    private boolean checkCmdNo(int nowCmd) {
        return BindData.CMD_NO == nowCmd;
    }

    /**
     * 处理应答数据
     * 0：表示数据正常，处理成功
     * 1、表示数据格式不对
     * 2、数据结果长度不对
     * 3、非可识别指令
     * 4、数据的结果不对
     *
     * @param data 当前需要处理的字符串
     * @return 处理结果
     */
    private int dealAckData(String data) {
        data = data.replace("+ACK:"+getHeadConfig(), "").replace("\r\n", "").replace("$", "");
        String[] buff = data.split("=");
        if (buff.length < 2) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorDescription = "收到数据格式不对";
                    errorInfo.errorCode = 103;
                    if(result != null) {
                        result.CmdResultEvent(false, errorInfo);
                    }
                }
            });
            return 1;
        }
        String type = buff[0];
        data = buff[1];
        buff = data.split(",");
        switch (type) {
            case "CON":   // 蓝牙参数配置应答指令
                if (buff.length != 2) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "收到数据格式不对";
                            errorInfo.errorCode = 103;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 2;
                }

                if (buff[0].equals("0")) {
                    errMsg = "处理失败";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "处理失败";
                            errorInfo.errorCode = 102;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[1]);
                cmd = 1;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            result.CmdResultEvent(true, null);
                        }
                    }
                });
                break;
            case "CAP":   // 车辆参数配置应答
                if (buff.length != 3) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "收到数据格式不对";
                            errorInfo.errorCode = 103;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 2;
                }
                if (!buff[0].equals(initInfo.carSn)) {
                    errMsg = "车辆SN比对失败";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "车辆SN比对失败";
                            errorInfo.errorCode = 118;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
//                if (!buff[1].equals(initInfo.typeName)) {
//                    errMsg = "车型名称比对失败";
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            errorInfo.errorDescription = "车型名称比对失败";
//                            errorInfo.errorCode = 119;
//                            if(result != null) {
//                                result.CmdResultEvent(false, errorInfo);
//                            }
//                        }
//                    });
//                    return 4;
//                }
                setCmdNo(buff[2]);
                cmd = 2;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            result.CmdResultEvent(true, null);
                        }
                    }
                });
                break;
            case "TET":   // 开始成品测试
                if (buff.length != 2) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "收到数据格式不对";
                            errorInfo.errorCode = 103;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 2;
                }
                if (buff[0].equals("0")) {
                    errMsg = "应答失败";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "应答失败";
                            errorInfo.errorCode = 102;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[1]);
                cmd = 3;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            result.CmdResultEvent(true, null);
                        }
                    }
                });
                break;
            case "XWM":   // 模式切换
                if (buff.length != 3) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "收到数据格式不对";
                            errorInfo.errorCode = 103;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 2;
                }

                if (buff[0].equals("0")) {
                    errMsg = "密码错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "密码错误";
                            errorInfo.errorCode = 101;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }

                if (Integer.parseInt(buff[1]) != initInfo.mode) {
                    errMsg = "模式切换失败，与目标模式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "模式切换失败，与目标模式不对";
                            errorInfo.errorCode = 120;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
                cmd = 4;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            result.CmdResultEvent(true, null);
                        }
                    }
                });
                break;
            case "LOC":   // 寻车
                if (buff.length != 2) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "收到数据格式不对";
                            errorInfo.errorCode = 103;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 2;
                }
                if (buff[0].equals("0")) {
                    errMsg = "密码错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "密码错误";
                            errorInfo.errorCode = 101;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[1]);
                cmd = 5;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            result.CmdResultEvent(true, null);
                        }
                    }
                });

                break;
            case "SCT":   // 车辆锁和机械锁
                if (buff.length != 7) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "收到数据格式不对";
                            errorInfo.errorCode = 103;
                            if(result != null) {
                                result.CmdResultEvent(false,errorInfo);
                            }
                        }
                    });
                    return 2;
                }
                if (buff[0].equals("0")) {
                    errMsg = "密码错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "密码错误";
                            errorInfo.errorCode = 101;
                            if(result != null) {
                                result.CmdResultEvent(false,errorInfo);
                            }
                        }
                    });
                    return 4;
                }

                if(lockInfo.vehicleLock != Integer.parseInt(buff[1])){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "对应锁状态不对";
                            errorInfo.errorCode = 102;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            result.CmdResultEvent(true,null);
                        }
                    }
                });

                setCmdNo(buff[6]);
                cmd = 6;
                break;
            case "ECP":   // 骑行参数配置
                if (buff.length != 3) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "收到数据格式不对";
                            errorInfo.errorCode = 103;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 2;
                }

                if (buff[0].equals("0")) {
                    errMsg = "密码错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "密码错误";
                            errorInfo.errorCode = 101;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }

                if (buff[1].equals("0")) {
                    errMsg = "骑行参数配置失败";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "骑行参数配置失败";
                            errorInfo.errorCode = 102;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
                cmd = 7;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            result.CmdResultEvent(true, null);
                        }
                    }
                });
                break;
            case "LED":   // LED控制
                if (buff.length != 3) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "收到数据格式不对";
                            errorInfo.errorCode = 103;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 2;
                }

                if (buff[0].equals("0")) {
                    errMsg = "密码错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "密码错误";
                            errorInfo.errorCode = 101;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }

                if (buff[1].equals("0")) {
                    errMsg = "LED配置失败";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "LED配置失败";
                            errorInfo.errorCode = 102;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
                cmd = 8;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            result.CmdResultEvent(true, null);
                        }
                    }
                });
                break;
            case "PWD":   // 密码修改
                if (buff.length != 3) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "收到数据格式不对";
                            errorInfo.errorCode = 103;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 2;
                }

                if (buff[0].equals("0")) {
                    errMsg = "密码错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "密码错误";
                            errorInfo.errorCode = 101;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }

                if (buff[1].equals("0")) {
                    errMsg = "密码修改失败";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "密码修改失败";
                            errorInfo.errorCode = 102;
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }

                setCmdNo(buff[2]);
                cmd = 9;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            result.CmdResultEvent(true, null);
                        }
                    }
                });
                break;
            case "ALC":   // 配置信息应答
                if(buff[0].equals("0")){
                    errMsg = "输入密码错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "密码错误";
                            errorInfo.errorCode = 101;
                            if(infoResult != null) {
                                infoResult.CmdInitInfo(errorInfo, null);
                            }
                        }
                    });
                    return 3;
                }

                if (buff.length < 26) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 103;
                            errorInfo.errorDescription = "收到数据格式不对";
                            if(infoResult != null) {
                                infoResult.CmdInitInfo(errorInfo, null);
                            }
                        }
                    });
                    return 2;
                }

                int i = 0;
                while (i < buff.length - 1) {
                    if (buff[i].equals("CON")) {
                        i++;
                        try {
                            initInfo.SN = buff[i++];
                        } catch (NumberFormatException e) {
                            errMsg = "SN参数解析异常";
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "SN参数解析异常";
                                    errorInfo.errorCode = 116;
                                    if(infoResult != null) {
                                        infoResult.CmdInitInfo(errorInfo, null);
                                    }
                                }
                            });
                            return 4;
                        }
                        try {
                            initInfo.broadcastSpace = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            errMsg = "广播间隔参数解析错误";
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "广播间隔参数解析错误 ";
                                    errorInfo.errorCode = 116;
                                    if(infoResult != null) {
                                        infoResult.CmdInitInfo(errorInfo, null);
                                    }
                                }
                            });
                            return 4;
                        }
                        try {
                            initInfo.broadcastTime = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            errMsg = "广播持续时间参数错误";
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "广播持续时间参数错误";
                                    errorInfo.errorCode = 116;
                                    if(infoResult != null) {
                                        infoResult.CmdInitInfo(errorInfo, null);
                                    }
                                }
                            });
                            return 4;
                        }
                        try {
                            initInfo.minConnectSpace = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            errMsg = "最小连接间隔参数错误";
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "最小连接间隔参数错误";
                                    errorInfo.errorCode = 116;
                                    if(infoResult != null) {
                                        infoResult.CmdInitInfo(errorInfo, null);
                                    }
                                }
                            });
                            return 4;
                        }
                        try {
                            initInfo.maxConnectSpace = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            errMsg = "最大连接间隔参数错误";
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "最大连接间隔参数错误";
                                    errorInfo.errorCode = 116;
                                    if(infoResult != null) {
                                        infoResult.CmdInitInfo(errorInfo, null);
                                    }
                                }
                            });
                            return 4;
                        }

                        if(buff.length == 27) {
                            i++;
                        }
                        // 此处清除当前密码的bug
                        i++;

                    } else if (buff[i].equals("CAP")) {
                        i++;
                        initInfo.carSn = buff[i++];
                        i++;
                    } else if (buff[i].equals("XWM")) {
                        i++;
                        try {
                            initInfo.mode = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            errMsg = "蓝牙模式参数错误";
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "蓝牙模式参数错误";
                                    errorInfo.errorCode = 116;
                                    if(infoResult != null) {
                                        infoResult.CmdInitInfo(errorInfo, null);
                                    }
                                }
                            });
                            return 4;
                        }
                    } else if (buff[i].equals("ECP")) {
                        i++;
                        try {
                            initInfo.MaxSpeed = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            errMsg = "最高速度参数错误";
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "最高速度参数错误";
                                    errorInfo.errorCode = 116;
                                    if(infoResult != null) {
                                        infoResult.CmdInitInfo(errorInfo, null);
                                    }
                                }
                            });
                            return 4;
                        }

                        try {
                            initInfo.addMode = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            errMsg = "加速模式参数错误";
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "加速模式参数错误";
                                    errorInfo.errorCode = 116;
                                    if(infoResult != null) {
                                        infoResult.CmdInitInfo(errorInfo, null);
                                    }
                                }
                            });
                            return 4;
                        }

                        try {
                            initInfo.showMode = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            errMsg = "仪表显示单位参数错误";
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "仪表显示单位参数错误";
                                    errorInfo.errorCode = 116;
                                    if(infoResult != null) {
                                        infoResult.CmdInitInfo(errorInfo, null);
                                    }
                                }
                            });
                            return 4;
                        }

                        try {
                            initInfo.reportSpace = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            errMsg = "车辆信息上报间参数错误";
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "车辆信息上报间参数错误";
                                    errorInfo.errorCode = 116;
                                    if(infoResult != null) {
                                        infoResult.CmdInitInfo(errorInfo, null);
                                    }
                                }
                            });
                            return 4;
                        }

                        try {
                            initInfo.standbyTime = Integer.parseInt(buff[i++]);
                        } catch (NumberFormatException e) {
                            errMsg = "待机关机时间参数错误";
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "待机关机时间参数错误";
                                    errorInfo.errorCode = 116;
                                    if(infoResult != null) {
                                        infoResult.CmdInitInfo(errorInfo, null);
                                    }
                                }
                            });
                            return 4;
                        }
                    } else if (buff[i].equals("VER")) {
                        i++;
                        initInfo.softVersion = buff[i++];
                        initInfo.wareVersion = buff[i++];
                        initInfo.controlSV = buff[i++];
                        initInfo.controlWV = buff[i++];
                        initInfo.bmsSV = buff[i++];
                        initInfo.bmsWV = buff[i++];
                    } else {
                        errMsg = "配置信息解析参数错误";
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                errorInfo.errorDescription = "配置信息解析参数错误";
                                errorInfo.errorCode = 116;
                                if(infoResult != null) {
                                    infoResult.CmdInitInfo(errorInfo, null);
                                }
                            }
                        });
                        return 4;
                    }
                }

                setCmdNo(buff[i++]);
                cmd = 10;
                // 缓存数据，根据蓝牙广播名称
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(infoResult != null) {
                            infoResult.CmdInitInfo(null, initInfo);
                        }
                    }
                });
                break;
            case "NAM":   // 修改名称应答
                if (buff.length != 3) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 103;
                            errorInfo.errorDescription = "收到数据格式不对";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });

                    return 2;
                }
                if (buff[0].equals("0")) {
                    errMsg = "密码错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 101;
                            errorInfo.errorDescription = "密码错误";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                if (buff[1].equals("0")) {
                    errMsg = "修改蓝牙名称失败";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 102;
                            errorInfo.errorDescription = "修改蓝牙名称失败";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
                cmd = 11;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            result.CmdResultEvent(true, null);
                        }
                    }
                });
                break;
            case "DSX":   // 开关定速巡航模式
                if (buff.length != 3) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 103;
                            errorInfo.errorDescription = "收到数据格式不对";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 2;
                }
                if (buff[0].equals("0")) {
                    errMsg = "密码错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 101;
                            errorInfo.errorDescription = "密码错误";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                if (buff[1].equals("0")) {
                    errMsg = "模式错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 102;
                            errorInfo.errorDescription = "模式错误";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
                cmd = 12;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            result.CmdResultEvent(true, null);
                        }
                    }
                });
                break;
            case "SCM":   // 开关车辆锁车模式
                if (buff.length != 3) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 103;
                            errorInfo.errorDescription = "收到数据格式不对";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 2;
                }
                if (buff[0].equals("0")) {
                    errMsg = "密码错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 101;
                            errorInfo.errorDescription = "密码错误";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                if (buff[1].equals("0")) {
                    errMsg = "模式错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 102;
                            errorInfo.errorDescription = "模式错误";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
               cmd = 13;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            result.CmdResultEvent(true, null);
                        }
                    }
                });
                break;
            case "SUM":   // 切换车身启动模式
                if (buff.length != 3) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 103;
                            errorInfo.errorDescription = "收到数据格式不对";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 2;
                }
                if (buff[0].equals("0")) {
                    errMsg = "密码错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 101;
                            errorInfo.errorDescription = "密码错误";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                if (buff[1].equals("0")) {
                    errMsg = "模式错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 102;
                            errorInfo.errorDescription = "模式错误";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
                cmd = 14;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            result.CmdResultEvent(true, null);
                        }
                    }
                });
                break;
            case "ONF":   // 切换车身启动模式
                if (buff.length != 3) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 103;
                            errorInfo.errorDescription = "收到数据格式不对";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 2;
                }
                if (buff[0].equals("0")) {
                    errMsg = "密码错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 101;
                            errorInfo.errorDescription = "密码错误";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                if (buff[1].equals("0")) {
                    errMsg = "模式错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 102;
                            errorInfo.errorDescription = "模式错误";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
                cmd = 15;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            result.CmdResultEvent(true, null);
                        }
                    }
                });
                break;
            case "SIM":   // 自检模式
                if (buff.length != 4) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 103;
                            errorInfo.errorDescription = "收到数据格式不对";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 2;
                }
                if (buff[0].equals("0")) {
                    errMsg = "密码错误";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 101;
                            errorInfo.errorDescription = "密码错误";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                if (buff[2].equals("0")) {
                    errMsg = "检测异常";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorCode = 102;
                            errorInfo.errorDescription = "检测异常";
                            if(result != null) {
                                result.CmdResultEvent(false, errorInfo);
                            }
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[3]);
                cmd = 16;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(result != null) {
                            result.CmdResultEvent(true, null);
                        }
                    }
                });
                break;
            default:
                return 3;
        }

        if (checkCmdNo(cmdNo)) {
            errMsg = "收到指令编号不正确";
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorDescription = "收到指令编号不正确";
                    errorInfo.errorCode = 123;
                    if(result != null) {
                        result.CmdResultEvent(false, errorInfo);
                    }
                }
            });
            return 5;
        }
        res = SUCCESS;
        return 0;
    }

    /**
     * 处理上报信息
     * 0：表示数据正常，处理成功
     * 1、表示数据格式不对
     * 2、数据结果长度不对
     * 3、数据各个具体内容不对
     *
     * @param data 当前需要解析的指令数据
     * @return 当前的处理结果
     */
    private int dealReportData(String data) {
        data = data.replace("+RESP:"+getHeadConfig(), "").replace("\r\n", "").replace("$", "");
        String[] buff = data.split("=");
        if (buff.length < 2) {
            return 1;
        }
        carRunState = new CarRunState();
        String type = buff[0];
        data = buff[1];
        buff = data.split(",");
        int i = 0;
        switch (type) {
            case "INF":   // 蓝牙参数配置上报
                if (buff.length < 23) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "收到数据格式不对";
                            errorInfo.errorCode = 103;
                            if(report != null) {
                                report.CmdReportEvent(errorInfo, null);
                            }
                        }
                    });
                    return 2;
                }
                try {
                    while (i < buff.length) {
                        if (buff[i].equals("ECU")) {
                            i++;
                            carRunState.lock = Integer.parseInt(buff[i++]);
                            carRunState.speed = Float.parseFloat(buff[i++]) * 0.1f;
                            carRunState.curMileage = Float.parseFloat(buff[i++]) * 0.01f;
                            carRunState.surplusMileage = Float.parseFloat(buff[i++]) * 0.1f;
                            carRunState.totalMileage = Float.parseFloat(buff[i++]) * 0.1f;
                            i++;
//                            carRunState.rideTime = Integer.parseInt(buff[i++]);
                            if(buff.length > 23) {
                                carRunState.ledState = Integer.parseInt(buff[i++]);
                                carRunState.addMode = Integer.parseInt(buff[i++]);
                            }
                        } else if (buff[i].equals("BMS")) {
                            i++;
                            i++;
                            i++;
                            //carRunState.chargeMos = Integer.parseInt(buff[i++]);
                            //carRunState.dischargeMos = Integer.parseInt(buff[i++]);
                            carRunState.power = Integer.parseInt(buff[i++]) * 0.1f;
                            //carRunState.soh = Integer.parseInt(buff[i++]) * 0.1f;
                            i++;
                            i++;
                            i++;
                            i++;
                            i++;
                            i++;
                            i++;
                            /*carRunState.eleCoreHigh = Integer.parseInt(buff[i++]);
                            carRunState.eleCoreLow = Integer.parseInt(buff[i++]);
                            carRunState.mosTemp = Integer.parseInt(buff[i++]);
                            carRunState.otherTemp = Integer.parseInt(buff[i++]);
                            carRunState.current = Integer.parseInt(buff[i++]);
                            carRunState.voltage = Integer.parseInt(buff[i++]);*/
                            carRunState.chargeFlag = Integer.parseInt(buff[i++]);
                            if(buff.length > 23) {
                                i++;
                                //carRunState.cycleIndex = Integer.parseInt(buff[i++]);
                            }
                        } else if (buff[i].equals("Meter")) {
                            i++;
                            /*carRunState.accelerateAD = Integer.parseInt(buff[i++]);
                            carRunState.brakeADLeft = Integer.parseInt(buff[i++]);
                            carRunState.brakeADRight = Integer.parseInt(buff[i++]);*/
                            i++;
                            i++;
                            i++;
                            if(buff.length > 23) {
                                carRunState.carCruise = Integer.parseInt(buff[i++]);
                                carRunState.carLockMode = Integer.parseInt(buff[i++]);
                                carRunState.carOpenMode = Integer.parseInt(buff[i++]);
                                carRunState.carSwitch = Integer.parseInt(buff[i++]);
                            }
                        } else {
                            errMsg = "蓝牙参数上报解析参数错误";
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "蓝牙参数上报解析参数错误";
                                    errorInfo.errorCode = 116;
                                    if(report != null) {
                                        report.CmdReportEvent(errorInfo, null);
                                    }
                                }
                            });
                            return 4;
                        }
                    }
                } catch (Exception e) {
                    errMsg = "其中某一个参数配置错误：" + i;
                    final int finalI = i;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "其中某一个参数配置错误：" + finalI;
                            errorInfo.errorCode = 116;
                            if(report != null) {
                                report.CmdReportEvent(errorInfo, null);
                            }
                        }
                    });
                    return 4;
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(report != null) {
                            report.CmdReportEvent(null, carRunState);
                        }
                    }
                });

                break;
            case "ECO":   // 车辆错误码上报
                if (buff.length < 2) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "收到数据格式不对";
                            errorInfo.errorCode = 103;
                            if(code != null) {
                                code.CmdErrorCode(false, "", errorInfo);
                            }
                        }
                    });
                    return 2;
                }
                CarConfig.errCode = buff[0];
                CarConfig.ybCode = buff[1];
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(code != null) {
                            code.CmdErrorCode(true, CarConfig.errCode, null);
                        }
                    }
                });
                break;
            case "CCF":
                try {
                    while (i < buff.length) {
                        if (buff[i].equals("ECU")) {
                            i++;
                            carRunState.lock = Integer.parseInt(buff[i++]);
                            carRunState.speed = Float.parseFloat(buff[i++]) * 0.1f;
                            carRunState.curMileage = Float.parseFloat(buff[i++]) * 0.01f;
                            carRunState.surplusMileage = Float.parseFloat(buff[i++]) * 0.1f;
                            carRunState.totalMileage = Float.parseFloat(buff[i++]) * 0.1f;
                            i++;
                            carRunState.ledState = Integer.parseInt(buff[i++]);
                            carRunState.addMode = Integer.parseInt(buff[i++]);
                        } else if (buff[i].equals("BMS")) {
                            i++;
                            carRunState.power = Integer.parseInt(buff[i++]) * 0.1f;
                            carRunState.chargeFlag = Integer.parseInt(buff[i++]);
                        } else if (buff[i].equals("Meter")) {
                            i++;
                            carRunState.carCruise = Integer.parseInt(buff[i++]);
                            carRunState.carLockMode = Integer.parseInt(buff[i++]);
                            carRunState.carOpenMode = Integer.parseInt(buff[i++]);
                            i++;
//                            carRunState.carSMode = Integer.parseInt(buff[i++]);
                        } else {
                            errMsg = "解析参数错误";
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "蓝牙参数上报解析参数错误";
                                    errorInfo.errorCode = 116;
                                    if(report != null) {
                                        report.CmdReportEvent(errorInfo, null);
                                    }
                                }
                            });
                            return 4;
                        }
                    }
                } catch (Exception e) {
                    errMsg = "其中某一个参数配置错误：" + i;
                    final int finalI = i;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "其中某一个参数配置错误：" + finalI;
                            errorInfo.errorCode = 116;
                            if(report != null) {
                                report.CmdReportEvent(errorInfo, null);
                            }
                        }
                    });
                    return 4;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(report != null) {
                            report.CmdReportEvent(null, carRunState);
                        }
                    }
                });
                break;
            case "TES":
                try {
                    while (i < buff.length) {
                        if (buff[i].equals("E")) {
                            i++;
                            i++;
//                            carRunState.rideTime = Integer.parseInt(buff[i++]);
                            carRunState.totalMileage = Float.parseFloat(buff[i++]) * 0.1f;
                            carRunState.surplusMileage = Float.parseFloat(buff[i++]) * 0.1f;
                            carRunState.curMileage = Float.parseFloat(buff[i++]) * 0.01f;
                            carRunState.speed = Float.parseFloat(buff[i++]) * 0.1f;
                            i++;
                            i++;
                            i++;
                            i++;
                            i++;
                            i++;
                            carRunState.carCruise = Integer.parseInt(buff[i++]);
                            i++;

                        } else if (buff[i].equals("B")) {
                            i++;
                            i++;
                            i++;
//                            carRunState.current = Integer.parseInt(buff[i++]);
//                            carRunState.voltage = Integer.parseInt(buff[i++]);
                        } else if (buff[i].equals("Y")) {
                            i++;
                            i++;
                            i++;
                            i++;
//                            carRunState.accelerateAD = Integer.parseInt(buff[i++]);
//                            carRunState.brakeADLeft = Integer.parseInt(buff[i++]);
//                            carRunState.brakeADRight = Integer.parseInt(buff[i++]);
                        } else {
                            errMsg = "解析参数错误";
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "蓝牙参数上报解析参数错误";
                                    errorInfo.errorCode = 116;
                                    if(report != null) {
                                        report.CmdReportEvent(errorInfo, null);
                                    }
                                }
                            });
                            return 4;
                        }
                    }
                } catch (Exception e) {
                    errMsg = "其中某一个参数配置错误：" + i;
                    final int finalI = i;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "其中某一个参数配置错误：" + finalI;
                            errorInfo.errorCode = 116;
                            if(report != null) {
                                report.CmdReportEvent(errorInfo, null);
                            }
                        }
                    });
                    return 4;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(report != null) {
                            report.CmdReportEvent(null, carRunState);
                        }
                    }
                });
                break;
        }
        return 0;
    }

    /**
     * 处理升级更新数据
     * @param data
     */
    private void dealUpgrade(String data) {
        logI(TAG,"收到升级指令：" + data);
        data = data.replace("+ACK:"+getHeadConfig(), "").replace("\r\n", "").replace("$", "");
        String[] buff = data.split("=");
        if (buff.length < 2) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorDescription = "收到数据格式不对";
                    errorInfo.errorCode = 103;
                    if(updateResult != null) {
                        updateResult.updateFailed(errorInfo);
                    }
                }
            });
            return;
        }
        String type = buff[0];
        data = buff[1];
        buff = data.split(",");
        switch (type) {
            case "UAS":
                int result = -1;
                try {
                    result = Integer.parseInt(buff[0]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (result != -1) {
                    int code = -1;
                    String msg = "";
                    int percent = 0;
                    switch (result) {
                        case 0: // 失败，硬件版本号异常
                            code = UpgradeResult.FAILED;
                            msg = "升级失败，硬件版本号异常";
                            stopUpgrade();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "升级失败，硬件版本号异常";
                                    errorInfo.errorCode = 108;
                                    if(updateResult != null) {
                                        updateResult.updating(3,"升级失败",getUpgradeProgress());
                                        updateResult.updateFailed(errorInfo);
                                    }
                                }
                            });
                            break;
                        case 1: // 更新，发送下一个包
                            code = UpgradeResult.UPDATE;
                            msg = "发送下一个包";
                            percent = getUpgradeProgress();
                            sendUpgradeData(true);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if(updateResult != null) {
                                        updateResult.updating(1,"发送数据中",getUpgradeProgress());
                                    }
                                }
                            });
                            break;
                        case 2: // 更新，重发当前包
                            code = UpgradeResult.UPDATE;
                            msg = "重发当前包";
                            sendUpgradeData(false);
                            percent = getUpgradeProgress();
                            logI(TAG,"重发当前包：" + percent);

                            break;
                        case 3: // 更新，重新发送
                            code = UpgradeResult.UPDATE;
                            msg = "固件丢失，重新发送";
                            sendStartData();
                            percent = getUpgradeProgress();
                            logI(TAG,"固件丢失，重新发送：" + percent);

                            break;
                        case 4: // 失败，程序升级失败
                            code = UpgradeResult.FAILED;
                            msg = "程序升级失败";
                            stopUpgrade();
                            final int finalPercent1 = percent;
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "程序升级失败";
                                    errorInfo.errorCode = 109;
                                    if(updateResult != null) {
                                        updateResult.updating(3,"升级失败",getUpgradeProgress());
                                        updateResult.updateFailed(errorInfo);
                                    }
                                }
                            });
                            break;
                        case 5: // 更新，升级准备就绪
                            code = UpgradeResult.UPDATE;
                            msg = "升级准备就绪";
                            sendStartData();
                            percent = getUpgradeProgress();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if(updateResult != null) {
                                        updateResult.updating(0,"升级准备就绪",getUpgradeProgress());
                                    }
                                }
                            });
                            break;
                        case 6: // 失败，密码错误
                            code = UpgradeResult.FAILED;
                            msg = "密码错误";
                            stopUpgrade();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "密码错误";
                                    errorInfo.errorCode = 101;
                                    if(updateResult != null) {
                                        updateResult.updating(3,"升级失败",getUpgradeProgress());
                                        updateResult.updateFailed(errorInfo);
                                    }
                                }
                            });
                            break;
                        case 7: // 成功
                            code = UpgradeResult.SUCCESS;
                            msg = "机器正常开机";
                            stopUpgrade();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if(updateResult != null) {
                                        updateResult.updating(1,"机器正常开机", getUpgradeProgress());
                                    }
                                }
                            });
                            break;
                        case 8: // 成功
                            code = UpgradeResult.SUCCESS;
                            msg = "程序升级成功";
                            stopUpgrade();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if(updateResult != null) {
                                        updateResult.updating(2,"程序升级成功", getUpgradeProgress());
                                        updateResult.updateSuccess();
                                    }
                                }
                            });
                            break;
                    }
                }
                break;
        }
    }

    // 是否升级
    private boolean isUpgrade = false;
    // 升级底层程序
    private UpgradeFrame upgradeFrame;

    /**
     * 开始控制器升级
     * @param type
     * @param path
     */
    private void startUpgrade(int type, String path) {
        if (upgradeFrame != null && isUpgrade) {
//            updateResult.updateResult(true,"正在升级，请勿重复点击");
            return;
        }

        if(!path.endsWith(".bin")){
            errorInfo.errorDescription = "该升级包不是控制器的升级包";
            errorInfo.errorCode = 128;
            if(updateResult != null) {
                updateResult.updateFailed(errorInfo);
            }
            return;
        }else if(StringUtils.isEmpty(path)){
            errorInfo.errorDescription = "本地文件不存在";
            errorInfo.errorCode = 112;
            if(updateResult != null) {
                updateResult.updateFailed(errorInfo);
            }
            return;
        }

        File file = new File(path);
        if(!file.exists()){
            errorInfo.errorDescription = "本地文件不存在";
            errorInfo.errorCode = 112;
            if(updateResult != null) {
                updateResult.updateFailed(errorInfo);
            }
            return;
        }

        if (upgradeFrame == null) {
            upgradeFrame = new UpgradeFrame(type, path);
        }
        upgradeFrame.setOnUpgradeListener(new UpgradeFrame.OnUpgradeListener() {
            @Override
            public void failed(final String msg) {
                stopUpgrade();
                errorInfo.errorDescription = "升级被暂停";
                errorInfo.errorCode = 107;
                if(updateResult != null) {
                    updateResult.updateFailed(errorInfo);
                }
            }
        });
        upgradeFrame.start();
        int code;
        if (type == UpgradeAction.TYPE_CONTROL) {
            code = 13;
        } else if (type == UpgradeAction.TYPE_BMS) {
            code = 14;
        } else if (type == UpgradeAction.TYPE_MCU){
            code = 15;
        }else {
            code = 16;
        }
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            if(updateResult != null) {
                updateResult.updateFailed(errorInfo);
            }
            return;
        }
        if(upgradeFrame.getPackNum() == 0){
            errorInfo.errorCode = 114;
            errorInfo.errorDescription = "控制器固件升级失败，数据打包错误0";
            if(updateResult != null) {
                updateResult.updateFailed(errorInfo);
            }
            return;
        }
        String cmdStr = getHeadInput()+"URD=" + initInfo.pwd + "," + code + "," + getCmdNo() + "$\r\n";
//        String cmdStr = upgradeControl(initInfo.pwd,code,upgradeFrame.getPackNum(),getCmdNo());
        logI(TAG,"发送固件升级指令:" + cmdStr);
        isUpgrade = true;
        boolean isSend = sendCmdStr(upgradeChannel, cmdStr);
        if (!isSend) {
            stopUpgrade();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorDescription = "数据发送失败";
                    errorInfo.errorCode = 117;
                    if(updateResult != null) {
                        updateResult.updateFailed(errorInfo);
                    }
                }
            });
            logI(TAG,"数据发送失败");
        }
    }

    private String upgradeChannel = BLEUUIDs.COMMON_CHANNEL;

    /**
     * 发送数据
     */
    private void sendStartData() {
        if (upgradeFrame != null && isUpgrade) {
            byte[] data = null;
            try {
                data = upgradeFrame.getStartData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (data != null) {
//                String str = getHeadInput()+"LOC=" + initInfo.pwd + "," + getCmdNo();
//                boolean isSend = sendCmdStr(upgradeChannel, str);
                boolean isSend = sendSCmd(upgradeChannel, data);
                if (!isSend) {
                    stopUpgrade();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "控制器固件升级失败，数据发送失败";
                            errorInfo.errorCode = 121;
                            if(updateResult != null) {
                                updateResult.updateFailed(errorInfo);
                            }
                        }
                    });
                    logI(TAG,"控制器固件升级失败，数据发送失败");
                }
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        errorInfo.errorDescription = "升级失败，升级数据包为空";
                        errorInfo.errorCode = 109;
                        if(updateResult != null) {
                            updateResult.updateFailed(errorInfo);
                        }
                    }
                });
                logI(TAG,"数据位空");
            }
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorDescription = "控制器固件升级停止";
                    errorInfo.errorCode = 122;
                    if(updateResult != null) {
                        updateResult.updateFailed(errorInfo);
                    }
                }
            });
            logI(TAG,"发送失败");
        }
    }

    /**
     * 发送更新包
     * @param next
     */
    private void sendUpgradeData(boolean next) {
        if (upgradeFrame != null && isUpgrade) {
            byte[] data = null;
            if (next) {
                try {
                    data = upgradeFrame.getNextData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    data = upgradeFrame.getNowData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (data != null) {
                sendSCmd(upgradeChannel, data);
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        errorInfo.errorDescription = "升级失败，升级数据包为空";
                        errorInfo.errorCode = 109;
                        if(updateResult != null) {
                            updateResult.updateFailed(errorInfo);
                        }
                    }
                });
                logI(TAG,"数据位空");
            }
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorDescription = "控制器固件升级停止";
                    errorInfo.errorCode = 122;
                    if(updateResult != null) {
                        updateResult.updateFailed(errorInfo);
                    }
                }
            });
            logI(TAG,"发送失败");
        }
    }

    /**
     * 更新进度
     * @return
     */
    private int getUpgradeProgress() {
        int percent = 0;
        if (upgradeFrame != null && isUpgrade) {
            percent = upgradeFrame.getPercent();
        }
        return percent;
    }

    /**
     * 停止更新
     */
    private void stopUpgrade() {
        if (upgradeFrame != null && isUpgrade) {
            upgradeFrame.stop();
            upgradeFrame = null;
        }
        isUpgrade = false;
    }


    private DfuServiceInitiator dfuServiceInitiator;

    /**
     * 开始蓝牙升级
     * @param localPath
     */
    protected void startUpgrade(String localPath) {
        if(!localPath.endsWith(".zip")){
            errorInfo.errorCode = 128;
            errorInfo.errorDescription = "该升级包不是蓝牙的升级包";
            if(updateResult != null) {
                updateResult.updateFailed(errorInfo);
            }
            return;
        }

        /*String pathDir = FileManager.UPGRADE_DIR;
        File fileDir = new File(pathDir);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }*/
        File file = new File(localPath);
        if (!file.exists()) {
            errorInfo.errorCode = 112;
            errorInfo.errorDescription = "本地文件不存在";
            if(updateResult != null) {
                updateResult.updateFailed(errorInfo);
            }
            return;
        }
        if (conDevice == null) {
            errorInfo.errorCode = 111;
            errorInfo.errorDescription = "Device not connect";
            if(updateResult != null) {
                updateResult.updateFailed(errorInfo);
            }
            return;
        }
        // 创建dfu通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DfuServiceInitiator.createDfuNotificationChannel(context);
        }
        dfuServiceInitiator = new DfuServiceInitiator(conDevice.getAddress())
                .setDeviceName(conDevice.getName())
                .setKeepBond(true)
                .setDisableNotification(true)
                .setZip(localPath);
        dfuServiceInitiator.start(context, DfuService.class);

    }

    /**
     * 停止蓝牙固件的升级
     */
    private void stopDfu() {
        if (dfuServiceInitiator == null) {
            return;
        }
        context.stopService(new Intent(context, DfuService.class));
        dfuServiceInitiator = null;
        DfuServiceListenerHelper.unregisterProgressListener(context, mDfuProgressListener);
    }

    private int progress = 0;   // 升级进度

    private final DfuProgressListener mDfuProgressListener = new DfuProgressListener() {
        @Override
        public void onDeviceConnecting(String deviceAddress) {
            logI(TAG,"连接中");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(updateResult != null) {
                        updateResult.updating(0,"连接设备，准备升级",progress);
                    }
                }
            });

        }

        @Override
        public void onDeviceConnected(String deviceAddress) {
            logI(TAG,"已连接");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(updateResult != null) {
                        updateResult.updating(0,"设备已连接，准备升级",progress);
                    }
                }
            });
        }

        @Override
        public void onDfuProcessStarting(String deviceAddress) {
            logI(TAG,"开始升级1");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(updateResult != null) {
                        updateResult.updating(0,"开始升级1",progress);
                    }
                }
            });
        }

        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            logI(TAG,"开始升级2");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(updateResult != null) {
                        updateResult.updating(0,"开始升级",progress);
                    }
                }
            });
        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            logI(TAG,"使能dfu");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(updateResult != null) {
                        updateResult.updating(0,"使能dfu",progress);
                    }
                }
            });
        }

        @Override
        public void onProgressChanged(String deviceAddress, final int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            logI(TAG,"升级进度：" + percent);
            progress = percent;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(updateResult != null) {
                        updateResult.updating(1,"升级中",progress);
                    }
                }
            });
        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
            logI(TAG,"警告");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(updateResult != null) {
                        updateResult.updating(1,"警告",progress);
                    }
                }
            });
        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            logI(TAG,"正在断开连接");

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(updateResult != null) {
                        updateResult.updating(1,"正在断开连接",progress);
                    }
                }
            });
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            logI(TAG,"连接断开");

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(updateResult != null) {
                        updateResult.updating(1,"连接断开",progress);
                    }
                }
            });
        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            logI(TAG,"升级成功,请重新连接设备");
            stopDfu();
            //升级成功，重新连接设备

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if(updateResult != null) {
                        updateResult.updating(2,"升级成功,请重新连接设备",progress);
                        updateResult.updateSuccess();
                    }
                }
            });
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            //升级流产，失败
            logI(TAG,"升级流产，请重新升级。");
            stopDfu();

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorCode = 110;
                    errorInfo.errorDescription = "升级流产，请重新升级。";
                    if(updateResult != null) {
                        updateResult.updating(3,"升级失败",progress);
                        updateResult.updateFailed(errorInfo);
                    }
                }
            });
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, final String message) {
            logI(TAG,"升级失败，请重新升级:" + error + "," + message);
            stopDfu();

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorCode = 113;
                    errorInfo.errorDescription = "升级失败，请重新升级。"+ message;
                    if(updateResult != null) {
                        updateResult.updating(3,"升级失败",progress);
                        updateResult.updateFailed(errorInfo);
                    }
                }
            });
        }
    };

    private OnBleConnectBack connectBack;

    public void setConnectListener(OnBleConnectBack back) {
        if(receiver != null){
            receiver.setBack(new BleConnectBack(){

                @Override
                public void connected() {
                    if (connectBack != null) {
                        connectBack.connected();
                    }
                }

                @Override
                public void disconnected() {
                    if (connectBack != null) {
                        connectBack.disconnected();
                    }
                }
            });
        }
        this.connectBack = back;
    }
}
