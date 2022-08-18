package com.yele.bluetoothlib.bean.config.part.head;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: HeadBleConfig
 *
 * @Author: Chenxc
 * @Date: 2021/7/20 9:51
 * @Description: 测试模式下，头盔蓝牙的配置参数
 * History:
 * <author> <time><version><desc>
 */
public class HTestBleConfig {
    // 仪表SN
    public String sn;
    // 出厂时蓝牙设备默认的广播名称
    public String name;

    public HTestBleConfig(String sn, String name) {
        this.sn = sn;
        this.name = name;
    }
}
