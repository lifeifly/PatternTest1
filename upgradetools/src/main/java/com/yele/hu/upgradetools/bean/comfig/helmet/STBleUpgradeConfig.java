package com.yele.hu.upgradetools.bean.comfig.helmet;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: HUpgradeConfig
 *
 * @Author: Chenxc
 * @Date: 2021/7/29 19:45
 * @Description: 头盔的升级的配置
 * History:
 * <author> <time><version><desc>
 */
public class STBleUpgradeConfig {

    public int type = 0;

    public int softVersion = 0;

    public int wareVersion = 0;

    public STBleUpgradeConfig(int type, int softVersion, int wareVersion) {
        this.type = type;
        this.softVersion = softVersion;
        this.wareVersion = wareVersion;
    }
}
