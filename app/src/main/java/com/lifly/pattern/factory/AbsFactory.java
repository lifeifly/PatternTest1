package com.lifly.pattern.factory;

public abstract class AbsFactory {

    abstract Transport createTransport();


    abstract Foodable createFood();
}
