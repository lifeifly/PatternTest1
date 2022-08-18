package com.yele.huht.bluetoothsdklib.bean;

public class ErrorInfo {

    public int errorCode;      // 错误码

    public String errorDescription;    // 错误类型


    @Override
    public String toString() {
        return "ErrorInfo{" +
                "errorCode=" + errorCode +
                ", errorDescription='" + errorDescription + '\'' +
                '}';
    }
}
