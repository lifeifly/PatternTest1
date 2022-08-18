package com.yele.hu.upgradetools.bean.comfig.knapsack;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: KFingerprintConfig
 *
 * @Author: Chenxc
 * @Date: 2021/8/3 20:01
 * @Description: 背包指纹配置
 * History:
 * <author> <time><version><desc>
 */
public class KFingerprintConfig {
    /**
     * 开关机状态
     *      0：开关机状态
     *      1：增加指纹
     *      2: 取消添加
     */
    public int cmd;
    /**
     * 要操作的指纹编号。0~10个。
     */
    public int no;

    public KFingerprintConfig(int cmd, int no) {
        this.cmd = cmd;
        this.no = no;
    }
}
