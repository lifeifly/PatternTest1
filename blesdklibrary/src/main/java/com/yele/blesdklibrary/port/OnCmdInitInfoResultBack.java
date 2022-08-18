package com.yele.blesdklibrary.port;

import com.yele.blesdklibrary.bean.ErrorInfo;
import com.yele.blesdklibrary.bean.InitInfo;

public interface OnCmdInitInfoResultBack {

    void CmdInitInfo(ErrorInfo error, InitInfo initInfo);

}
