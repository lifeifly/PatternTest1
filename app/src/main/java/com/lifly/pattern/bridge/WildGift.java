package com.lifly.pattern.bridge;

public class WildGift extends Gift{
    public WildGift(GiftImpl impl) {
        super(impl);
    }
}
