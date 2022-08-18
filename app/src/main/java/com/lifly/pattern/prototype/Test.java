package com.lifly.pattern.prototype;

import androidx.annotation.NonNull;

import java.io.Closeable;

public class Test {
    public static void main(String[] args) throws CloneNotSupportedException {
        Person p1 = new Person();
        Person p2= (Person) p1.clone();
    }
}

class Person implements Cloneable {
    int age = 8;
    int score = 100;
    Location loc = new Location("bj", 22);

    @Override
    public Object clone() throws CloneNotSupportedException {
        Person p= (Person) super.clone();
        p.loc= (Location) loc.clone();
        return p;
    }
}

class Location implements Cloneable {
    String address;
    int age;

    public Location(String address, int age) {
        this.address = address;
        this.age = age;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}