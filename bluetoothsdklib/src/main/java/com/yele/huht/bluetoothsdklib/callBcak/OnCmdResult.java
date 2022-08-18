package com.yele.huht.bluetoothsdklib.callBcak;

import com.yele.huht.bluetoothsdklib.bean.ErrorInfo;

public interface OnCmdResult {

    void CmdResultEvent(boolean hasSuccess, ErrorInfo error);

}
