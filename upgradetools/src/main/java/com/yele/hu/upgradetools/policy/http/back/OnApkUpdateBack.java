package com.yele.hu.upgradetools.policy.http.back;

public interface OnApkUpdateBack {

    void backFailed(int code, String errMsg);

    void backSuccess();

    void backSuccess(int versionCode, String versionName, String url, String content, long size, boolean must, String date);
}
