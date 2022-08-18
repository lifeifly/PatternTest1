package com.yele.huht.bluetoothsdklib.callBcak;

import com.yele.huht.bluetoothsdklib.bean.ErrorInfo;
import com.yele.huht.bluetoothsdklib.bean.LockStateInfo;

/**
 * 车辆锁
 */
public interface OnCmdLockState {

    void CmdLockState(ErrorInfo error, LockStateInfo stateInfo);

}
