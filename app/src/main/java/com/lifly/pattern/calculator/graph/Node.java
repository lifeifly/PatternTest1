package com.lifly.pattern.calculator.graph;

import java.util.ArrayList;

public class Node {
    public int value;
    public int in;//入度：有多少点直接连向自己的
    public int out;//出度：有多少连出去的
    public ArrayList<Node> nexts;
    public ArrayList<Edge> edges;

    public Node(int value) {
        this.value = value;
        in=0;
        out=0;
        nexts=new ArrayList<>();
        edges=new ArrayList<>();
    }
}
