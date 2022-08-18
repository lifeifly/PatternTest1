package com.lifly.pattern.decorate;

import com.lifly.pattern.strategy.Tank;

public class BlueReadWrapper extends TankWrapper{
    public BlueReadWrapper(Tank tank) {
        super(tank);
    }

    @Override
    protected void paint() {
        //加装饰
        tank.paint();
    }
}
