package com.lifly.pattern.factory;

/**
 * 静态工厂
 */
public class TransportFactory {
    public static Transport create(int type){
        switch (type){
            case 1:
                return new Car();
            case 2:
                return new Plane();
        }
        return null;
    }
}
