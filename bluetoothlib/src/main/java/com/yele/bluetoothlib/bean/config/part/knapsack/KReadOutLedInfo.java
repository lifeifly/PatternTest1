package com.yele.bluetoothlib.bean.config.part.knapsack;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: KReadOutLedInfo
 *
 * @Author: Chenxc
 * @Date: 2021/8/3 20:10
 * @Description: 包外示廓灯控制状态查询
 * History:
 * <author> <time><version><desc>
 */
public class KReadOutLedInfo {

    /**
     * 表示第几个闹钟，范围 1~10
     */
    public int num;
    /**
     * 表示当前闹钟的状态是开启还是关闭。
     * 0：关闭。
     * 1：开启。
     */
    public int state;
    /**
     * 开启时间，星期
     */
    public String openWeek;
    /**
     * 开启时间，时
     */
    public String openHour;
    /**
     * 开启时间，分
     */
    public String openMin;
    /**
     * 关闭时间，时
     */
    public String closeHour;
    /**
     * 关闭时间，分
     */
    public String closeMin;
}
