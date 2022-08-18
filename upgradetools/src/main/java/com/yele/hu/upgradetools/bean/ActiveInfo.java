package com.yele.hu.upgradetools.bean;

public class ActiveInfo {
    /**
     * 如果为0，则为未申请
     * 如果为1，则为申请激活，等待激活
     * 如果为2，则为已经激活
     */
    public int code;
    /**
     * 等待激活序列号
     */
    public String waitSerial;
    /**
     * 激活码
     */
    public String activeNo;
    /**
     * 离线激活时间
     */
    public String offlineTime;
}
