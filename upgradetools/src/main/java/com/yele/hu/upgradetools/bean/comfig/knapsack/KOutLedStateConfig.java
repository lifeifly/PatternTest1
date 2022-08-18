package com.yele.hu.upgradetools.bean.comfig.knapsack;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: KOutLedStateConfig
 *
 * @Author: Chenxc
 * @Date: 2021/8/3 19:57
 * @Description: 背包示廓灯状态配置
 * History:
 * <author> <time><version><desc>
 */
public class KOutLedStateConfig {
    /**
     * 示廓灯控制
     *      0：关闭包外示廓灯
     *      1：打开包外示廓灯
     *      2：包外示廓灯设置为自动模式，到时自动打开
     */
    public int control;
    /**
     * 氛围灯状态选择
     *      0:关闭示廓灯
     *      1：示廓灯状态1
     *      2：示廓灯状态2
     *      3：示廓灯状态3
     *      4~99：示廓灯状态4及以上预留
     */
    public int state;
    /**
     * 示廓灯颜色，颜色为GRB不同于手机对的RGB
     */
    public String color;

    public KOutLedStateConfig(int control, int state, String color) {
        this.control = control;
        this.state = state;
        this.color = color;
    }
}
