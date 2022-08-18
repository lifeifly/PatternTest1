package com.yele.hu.upgradetools.bean.comfig.knapsack;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: KDisinfectControl
 *
 * @Author: Chenxc
 * @Date: 2021/8/3 19:10
 * @Description: 背包紫外线消毒功能控制
 * History:
 * <author> <time><version><desc>
 */
public class KDisinfectControl {
    /**
     * 指令ID，控制状态
     *      0：关闭紫外灯
     *      1：打开紫外消毒等
     *      2：关闭包外消毒指示氛围灯
     *      3：打开保外消毒指示氛围灯
     *      4：修改消毒氛围灯配置
     */
    public int control;
    /**
     * 消毒时长设置，单位：min
     * 范围10~30 默认15，只有指令ID1时才生效。
     */
    public int durationTime;
    /**
     * 氛围灯开关配置
     *  指令ID为4时才有效。默认打开
     *      0：关闭消毒氛围灯
     *      1：打开消毒氛围灯
     */
    public int ledOpen;

    public KDisinfectControl(int control, int durationTime, int ledOpen) {
        this.control = control;
        this.durationTime = durationTime;
        this.ledOpen = ledOpen;
    }
}
