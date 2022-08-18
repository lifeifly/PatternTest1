package com.lifly.pattern.iterator;

import org.w3c.dom.Node;

public class Main {
    public static void main(String[] args) {
        ArrayList_ list_=new ArrayList_();
        for (int i = 0; i < 5; i++) {
            list_.add(i+"");
        }
    }
}
class ArrayList_ implements Collection_{
    Object[] objects=new Object[10];
    private int index=0;
    public void add(Object o){
        if (index== objects.length){
            Object[] newObjects=new Object[objects.length*2];
            System.arraycopy(objects,0,newObjects,0,objects.length);
            objects=newObjects;
        }
        objects[index]=o;
        index++;
    }

    public int size(){
        return index;
    }

    @Override
    public Iterator_ iterator() {
        return new ArrayListIterator();
    }

    private class ArrayListIterator implements Iterator_{
        private int currentIndex=0;
        @Override
        public boolean hasNext() {
            if (currentIndex>=index)return false;
            return true;
        }

        @Override
        public Object next() {
            Object o=objects[currentIndex];
            currentIndex++;
            return o;
        }
    }
}

class LinkedList_ implements Collection_{
    Node head=null;
    Node tail=null;

    private int size=0;
    public void add(Object o){
        Node n=new Node(o);
        n.next=null;
        if (head==null){
            head=n;
            tail=n;
        }
        tail.next=n;
        tail=n;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator_ iterator() {
        return null;
    }

    private class Node{
        private Object o;
        Node next;

        public Node(Object o) {
            this.o = o;
        }

    }
    private class LinkedListIterator implements Iterator_{

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            return null;
        }
    }
}