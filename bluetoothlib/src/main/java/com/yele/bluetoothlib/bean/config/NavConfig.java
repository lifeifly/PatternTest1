package com.yele.bluetoothlib.bean.config;

public class NavConfig {
    // 是否进入导航模式，0：关闭导航；1：进入导航
    public int mode = 0;
    // 目标地址
    public String aimAdd = null;
    // 目标剩余距离
    public long aimDis = 0;
    // 下一个路口名称
    public long navTime = 0;
    // 下一个路口名称
    public String nextStreet = null;
    // 进入下一个路口剩余时间
    public long nextTime = 0;
    // 下一个入口动作：0：直行；1：右转；2：左转；3：掉头
    public int nextAction = 0;

    public NavConfig(int mode, String aimAdd, long aimDis,
                     long navTime, String nextStreet, long nextTime, int nextAction) {
        this.mode = mode;
        this.aimAdd = aimAdd;
        this.aimDis = aimDis;
        this.navTime = navTime;
        this.nextStreet = nextStreet;
        this.nextTime = nextTime;
        this.nextAction = nextAction;
    }
}
