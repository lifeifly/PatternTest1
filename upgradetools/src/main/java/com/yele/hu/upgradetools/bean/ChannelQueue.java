package com.yele.hu.upgradetools.bean;

import android.bluetooth.BluetoothGattCharacteristic;

public class ChannelQueue {
    private int maxSize;
    private int front;
    private int rear;
    private BluetoothGattCharacteristic[] arr;

    public ChannelQueue(int maxSize) {
        this.maxSize = maxSize;
        this.arr = new BluetoothGattCharacteristic[maxSize];
        this.front = -1;
        this.rear = -1;
    }

    public boolean isFull() {
        return this.rear == this.maxSize - 1;
    }

    public boolean isEmpty() {
        return this.rear == this.front;
    }

    public void addQueue(BluetoothGattCharacteristic n) {
        if (this.isFull()) {
            System.out.println("队列满，不能加入数据~");
        } else {
            ++this.rear;
            this.arr[this.rear] = n;
        }
    }

    public BluetoothGattCharacteristic getQueue() {
        if (this.isEmpty()) {
            return  null;
        } else {
            ++this.front;
            return this.arr[this.front];
        }
    }



    public BluetoothGattCharacteristic  headQueue() {
        if (this.isEmpty()) {
            throw null;
        } else {
            return this.arr[this.front + 1];
        }
    }
}