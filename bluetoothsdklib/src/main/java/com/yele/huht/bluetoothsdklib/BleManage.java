package com.yele.huht.bluetoothsdklib;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.yele.baseapp.utils.LogUtils;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdData;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdErrorCode;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdInitInfoResult;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdLockState;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdReport;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdResult;
import com.yele.huht.bluetoothsdklib.callBcak.OnConnectDevState;
import com.yele.huht.bluetoothsdklib.callBcak.OnDisConnectDevState;
import com.yele.huht.bluetoothsdklib.callBcak.OnScanDevState;
import com.yele.huht.bluetoothsdklib.callBcak.OnUpdateResult;
import com.yele.huht.bluetoothsdklib.callBcak.OnUpdateVersion;
import com.yele.huht.bluetoothsdklib.data.LockStateEnum;
import com.yele.huht.bluetoothsdklib.data.StateEnum;
import com.yele.huht.bluetoothsdklib.service.BleService;

public class BleManage {

    private static BleManage bleManager;


    public static void init(Context context) {
        if(context == null){
            LogUtils.i("ble","context is null!");
            throw new IllegalArgumentException("context is null!");
        }else {
            if (bleManager == null) {
                synchronized (BleManage.class) {
                    if (bleManager == null) {
                        bleManager = new BleManage(context);
                    }
                }
            }
        }
    }

    private Context mContext;

    private BleManage(Context context) {
        this.mContext = context;
    }

