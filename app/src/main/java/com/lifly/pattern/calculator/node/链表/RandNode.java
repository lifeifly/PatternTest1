package com.lifly.pattern.calculator.node.链表;

import java.util.HashMap;

/**
 * rand可能指向内部某个节点也可能指向空
 */
public class RandNode {
    int value;
    RandNode next;
    RandNode rand;

    public RandNode(int value) {
        this.value = value;
    }

    /**
     * 给定以该节点组成的无环链表，怎么复制成新的链表，返回头节点
     *
     * @param head
     * @return
     */
    public static RandNode copy(RandNode head) {
        //存储节点和每个节点复制的节点
        HashMap<RandNode, RandNode> map = new HashMap<>();
        //第一次遍历，将每个节点复制对应放到map
        RandNode cur = head;
        while (cur != null) {
            map.put(cur, new RandNode(cur.value));
            cur = cur.next;
        }
        //第二次遍历，找到每个next\rand，给复制的节点连接上
        cur = head;
        while (cur != null) {
            map.get(cur).next = map.get(cur.next);
            map.get(cur).rand = map.get(cur.rand);
            cur = cur.next;
        }
        return map.get(head);
    }

    /**
     * 给定以该节点组成的无环链表，怎么复制成新的链表，返回头节点
     *
     * @param head
     * @return
     */
    public static RandNode copy1(RandNode head) {
        //依次复制并连接到原节点之间 1->1'->2
        RandNode cur=head;
        RandNode next=null;
        while (cur!=null){
            //先记录原链表的下一个
            next=cur.next;
            //复制
            RandNode copy=new RandNode(cur.value);
            //挂到原节点之后，原下一个节点之前
            cur.next=copy;
            copy.next=next;
            //跳到原下一个节点
            cur=next;
        }
        //找到对应的rand，给复制的元素挂上
        cur=head;
        while (cur!=null){
            //记录下一个原链表环境
            next=cur.next.next;
            if (cur.rand!=null){
                //原链表节点有rand，找到该rand下一个就是复制的节点rand
                cur.next.rand=cur.rand.next;
            }
            //跳转到原链表下一个节点
            cur=next;
        }
        //分离原链表和复制的链表,由于rand互不影响，只需通过next分离

        cur=head;
        RandNode res=cur.next;
        while (cur!=null){
            //记录原链表下一个节点环境
            next=cur.next.next;
            //记录当前复制的节点
            RandNode copy=cur.next;
            //原链表下一个节点挂到前一个原链表的节点
            cur.next=next;
            //复制节点的下一个挂到原链表下一个节点的下一个（即下一个复制节点,可能为空）
            copy.next=next==null?null:next.next;
            //跳转到下一个原链表节点
            cur=next;
        }
        return res;
    }

    public static void main(String[] args) {
        RandNode randNode1=new RandNode(1);
        RandNode randNode2=new RandNode(2);
        RandNode randNode3=new RandNode(3);
        RandNode randNode4=new RandNode(4);

        randNode1.next=randNode2;
        randNode2.next=randNode3;
        randNode3.next=randNode4;
        randNode1.rand=randNode2;
        randNode3.rand=randNode4;

        RandNode randNode=copy1(randNode1);
        RandNode cur=randNode;
        while (cur!=null){
            System.out.println("value"+cur.value);
            if (cur.rand!=null){
                System.out.println("rand"+cur.rand.value);
            }
            cur=cur.next;
        }
    }
}
