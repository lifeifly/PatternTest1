package com.yele.hu.upgradetools.bean.info.helmet;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: HStudyAck
 *
 * @Author: Chenxc
 * @Date: 2021/8/2 20:17
 * @Description: 学习指令的应答数据
 * History:
 * <author> <time><version><desc>
 */
public class HStudyAck {
    /**
     * 钥匙序号
     *      0：取消/停止学习模式成功
     *      1：开启学习模式成功
     */
    public int studyFlag;
    /**
     * 钥匙序号
     *      0：保留
     *      1：开始学习模钥匙1
     *      2：开始学习模钥匙2
     */
    public int keyNo;
    /**
     * 录入应答
     *      0：钥匙录入
     *      1：NFC录入
     */
    public int keyType;
    /**
     * 应答标志
     *      0：失败
     *      1：成功
     */
    public int ackType;
}
