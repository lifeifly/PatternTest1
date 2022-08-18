package com.yele.hu.upgradetools.bean.comfig.car;

public class KeyConfig {

    /**
     * 学习模式标志
     *      0：取消/停止学习模式。
     *      1：开启学习模式
     */
    public int mode;

    /**
     * <钥匙序号>
     *     0：保留
     *     1：开始学习模钥匙 1
     *     2：开始学习模钥匙 2
     */
    public int num;

    public KeyConfig(int mode, int num) {
        this.mode = mode;
        this.num = num;
    }
}
