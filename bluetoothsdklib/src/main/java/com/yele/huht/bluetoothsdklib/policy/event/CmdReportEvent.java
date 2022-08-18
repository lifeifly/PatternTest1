package com.yele.huht.bluetoothsdklib.policy.event;

import com.yele.huht.bluetoothsdklib.bean.RevResult;
import com.yele.huht.bluetoothsdklib.service.BleService;

public class CmdReportEvent {

    public String cmdStr = null;

    public RevResult revResult;

    public CmdReportEvent(String cmd, RevResult revResult) {
        this.cmdStr = cmd;
        this.revResult = revResult;
    }
}
