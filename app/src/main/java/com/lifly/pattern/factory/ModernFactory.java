package com.lifly.pattern.factory;

public class ModernFactory extends AbsFactory{
    @Override
    Transport createTransport() {
        return new Car();
    }

    @Override
    Foodable createFood() {
        return new Bread();
    }
}
