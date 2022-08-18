package com.yele.hu.upgradetools.policy.event;

import com.yele.hu.upgradetools.bean.RevResult;

public class CmdReportEvent {

    public String cmdStr = null;

    public RevResult revResult;

    public CmdReportEvent(String cmd,RevResult revResult) {
        this.cmdStr = cmd;
        this.revResult = revResult;
    }
}
