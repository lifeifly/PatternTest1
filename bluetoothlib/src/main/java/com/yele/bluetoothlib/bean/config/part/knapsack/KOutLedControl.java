package com.yele.bluetoothlib.bean.config.part.knapsack;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: KOutLedControl
 *
 * @Author: Chenxc
 * @Date: 2021/8/3 19:49
 * @Description: 包外示廓灯控制
 * History:
 * <author> <time><version><desc>
 */
public class KOutLedControl {
    /**
     * 包外示廓灯的控制状态:
     *      0：关闭闹钟；
     *      1：打开闹钟；
     *      2：设置闹钟时间；
     *      3：删除闹钟；
     */
    public int cmd;

    /**
     * <闹钟序列>：包外想要控制的闹钟序列:
     *      0：所有闹钟
     *      1：闹钟 1；
     *      2：闹钟 2；
     *      3：闹钟 3；
     *      4：闹钟 4；
     *      5：闹钟 5；
     *      6：闹钟 6；
     *      7：闹钟 7；
     *      8：闹钟 8；
     *      9：闹钟 9；
     *      10：闹钟 10；
     */
    public int num;

    /**
     * 开启时间，星期 ，范围 1~7F，7F 表示周一到周日开启
     * 00000001：星期一，01
     * 00000010：星期二，02
     * 00000100：星期三，04
     * 00001000：星期四，08
     * 00010000：星期五，10
     * 00100000：星期六，20
     * 01000000：星期日，40
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

    public KOutLedControl(int cmd) {
        this.cmd = cmd;
    }

    public KOutLedControl(int cmd, int num, String openWeek, String openHour, String openMin, String closeHour, String closeMin) {
        this.cmd = cmd;
        this.num = num;
        this.openWeek = openWeek;
        this.openHour = openHour;
        this.openMin = openMin;
        this.closeHour = closeHour;
        this.closeMin = closeMin;
    }
}
