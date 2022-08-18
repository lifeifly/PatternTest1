package com.yele.bluetoothlib.policy.protrol.control;

import com.yele.bluetoothlib.bean.cmd.RevResult;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: OnBleDataChangeListener
 *
 * @Author: Chenxc
 * @Date: 2021/7/21 9:30
 * @Description: 蓝牙数据发送变化的接口
 * History:
 * <author> <time><version><desc>
 */
public interface OnBleDataChangeListener {

    void revACKData(RevResult result);

    void revReportData(RevResult result);

    void revUpgradeData(RevResult result);
}
