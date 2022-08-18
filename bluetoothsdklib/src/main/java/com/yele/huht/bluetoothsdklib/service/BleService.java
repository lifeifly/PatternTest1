package com.yele.huht.bluetoothsdklib.service;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.yele.baseapp.utils.ByteUtils;
import com.yele.baseapp.utils.LogUtils;
import com.yele.baseapp.utils.StringUtils;
import com.yele.huht.bluetoothsdklib.bean.CarConfig;
import com.yele.huht.bluetoothsdklib.bean.CarRunReport;
import com.yele.huht.bluetoothsdklib.bean.DebugFlag;
import com.yele.huht.bluetoothsdklib.bean.ErrorInfo;
import com.yele.huht.bluetoothsdklib.bean.FileManager;
import com.yele.huht.bluetoothsdklib.bean.InitInfo;
import com.yele.huht.bluetoothsdklib.bean.LockStateInfo;
import com.yele.huht.bluetoothsdklib.bean.OkaiBleDevice;
import com.yele.huht.bluetoothsdklib.bean.RevResult;
import com.yele.huht.bluetoothsdklib.bean.UpdateInfo;
import com.yele.huht.bluetoothsdklib.bean.UpgradeAction;
import com.yele.huht.bluetoothsdklib.bean.UpgradeResult;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdData;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdErrorCode;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdInitInfoResult;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdLockState;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdReport;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdResult;
import com.yele.huht.bluetoothsdklib.callBcak.OnConnectDevState;
import com.yele.huht.bluetoothsdklib.callBcak.OnDisConnectDevState;
import com.yele.huht.bluetoothsdklib.callBcak.OnScanDevState;
import com.yele.huht.bluetoothsdklib.callBcak.OnUpdateListener;
import com.yele.huht.bluetoothsdklib.callBcak.OnUpdateResult;
import com.yele.huht.bluetoothsdklib.callBcak.OnUpdateVersion;
import com.yele.huht.bluetoothsdklib.data.BLEUUIDs;
import com.yele.huht.bluetoothsdklib.data.BindData;
import com.yele.huht.bluetoothsdklib.data.LockStateEnum;
import com.yele.huht.bluetoothsdklib.data.StateEnum;
import com.yele.huht.bluetoothsdklib.policy.UpgradeFrame;
import com.yele.huht.bluetoothsdklib.policy.downloadlib.bean.FileInfo;
import com.yele.huht.bluetoothsdklib.policy.http.HttpManager;
import com.yele.huht.bluetoothsdklib.policy.http.back.OnApkUpdateBack;
import com.yele.huht.bluetoothsdklib.policy.load.LoadManager;
import com.yele.huht.bluetoothsdklib.policy.load.event.OnLoadEvent;
import com.yele.huht.bluetoothsdklib.util.JavaAES128Encryption;

