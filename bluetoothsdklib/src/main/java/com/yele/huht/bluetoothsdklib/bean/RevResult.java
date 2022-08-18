package com.yele.huht.bluetoothsdklib.bean;

import com.yele.baseapp.utils.ByteUtils;

/**
 * 解析出来的数据的磊
 */
public class RevResult {

    public int cmd;  // 解析出来对应的协议指令

    public int cmdNo;   // 当前指令的序列号

    public Object object;   // 当前接收到的指令对象

    public static final int ERR = 1;
    public static final int SUCCESS = 0;
    public int result = ERR;   // 当前指令的处理结果

    public String errMsg = null;  // 当前指令的错误信息

    /**
     * 设置指令序列号
     * @param cmdNoStr 指令序列号的字符串形式
     */
    public void setCmdNo(String cmdNoStr) {
        byte[] buff = ByteUtils.strToBytes(cmdNoStr);
        this.cmdNo = ByteUtils.bytesToIntByBig(buff, 2);
    }
}
