package com.lifly.pattern.command;

/**
 * 封装一个个命令
 */
public abstract class Command {
    public abstract void doit();
    public abstract void undo();
}
