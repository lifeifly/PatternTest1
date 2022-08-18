package com.yele.bluetoothlib.bean.config.part.head;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: HDriveLed
 *
 * @Author: Chenxc
 * @Date: 2021/7/20 10:13
 * @Description: 骑行状态灯选择
 * History:
 * <author> <time><version><desc>
 */
public class HDriveLed {

    // 前灯转向状态
    public int frontTurnState = 0;
    // 前灯骑行状态
    public int frontDriveState = 0;
    // 前灯骑行灯条颜色
    public String frontDriveColor = "";

    // 后灯转向状态
    public int rearTurnState = 0;
    // 后灯骑行状态
    public int rearDriveState = 0;
    // 后灯骑行灯条颜色
    public String rearDriveColor = "";

    // 警示灯状态
    public int warningState = 0;

    public HDriveLed() {
    }

    public HDriveLed(int frontTurnState, int frontDriveState, String frontDriveColor, int rearTurnState, int rearDriveState, String rearDriveColor, int warningState) {
        this.frontTurnState = frontTurnState;
        this.frontDriveState = frontDriveState;
        this.frontDriveColor = frontDriveColor;
        this.rearTurnState = rearTurnState;
        this.rearDriveState = rearDriveState;
        this.rearDriveColor = rearDriveColor;
        this.warningState = warningState;
    }
}
