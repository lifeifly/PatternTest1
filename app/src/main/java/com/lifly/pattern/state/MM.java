package com.lifly.pattern.state;

public class MM {
    String name;
    MMState state;

    public MM(MMState state) {
        this.state = state;
    }

    public void smile(){
        state.smile();
    }
    public void cry(){
        state.cry();
    }
    public void say(){
        state.say();
    }
}
