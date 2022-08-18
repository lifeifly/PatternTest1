package com.lifly.pattern.observer;

public class Event {
    long timeStamp;
    String loc;

    public Event(long timeStamp, String loc) {
        this.timeStamp = timeStamp;
        this.loc = loc;
    }
}
