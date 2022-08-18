package com.yele.bluetoothlib.bean;

public class SendMessage {



    public   String channel;
    public  byte[] data;


    public SendMessage(String channel, byte[] data) {
        this.channel = channel;
        this.data = data;
    }
}
