package com.yele.bluetoothlib.bean.config.part.head;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: HCarInfoConfig
 *
 * @Author: Chenxc
 * @Date: 2021/7/20 10:42
 * @Description: 车身状态数据下发，部分配置信息
 * History:
 * <author> <time><version><desc>
 */
public class HCarInfoConfig {
    // 车辆状态 0：车辆关机  1：车辆开机
    public int carState = 0;
    // 当前车辆的转吧值 0~999
    public int accValue = 0;
    // 当前车辆的左刹把值 0~999
    public int leftBrake = 0;
    // 当前车辆的右刹把值 0~999
    public int rightBrake = 0;
    // 车辆速度值
    public int speedValue = 0;
    // 车辆速度单位
    public int speedUnit = 0;
}
