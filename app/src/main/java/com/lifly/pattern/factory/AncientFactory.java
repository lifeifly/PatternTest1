package com.lifly.pattern.factory;

public class AncientFactory extends AbsFactory{
    @Override
    Transport createTransport() {
        return new Horse();
    }

    @Override
    Foodable createFood() {
        return new Rice();
    }
}
