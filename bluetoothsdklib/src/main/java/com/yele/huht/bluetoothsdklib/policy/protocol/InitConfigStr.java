package com.yele.huht.bluetoothsdklib.policy.protocol;

public interface InitConfigStr {

    void setReadPwd(String pwd);

    void setReadCarSn(String carSn);

    String getReadConfig();    // 读取配置

    String getFindLocate();    // 寻车

    String getDeviceLock(String cmd);    // 车辆锁开关

    String getRideConfig(int maxSpeed,int addMode,int showModel,int reportInterval,int standbyTime);    // 骑行时间

    String getLedControl(int mode);    // 车辆大灯开关

    String getModifyBleName(String name);   // 修改蓝牙名称

    String getModifyOpenCar(int mode);   // 修改开关机模式

    String getCarCruise(int mode);  // 开关定速巡航

    String getCarLockMode(int mode);  // 开关车辆锁车模式

    String getCarOpenMode(int mode);   // 切换车辆启动模式

    String getCarConfigTest(String sn, String name);   // 辆参数配置--测试模式

    String getBleConfigTest(String sn, int broadcastSpace, int broadcastTime, int minSpace, int maxSpace);    // 蓝牙参数配置--测试模式

    String getTestProduct(int testSign);

    String getSwitchMode(String pwd,int mode);   // 切换模式

    String getModifyPwd(String pwd);   // 修改密码

    String getControlUpgrade(int code,int packNum);

}
