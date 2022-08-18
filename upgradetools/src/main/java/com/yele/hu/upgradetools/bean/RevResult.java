package com.yele.hu.upgradetools.bean;

import com.yele.baseapp.utils.ByteUtils;

/**
 * 解析出来的数据的磊
 */
public class RevResult {

    public static final int INIT = -1;          // 初始化状态

    public static final int SUCCESS = 0;        // 处理成功
    public static final int LENGTH_ERR = 1;     // 数据非法，常见为指令长度不对
    public static final int ILLEGAL = 2;        // 指令非法，未包含在常规指令协议内的指令
    public static final int PWD_ERR = 3;        // 密码错误
    public static final int FAILED = 4;         // 设备返回失败
    public int result = INIT;   // 当前指令的处理结果


    public static final int TYPE_ACK = 1;       // 应答指令
    public static final int TYPE_REPORT = 2;    // 上报指令
    public static final int TYPE_UPGRADE = 3;   // 升级指令
    public int type = INIT;   // 指令类型


    public int cmd = INIT;  // 解析出来对应的协议指令
    public int cmdNo;   // 当前指令的序列号
    public Object object;   // 当前接收到的指令对象
    public String errMsg = null;  // 当前指令的错误信息

    public Object object1;
    public String cmdType="";

    public String srcData;

    /**
     * 设置指令序列号
     * @param cmdNoStr 指令序列号的字符串形式
     */
    public void setCmdNo(String cmdNoStr) {
        byte[] buff = ByteUtils.strToBytes(cmdNoStr);
        this.cmdNo = ByteUtils.bytesToIntByBig(buff, 2);
    }


}
