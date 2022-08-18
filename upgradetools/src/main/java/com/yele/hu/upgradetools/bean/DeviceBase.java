package com.yele.hu.upgradetools.bean;

public class DeviceBase {

    // 车辆的密码
    public static String PWD = "OKAIYLBT";



    // 车辆仪表的SN
    private static String SN = null;


    private static String carSn = null;

    /**
     * 配置当前的SN
     *
     * @param sn 配置的SN
     */
    public static void setSN(String sn) {
        SN = sn;
    }

    /**
     * 获取当前设备的SN
     * @return SN
     */
    public static String getSN() {
        return SN != null ? SN : "";
    }


    public static String getCarSn() {
        return carSn;
    }

    public static void setCarSn(String carSN) {
        carSn = carSN;
    }
}
