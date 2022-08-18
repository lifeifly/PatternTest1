package com.lifly.pattern.visitor;

public class Computer {
    ComputerPart cpu=new CPU();
    ComputerPart memory=new Memory();
    ComputerPart board=new Board();

    public void accept(Visitor v){
        this.cpu.accept(v);
        this.memory.accept(v);
        this.board.accept(v);
    }

    public static void main(String[] args) {
        PersonelVisitor p=new PersonelVisitor();
        new Computer().accept(p);
        System.out.println(p.totalPrice);
    }
}

abstract class ComputerPart{
    abstract void accept(Visitor v);
    abstract double getPrice();
}

class CPU extends ComputerPart{

    @Override
    void accept(Visitor v) {
        v.visitCpu(this);
    }

    @Override
    double getPrice() {
        return 500;
    }
}
class Memory extends ComputerPart{

    @Override
    void accept(Visitor v) {
        v.visitMemory(this);
    }

    @Override
    double getPrice() {
        return 100;
    }
}
class Board extends ComputerPart{

    @Override
    void accept(Visitor v) {
        v.visitBoard(this);
    }

    @Override
    double getPrice() {
        return 200;
    }
}

interface Visitor{
    void visitCpu(CPU cpu);
    void visitMemory(Memory memory);
    void visitBoard(Board board);
}

class PersonelVisitor implements Visitor{
    double totalPrice=0.0;
    @Override
    public void visitCpu(CPU cpu) {
        totalPrice+=cpu.getPrice();
    }

    @Override
    public void visitMemory(Memory memory) {
        totalPrice+=memory.getPrice();
    }

    @Override
    public void visitBoard(Board board) {
        totalPrice+=board.getPrice();
    }
}