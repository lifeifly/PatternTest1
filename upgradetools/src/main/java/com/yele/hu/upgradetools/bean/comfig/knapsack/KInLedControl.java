package com.yele.hu.upgradetools.bean.comfig.knapsack;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: KInLedControl
 *
 * @Author: Chenxc
 * @Date: 2021/8/3 20:04
 * @Description: 包内照明灯控制
 * History:
 * <author> <time><version><desc>
 */
public class KInLedControl {
    /**
     * 指令ID
     *      0：关闭包内照明灯
     *      1：打开包内照明灯
     *      2：设置自动开启时间段：只有在设置为2时，以下时间才生效。
     */
    public int cmd;
    /**
     * 包内照明灯自动开启功能开启时间，以24小时计算
     */
    public int openTime;
    /**
     * 包内照明灯自动关闭功能关闭时间，以24小时计算
     */
    public int closeTime;

    public KInLedControl(int cmd, int openTime, int closeTime) {
        this.cmd = cmd;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }
}
