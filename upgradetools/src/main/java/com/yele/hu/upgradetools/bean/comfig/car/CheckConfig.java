package com.yele.hu.upgradetools.bean.comfig.car;

public class CheckConfig {
    // 自检标志 0：开始自检；1：退出自检
    public int mode;
    // 自检的部位； 0：速度区；1：电量区；2：功能区；3：全灭
    public int position;

    public CheckConfig(int mode, int position) {
        this.mode = mode;
        this.position = position;
    }
}
