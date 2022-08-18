package com.yele.huht.bluetoothsdklib.policy.load.event;

public class OnLoadEvent {

    public static final int LOAD_STATE = 0;
    public static final int LOAD_INIT_FAILED = 1;
    public static final int LOAD_UPDATE = 2;
    public static final int LOAD_STOP = 3;
    public static final int LOAD_DELETE = 4;
    public static final int LOAD_FAILED = 5;
    public static final int LOAD_FINISH = 6;

    public int code;

    public Object obj;

    public OnLoadEvent(int code, Object obj) {
        this.code = code;
        this.obj = obj;
    }



}