    public static BleManage getInstance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        return bleManager;
    }

    /**
     * 开始扫描
     * @param device 需要进行扫描的目标蓝牙设备名称
     */
    public void deviceStartScan(String device, OnScanDevState state){
        BleService.getBleControl(mContext).deviceStartScan(device,state);
    }

    /**
     * 停止扫描
     */
    public void deviceStopScan(){
        BleService.getBleControl(mContext).deviceStopScan();
    }

    /**
     * 蓝牙连接
     * @param id  设备id
     * @param state
     */
    public void deviceConnect(int id, OnConnectDevState state){
        BleService.getBleControl(mContext).deviceConnect(id,state);
    }

    /**
     * 停止连接/断开连接
     */
    public void deviceDisConnect(OnDisConnectDevState state){
        BleService.getBleControl(mContext).deviceDisConnect(state);
    }

    /**
     * 读取配置信息/初始化
     * @param pwd 初始蓝牙密码
     * @param result
     */
    public void setReadConfig(String pwd, OnCmdInitInfoResult result){
        BleService.getBleControl(mContext).sendReadConfig(pwd,result);
    }

    /**
     * 车辆参数配置
     * @param sn 车辆SN
     * @param name 车型名称
     */
    public void setConfigCar(String sn, String name, OnCmdResult result){
        BleService.getBleControl(mContext).sendConfigCar(sn,name,result);
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
    public void setConfigBle(String pwd,String sn,int broadcastSpace,int broadcastTime,int minSpace,int maxSpace,OnCmdResult result){
        BleService.getBleControl(mContext).sendConfigBle(pwd,sn,broadcastSpace,broadcastTime,minSpace,maxSpace,result);
    }

    /**
     * 成品下发测试
     * 测试标志 0：取消/停止成品测试，1：开始成品测试
     */
    public void setTestProduct(StateEnum stateEnum, OnCmdResult result){
        BleService.getBleControl(mContext).sendTestProduct(stateEnum,result);
    }

    /**
     * 出厂车辆参数配置
     *
     */
    public void setOutConfig(){

    }


    /**
     * 骑行参数
     * @param maxSpeed 当前车辆允许的最大速度 0~63
     * @param speedMode 车辆的加速模式 0：柔和模式，1：运动模式
     * @param showModel 仪表的显示模式 0：YD,1：KM
     * @param reportSpace 上报时间间隔，默认 100ms,最大9999ms,0不上报
     * @param time 待机关机时间，默认 30s,0不自动关机
     */
    public void setNormalBikeConfig(int maxSpeed,int speedMode,int showModel,int reportSpace,int time,OnCmdResult result){
        BleService.getBleControl(mContext).sendNormalBikeConfig(maxSpeed,speedMode,showModel,reportSpace,time,result);
    }


    /**
     * 切换模式
     * @param mode 0：正常模式，1：测试模式，2：恢复出厂设置
     * @param result
     */
    public void setNormalChangeMode(int mode,OnCmdResult result){
        BleService.getBleControl(mContext).sendNormalChangeMode(mode,result);
    }


    /**
     * 寻车
     * @param result
     */
    public void setSearchCar(OnCmdResult result){
        BleService.getBleControl(mContext).sendSearchCar(result);
    }


    /**
     * 车辆锁开关
     * @param state 设置开/关/不支持设备
     * @param lockState
     */
    public void setAllLockCar(LockStateEnum state, OnCmdLockState lockState){
        BleService.getBleControl(mContext).sendAllLock(state,lockState);
    }


    /**
     * LED控制
     * @param ledStatus 0：关闭，1：打开
     * @param result
     */
    public void setLedControl(int ledStatus,OnCmdResult result){
        BleService.getBleControl(mContext).sendLed(ledStatus,result);
    }

    /**
     * 修改蓝牙密码
     * @param oldPwd 原来的密码
     * @param newPwd 新的蓝牙密码
     * @param result
     */
    public void setChangePassword(String oldPwd,String newPwd,String vehicleSN,OnCmdResult result){
        BleService.getBleControl(mContext).sendChangePwd(oldPwd,newPwd,vehicleSN,result);
    }

    /**
     * 修改蓝牙名称
     * @param newName 新的蓝牙名称
     * @param result
     */
    public void setChangeBleName(String newName,OnCmdResult result){
        BleService.getBleControl(mContext).sendChangeBleName(newName,result);
    }

    /**
     * 各个锁的开关
     * @param vehicleLock 车辆锁
     * @param batteryLock 电池锁
     * @param straightLock 直杆锁
     * @param basketLock 篮筐锁
     * @param spareLock 备用锁
     * @param result
     */
    public void setLockState(int vehicleLock,int batteryLock,int straightLock,int basketLock,int spareLock,OnCmdResult result){
        BleService.getBleControl(mContext).sendLockState(vehicleLock,batteryLock,straightLock,basketLock,spareLock,result);
    }

    /**
     * 更新
     */
    private void setUpdateVersion(OnUpdateVersion back){
        BleService.getBleControl(mContext).updateVersion(back);
    }

    /**
     * 本地升级蓝牙
     * @param localPath
     * @param result
     */
    public void setUpgrade(int sign, String localPath, OnUpdateResult result){
        BleService.getBleControl(mContext).upGradeBle(sign,localPath,result);
    }

    /**
     * 升级
     * @param sign 升级标识 标识对蓝牙/控制器进行升级 0：蓝牙，1：控制器
     * @param result
     */
    private void setUpgrade(int sign,OnUpdateResult result){
        BleService.getBleControl(mContext).updateAction(sign,result);
    }

    /**
     * 报告信道
     * @param report
     */
    public void getReportConfigRev(OnCmdReport report){
        BleService.getBleControl(mContext).getReportConfigRev(report);
    }

    /**
     * 错误码信息
     * @param cmdErrorCode
     */
    public void getErrorCodeInfo(OnCmdErrorCode cmdErrorCode){
        BleService.getBleControl(mContext).getErrorCode(cmdErrorCode);
    }

    /**
     * 测试，显示返回数据
     * @param data
     */
    public void getCmdConfig(OnCmdData data){
        BleService.getBleControl(mContext).getCmdConfig(data);
    }


    /**
     * 开关定速巡航
     * @param mode
     * 0：关闭车辆定速巡航模式。
     * 1：开启车辆定速巡航模式。
     * @param result
     */
    public void setCarCruise(int mode,OnCmdResult result){
        BleService.getBleControl(mContext).sendCarCruise(mode,result);
    }

    /**
     * 开关车辆锁车模式
     * @param mode
     * 0：关闭车辆锁车模式。
     * 1：开启车辆锁车模式。
     * @param result
     */
    public void setCarLockMode(int mode,OnCmdResult result){
        BleService.getBleControl(mContext).sendCarLockMode(mode,result);
    }

    /**
     * 切换车辆启动模式
     * @param mode
     * 0：车辆助力启动模式。
     * 1：车辆无助力启动模式。
     * @param result
     */
    public void setCarOpenMode(int mode,OnCmdResult result){
        BleService.getBleControl(mContext).sendCarOpenMode(mode,result);
    }

    /**
     * 修改开关机模式
     * @param mode
     * 0：开关机模式修改为芯片休眠/唤醒模式，蓝牙广播不停止
     * 1：开关机模式修改为芯片掉电/上电模式，蓝牙广播停止
     * @param result
     */
    public void setOpenCar(int mode,OnCmdResult result){
        BleService.getBleControl(mContext).sendOpenCar(mode,result);
    }





}
