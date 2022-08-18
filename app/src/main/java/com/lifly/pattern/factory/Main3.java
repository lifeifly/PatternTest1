package com.lifly.pattern.factory;

public class Main3 {

    public static void main(String[] args) {
        AbsFactory absFactory=new ModernFactory();
        Transport transport=absFactory.createTransport();
        transport.go();
        Foodable foodable=absFactory.createFood();
        foodable.eat();
    }
}
