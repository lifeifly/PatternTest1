package com.yele.blesdklibrary.port;

import com.yele.blesdklibrary.bean.ErrorInfo;

public interface OnCmdResultBack {

    void CmdResultEvent(boolean hasSuccess, ErrorInfo error);

}
