package com.yele.huht.bluetoothsdklib.policy.http.back;

public interface OnApkUpdateBack {

    void backFailed(int code, String errMsg);

    void backSuccess();

    void backSuccess(int sign, int versionCode, String versionName, String url, String content, long size);
}
