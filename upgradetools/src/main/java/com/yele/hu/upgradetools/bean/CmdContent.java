package com.yele.hu.upgradetools.bean;

public class CmdContent {
    // group类型
    public String groupName;

    public CmdContent(String groupName) {
        this.groupName = groupName;
    }

    /**
     * 判断当前是否是Group类型
     * @return 是否是group类型
     */
    public boolean isGroup() {
        return groupName != null;
    }

    // 指令的显示名称
    public String childName;

    // 对应的指令
    public int cmd;

    // 配置对象
    public Object obj;

    /**
     * 普通指令
     * @param cmd 对应的指令
     * @param obj 当前的指令的配置对象
     */
    public CmdContent(String name, int cmd, Object obj) {
        this.childName = name;
        this.cmd = cmd;
        this.obj = obj;
    }
}
