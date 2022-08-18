package com.yele.hu.upgradetools.policy.http.back;


import com.yele.hu.upgradetools.bean.ActiveInfo;

public interface BackLoginState {
    /**
     * 登陆成功
     */
    void loginSuccess(ActiveInfo info);

    /**
     * 登陆失败的返回接口
     * @param state 状态
     * @param err 错误内容
     */
    void loginFailed(int state, String err);
}
