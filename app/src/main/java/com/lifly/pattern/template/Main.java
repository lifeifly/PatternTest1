package com.lifly.pattern.template;

public class Main {
    public static void main(String[] args) {
        F f=new M();
        f.m();
    }
}
abstract class F{
    void m(){
        op1();
        op2();
    }

    protected abstract void op2();

    protected abstract void op1();
}

class M extends F{

    @Override
    protected void op2() {

    }

    @Override
    protected void op1() {

    }
}