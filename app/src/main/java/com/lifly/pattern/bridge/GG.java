package com.lifly.pattern.bridge;

public class GG {
    public void chase(MM mm) {
        Gift gift = new WarmGift(new Book());
        give(mm,gift);
    }

    private void give(MM mm, Gift gift) {

    }

}
