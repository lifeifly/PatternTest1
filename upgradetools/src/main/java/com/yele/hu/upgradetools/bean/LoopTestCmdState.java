package com.yele.hu.upgradetools.bean;

public class LoopTestCmdState {

    public int cmdId = 0;

    public String cmdName;

    // 配置对象
    public Object obj;


    public LoopTestCmdState(int id, String name) {
        this.cmdId = id;
        this.cmdName = name;
    }

    public LoopTestCmdState(int cmdId, String cmdName, Object obj) {
        this.cmdId = cmdId;
        this.cmdName = cmdName;
        this.obj = obj;
    }
}
