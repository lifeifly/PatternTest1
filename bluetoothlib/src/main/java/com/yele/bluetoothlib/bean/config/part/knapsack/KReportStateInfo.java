package com.yele.bluetoothlib.bean.config.part.knapsack;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: KReportState
 *
 * @Author: Chenxc
 * @Date: 2021/8/3 20:13
 * @Description: 背包状态定时上报
 * History:
 * <author> <time><version><desc>
 */
public class KReportStateInfo {
    /**
     * 背包开关状态
     *          0：关闭状态
     *          1：打开状态
     */
    public int openState;
    /**
     * 包内照明灯状态
     *          0：包内照明灯状态关闭
     *          1：包内照明灯状态打开
     */
    public int inLedState;
    /**
     * 包内照明灯配置状态
     *          0：包内照明灯设置为光线自动模式
     *          1：包内照明灯设置为开关自动模式
     */
    public int inLedConfig;
    /**
     * 包内照明灯颜色
     */
    public String inLedColor;
    /**
     * 紫外灯消毒状态
     *          0：消毒灯关闭
     *          1：消毒灯打开
     */
    public int disinfectState;
    /**
     * 剩余消毒时间
     */
    public int disinfectTime;
    /**
     * 剩余时长
     */
    public int disinfectSurplus;
    /**
     * 充电宝电量 范围 0~100%
     */
    public int battery;
    /**
     * 无线充状态
     *          0：无线充关闭
     *          1：无线充打开
     */
    public int wireless;
    /**
     * 氛围灯状态模式
     *          0：关闭
     *          1~4：氛围灯1~4.大于4预留
     */
    public int outLedState;
    /**
     * 氛围灯颜色
     */
    public String outLedColor;

    public long errCode;
}
