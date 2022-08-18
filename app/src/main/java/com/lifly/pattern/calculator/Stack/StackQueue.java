package com.lifly.pattern.calculator.Stack;

import java.util.Stack;

/**
 * 用栈结构实现队列
 */
public class StackQueue {

    private Stack<Integer> data;
    private Stack<Integer> help;

    public StackQueue() {
        data = new Stack<>();
        help = new Stack<>();
    }

    public void push(int value) {
        data.push(value);
    }

    public int pop(){
        while (!data.isEmpty()){
            help.push(data.pop());
        }
        int value=help.pop();
        while (!help.isEmpty()){
            data.push(help.pop());
        }
        return value;
    }
}
