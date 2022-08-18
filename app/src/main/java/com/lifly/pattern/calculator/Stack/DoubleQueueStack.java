package com.lifly.pattern.calculator.Stack;

import com.lifly.pattern.calculator.queue.DoubleNodeQueue;

public class DoubleQueueStack<T> {
    private DoubleNodeQueue<T> queue;

    public DoubleQueueStack() {
        queue=new DoubleNodeQueue<>();
    }

    public void push(T value){
        queue.addFromHead(value);
    }
    public T pop(){
        return queue.popFromHead();
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }
}
