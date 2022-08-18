package com.yele.blesdklibrary.bean;

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
