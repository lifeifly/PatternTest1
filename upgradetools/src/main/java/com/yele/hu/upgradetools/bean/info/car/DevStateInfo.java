package com.yele.hu.upgradetools.bean.info.car;

public class DevStateInfo {
    // 充电MOS状态
    public int chargeMos = 0;
    // 放电MOS状态
    public int dischargeMos = 0;
    // 电池健康度 %
    public float soh = 0;
    // 电芯最高/最低温度，MOS管温度，其他温度
    public int highTemp = 0,lowTemp = 0,mosTemp = 0,otherTemp = 0;
    // 电池循环次数
    public int loopTimes = 0;
    // 加速转吧AD，左刹把AD,右刹把AD
    public int accAd = 0,leftAd = 0,rightAd = 0;
    // 报警功能状态开关
    public int brakeState = 0;
}
