package com.yele.bluetoothlib.bean.config.part.head;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: HeadLedControl
 *
 * @Author: Chenxc
 * @Date: 2021/7/20 10:06
 * @Description: 测试模式下，前后灯显示状态控制
 * History:
 * <author> <time><version><desc>
 */
public class HTestLedControl {
    // 前灯LED控制状态 范围 0~99
    public int frontLed;
    // 后灯LED控制状态 范围 0~99
    public int rearLed;

    public HTestLedControl(int frontLed, int rearLed) {
        this.frontLed = frontLed;
        this.rearLed = rearLed;
    }
}
