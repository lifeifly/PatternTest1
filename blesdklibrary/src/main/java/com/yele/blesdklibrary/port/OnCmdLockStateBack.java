package com.yele.blesdklibrary.port;

import com.yele.blesdklibrary.bean.ErrorInfo;
import com.yele.blesdklibrary.bean.LockStateInfo;

/**
 * 车辆锁
 */
public interface OnCmdLockStateBack {

    void CmdLockState(ErrorInfo error, LockStateInfo stateInfo);

}
