package com.yele.hu.upgradetools.bean.comfig.knapsack;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: KTestBleConfig
 *
 * @Author: Chenxc
 * @Date: 2021/8/3 18:47
 * @Description: 测试模式下，蓝牙参数配置
 * History:
 * <author> <time><version><desc>
 */
public class KTestBleConfig {
    /**
     * 设备SN
     */
    public String sn;
    /**
     * 设备新名称
     */
    public String name;

    public KTestBleConfig(String sn, String name) {
        this.sn = sn;
        this.name = name;
    }
}
