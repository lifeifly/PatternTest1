package com.yele.blesdklibrary.port;

import com.yele.blesdklibrary.bean.ErrorInfo;

public interface OnCmdErrorCodeBack {

    void CmdErrorCode(boolean hasSuccess, String code, ErrorInfo error);

}
