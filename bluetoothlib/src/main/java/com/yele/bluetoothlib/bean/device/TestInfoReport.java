package com.yele.bluetoothlib.bean.device;

public class TestInfoReport {

    // 骑行时间
    public String rideTime;

    // 当前里程、剩余里程、总里程 .1km
    public String currentMileage,surplusMileage,totalMileage;

    public String speed;

    public String controlTemp,machineryTemp;

    public String currentValue,driveValue,brakeValue;

    public String controlState,dlccValue,conFailTimes;

    public String eleCurrent,eleVoltage,power;

    public String accAD,leftAD,rightAD;

    public TestInfoReport(String str) {
        if (str == null || str.equals("")) {
            return;
        }
        String[] buff = str.split(",");
        int j = 0;
        try {
            while (j < buff.length) {
                if (buff[j].equals("E")) {
                    j++;
                    rideTime = buff[j++];
                    totalMileage = buff[j++];
                    surplusMileage = buff[j++];
                    currentMileage = buff[j++];
                    speed = buff[j++];
                    controlTemp = buff[j++];
                    machineryTemp = buff[j++];
                    currentValue = buff[j++];
                    driveValue = buff[j++];
                    brakeValue = buff[j++];
                    controlState = buff[j++];
                    dlccValue = buff[j++];
                    conFailTimes = buff[j++];
                } else if (buff[j].equals("B")) {
                    j++;
                    eleCurrent = buff[j++];
                    eleVoltage = buff[j++];

                }else if (buff[j].equals("Y")) {
                    j++;
                    accAD = buff[j++];
                    leftAD = buff[j++];
                    rightAD = buff[j++];
                }else{
                    j++;
                }
            }
        } catch (Exception e) {

        }
    }

    public TestInfoReport() {
    }
}
