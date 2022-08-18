package com.lifly.pattern.singleton;

/**
 * 饿汉式，用到才初始化实例
 * 线程不安全
 */
public class SingleTon2 {
    private static SingleTon2 INSTANCE;

    private SingleTon2() {
    }

    public static SingleTon2 getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SingleTon2();
        }
        return INSTANCE;
    }

    public static synchronized SingleTon2 getInstance1() {
        if (INSTANCE == null) {
            INSTANCE = new SingleTon2();
        }
        return INSTANCE;
    }

    public static SingleTon2 getInstance2() {
        if (INSTANCE == null) {
            synchronized (SingleTon2.class) {
                if (INSTANCE==null){
                    INSTANCE = new SingleTon2();
                }
            }
        }
        return INSTANCE;
    }
}
