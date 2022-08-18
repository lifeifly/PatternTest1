package com.yele.bluetoothlib.policy.protrol.control;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: OnBleConnectListener
 *
 * @Author: Chenxc
 * @Date: 2021/7/21 9:29
 * @Description: 蓝牙设备连接监听
 * History:
 * <author> <time><version><desc>
 */
public interface OnBleConnectListener {
    /**
     * 设备已连接
     * @param code 当前的状态
     *             0：连接成功
     *             1：连接超时
     *             2：设备不存在
     */
    void connected(int code);

    /**
     * 设备已断开
     * @param code 状态值
     *             0：设备已断开
     *             1：断开失败（可能已超时）
     *             2：设备
     */
    void disConnected(int code);
}
