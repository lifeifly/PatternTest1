package com.yele.hu.blesdk520demo.event;

public class ShowReportEvent {

    public static final int REPORT_CODE = 1;
    public static final int ERROR_CODE = 2;

    public int code = -1;
    public Object object;

    public ShowReportEvent(Object object) {
        this.object = object;
    }

    public ShowReportEvent(int code, Object object) {
        this.code = code;
        this.object = object;
    }
}
