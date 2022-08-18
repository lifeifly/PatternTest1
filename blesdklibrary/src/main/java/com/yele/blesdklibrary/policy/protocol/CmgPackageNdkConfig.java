package com.yele.blesdklibrary.policy.protocol;

import com.yele.blesdklibrary.data.BindData;
import com.yele.blesdklibrary.util.ByteUtils;

public class CmgPackageNdkConfig implements InitConfigStr {

    private String pwd;
    private String carSn;


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

    static {
        System.loadLibrary("native-lib");
    }

    // 读取配置
    public native static String readConfig(String pwd,String code);
    // 骑行参数配置
    public native static String rideConfig(String pwd,int maxSpeed,int addMode,int showModel,int reportInterval,int time,String code);
    // 寻车
    public native static String findDevice(String pwd,String code);
    // 开关机
    public native static String lockAllDevice(String pwd,String cmd,String code);
    // LED控制
    public native static String ledControl(String pwd,int cmd,String code);
    // 修改蓝牙名称
    public native static String modifyBleName(String pwd,String name,String code);
    // 修改开关机模式
    public native static String modifyOpenCar(String pwd,int cmd,String code);
    // 开关定速巡航模式
    public native static String deviceCruise(String pwd,int cmd,String code);
    // 开关锁车模式
    public native static String carLockMode(String pwd,int cmd,String code);
    // 切换启动模式
    public native static String carOpenMode(String pwd,int cmd,String code);
    // 控制器升级
    public native static String upgradeControl(String pwd,int cmd,int length,String code);

    @Override
    public void setReadPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public void setReadCarSn(String carSn) {
        this.carSn = carSn;
    }

    @Override
    public String getReadConfig() {
        return readConfig(pwd,getCmdNo());
    }

    @Override
    public String getFindLocate() {
        return findDevice(pwd,getCmdNo());
    }

    @Override
    public String getDeviceLock(String cmd) {
        return lockAllDevice(pwd,cmd,getCmdNo());
    }

    @Override
    public String getRideConfig(int maxSpeed, int addMode, int showModel, int reportInterval, int standbyTime) {
        return rideConfig(pwd,maxSpeed,addMode,showModel,reportInterval,standbyTime,getCmdNo());
    }

    @Override
    public String getLedControl(int mode) {
        return ledControl(pwd,mode,getCmdNo());
    }

    @Override
    public String getModifyBleName(String name) {
        return modifyBleName(pwd,name,getCmdNo());
    }

    @Override
    public String getModifyOpenCar(int mode) {
        return modifyOpenCar(pwd,mode,getCmdNo());
    }

    @Override
    public String getCarCruise(int mode) {
        return deviceCruise(pwd,mode,getCmdNo());
    }

    @Override
    public String getCarLockMode(int mode) {
        return carLockMode(pwd,mode,getCmdNo());
    }

    @Override
    public String getCarOpenMode(int mode) {
        return carOpenMode(pwd,mode,getCmdNo());
    }

    @Override
    public String getCarConfigTest(String sn, String name) {
        return null;
    }

    @Override
    public String getBleConfigTest(String sn, int broadcastSpace, int broadcastTime, int minSpace, int maxSpace) {
        return null;
    }

    @Override
    public String getTestProduct(int testSign) {
        return null;
    }

    @Override
    public String getSwitchMode(String pwd, int mode) {
        return null;
    }

    @Override
    public String getModifyPwd(String pwd) {
        return null;
    }

    @Override
    public String getControlUpgrade(int code, int packNum) {
        return upgradeControl(pwd,code,packNum,getCmdNo());
    }
}
