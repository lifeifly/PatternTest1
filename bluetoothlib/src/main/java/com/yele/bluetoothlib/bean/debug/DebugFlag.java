package com.yele.bluetoothlib.bean.debug;

public class DebugFlag {

    public static boolean debugMode = false;

    public static boolean isOKProtocol = true;


    private static String OK_SEND_PROTOCOL = "AT+OK";
    private static String OK_ACK_PROTOCOL = "+ACK:OK";
    private static String OK_RESP_PROTOCOL = "+RESP:OK";

    private static String BK_SEND_PROTOCOL = "AT+BK";
    private static String BK_ACK_PROTOCOL = "+ACK:BK";
    private static String BK_RESP_PROTOCOL = "+RESP:BK";

    public static String PART_SEND_PROTOCOL = "AT+SH";
    public static String PART_ACK_PROTOCOL = "+ACK:SH";
    public static String PART_RESP_PROTOCOL = "+RESP:SH";

    public static String PART_KNAP_SEND_PROTOCOL = "AT+SK";
    public static String PART_KNAP_ACK_PROTOCOL = "+ACK:SK";
    public static String PART_KNAP_RESP_PROTOCOL = "+RESP:SK";


    public static String SEND_PROTOCOL = isOKProtocol ? OK_SEND_PROTOCOL : BK_SEND_PROTOCOL;
    public static String ACK_PROTOCOL = isOKProtocol ? OK_ACK_PROTOCOL : BK_ACK_PROTOCOL;
    public static String RESP_PROTOCOL = isOKProtocol ? OK_RESP_PROTOCOL : BK_RESP_PROTOCOL;


    public static void initProtocol() {
        SEND_PROTOCOL = isOKProtocol ? OK_SEND_PROTOCOL : BK_SEND_PROTOCOL;
        ACK_PROTOCOL = isOKProtocol ? OK_ACK_PROTOCOL : BK_ACK_PROTOCOL;
        RESP_PROTOCOL = isOKProtocol ? OK_RESP_PROTOCOL : BK_RESP_PROTOCOL;
    }

}
