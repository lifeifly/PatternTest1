package com.yele.huht.bluetoothsdklib.bean;

public class DebugFlag {

    public static boolean debugMode = false;

    public static boolean isOKProtocol = true;


    private static String OK_SEND_PROTOCOL = "AT+OK";
    private static String OK_ACK_PROTOCOL = "+ACK:OK";
    private static String OK_RESP_PROTOCOL = "+RESP:OK";

    private static String BK_SEND_PROTOCOL = "AT+BK";
    private static String BK_ACK_PROTOCOL = "+ACK:BK";
    private static String BK_RESP_PROTOCOL = "+RESP:BK";

    public static String SEND_PROTOCOL = isOKProtocol ? OK_SEND_PROTOCOL : BK_SEND_PROTOCOL;
    public static String ACK_PROTOCOL = isOKProtocol ? OK_ACK_PROTOCOL : BK_ACK_PROTOCOL;
    public static String RESP_PROTOCOL = isOKProtocol ? OK_RESP_PROTOCOL : BK_RESP_PROTOCOL;


    // 是否是调试模式，如果调试模式下，会出现返回数据显示以及打印信息
    public static final boolean IS_DEBUG = true;


    // 指令应答和写入的头信息判断，ture为OK，false为AK
    public static boolean isHead = true;

}
