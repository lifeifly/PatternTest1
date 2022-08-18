package com.lifly.pattern.command;

public class InsertCommand extends Command {
    Content c;
    String strToInsert = "asfasfasd";

    @Override
    public void doit() {
        c.msg = c.msg + strToInsert;
    }

    @Override
    public void undo() {
        c.msg = c.msg.substring(0, c.msg.length() - strToInsert.length());
    }
}
