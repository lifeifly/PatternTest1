package com.yele.bluetoothlib.bean.config;

/**
 * 氛围模式配置
 */
public class AtmosphereConfig {

    public int mode = 0;

    public long speed = 0;

    public AtmosphereConfig(int mode, long speed) {
        this.mode = mode;
        this.speed = speed;
    }
}
