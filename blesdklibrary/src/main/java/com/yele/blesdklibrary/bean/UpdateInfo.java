package com.yele.blesdklibrary.bean;

/**
 * 更新内容类
 */
public class UpdateInfo {

    public int sign;  // 标识当前对象是蓝牙/控制器的新版本信息
    public int versionCode;  // 新的版本号
    public String updateContent;   // 升级内容
    public long apkSize;    // 升级包大小

    @Override
    public String toString() {
        return "UpdateInfo{" +
                "sign=" + sign +
                ", versionCode=" + versionCode +
                ", updateContent='" + updateContent + '\'' +
                ", apkSize=" + apkSize +
                '}';
    }
}
