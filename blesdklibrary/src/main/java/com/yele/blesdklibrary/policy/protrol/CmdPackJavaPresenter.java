package com.yele.blesdklibrary.policy.protrol;

import com.yele.blesdklibrary.data.BindData;
import com.yele.blesdklibrary.util.ByteUtils;

public class CmdPackJavaPresenter implements ICmdPackage {

    private String pwd;

    private boolean isHead = true;
    /**
     * 写入的头信息
     * @return
     */
    private String getHeadInput(){
        String headInfo;
        if(!isHead){
            headInfo = "AT+BK";
        }else {
            headInfo = "AT+OK";
        }
        return headInfo;
    }

    /**
     * 指令序列号
     * @return
     */
    private String getCmdNo() {
        int cmdNo = BindData.CMD_NO++;
        if (cmdNo > 65535) {
            cmdNo = 0;
            BindData.CMD_NO = 0;
        }
        byte[] buff = ByteUtils.longToBytesByBig(cmdNo, 2);
        return ByteUtils.bytesToStringByBig(buff);
    }

    @Override
    public void setPassword(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public String getReadConfigCmdStr() {
        return getHeadInput()+"ALC=" + pwd + "," + getCmdNo();
    }
}
