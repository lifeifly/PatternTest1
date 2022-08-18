package com.yele.hu.upgradetools.policy.http.back;


import com.yele.hu.upgradetools.bean.ActiveInfo;

public interface BackDeviceActive {
    /**
     * 激活状态返回
     */
    void activeStateBack(ActiveInfo info);
    /**
     * 请求失败，附带失败code以及原因内容
     * @param code
     * @param errMsg
     */
    void failed(int code, String errMsg);
}
