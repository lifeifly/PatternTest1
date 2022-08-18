package com.lifly.pattern.proxy.staticproxy;

public class StaticTankProxy implements Movable{
    private Movable movable;
    public StaticTankProxy(Movable movable) {
        this.movable=movable;
    }

    @Override
    public void move() {
        System.out.println(System.currentTimeMillis()+"");
        movable.move();
        System.out.println(System.currentTimeMillis()+"");
    }
}
