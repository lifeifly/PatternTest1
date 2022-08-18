package com.yele.bluetoothlib.bean.config.part.head;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: HLedConfig
 *
 * @Author: Chenxc
 * @Date: 2021/7/20 10:37
 * @Description: 灯状态配置
 * History:
 * <author> <time><version><desc>
 */
public class HLedConfig {
    // 前灯流水速度  1/2/3
    public int frontFlowingSpeed;
    // 前灯亮度 1~9
    public int frontLight;
    // 后灯流水速度 1/2/3
    public int rearFlowingSpeed;
    // 后灯亮度 1~9
    public int rearLight;

    public HLedConfig() {
    }

    public HLedConfig(int frontFlowingSpeed, int frontLight, int rearFlowingSpeed, int rearLight) {
        this.frontFlowingSpeed = frontFlowingSpeed;
        this.frontLight = frontLight;
        this.rearFlowingSpeed = rearFlowingSpeed;
        this.rearLight = rearLight;
    }
}
