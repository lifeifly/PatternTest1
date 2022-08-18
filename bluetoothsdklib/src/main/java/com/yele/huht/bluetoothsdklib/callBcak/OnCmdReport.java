package com.yele.huht.bluetoothsdklib.callBcak;

import com.yele.huht.bluetoothsdklib.bean.CarRunReport;
import com.yele.huht.bluetoothsdklib.bean.ErrorInfo;

public interface OnCmdReport {

    void CmdReportEvent(ErrorInfo error, CarRunReport carRunState);

}
