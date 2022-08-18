package com.yele.hu.upgradetools.policy.event;


import com.yele.hu.upgradetools.bean.RevResult;

public class CmdRevEvent {

    public String data;

    public RevResult object;

    public CmdRevEvent(String data,RevResult object) {
        this.data = data;
        this.object = object;
    }
}