import java.io.File;
import java.io.IOException;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class BleService extends BaseBleService {

    private static final String TAG = "BleService";

    private Context context;

    private static BleService bleService;

    private BleService(Context context) {
        this.context = context;
    }

    public static BleService getBleControl(Context context){
        if(bleService == null){
            synchronized (BleService.class){
                if (bleService == null) {
                    bleService = new BleService(context);
                }
            }
        }

        return bleService;
    }


    private OnCmdData cmdData;

    public void getCmdConfig(OnCmdData data){
        this.cmdData = data;
    }

    /**
     * 扫描设备列表
     */
    public void deviceStartScan(String devName, OnScanDevState state){
        setAimScanDevName(devName);
        startScanDev();
        this.scanState = state;
    }

    /**
     * 停止扫描
     */
    public void deviceStopScan(){
        stopScanDev();
    }

    /**
     * 蓝牙连接
     * @param id 设备id
     */
    public void deviceConnect(int id, OnConnectDevState state){
        this.connState = state;
        String macAddress = listScanDevice.get(id).device.getAddress();
        if (conGatt!=null){
            //  gatt.disconnect();
            conGatt.close();
        }
        connectDevice(macAddress);
        if (conGatt == null) {
            connState.connectState(false,"连接的通讯接口失败");
            return;
        }
        /*if (conGatt.discoverServices()) {
            logi( "发现服务了");
            conGatt.connect();
        }*/
    }

    /**
     * 蓝牙断开连接
     *
     */
    public void deviceDisConnect(OnDisConnectDevState state){
        this.disConnState = state;
        if (conDevice == null) {
            logi( "当前无设备连接");
            if (conGatt != null) {
                conGatt.close();
            }
            disConnState.disConnectState(true);
            return;
        }

        if(conGatt != null){
            conGatt.close();
            disConnState.disConnectState(true);
            logi( "通讯接口以及断开连接了");
            return;
        }
        conGatt.disconnect();
        logi("断开连接");
    }

    // 读取配置接口
    private OnCmdInitInfoResult infoResult;
    // 车辆锁状态接口
    private OnCmdLockState lockState;
    // 发送数据结果接口
    private OnCmdResult result;
    // 报告信道接口
    private OnCmdReport report;
    // 错误码信息
    private OnCmdErrorCode code;
    // 所有配置信息类
    private InitInfo initInfo = new InitInfo();
    // 错误返回信息类
    private ErrorInfo errorInfo = new ErrorInfo();
    // 蓝牙配置类
    private CarRunReport carRunState;

    /**
     * 读取所有配置信息、初始化
     * @param pwd 输入蓝牙密码
     * @param result
     */
    public void sendReadConfig(final String pwd, OnCmdInitInfoResult result){
        initInfo.pwd = pwd;
        final String channel = BLEUUIDs.COMMON_CHANNEL;
        String str = DebugFlag.SEND_PROTOCOL+"ALC=" + pwd + "," + getCmdNo();
//        String str = showMsg(pwd,getCmdNo());
        str += "$\r\n";
        if (sendCmdStr(channel, str)) {
            logi("发送成功");
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorCode = 117;
                    errorInfo.errorDescription = "发送数据失败";
                    infoResult.CmdInitInfo(errorInfo,null);
                }
            });
        }
        this.infoResult = result;
    }

    /*// Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public native String showMsg(String msg,String cmdNo);*/

    /**
     * 车辆参数配置
     * @param name 车型名称
     * @param sn 车辆Sn
     */
    public void sendConfigCar(String sn, String name, OnCmdResult cmdResult){
        // 当前车辆的SN
        initInfo.carSn = sn;
        // 车型名称
        initInfo.typeName = name;

        String str = DebugFlag.SEND_PROTOCOL+"CAP=" + sn + "," + name + "," + getCmdNo();
        // 发送指令
        sendConfig(str);
        this.result = cmdResult;
    }


    /**
     * 蓝牙参数配置
     * @param pwd 蓝牙密码
     * @param sn 仪表SN
     * @param broadcastSpace  广播间隔
     * @param broadcastTime 广播持续时间
     * @param minSpace 最小连接间隔
     * @param maxSpace 最大时间间隔范围
     */
    public void sendConfigBle(String pwd, String sn, int broadcastSpace, int broadcastTime, int minSpace, int maxSpace,OnCmdResult cmdResult){
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

        String str = DebugFlag.SEND_PROTOCOL+"CON=" + sn + "," + broadcastSpace + "," + broadcastTime + "," +
                minSpace + "," + maxSpace + "," + pwd + ",," + getCmdNo();
        // 发送指令
        sendConfig(str);
        this.result = cmdResult;
    }


    /**
     * 成品下发
     * 测试标志 0：取消/停止成品测试，1：开始成品测试
     */
    public void sendTestProduct(StateEnum stateEnum, OnCmdResult cmdResult){
        int testSign;
        if(stateEnum == StateEnum.ON){
            testSign = 1;
        }else {
            testSign = 0;
        }
        String str = DebugFlag.SEND_PROTOCOL+"TET=" + testSign + "," + getCmdNo();
        // 发送指令
        sendConfig(str);
        this.result = cmdResult;
    }

    /**
     * 出厂车辆参数配置
     * @param brakeSelect 0单刹（左）      霍尔刹,
     *                    1双刹           霍尔刹,
     *                    2单刹           开关刹
     * @param speedMode 0：mph
     *                  1：KM/h
     * @param gearE 0：无此档位 1：限速5
     * @param gearL 0：无此档位 1：限速15
     * @param gearH 0：无此档位 1：限速20  1：限速25  1：限速63
     * @param gear 0：L,H加速曲线分别为0，1
     *             1：L,H加速曲线分别为1，2
     *             2：L,H加速曲线分别为2，3........
     *             0：无此档位
     * @param batteryType  0：硬件板
     *                     1：软件板
     * @param eleGearSelect 数字由0-3，电子刹车力度由弱增强
     * @param ledMode 0：开机车大灯默认熄灭
     *                1：开机车大灯默认点亮
     * @param taillightMode 0：无刹车:低亮  刹车:低亮
     *                      1：无刹车:高亮  刹车:高亮
     *                      2：无刹车:低亮  刹车:高低亮闪烁
     *                      3：无刹车:低亮  刹车:灭灯低亮闪烁
     *                      4：无刹车:低亮  刹车:高亮
     *                      5：无刹车:灭灯/低亮闪烁  刹车:灭灯/低亮闪烁
     * @param saleCode 由此码可查表得该车辆批次发往的销售地区
     * @param clientCode  可由此码查表得该车辆批次的购买客户（公司）
     * @param deviceType 用于表示当前车辆的型号
     * @param bleBroadcast 用于表示当前车辆的型号
     *                     0：S052T
     *                     1：S521T
     *                     2：S522T
     *                     3：S520T
     * @param bleSwitch 0：开启蓝牙
     *                  1：关闭蓝牙
     * @param cmdResult
     */
    public void sendOutConfig(int brakeSelect,int speedMode,int gearE,int gearL,int gearH,int gear,int batteryType,
                              int eleGearSelect,int ledMode,int taillightMode,int saleCode,int clientCode,String deviceType,
                              int bleBroadcast,int bleSwitch,OnCmdResult cmdResult){

        /*byte[] bytes = JavaAES128Encryption.encryptAES128(initInfo.SN);
        if(bytes == null){
            errorInfo.errorCode = 901;
            errorInfo.errorDescription = "加密失败，密码为空";
            cmdResult.CmdResultEvent(false,errorInfo);
            return;
        }
        String carPwd = ByteUtils.bytesToStringByBig(bytes);*/

        String carPwd = "OKAI_CAR";
        String str;
        if(bleSwitch != 0 && bleSwitch != 1){
            str = "LFC=" + carPwd + "," + brakeSelect + ","
                    + speedMode + "," + gearE + "," + gearL + "," + gearH + ","
                    + gear + "," + batteryType + "," + eleGearSelect + "," + ledMode
                    + "," + taillightMode + "," + saleCode + "," + clientCode + "," + deviceType + "," + bleBroadcast + "," + getCmdNo();
        }else {
            str = "LFC=" + carPwd + "," + brakeSelect + ","
                    + speedMode + "," + gearE + "," + gearL + "," + gearH + ","
                    + gear + "," + batteryType + "," + eleGearSelect + "," + ledMode
                    + "," + taillightMode + "," + saleCode + "," + clientCode + "," + deviceType + "," + bleBroadcast + "," + bleSwitch + "," + getCmdNo();
        }
        initInfo.showMode = speedMode;
        initInfo.typeName = deviceType;

        // 发送指令
        sendConfig(str);
        this.result = cmdResult;
    }


    /**
     * 骑行参数
     * @param maxSpeed 当前车辆允许的最大速度 0~63
     * @param speedMode 车辆的加速模式 0：柔和模式，1：运动模式
     * @param showModel 仪表的显示模式 0：YD,1：KM
     * @param reportSpace 上报时间间隔，默认 100ms,最大9999ms,0不上报
     * @param time 待机关机时间，默认 30s,0不自动关机
     */
    public void sendNormalBikeConfig(int maxSpeed, int speedMode, int showModel, int reportSpace, int time,OnCmdResult cmdResult){
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            cmdResult.CmdResultEvent(false,errorInfo);
            return;
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

        String str = DebugFlag.SEND_PROTOCOL+"ECP=" + initInfo.pwd + "," + maxSpeed + "," + speedMode + ","
                + showModel + "," + reportSpace + "," + time + "," + getCmdNo();
        // 发送指令
        sendConfig(str);
        this.result = cmdResult;
    }


    /**
     * 切换模式
     * @param cmd
     * @param cmdResult
     */
    public void sendNormalChangeMode(int cmd, final OnCmdResult cmdResult){


        byte[] bytes = JavaAES128Encryption.encryptAES128(initInfo.SN);
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
            if(bytes == null){
                errorInfo.errorCode = 901;
                errorInfo.errorDescription = "加密失败，密码为空";
                cmdResult.CmdResultEvent(false,errorInfo);
                return;
            }
            carPwd = ByteUtils.bytesToStringByBig(bytes);
        }
        initInfo.mode = cmd;

        String str = DebugFlag.SEND_PROTOCOL+"XWM=" + carPwd + "," + cmd + "," + getCmdNo();
        // 发送指令
        sendConfig(str);
        this.result = cmdResult;

    }

    /**
     * 寻车
     * @param cmdResult
     */
    public void sendSearchCar(OnCmdResult cmdResult){
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            cmdResult.CmdResultEvent(false,errorInfo);
            return;
        }
        String str = DebugFlag.SEND_PROTOCOL+"LOC=" + initInfo.pwd + "," + getCmdNo();
        // 发送指令
        sendConfig(str);
        this.result = cmdResult;
    }

    /**
     * 车辆锁控制
     * @param lockState
     */
    public void sendAllLock(final LockStateEnum state, final OnCmdLockState lockState){
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            lockState.CmdLockState(errorInfo,null);
            return;
        }
        String cmd = null;
        if(state == LockStateEnum.UN_LOCK){
            cmd = "0,0,0,0,0";
        }else if(state == LockStateEnum.LOCK){
            cmd = "1,1,1,1,1";
        }else if(state == LockStateEnum.UN_SUPPORT){
            cmd = ",2,2,2,2";
        }
        final String channel = BLEUUIDs.COMMON_CHANNEL;
        String str = DebugFlag.SEND_PROTOCOL+"SCT=" + initInfo.pwd + "," + cmd + "," + getCmdNo();
        if (str != null) {
            str += "$\r\n";
        }

        if (sendCmdStr(channel, str)) {
            logi("发送成功" + str);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorCode = 117;
                    errorInfo.errorDescription = "数据发送失败";
                    lockState.CmdLockState(errorInfo,null);
                }
            });
        }
        this.lockState = lockState;
    }

    /**
     * led控制
     * @param cmd 0：关闭，1：打开
     * @param cmdResult
     */
    public void sendLed(int cmd,OnCmdResult cmdResult){

        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            cmdResult.CmdResultEvent(false,errorInfo);
            return;
        }

        String str = DebugFlag.SEND_PROTOCOL+"LED=" + initInfo.pwd + ",0," + cmd + "," + getCmdNo();
        sendConfig(str);
        this.result = cmdResult;
    }

    /**
     * 修改密码
     * @param newPwd 新密码
     * @param cmdResult
     */
    public void sendChangePwd(String oldPwd, final String newPwd, String vehicleSN,OnCmdResult cmdResult){
        if(!oldPwd.equals(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            cmdResult.CmdResultEvent(false,errorInfo);
            return;
        }
        String str = DebugFlag.SEND_PROTOCOL+"PWD=" + oldPwd + "," + newPwd + "," + vehicleSN + "," + getCmdNo();
        final String channel = BLEUUIDs.COMMON_CHANNEL;
        if (str != null) {
            str += "$\r\n";
        }
        if (sendCmdStr(channel, str)) {
            logi("发送成功");
            initInfo.pwd = newPwd;
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorCode = 117;
                    errorInfo.errorDescription = "数据发送失败";
                    result.CmdResultEvent(false,errorInfo);
                }
            });
        }
        this.result = cmdResult;
    }


    /**
     * 修改蓝牙名称
     * @param bleName 新的蓝牙名称
     * @param cmdResult
     */
    public void sendChangeBleName(String bleName,OnCmdResult cmdResult){
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            cmdResult.CmdResultEvent(false,errorInfo);
            return;
        }
        initInfo.NameNew = bleName;
        String str = DebugFlag.SEND_PROTOCOL+"NAM=" + initInfo.pwd + "," + bleName + "," + getCmdNo();
        // 发送指令
        sendConfig(str);
        this.result = cmdResult;
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
    public void sendLockState(int vehicleLock,int batteryLock,int straightLock,int basketLock,int spareLock,OnCmdResult cmdResult){
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            cmdResult.CmdResultEvent(false,errorInfo);
            return;
        }
        String str = DebugFlag.SEND_PROTOCOL+"SCT=" + initInfo.pwd + "," + vehicleLock + ","+  batteryLock + ","+  straightLock + ","+  basketLock + ","+  spareLock + "," + getCmdNo();
        // 发送指令
        sendConfig(str);
        this.result = cmdResult;
    }


    /**
     * 开关定速巡航
     * @param cruise
     * 0：关闭车辆定速巡航模式。
     * 1：开启车辆定速巡航模式。
     * @param cmdResult
     */
    public void sendCarCruise(int cruise,OnCmdResult cmdResult){
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            cmdResult.CmdResultEvent(false,errorInfo);
            return;
        }

        String str = DebugFlag.SEND_PROTOCOL+"DSX=" + initInfo.pwd + "," + cruise + "," + getCmdNo();
        // 发送指令
        sendConfig(str);
        this.result = cmdResult;
    }


    /**
     * 开关车辆锁车模式
     * @param mode
     * 0：关闭车辆锁车模式。
     * 1：开启车辆锁车模式。
     * @param cmdResult
     */
    public void sendCarLockMode(int mode,OnCmdResult cmdResult){
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            cmdResult.CmdResultEvent(false,errorInfo);
            return;
        }

        String str = DebugFlag.SEND_PROTOCOL+"SCM=" + initInfo.pwd + "," + mode + "," + getCmdNo();
        // 发送指令
        sendConfig(str);
        this.result = cmdResult;
    }


    /**
     * 修改开关机模式
     * @param mode
     * 0：开关机模式修改为芯片休眠/唤醒模式，蓝牙广播不停止
     * 1：开关机模式修改为芯片掉电/上电模式，蓝牙广播停止
     * @param cmdResult
     */
    public void sendOpenCar(int mode,OnCmdResult cmdResult){
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            cmdResult.CmdResultEvent(false,errorInfo);
            return;
        }

        String str = DebugFlag.SEND_PROTOCOL+"ONF=" + initInfo.pwd + "," + mode + "," + getCmdNo();
        // 发送指令
        sendConfig(str);
        this.result = cmdResult;
    }



    /**
     * 切换车辆启动模式
     * @param mode
     * 0：车辆助力启动模式。
     * 1：车辆无助力启动模式。
     * @param cmdResult
     */
    public void sendCarOpenMode(int mode,OnCmdResult cmdResult){
        if(StringUtils.isEmpty(initInfo.pwd)){
            errorInfo.errorCode = 115;
            errorInfo.errorDescription = "密码为空";
            cmdResult.CmdResultEvent(false,errorInfo);
            return;
        }

        String str = DebugFlag.SEND_PROTOCOL+"SUM=" + initInfo.pwd + "," + mode + "," + getCmdNo();
        // 发送指令
        sendConfig(str);
        this.result = cmdResult;
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
            logi("发送成功：" + str);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorCode = 117;
                    errorInfo.errorDescription = "数据发送失败";
                    result.CmdResultEvent(false,errorInfo);
                }
            });
        }
    }

    /**
     * 报告信道
     * @param cmdReport
     */
    public void getReportConfigRev(OnCmdReport cmdReport){
        this.report = cmdReport;
    }

    /**
     * 滑板车错误码信息
     * @param errorCode
     */
    public void getErrorCode(OnCmdErrorCode errorCode){
        this.code = errorCode;
    }

    // 下载蓝牙地址
    private String bleUrl;
    // 蓝牙下载包大小
    private long bleSize;
    // 下载控制器地址
    private String controlUrl;
    // 控制器下载包大小
    private long controlSize;

    /**
     * 更新蓝牙
     * @param back
     */
    public void updateVersion(final OnUpdateVersion back) {
        HttpManager.requestUpdateInfo("1", "1", new OnApkUpdateBack() {
            @Override
            public void backFailed(int code, String errMsg) {
                errorInfo.errorCode = 104;
                errorInfo.errorDescription = errMsg;
                back.updateFail(errorInfo);
            }

            @Override
            public void backSuccess() {
                back.updateFail("暂无更新");
            }

            @Override
            public void backSuccess(int sign, int versionCode, String versionName, String url, String content, long size) {

                UpdateInfo info = new UpdateInfo();
                info.sign = sign;
                info.versionCode = versionCode;
                info.apkSize = size;
                info.updateContent = content;
                back.updateVersion(info);
                if(sign == 0){
                    bleUrl = url;
                    bleSize = size;
                }else {
                    controlUrl = url;
                    controlSize = size;
                }
            }
        });
    }


    // 升级返回结果接口
    private OnUpdateResult updateResult;

    private LoadManager loadManager;    // 下载管理器
    private FileInfo mFileInfo;         // 需要下载的文件

    /**
     * 升级包
     * @param sign
     * @param result
     */
    public void updateAction(int sign, OnUpdateResult result){
        // 下载升级包
        if(sign == 0){
            downLoadBleUrl();
        }else {
            downLoadControlUrl();
        }
        this.updateResult = result;
    }

    /**
     * 下载蓝牙升级包
     */
    private void downLoadBleUrl() {
        mFileInfo = new FileInfo();
        String name = null;
        if (loadManager == null) {
            loadManager = LoadManager.getInstance(context);
        }

        if(StringUtils.isEmpty(bleUrl)){
            updateResult.updateResult(false,"请先检测新版本");
        }

        mFileInfo.url = bleUrl;
        mFileInfo.size = bleSize;
        String[] buff = bleUrl.split("/");
        if (buff.length > 0) {
            name = buff[buff.length - 1];
            if (StringUtils.isEmpty(name) || !name.endsWith(".zip")) {
                name = null;
            }
        }
        if (StringUtils.isEmpty(name)) {
            name = "base.zip";
        }

        mFileInfo.localPath = FileManager.UPGRADE_DIR + name;

        // 下载监听
        loadManager.startLoad(mFileInfo, new OnUpdateListener() {
            @Override
            public void updateListener(int code) {
                if(code == OnLoadEvent.LOAD_FINISH){
                    updateResult.updateResult(true,"下载完成");
                    logi("下载完成");
                    // 更新蓝牙
                    updateBle(mFileInfo.localPath);
                }else if(code == OnLoadEvent.LOAD_FAILED){
                    errorInfo.errorCode = 105;
                    errorInfo.errorDescription = "下载蓝牙固件包失败";
                    updateResult.updateResult(false,errorInfo);
                }else if(code == OnLoadEvent.LOAD_INIT_FAILED){
                    errorInfo.errorCode = 105;
                    errorInfo.errorDescription = "DFU升级包下载失败";
                    updateResult.updateResult(false,errorInfo);
                }
            }
        });
    }

    /**
     * 下载控制器升级包
     */
    public void downLoadControlUrl(){
        mFileInfo = new FileInfo();
        String name = null;
        if (loadManager == null) {
            loadManager = LoadManager.getInstance(context);
        }
        mFileInfo.url = controlUrl;
        mFileInfo.size = controlSize;
        String[] buff = controlUrl.split("/");
        if (buff.length > 0) {
            name = buff[buff.length - 1];
            if (StringUtils.isEmpty(name) || !name.endsWith(".bin")) {
                name = null;
            }
        }
        if (StringUtils.isEmpty(name)) {
            name = "base.bin";
        }

        mFileInfo.localPath = FileManager.UPGRADE_DIR + name;
        logi(mFileInfo.localPath);
        // 下载监听
        loadManager.startLoad(mFileInfo, new OnUpdateListener() {
            @Override
            public void updateListener(int code) {
                if(code == OnLoadEvent.LOAD_FINISH){
                    updateResult.updateResult(true,"下载完成");
                    // 更新控制器
                    upgradeActionEvent(UpgradeAction.ACTION_START, UpgradeAction.TYPE_CONTROL,mFileInfo.localPath);
                }else if(code == OnLoadEvent.LOAD_FAILED){
                    errorInfo.errorCode = 106;
                    errorInfo.errorDescription = "硬件升级包下载失败";
                    updateResult.updateResult(false,errorInfo);
                }else if(code == OnLoadEvent.LOAD_INIT_FAILED){
                    errorInfo.errorCode = 106;
                    errorInfo.errorDescription = "硬件升级包下载失败";
                    updateResult.updateResult(false,errorInfo);
                }
            }
        });
    }


    /**
     * 本地升级蓝牙
     * @param localPath
     * @param result
     */
    public void upGradeBle(int sign,String localPath,OnUpdateResult result){
        if(sign == 0) {
            updateBle(localPath);
        }else {
            upgradeActionEvent(UpgradeAction.ACTION_START, UpgradeAction.TYPE_CONTROL,localPath);
        }
        this.updateResult = result;
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
        disConnState.disConnectState(false);
    }

    /**
     * 设备连接失败
     */
    @Override
    protected void deviceConnectFailed() {
        connState.connectState(false,"连接失败");
    }

    /**
     * 设备断开连接
     */
    @Override
    protected void deviceDisconnect() {
        disConnState.disConnectState(true);
    }

    /**
     * 设备连接蓝牙
     */
    @Override
    protected void deviceConnected() {
        connState.connectState(true,"连接成功");
    }

    /**
     *  设备连接超时
     */
    @Override
    protected void deviceConnectTimeOut() {
        connState.connectState(false,"连接超时");
    }


    /**
     * 发现新的设备
     * @param device 蓝牙扫描到的新设备
     */
    @Override
    protected void discoverNewDevice(OkaiBleDevice device) {
        scanState.onScanSuccess(device);
    }

    /**
     * 发现具体设备
     * @param device 具体的设备信息
     */
    @Override
    protected void discoverAimDevice(OkaiBleDevice device) {
        scanState.onScanSuccess(device);
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
        carRunState = new CarRunReport();
        if (channel.equals(BLEUUIDs.COMMON_CHANNEL.toLowerCase())) {
            if (data.startsWith("+ACK")) {
                res = ERR;
                int code = dealAckData(data);
                if (code != 3) {
                    if(DebugFlag.IS_DEBUG) {
                        logi("收到的数据：" + data);
                        final String finalData = data;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if(cmdData != null) {
                                    cmdData.cmdData(finalData);
                                }
                            }
                        });
                    }
                } else {
                    dealUpgrade(data);
                    if(DebugFlag.IS_DEBUG) {
//                        logi("收到的数据：" + data);
                        final String finalData = data;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if(cmdData != null) {
                                    cmdData.cmdData(finalData);
                                }
                            }
                        });
                    }
                }
            } else {
                dealReportData(data);
                if(DebugFlag.IS_DEBUG) {
                    logi("收到的数据：" + data);
                    final String finalData = data;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if(cmdData != null) {
                                cmdData.cmdData(finalData);
                            }
                        }
                    });
                }
            }
        } else if (channel.equals(BLEUUIDs.REPORT_CHANNEL.toLowerCase())) {
            dealReportData(data);
            if(DebugFlag.IS_DEBUG) {
                logi("收到广播数据：" + data);
                final String finalData = data;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(cmdData != null) {
                            cmdData.cmdData(finalData);
                        }
                    }
                });
            }
        } else if (channel.equals(BLEUUIDs.UPGRADE_CHANNEL.toLowerCase())) {
            if(DebugFlag.IS_DEBUG) {
                logi("收到更新数据：" + data);
                final String finalData = data;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(cmdData != null) {
                            cmdData.cmdData(finalData);
                        }
                    }
                });
            }
            if (data.startsWith("+ACK")) {
                dealUpgrade(data);
            }
        } else if (channel.equals(BLEUUIDs.CONFIG_CHANNEL.toLowerCase())) {
            if(DebugFlag.IS_DEBUG) {
                logi("收到更新固件数据：" + data);
                final String finalData = data;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(cmdData != null) {
                            cmdData.cmdData(finalData);
                        }
                    }
                });
            }
        } else {
            if(DebugFlag.IS_DEBUG) {
                logi("收到其他数据：" + data + " channel:" + channel);
            }
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
     * 0：表示数据正常，处理陈宫
     * 1、表示数据格式不对
     * 2、数据结果长度不对
     * 3、非可识别指令
     * 4、数据的结果不对
     *
     * @param data 当前需要处理的字符串
     * @return 处理结果
     */
    private int dealAckData(String data) {
        data = data.replace(DebugFlag.ACK_PROTOCOL, "").replace("\r\n", "").replace("$", "");
        String[] buff = data.split("=");
        if (buff.length < 2) {
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
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 2;
                }

                /*if (buff[0].equals("0")) {
                    errMsg = "处理失败";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "处理失败";
                            errorInfo.errorCode = 102;
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 4;
                }*/
                if (buff[0].equals("0")) {
                    errMsg = "应答失败";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "应答失败";
                            errorInfo.errorCode = 103;
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 4;
                }

                setCmdNo(buff[1]);
                cmd = 1;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        result.CmdResultEvent(true,null);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 4;
                }
                if (!buff[1].equals(initInfo.typeName)) {
                    errMsg = "车型名称比对失败";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "车型名称比对失败";
                            errorInfo.errorCode = 119;
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
                cmd = 2;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        result.CmdResultEvent(true,null);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[1]);
                cmd = 3;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        result.CmdResultEvent(true,null);
                    }
                });
                break;
            case "LFC":
                if(buff.length != 15){
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "收到数据格式不对";
                            errorInfo.errorCode = 103;
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 2;
                }

                /*if(Integer.parseInt(buff[0]) != CarConfig.brakeSelect){
                    errMsg = "单双刹选择错误";
                    return 4;
                }*/
                if(Integer.parseInt(buff[1]) != initInfo.showMode){
                    errMsg = "速度单位选择错误";
                    return 4;
                }
                /*if(Integer.parseInt(buff[2]) != CarConfig.gear1){
                    errMsg = "档位1错误";
                    return 4;
                }
                if(Integer.parseInt(buff[3]) != CarConfig.gear2){
                    errMsg = "档位2错误";
                    return 4;
                }
                if(Integer.parseInt(buff[4]) != CarConfig.gear3){
                    errMsg = "档位3错误";
                    return 4;
                }
                if(Integer.parseInt(buff[5]) != CarConfig.gear4){
                    errMsg = "档位4错误";
                    return 4;
                }
                if(Integer.parseInt(buff[6]) != CarConfig.batteryType){
                    errMsg = "电池保护板类型错误";
                    return 4;
                }
                if(Integer.parseInt(buff[7]) != CarConfig.electronicBrakeSelect){
                    errMsg = "电子刹车力度档位选择错误";
                    return 4;
                }
                if(Integer.parseInt(buff[8]) != CarConfig.openCarLedMode){
                    errMsg = "开机车大灯模式错误";
                    return 4;
                }
                if(Integer.parseInt(buff[9]) != CarConfig.taillightMode){
                    errMsg = "正常模式下尾灯模式错误";
                    return 4;
                }
                if(!buff[10].equals(CarConfig.salesLocationCode)){
                    errMsg = "车辆销售地编码错误";
                    return 4;
                }
                if(!buff[11].equals(CarConfig.customerCode)){
                    errMsg = "客户编码错误";
                    return 4;
                }*/
                if(!buff[12].equals(initInfo.typeName)){
                    errMsg = "车辆型号错误";
                    return 4;
                }
                /*if(Integer.parseInt(buff[13]) != YBConfig.bleModel){
                    revResult.errMsg = "蓝牙缩写错误";
                    return 4;
                }*/
                setCmdNo(buff[14]);
                cmd = 16;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        result.CmdResultEvent(true,null);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
                cmd = 4;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        result.CmdResultEvent(true,null);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[1]);
                cmd = 5;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        result.CmdResultEvent(true,null);
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
                            lockState.CmdLockState(errorInfo,null);
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
                            lockState.CmdLockState(errorInfo,null);
                        }
                    });
                    return 4;
                }

                setCmdNo(buff[6]);
                cmd = 6;

                final LockStateInfo info = new LockStateInfo();
                info.vehicleLock = Integer.parseInt(buff[1]);
                info.batteryLock = Integer.parseInt(buff[2]);
                info.straightLock = Integer.parseInt(buff[3]);
                info.basketLock = Integer.parseInt(buff[4]);
                info.spareLock = Integer.parseInt(buff[5]);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        lockState.CmdLockState(null,info);
                    }
                });
                break;
            case "ECP":   // 骑行参数配置
                if (buff.length != 3) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "收到数据格式不对";
                            errorInfo.errorCode = 103;
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
                cmd = 7;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        result.CmdResultEvent(true,null);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
                cmd = 8;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        result.CmdResultEvent(true,null);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 4;
                }

                setCmdNo(buff[2]);
                cmd = 9;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        result.CmdResultEvent(true,null);
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
                            infoResult.CmdInitInfo(errorInfo,null);
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
                            infoResult.CmdInitInfo(errorInfo,null);
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
                                    infoResult.CmdInitInfo(errorInfo,null);
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
                                    infoResult.CmdInitInfo(errorInfo,null);
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
                                    infoResult.CmdInitInfo(errorInfo,null);
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
                                    infoResult.CmdInitInfo(errorInfo,null);
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
                                    infoResult.CmdInitInfo(errorInfo,null);
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
                        initInfo.typeName = buff[i++];
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
                                    infoResult.CmdInitInfo(errorInfo,null);
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
                                    infoResult.CmdInitInfo(errorInfo,null);
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
                                    infoResult.CmdInitInfo(errorInfo,null);
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
                                    infoResult.CmdInitInfo(errorInfo,null);
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
                                    infoResult.CmdInitInfo(errorInfo,null);
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
                                    infoResult.CmdInitInfo(errorInfo,null);
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
                                infoResult.CmdInitInfo(errorInfo,null);
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
                        infoResult.CmdInitInfo(null,initInfo);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
                cmd = 11;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        result.CmdResultEvent(true,null);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
                cmd = 12;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        result.CmdResultEvent(true,null);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
               cmd = 13;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        result.CmdResultEvent(true,null);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
                cmd = 14;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        result.CmdResultEvent(true,null);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
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
                            result.CmdResultEvent(false,errorInfo);
                        }
                    });
                    return 4;
                }
                setCmdNo(buff[2]);
                cmd = 15;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        result.CmdResultEvent(true,null);
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
                    result.CmdResultEvent(false,errorInfo);
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
        data = data.replace(DebugFlag.RESP_PROTOCOL, "").replace("\r\n", "").replace("$", "");
        String[] buff = data.split("=");
        if (buff.length < 2) {
            return 1;
        }
        String type = buff[0];
        data = buff[1];
        buff = data.split(",");
        switch (type) {
            case "INF":   // 蓝牙参数配置上报
                if (buff.length < 23) {
                    errMsg = "收到数据格式不对";
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "收到数据格式不对";
                            errorInfo.errorCode = 103;
                            report.CmdReportEvent(errorInfo,null);
                        }
                    });
                    return 2;
                }

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
                            if(buff.length > 23) {
                                carRunState.ledState = Integer.parseInt(buff[i++]);
                                carRunState.addMode = Integer.parseInt(buff[i++]);
                            }
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
                            if(buff.length > 23) {
                                carRunState.cycleIndex = Integer.parseInt(buff[i++]);
                            }
                        } else if (buff[i].equals("Meter")) {
                            i++;
                            carRunState.accelerateAD = Integer.parseInt(buff[i++]);
                            carRunState.brakeADLeft = Integer.parseInt(buff[i++]);
                            carRunState.brakeADRight = Integer.parseInt(buff[i++]);
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
                                    report.CmdReportEvent(errorInfo,null);
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
                            report.CmdReportEvent(errorInfo,null);
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
                if (buff.length != 2) {
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
        }
        return 0;
    }

    /**
     * 处理升级更新数据
     * @param data
     */
    private void dealUpgrade(String data) {
        loge("收到升级指令：" + data);
        data = data.replace(DebugFlag.ACK_PROTOCOL, "").replace("\r\n", "").replace("$", "");
        String[] buff = data.split("=");
        if (buff.length < 2) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorDescription = "收到数据格式不对";
                    errorInfo.errorCode = 103;
                    updateResult.updateResult(false,errorInfo);
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
                                    updateResult.updateResult(false,errorInfo);
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
                                    updateResult.updateResult(true,"发送下一个包：" + getUpgradeProgress());
                                }
                            });
                            break;
                        case 2: // 更新，重发当前包
                            code = UpgradeResult.UPDATE;
                            msg = "重发当前包";
                            sendUpgradeData(false);
                            percent = getUpgradeProgress();
                            logi("重发当前包：" + percent);
                            break;
                        case 3: // 更新，重新发送
                            code = UpgradeResult.UPDATE;
                            msg = "固件丢失，重新发送";
                            sendStartData();
                            percent = getUpgradeProgress();
                            logi("固件丢失，重新发送：" + percent);
                            break;
                        case 4: // 失败，程序升级失败
                            code = UpgradeResult.FAILED;
                            msg = "程序升级失败";
                            stopUpgrade();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    errorInfo.errorDescription = "硬件升级失败";
                                    errorInfo.errorCode = 109;
                                    updateResult.updateResult(false,errorInfo);
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
                                    updateResult.updateResult(true,"升级准备就绪：" + getUpgradeProgress());
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
                                    updateResult.updateResult(false,errorInfo);
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
                                    updateResult.updateResult(true, "机器正常开机");
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
                                    updateResult.updateResult(true, "硬件升级成功");
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
//        if (upgradeFrame != null && isUpgrade) {
//            updateResult.updateResult(true,"正在升级，请勿重复点击");
//            return;
//        }
        if (upgradeFrame == null) {
            upgradeFrame = new UpgradeFrame(type, path);
        }
        upgradeFrame.setOnUpgradeListener(new UpgradeFrame.OnUpgradeListener() {
            @Override
            public void failed(final String msg) {
                stopUpgrade();
                errorInfo.errorDescription = "升级被暂停";
                errorInfo.errorCode = 107;
                updateResult.updateResult(false,errorInfo);
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
            updateResult.updateResult(false,errorInfo);
            return;
        }
        if(upgradeFrame.getPackNum() == 0){
            errorInfo.errorCode = 114;
            errorInfo.errorDescription = "控制器固件升级失败，数据打包错误0";
            updateResult.updateResult(false,errorInfo);
            return;
        }
        String cmdStr = DebugFlag.SEND_PROTOCOL+"URD=" + initInfo.pwd + "," + code + "," +  upgradeFrame.getPackNum() + "," + getCmdNo() + "$\r\n";
        logi("发送固件升级指令:" + cmdStr);
        isUpgrade = true;
        boolean isSend = sendCmdStr(upgradeChannel, cmdStr);
        if (!isSend) {
            stopUpgrade();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorDescription = "数据发送失败";
                    errorInfo.errorCode = 117;
                    updateResult.updateResult(false,errorInfo);
                }
            });
            logi("数据发送失败");
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
//                String str = DebugFlag.SEND_PROTOCOL+"LOC=" + initInfo.pwd + "," + getCmdNo();
//                boolean isSend = sendCmdStr(upgradeChannel, str);
                boolean isSend = sendSCmd(upgradeChannel, data);
                if (!isSend) {
                    stopUpgrade();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            errorInfo.errorDescription = "控制器固件升级失败，数据发送失败";
                            errorInfo.errorCode = 121;
                            updateResult.updateResult(false,errorInfo);
                        }
                    });
                    logi("控制器固件升级失败，数据发送失败");
                }
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        errorInfo.errorDescription = "升级失败，升级数据包为空";
                        errorInfo.errorCode = 109;
                        updateResult.updateResult(false,errorInfo);
                    }
                });
                logi("数据位空");
            }
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorDescription = "控制器固件升级停止";
                    errorInfo.errorCode = 122;
                    updateResult.updateResult(false,errorInfo);
                }
            });
            logi("发送失败");
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
                        updateResult.updateResult(false,errorInfo);
                    }
                });
                logi("数据位空");
            }
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    errorInfo.errorDescription = "控制器固件升级停止";
                    errorInfo.errorCode = 122;
                    updateResult.updateResult(false,errorInfo);
                }
            });
            logi("发送失败");
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
        String pathDir = FileManager.UPGRADE_DIR;
        File fileDir = new File(pathDir);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
            return;
        }
        File file = new File(localPath);
        if (!file.exists()) {
            errorInfo.errorCode = 112;
            errorInfo.errorDescription = "本地文件不存在";
            updateResult.updateResult(false,errorInfo);
            return;
        }
        if (conDevice == null) {
            errorInfo.errorCode = 111;
            errorInfo.errorDescription = "Device not connect";
            updateResult.updateResult(false,errorInfo);
            return;
        }
        // 创建dfu通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DfuServiceInitiator.createDfuNotificationChannel(context);
        }
        dfuServiceInitiator = new DfuServiceInitiator(conDevice.getAddress())
                .setDisableNotification(true)
                .setZip(localPath);
        dfuServiceInitiator.start((context), DfuService.class);

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
    }

    private final DfuProgressListener mDfuProgressListener = new DfuProgressListener() {
        @Override
        public void onDeviceConnecting(String deviceAddress) {
//            Log.i(TAG,"连接中");
            updateResult.updateResult(true,"连接设备，准备升级");
        }

        @Override
        public void onDeviceConnected(String deviceAddress) {
//            Log.i(TAG,"已连接");
            updateResult.updateResult(true,"设备已连接，准备升级");
        }

        @Override
        public void onDfuProcessStarting(String deviceAddress) {
//            tvResult.append("开始升级1\n");
            updateResult.updateResult(true,"开始升级1");
        }

        @Override
        public void onDfuProcessStarted(String deviceAddress) {
//            tvResult.append("开始升级2\n");
            updateResult.updateResult(true,"开始升级");
        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
//            Log.i(TAG,"使能dfu");
            logi("使能dfu");
            updateResult.updateResult(true,"使能 dfu");
        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            updateResult.updateResult(true,"升级进度："+percent);
        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
//            tvResult.append("警告\n");
            logi("警告");
        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
//            Log.i(TAG,"正在断开连接");
            logi("正在断开连接");
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
//            Log.i(TAG,"连接断开");
            logi("连接断开");
        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            stopDfu();
            //升级成功，重新连接设备
            updateResult.updateResult(true,"升级成功,请重新连接设备");
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            //升级流产，失败
            stopDfu();
            errorInfo.errorCode = 110;
            errorInfo.errorDescription = "升级流产，请重新升级。";
            updateResult.updateResult(false,errorInfo);
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            stopDfu();
            LogUtils.i(TAG,"升级失败，请重新升级:" + error + "," + message);
            errorInfo.errorCode = 113;
            errorInfo.errorDescription = "升级失败，请重新升级。"+ message;
            updateResult.updateResult(false,errorInfo);
        }
    };
}
