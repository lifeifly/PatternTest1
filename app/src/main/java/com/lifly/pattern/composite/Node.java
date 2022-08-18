package com.lifly.pattern.composite;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
  public  abstract void p();
}
class LeafNode extends Node{
    String content;


    public LeafNode(String content) {
        this.content = content;
    }

    @Override
    public void p() {
        System.out.println(content);
    }
}

class BranchNode extends Node{
    List<Node> nodes=new ArrayList<>();

    String name;

    public BranchNode(String name) {
        this.name = name;
    }

    @Override
    public void p() {

    }

    public void add(Node node){
        nodes.add(node);
    }
}