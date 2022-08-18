package com.lifly.pattern.strategy;

import com.lifly.pattern.decorate.Frame;

public class Tank extends Frame {
    private static FireStrategy fs=new DefaultFireStrategy();
    public void shot(){
        fs.fire(this);
    }

    @Override
    public void paint() {

    }
}
