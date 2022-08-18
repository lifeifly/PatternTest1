package com.lifly.pattern.calculator.node.链表;

public class NodeTest {
    public static void main(String[] args) {
        SingleNode singleNode = new SingleNode();
        singleNode.data = 0;
        SingleNode singleNode1 = new SingleNode();
        singleNode.data = 1;
        singleNode.next = singleNode1;
        SingleNode singleNode2 = new SingleNode();
        singleNode.data = 2;
        singleNode1.next = singleNode2;
        SingleNode singleNode3 = new SingleNode();
        singleNode.data = 3;
        singleNode2.next = singleNode3;
        SingleNode singleNode4 = new SingleNode();
        singleNode.data = 4;
        singleNode3.next = singleNode4;

        SingleNode head = singleNode;
        while (head != null) {

            System.out.println("处理前" + head.data);
            head = head.next;
        }
        reverseSingleNode(singleNode);

        head = singleNode4;
        while (head != null) {

            System.out.println("处理后" + head.data);
            head = head.next;
        }
    }

    /**
     * 单链表反转
     */
    public static SingleNode reverseSingleNode(SingleNode head) {
        SingleNode next = null;
        SingleNode pre = null;
        while (head != null) {
            next = head.next;
            head.next = pre;
            pre = head;
            head = next;
        }
        return pre;
    }

    /**
     * 单链表删除给定的值
     */
    public static SingleNode delete(SingleNode head, int target) {
        while (head!=null){//过滤掉头部给定的值，以第一个不是给定值的节点为头部
            if (head.data!=target){
                break;
            }
            head=head.next;
        }
        SingleNode pre=head;
        SingleNode cur=head;
        while (cur!=null){
            if (cur.data==target){//pre一定不是目标位置
                pre.next=cur.next;
            }else {
                pre=cur;
            }
            cur=cur.next;
        }
        return head;
    }

    /**
     * 双链表反转
     */
    public static DoubleNode reverseDoubleNode(DoubleNode head) {
        DoubleNode next = null;
        DoubleNode pre = null;
        while (head != null) {
            next = head.next;
            head.next = pre;
            head.last = next;
            pre = head;
            head = next;
        }
        return pre;
    }

}
