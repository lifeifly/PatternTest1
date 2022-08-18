package com.yele.huht.bluetoothsdklib.callBcak;

import com.yele.huht.bluetoothsdklib.bean.UpdateInfo;

public interface OnUpdateVersion {

    void updateVersion(UpdateInfo info);

    void updateFail(Object object);

}
