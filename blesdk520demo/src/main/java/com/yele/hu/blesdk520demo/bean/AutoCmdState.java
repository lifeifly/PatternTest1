package com.yele.hu.blesdk520demo.bean;

public class AutoCmdState {

    public int cmdId = 0;

    public String cmdName;

    public int cmdState = STATE_WAIT;

    public static final int STATE_WAIT = 0;
    public static final int STATE_SEND = 1;
    public static final int STATE_FAIL = 2;
    public static final int STATE_PASS = 3;

    public AutoCmdState(int id, String name) {
        this.cmdId = id;
        this.cmdName = name;
    }

    public AutoCmdState(String cmdName) {
        this.cmdName = cmdName;
    }
}
