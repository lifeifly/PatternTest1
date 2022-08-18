package com.yele.bluetoothlib.policy.protrol.control;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: INormalSet
 * @Author: Chenxc
 * @Date: 2021/7/16 16:03
 * @Description: 设备控制相关的接口类
 * History:
 * <author> <time><version><desc>
 */
public interface INormalSet {
    /**
     * 连接目标设备
     */
    void connectDevice();

    /**
     * 与目标设备断开连接
     */
    void disconnect();

    /**
     * 发送普通数据
     * @param dates 需要发送的数据
     */
    void sendNormalData(byte[] dates);

    /**
     * 当前的链接状态的接口返回
     * @param listener 设备的监听接口
     */
    void setOnConnectListener(OnBleConnectListener listener);

    /**
     * 设置当前蓝牙数据回调的监听
     * @param listener 蓝牙数据变化的监听
     */
    void setOnBleDataChangeListener(OnBleDataChangeListener listener);

    /**
     * 升级控制器
     */
    void upgradeMCU(String path);

    /**
     * 发送当前的数据
     * @param data
     * @return
     */
    boolean sendCmd( byte[] data);

    /**
     * 根据通道发送当前的数据
     * @param data
     * @return
     */
    boolean sendCmd(String channel, byte[] data);

    /**
     * 升级更新数据
     * @param data
     * @return
     */
    boolean updateData(byte[] data);

    /**
     * 根据通道升级更新数据
     * @param data
     * @return
     */
    boolean updateData(String channel, byte[] data);
}
