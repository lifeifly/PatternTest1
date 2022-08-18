package com.lifly.pattern.factory;

/**
 * 简单工厂
 */
public class SimpleFactory {

    public Car createCar() {
        return new Car();
    }

    public Plane createPlane() {
        return new Plane();
    }


}
