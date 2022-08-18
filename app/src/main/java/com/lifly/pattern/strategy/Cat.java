package com.lifly.pattern.strategy;

/**
 * 这种是多态
 */
public class Cat implements Comparable<Cat>{
    public int age;
    public String name;


    @Override
    public int compareTo(Cat o) {
        return this.age-o.age;
    }
}
