package com.lifly.pattern.decorate;

import com.lifly.pattern.strategy.Tank;

public abstract class TankWrapper extends Frame{

    protected Tank tank;

    public TankWrapper(Tank tank) {
        this.tank = tank;
    }



}
