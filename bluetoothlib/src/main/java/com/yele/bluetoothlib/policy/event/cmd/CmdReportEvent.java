package com.yele.bluetoothlib.policy.event.cmd;

import com.yele.bluetoothlib.bean.cmd.RevResult;

public class CmdReportEvent {

    public String cmdStr = null;

    public RevResult revResult;

    public CmdReportEvent(String cmd, RevResult revResult) {
        this.cmdStr = cmd;
        this.revResult = revResult;
    }
}
