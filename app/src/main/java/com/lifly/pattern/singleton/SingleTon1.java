package com.lifly.pattern.singleton;

/**
 * 懒汉式，线程安全
 * 缺点：不管用到与否，类加载就会初始化实例
 */
public class SingleTon1 {

    private static final SingleTon1 INSTANCE=new SingleTon1();

    private SingleTon1(){}

    public static SingleTon1 getInstance(){
        return INSTANCE;
    }
}
