package com.lifly.pattern.strategy;

import java.util.Comparator;

public class DogComparator implements Comparator<Cat> {

    @Override
    public int compare(Cat o1, Cat o2) {
        return o1.age - o2.age;
    }
}
