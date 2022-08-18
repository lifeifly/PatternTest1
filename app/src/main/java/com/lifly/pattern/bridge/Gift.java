package com.lifly.pattern.bridge;

public abstract class Gift {
    private GiftImpl impl;

    public Gift(GiftImpl impl) {
        this.impl = impl;
    }
}
