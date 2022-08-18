package com.yele.hu.upgradetools.data;


import com.yele.hu.upgradetools.bean.info.car.BluetoothConfig;
import com.yele.hu.upgradetools.bean.info.car.CarConfigInfo;
import com.yele.hu.upgradetools.bean.info.car.CarRunState;
import com.yele.hu.upgradetools.bean.info.car.DesignConfig;
import com.yele.hu.upgradetools.bean.info.car.OkaiBleDevice;
import com.yele.hu.upgradetools.bean.info.car.ProduceConfig;
import com.yele.hu.upgradetools.bean.info.car.ReportInfo;
import com.yele.hu.upgradetools.bean.info.car.VersionInfo;

public class DataManager {

    private static DataManager dataManager;

    public static void initData() {
        if (dataManager == null) {
            synchronized (DataManager.class) {
                if (dataManager == null) {
                    dataManager = new DataManager();
                }
            }
        }
    }

    public static DataManager getInstance() {
        return dataManager;
    }

    /**
     * 构造类
     */
    private DataManager() {
    }

    /**
     * 当前的设备
     */
    public OkaiBleDevice device;

    /**
     * 当前设备的名称
     */
    public String name;

    /**
     * 当前设备的车辆密码
     */
    public String password;

    /* ******** 以下信息在读取配置时获取 ******* */
    public void setConfigInfo(ReportInfo reportInfo) {
        if (reportInfo == null) {
            return;
        }
        this.bluetoothConfig = reportInfo.bluetoothConfig;
        this.designConfig = reportInfo.designConfig;
        this.carConfig = reportInfo.carConfig;
        this.mode = reportInfo.mode;
        this.versionInfo = reportInfo.versionInfo;
    }

    // 上报上来的蓝牙配置信息
    public BluetoothConfig bluetoothConfig ;

    // 上报上来的蓝牙设计信息
    public DesignConfig designConfig;

    // 车辆的配置信息
    public CarConfigInfo carConfig;

    // 当前车辆的模式
    public int mode;

    // 版本信息
    public VersionInfo versionInfo;


    public void setReportInfo(CarRunState carRunState){
        if(carRunState == null){
            return;
        }
        this.carRunState = carRunState;
    }

    public CarRunState carRunState;



    public void setProduceConfig(ProduceConfig produceConfig){
        if(produceConfig == null){
            return;
        }
        this.produceConfig = produceConfig;
    }

    public ProduceConfig produceConfig;




}
