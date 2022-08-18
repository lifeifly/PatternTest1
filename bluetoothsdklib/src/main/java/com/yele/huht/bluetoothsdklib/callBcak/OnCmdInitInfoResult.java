package com.yele.huht.bluetoothsdklib.callBcak;

import com.yele.huht.bluetoothsdklib.bean.ErrorInfo;
import com.yele.huht.bluetoothsdklib.bean.InitInfo;

public interface OnCmdInitInfoResult {

    void CmdInitInfo(ErrorInfo error, InitInfo initInfo);

}
