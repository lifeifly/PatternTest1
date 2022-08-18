package com.yele.hu.upgradetools.bean;

public class DebugFlag {

    public static boolean debugMode = false;    // 是否对超级密码加密

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

    public static String SEND_KNAP_PROTOCOL = "AT+SK";
    public static String ACK_KNAP_PROTOCOL = "+ACK:SK";
    public static String RESP_KNAP_PROTOCOL = "+RESP:SK";

    public static String SEND_HEAD_PROTOCOL = "AT+SH";
    public static String ACK_HEAD_PROTOCOL = "+ACK:SH";
    public static String RESP_HEAD_PROTOCOL = "+RESP:SH";

    public static boolean isDebugMode = false;  // 是否对蓝牙密码加密

    // 是否是测试模式下的
    public static boolean IS_DEBUG = true;

}
