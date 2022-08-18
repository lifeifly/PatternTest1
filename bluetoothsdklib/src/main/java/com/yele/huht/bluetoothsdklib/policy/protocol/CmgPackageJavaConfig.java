package com.yele.huht.bluetoothsdklib.policy.protocol;


import com.yele.baseapp.utils.ByteUtils;
import com.yele.huht.bluetoothsdklib.data.BindData;

public class CmgPackageJavaConfig implements InitConfigStr {

    private String pwd;
    private String carSn;


    private boolean isHead = true;

    /**
     * 写入的头信息
     * @return
     */
    private String getHeadInput() {
        String headInfo;
        if (!isHead) {
            headInfo = "AT+BK";
        } else {
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
    public void setReadPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public void setReadCarSn(String carSn) {
        this.carSn = carSn;
    }

    @Override
    public String getReadConfig() {
        return getHeadInput()+"ALC=" + pwd + "," + getCmdNo();
    }

    @Override
    public String getFindLocate() {
        return getHeadInput()+"LOC=" + pwd + "," + getCmdNo();
    }

    @Override
    public String getDeviceLock(String cmd) {
        return getHeadInput()+"SCT=" + pwd + "," + cmd + "," + getCmdNo();
    }

    @Override
    public String getRideConfig(int maxSpeed, int addMode, int showModel, int reportInterval, int standbyTime) {
        return getHeadInput()+"ECP=" + pwd + "," + maxSpeed + "," + addMode + "," + showModel + "," + reportInterval + "," + standbyTime + "," + getCmdNo();
    }

    @Override
    public String getLedControl(int mode) {
        return getHeadInput()+"LED=" + pwd + ",0," + mode + "," + getCmdNo();
    }

    @Override
    public String getModifyBleName(String name) {
        return getHeadInput()+"NAM=" + pwd + "," + name + "," + getCmdNo();
    }

    @Override
    public String getModifyOpenCar(int mode) {
        return getHeadInput()+"ONF=" + pwd + "," + mode + "," + getCmdNo();
    }

    @Override
    public String getCarCruise(int mode) {
        return getHeadInput()+"DSX=" + pwd + "," + mode + "," + getCmdNo();
    }

    @Override
    public String getCarLockMode(int mode) {
        return getHeadInput()+"SCM=" + pwd + "," + mode + "," + getCmdNo();
    }

    @Override
    public String getCarOpenMode(int mode) {
        return getHeadInput()+"SUM=" + pwd + "," + mode + "," + getCmdNo();
    }

    @Override
    public String getCarConfigTest(String sn, String name) {
        return getHeadInput() +"CAP=" + sn + "," + name + "," + getCmdNo();
    }

    @Override
    public String getBleConfigTest(String sn, int broadcastSpace, int broadcastTime, int minSpace, int maxSpace) {
        return getHeadInput()+"CON=" + sn + "," + broadcastSpace + "," + broadcastTime + "," + minSpace + "," + maxSpace + "," + pwd + ",," + getCmdNo();
    }

    @Override
    public String getTestProduct(int testSign) {
        return getHeadInput()+"TET=" + testSign + "," + getCmdNo();
    }

    @Override
    public String getSwitchMode(String pwd,int mode) {
        return getHeadInput()+"XWM=" + pwd + "," + mode + "," + getCmdNo();
    }

    @Override
    public String getModifyPwd(String newPwd) {
        return getHeadInput()+"PWD=" + pwd + "," + newPwd + "," + carSn + "," + getCmdNo();
    }

    @Override
    public String getControlUpgrade(int code, int packNum) {
        return getHeadInput()+"URD=" + pwd + "," + code + "," +  packNum + "," + getCmdNo() + "$\r\n";
    }


}
