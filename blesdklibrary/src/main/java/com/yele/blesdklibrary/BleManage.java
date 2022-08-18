package com.yele.blesdklibrary;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.yele.blesdklibrary.ble.BleControl;
import com.yele.blesdklibrary.data.LockStateEnum;
import com.yele.blesdklibrary.data.StateEnum;
import com.yele.blesdklibrary.data.SwitchStatusEnum;
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
import com.yele.blesdklibrary.util.CarCodeUtils;
import com.yele.blesdklibrary.util.LogUtils;

public class BleManage {

    private static BleManage bleManager;

    /**
     * 初始化定义
     * @param context
     * @param isShowLog  判断是否显示打印日志
     */
    public static void init(Context context,boolean isShowLog) {
        if (bleManager == null) {
            synchronized (BleManage.class) {
                if (bleManager == null) {
                    bleManager = new BleManage(context);
                }
            }
        }
        LogUtils.isPrint = isShowLog;
    }

    public static void init(Context context) {
       init(context,false);
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
     * 是否显示打印日志
     * @param isBleLog
     */
    /*public void setBleLog(boolean isBleLog){
        LogUtils.isPrint = isBleLog;
    }*/


    /**
     * 查询车辆的设备信息是否有权限
     * @param devType 车型
     * @param devSn 车辆SN
     * @param back 返回
     */
    public void setQueryDevicePermission(String devType,String devSn, OnDevicePermissionBack back){
        if(back != null){
            if (!CarCodeUtils.switchCarType(devType)) {
                back.resultQuery(false);
                return;
            }

            if(!CarCodeUtils.switchCarCode(devSn)){
                back.resultQuery(false);
                return;
            }

            back.resultQuery(true);
        }
        //BleControl.getBleControl(mContext).queryDevicePermission(device,back);
    }

    /**
     * 开始扫描
     * @param deviceSN 需要进行扫描的目标蓝牙设备名称
     *                 如果不设置值，就是正常的扫描功能
     */
    public void setStartScan(String deviceSN, OnScanDevStateBack state){
        BleControl.getBleControl(mContext).deviceStartScan(deviceSN,state);
    }

    /**
     * 停止扫描
     */
    public void setStopScan(){
        BleControl.getBleControl(mContext).deviceStopScan();
    }

    /**
     * 蓝牙连接
     * @param id  列表id名称
     * @param state
     */
    public void setDeviceConnect(int id, OnConnectDevStateBack state){
        BleControl.getBleControl(mContext).deviceConnect(id,state);
    }

    /**
     * 停止连接/断开连接
     */
    public void setDeviceDisconnect(){
        BleControl.getBleControl(mContext).deviceDisConnect();
    }

    /**
     * 设置车辆的密码
     * @param pwd 蓝牙的通讯密码
     */
    public void setPassword(String pwd) {
        BleControl.getBleControl(mContext).setPassword(pwd);
    }
    /**
     * 读取配置信息/初始化
     * @param result
     */
    public void setReadConfig(OnCmdInitInfoResultBack result){
        BleControl.getBleControl(mContext).sendReadConfig(result);
    }

    /**
     * 车辆参数配置
     * @param sn 车辆SN
     * @param name 车型名称
     */
    private void setCarConfig(String sn, String name, OnCmdResultBack result){
        BleControl.getBleControl(mContext).sendConfigCar(sn,name,result);
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
    private void setBleConfig(String pwd, String sn, int broadcastSpace, int broadcastTime, int minSpace, int maxSpace, OnCmdResultBack result){
        BleControl.getBleControl(mContext).sendConfigBle(pwd,sn,broadcastSpace,broadcastTime,minSpace,maxSpace,result);
    }

    /**
     * 成品下发测试
     * 测试标志 0：取消/停止成品测试，1：开始成品测试
     */
    private void setTestProduct(StateEnum stateEnum, OnCmdResultBack result){
        BleControl.getBleControl(mContext).sendTestProduct(stateEnum,result);
    }


    /**
     * 骑行参数
     * @param maxSpeed 当前车辆允许的最大速度 0~63
     * @param speedMode 车辆的加速模式 0：柔和模式，1：运动模式
     * @param showModel 仪表的显示模式 0：YD,1：KM
     * @param reportSpace 上报时间间隔，默认 100ms,最大9999ms,0不上报
     * @param time 待机关机时间，默认 30s,0不自动关机
     */
    public void setRideConfig(int maxSpeed, int speedMode, int showModel, int reportSpace, int time, OnCmdResultBack result){
        BleControl.getBleControl(mContext).sendNormalBikeConfig(maxSpeed,speedMode,showModel,reportSpace,time,result);
    }


    /**
     * 骑行参数
     * @param gearS S档开关  1：开启S档 0：关闭S档。
     * @param speedMode 车辆的加速模式 0：柔和模式，1：运动模式
     * @param showModel 仪表的显示模式 0：YD,1：KM
     * @param reportSpace 上报时间间隔，默认 100ms,最大9999ms,0不上报
     * @param time 待机关机时间，默认 30s,0不自动关机
     */
    public void setRideConfig(SwitchStatusEnum gearS, int speedMode, int showModel, int reportSpace, int time, OnCmdResultBack result){
        BleControl.getBleControl(mContext).sendGearSBikeConfig(gearS,speedMode,showModel,reportSpace,time,result);
    }


    /**
     * 切换模式
     * @param mode 0：正常模式，1：测试模式，2：恢复出厂设置
     * @param result
     */
    private void setNormalChangeMode(int mode, OnCmdResultBack result){
        BleControl.getBleControl(mContext).sendNormalChangeMode(mode,result);
    }


    /**
     * 寻车
     * @param result
     */
    public void setSearchCar(OnCmdResultBack result){
        BleControl.getBleControl(mContext).sendSearchCar(result);
    }


    /**
     * 车辆开关机
     * @param state 设置开/关/不支持设备
     * @param lockState
     */
    public void setAllLockCar(LockStateEnum state, OnCmdResultBack lockState){
        BleControl.getBleControl(mContext).sendAllLock(state,lockState);
    }


    /**
     * LED控制
     * @param ledStatus 0：关闭，1：打开
     * @param result
     */
    public void setLedControl(int ledStatus, OnCmdResultBack result){
        BleControl.getBleControl(mContext).sendLed(ledStatus,result);
    }

    /**
     * 修改蓝牙密码
     * @param oldPwd 原来的密码
     * @param newPwd 新的蓝牙密码
     * @param result
     */
    private void setChangePassword(String oldPwd, String newPwd, String vehicleSN, OnCmdResultBack result){
        BleControl.getBleControl(mContext).sendChangePwd(oldPwd,newPwd,vehicleSN,result);
    }

    /**
     * 修改蓝牙名称
     * @param newName 新的蓝牙名称
     * @param result
     */
    public void setChangeBleName(String newName, OnCmdResultBack result){
        BleControl.getBleControl(mContext).sendChangeBleName(newName,result);
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
    private void setLockState(int vehicleLock, int batteryLock, int straightLock, int basketLock, int spareLock, OnCmdResultBack result){
        BleControl.getBleControl(mContext).sendLockState(vehicleLock,batteryLock,straightLock,basketLock,spareLock,result);
    }

    /**
     * 本地升级
     * @param localPath
     * @param result
     */
    public void setUpgrade(int sign, String localPath, OnUpdateResultBack result){
        BleControl.getBleControl(mContext).upGradeBle(sign,localPath,result);
    }


    /**
     * 报告信道
     * @param report
     */
    public void getReportConfigRev(OnCmdReportBack report){
        BleControl.getBleControl(mContext).getReportConfigRev(report);
    }

    /**
     * 错误码信息
     * @param cmdErrorCode
     */
    public void getErrorCodeInfo(OnCmdErrorCodeBack cmdErrorCode){
        BleControl.getBleControl(mContext).getErrorCode(cmdErrorCode);
    }

    /**
     * 测试，显示返回数据
     * @param data
     */
    private void getCmdConfig(OnCmdDataBack data){
        BleControl.getBleControl(mContext).getCmdConfig(data);
    }


    /**
     * 开关定速巡航
     * @param mode
     * 0：关闭车辆定速巡航模式。
     * 1：开启车辆定速巡航模式。
     * @param result
     */
    public void setCarCruise(int mode, OnCmdResultBack result){
        BleControl.getBleControl(mContext).sendCarCruise(mode,result);
    }

    /**
     * 开关车辆锁车模式
     * @param mode
     * 0：关闭车辆锁车模式。
     * 1：开启车辆锁车模式。
     * @param result
     */
    public void setCarLockMode(int mode, OnCmdResultBack result){
        BleControl.getBleControl(mContext).sendCarLockMode(mode,result);
    }

    /**
     * 蓝牙连接监听
     * @param back
     */
    public void setBleConnectListener(OnBleConnectBack back) {
        BleControl.getBleControl(mContext).setConnectListener(back);
    }

    /**
     * 切换车辆启动模式
     * @param mode
     * 0：车辆助力启动模式。
     * 1：车辆无助力启动模式。
     * @param result
     */
    public void setCarOpenMode(int mode, OnCmdResultBack result){
        BleControl.getBleControl(mContext).sendCarOpenMode(mode,result);
    }

    /**
     * 修改开关机模式
     * @param mode
     * 0：开关机模式修改为芯片休眠/唤醒模式，蓝牙广播不停止
     * 1：开关机模式修改为芯片掉电/上电模式，蓝牙广播停止
     * @param result
     */
    public void setOpenCar(int mode, OnCmdResultBack result){
        BleControl.getBleControl(mContext).sendOpenCar(mode,result);
    }

    /**
     * 自检模式
     * @param status 自检状态，是否开启自检模式 1：开始自检模式,0：退出自检模式
     * @param position 自检部位
     * @param result 返回
     */
    public void setDeviceCheckMode(int status, int position, OnCmdResultBack result){
        BleControl.getBleControl(mContext).sendDeviceCheckMode(status,position,result);
    }

}
