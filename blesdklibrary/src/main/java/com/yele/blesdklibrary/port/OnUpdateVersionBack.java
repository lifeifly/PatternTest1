package com.yele.blesdklibrary.port;

import com.yele.blesdklibrary.bean.UpdateInfo;

public interface OnUpdateVersionBack {

    void updateVersion(UpdateInfo info);

    void updateFail(Object object);

}
