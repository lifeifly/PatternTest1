package com.lifly.pattern.calculator.queue;

public class ArrayQueue {
    private int[] pool;
    private int pushi;
    private int polli;
    private int size;
    private final int limit;

    public ArrayQueue(int limit) {
        this.limit = limit;
        pushi = 0;
        polli = 0;
        size = 0;
        pool = new int[limit];
    }

    public void push(int value) {
        if (size >= limit) return;
        size++;
        pool[pushi] = value;
        pushi = nextIndex(pushi);
    }

    public int pop() {
        if (size == 0) {
            throw new RuntimeException("栈空");
        }
        size--;
        int ans = pool[polli];
        polli = nextIndex(polli);
        return ans;
    }

    public boolean isEmpty(){
        return size==0;
    }
    //到了最后一个位置返回0，不在最后一个位置为i+1
    private int nextIndex(int i) {
        return i < limit - 1 ? i + 1 : 0;
    }


}
