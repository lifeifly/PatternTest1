package com.yele.huht.bluetoothsdklib.callBcak;

import com.yele.huht.bluetoothsdklib.bean.ErrorInfo;

public interface OnCmdErrorCode {

    void CmdErrorCode(boolean hasSuccess, String code, ErrorInfo error);

}
