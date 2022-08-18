package com.lifly.pattern.command;

import android.content.Context;

public class DeleteCommadn extends Command{
    Content c;
    String deleted;
    @Override
    public void doit() {
        deleted=c.msg.substring(0,5);
        c.msg=c.msg.substring(5);
    }

    @Override
    public void undo() {
        c.msg=c.msg+deleted;
    }
}
