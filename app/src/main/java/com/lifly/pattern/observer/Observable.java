package com.lifly.pattern.observer;

import java.util.ArrayList;
import java.util.List;

public class Observable {
    private List<Observer> observers=new ArrayList<>();

    public void cry(){
        Event event=new Event(1,"w");
        for (Observer o:observers){
            o.know(event);
        }
    }

    public void add(Observer observer){
        observers.add(observer);
    }

    public void remove(Observer observer){
        observers.remove(observer);
    }
}
