package com.yele.blesdklibrary.port;

import com.yele.blesdklibrary.bean.CarRunState;
import com.yele.blesdklibrary.bean.ErrorInfo;

public interface OnCmdReportBack {

    void CmdReportEvent(ErrorInfo error, CarRunState carRunState);

}
