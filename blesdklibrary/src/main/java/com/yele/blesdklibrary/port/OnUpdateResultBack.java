package com.yele.blesdklibrary.port;

import com.yele.blesdklibrary.bean.ErrorInfo;

public interface OnUpdateResultBack {

    /**
     * 升级中
     * @param state 0:初始化，1：升级中，2：升级成功，3：升级失败
     * @param msg 升级过程描述
     * @param progress 进度条
     */
    void updating(int state,String msg,int progress);

    /**
     * 升级成功
     */
    void updateSuccess();

    /**
     * 升级失败
     * @param errorInfo 失败信息类
     */
    void updateFailed(ErrorInfo errorInfo);


}
