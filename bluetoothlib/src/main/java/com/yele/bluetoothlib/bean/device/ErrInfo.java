package com.yele.bluetoothlib.bean.device;

/**
 * 错误信息
 */
public class ErrInfo {

    /**
     * 当前的错误码
     */
    public String errCode;
    /**
     * 仪表错误
     */
    public String ybCode;
    /**
     * 仪表显示值
     */
    public String[] errCodes;

    public String getErrCodeString() {
        if (errCodes == null) {
            return "";
        }
        String errStr = "";
        for (int i = 0; i < errCodes.length; i++) {
            errStr += "错误" + i + " :" + errCodes[i] + "; ";
        }
        return errStr;
    }
}
