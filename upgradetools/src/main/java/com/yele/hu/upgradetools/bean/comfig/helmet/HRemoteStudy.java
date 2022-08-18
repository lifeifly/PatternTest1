package com.yele.hu.upgradetools.bean.comfig.helmet;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: HRemoteStudy
 *
 * @Author: Chenxc
 * @Date: 2021/7/20 11:01
 * @Description: 开启遥控按键学习模式
 * History:
 * <author> <time><version><desc>
 */
public class HRemoteStudy {
    /**
     * 学习模式标志
     * 0：取消/停止学习模式
     * 1：顺序录入
     * 2：分步录入
     */
    public int studyMode = 0;
    /**
     * 钥匙序号
     * 0：保留
     * 1：开始学习钥匙1
     * 2：开始学习钥匙2
     */
    public int keyNo = 0;
    /**
     * 密钥类型
     * 0：保留
     * 1：开锁密钥
     * 2：关锁密钥
     * 3：NFC
     */
    public int keyType = 0;
}
