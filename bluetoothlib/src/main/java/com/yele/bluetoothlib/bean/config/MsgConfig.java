package com.yele.bluetoothlib.bean.config;

import com.yele.baseapp.utils.StringUtils;

public class MsgConfig {

    // 消息类型：0：短信；1：电话；`
    public int msgType;

    // 消息来源人名
    public String resName;

    // 消息内容
    public String content;

    public MsgConfig(int msgType,  String content,String resName) {
        this.msgType = msgType;
        this.resName = resName;
        this.content = content;
    }

    /**
     * 获取人名长度
     * @return 人名长度
     */
    public int getNameLen() {
        return StringUtils.isEmpty(resName) ? 0 : resName.length();
    }

    /**
     * 获取消息内容长度
     * @return 消息内容长度
     */
    public int getContentLen() {
        return StringUtils.isEmpty(content) ? 0 : content.length();
    }
}
