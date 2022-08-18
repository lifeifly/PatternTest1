package com.yele.bluetoothlib.bean.config.part.head;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: HShowMode
 *
 * @Author: Chenxc
 * @Date: 2021/7/20 10:31
 * @Description: 显示模式选择
 * History:
 * <author> <time><version><desc>
 */
public class HShowMode {
    /**
     * 指令ID：指令环境
     * 0：白天模式
     * 1：夜间模式
     * 2：雨雾模式
     * 3：DIY模式
     */
    public int cmd = 0;
    /**
     * 转向灯模式
     * 0：模式0
     * 1：模式1
     */
    public int turnMode = 0;

    public HShowMode() {
    }

    public HShowMode(int cmd, int turnMode) {
        this.cmd = cmd;
        this.turnMode = turnMode;
    }
}
