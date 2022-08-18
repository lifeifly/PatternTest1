package com.lifly.pattern.calculator.Stack;

import java.util.Stack;

/**
 * 扩展栈得功能，可以获取最小值
 */
public class ExtentMinStack {
    private Stack<Integer> data;
    private Stack<Integer> min;

    public ExtentMinStack() {
        data=new Stack<>();
        min=new Stack<>();
    }

    public void push(int num){
        if (min.isEmpty()){
            min.push(num);
        }else if (num<getMin()){
            min.push(num);
        }else {
            min.push(getMin());
        }
        data.push(num);
    }
    public int pop(){
        if (data.isEmpty()){
            throw new RuntimeException("Stack is empty");
        }
        int value=data.pop();
        min.pop();
        return value;
    }

    public int getMin(){
        if (min.isEmpty()){
            throw new RuntimeException("Stack is empty");
        }
        return min.peek();
    }
}
