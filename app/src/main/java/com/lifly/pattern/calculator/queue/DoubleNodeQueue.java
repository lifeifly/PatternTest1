package com.lifly.pattern.calculator.queue;

public class DoubleNodeQueue<T> {


    public boolean isEmpty() {
        return head == null;
    }

    public static class Node<T> {
        public T value;
        public Node<T> last;
        public Node<T> next;

        public Node(T value) {
            this.value = value;
        }
    }

    public Node<T> head;
    public Node<T> tail;

    public void addFromHead(T value) {
        Node<T> cur = new Node<>(value);
        if (head == null) {
            head = cur;
            tail = cur;
        } else {
            cur.next = head;
            head.last = cur;
            head = cur;
        }
    }

    public T popFromHead() {
        Node<T> cur = head.next;
        head.next = null;
        head = cur;
        head.last = null;

        return head.value;
    }

    public void addFromBottom(T value) {
        Node<T> cur = new Node<>(value);
        if (head == null) {
            head = cur;
            tail = cur;
        } else {
            tail.next = cur;
            cur.last = tail;
            tail = cur;
        }
    }
}
