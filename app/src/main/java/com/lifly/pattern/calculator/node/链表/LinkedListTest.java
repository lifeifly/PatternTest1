
package com.lifly.pattern.calculator.node.链表;

import java.util.Stack;

public class LinkedListTest {

    public static class Node {
        public int value;
        public Node next;

        public Node(int value) {
            this.value = value;
        }
    }

    /**
     * 快慢指针
     * 输入链表头节点，奇数长度返回中点，偶数长度返回上中点
     *
     * @return
     */
    public static Node findMidOrUpMid(Node head) {
        if (head == null || head.next == null || head.next.next == null) {
            return head;
        }
        Node slow = head.next;
        Node fast = head.next.next;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    /**
     * 快慢指针
     * 输入链表头节点，奇数长度返回中点，偶数长度返回下中点
     *
     * @return
     */
    public static Node findMidOrDownMid(Node head) {
        if (head == null || head.next == null) {
            return head;
        }
        Node slow = head.next;
        Node fast = head.next;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    /**
     * 快慢指针
     * 输入链表头节点，奇数长度返回中点的前一个，偶数长度返回上中点的前一个
     *
     * @return
     */
    public static Node findMidOrUpMidPre(Node head) {
        if (head == null || head.next == null || head.next.next == null) {
            return head;
        }
        Node slow = head;
        Node fast = head.next.next;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    /**
     * 快慢指针
     * 输入链表头节点，奇数长度返回中点的前一个，偶数长度返回下中点的前一个
     *
     * @return
     */
    public static Node findMidOrDownMidPre(Node head) {
        if (head == null || head.next == null) {
            return head;
        }
        if (head.next.next == null) {
            return head;
        }
        Node slow = head;
        Node fast = head.next;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    /**
     * 给定头节点Head判断链表是否为回文结构
     * 两边对称
     * 优化：快慢指针找到中点，将右半部分压入栈中，在依次弹出比较
     */
    public static boolean isRing(Node head) {
        //依次放到栈中
        Stack<Node> stack = new Stack<>();
        Node cur = head;
        while (cur != null) {
            stack.push(cur);
            cur = cur.next;
        }
        //依次从栈中逆序弹出和链表比较
        cur = head;
        while (!stack.isEmpty()) {
            if (stack.pop().value != cur.value) {
                return false;
            }
            cur = cur.next;
        }
        return true;
    }

    /**
     * O（1）额外空间复杂度判断是不是回文结构
     */
    public static boolean isPlindrome(Node head) {
        if (head == null || head.next == null) {
            return true;
        }
        //快慢指针找到中点或偶数的上中点
        Node slow = head;
        Node fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        //将中点的后半段开始逆序
        Node pre = null;
        Node next = null;
        Node head1 = slow;
        while (head1 != null) {
            next = head1.next;
            head1.next = pre;
            pre = head1;
            head1 = next;
        }
        Node n = head;
        while (n != null) {
            System.out.println(n.value);
            n = n.next;
        }
        n = pre;
        while (n != null) {
            System.out.println(n.value);
            n = n.next;
        }
        //左右两边依次比较
        boolean result = true;
        System.out.println(head.value + "pre:" + pre.value);
        Node n1 = head;
        Node n2 = pre;
        //任一到达空位置则代表比较一直顺利到结束
        while (n1 != null && n2 != null) {
            System.out.println(n1.value + "n2:" + n2.value);
            if (n1.value != n2.value) {
                result = false;
                break;
            }
            n1 = n1.next;
            n2 = n2.next;
        }
        //将逆序的部分恢复到以前的结构
        head1 = pre;
        next = null;
        pre = null;
        while (head1 != null) {
            next = head1.next;
            head1.next = pre;
            pre = head1;
            head1 = next;
        }
        return result;
    }

    /**
     * 给定链表头节点，小于pivot在左边，等于pivot在中间，大于在右边
     * 思路：先把元素加到数组，然后按快速排序的partion分隔，然后重新串成新链表
     *
     * @param head
     * @param pivot
     * @return
     */
    public static Node listPartion(Node head, int pivot) {
        if (head == null || head.next == null) {
            return head;
        }
        //计算需要开辟的大小
        int i = 0;
        Node cur = head;
        while (cur != null) {
            i++;
            cur = cur.next;
        }
        //依次加入到数组
        Node[] nodes = new Node[i];
        i = 0;
        cur = head;
        for (; i < nodes.length; i++) {
            Node node = new Node(cur.value);
            nodes[i] = node;
            cur = cur.next;
        }
        //partion荷兰国旗
        partion(nodes, pivot);
        //重新串成新链表
        Node pre = null;
        for (int j = nodes.length - 1; j >= 0; j--) {
            nodes[j].next = pre;
            pre = nodes[j];
        }
        return pre;
    }

    /**
     * 跟快速排序partion类似
     *
     * @param nodes
     * @param pivot
     */
    private static void partion(Node[] nodes, int pivot) {
        int smaller = -1;
        int bigger = nodes.length;
        int i = 0;
        while (i < bigger) {
            if (nodes[i].value > pivot) {
                //和大于区的前一个交换
                Node temp = nodes[i];
                --bigger;
                nodes[i] = nodes[bigger];
                nodes[bigger] = temp;
                //索引不加，因为此时交换的node还没检查
            } else if (nodes[i].value == pivot) {
                //索引向前，开始检查下一个
                i++;
            } else {
                //和小于区后一个交换，同时小于区扩大，小于区后一个本身就是大于等于pivot的,不需检查，直接检查下一个
                ++smaller;
                Node temp = nodes[i];
                nodes[i] = nodes[smaller];
                nodes[smaller] = temp;
                i++;
            }
        }
    }

    /**
     * 给定链表头节点，小于pivot在左边，等于pivot在中间，大于在右边
     * 思路：分别遍历传承小于区链表，等于区链表，大于区链表，最后串一起
     */
    public static Node divi(Node head, int pivot) {
        Node sH = null;
        Node sE = null;
        Node eH = null;
        Node eE = null;
        Node bH = null;
        Node bE = null;
        Node cur = null;
        while (head != null) {
            cur = head;
            head = head.next;
            cur.next = null;
            if (cur.value == pivot) {
                if (eH == null) {
                    eH = cur;
                    eE = cur;
                } else {
                    eE.next = cur;
                    eE = eE.next;
                }
            } else if (cur.value < pivot) {
                if (sH == null) {
                    sH = cur;
                    sE = cur;
                } else {
                    sE.next = cur;
                    sE = sE.next;
                }
            } else {
                if (bH == null) {
                    bH = cur;
                    bE = cur;
                } else {
                    bE.next = cur;
                    bE = bE.next;
                }
            }

        }
        //串起来
        if (sE != null) {
            sE.next = eH;
            eE = eE == null ? sE : eE;
        }
        if (eE != null) {
            eE.next = bH;
        }
        return sH != null ? sH : (eH != null ? eH : bH);
    }

    /**
     * 两个可能有环可能无环的单链表，头节点head1、head2，如果两个链表相交，请返回第一个相交的节点，不相交返回null
     */
    public static Node findIntersectNode(Node head1,Node head2){
        if (head1==null||head2==null){
            return null;
        }
        Node loop1=getLoopNode(head1);
        Node loop2=getLoopNode(head2);
        if (loop1==null&&loop2==null){
            return noLoop(head1,head2);
        }
        if (loop1!=null&&loop2!=null){
            return bothLoop(head1,loop1,head2,loop2);
        }
        //一个有环一个无环必无交点
        return null;
    }

    /**
     * 给定链表头节点head，如果有环返回第一个入环节点，无环则返回空
     * 思路：1.可以用hashset2.快慢指针
     * @param head
     * @return
     */
    public static Node getLoopNode(Node head){
        if (head==null||head.next==null||head.next.next==null){
            return null;
        }
        //快慢指针
        Node slow=head.next;
        Node fast=head.next.next;
        while (slow!=fast){//如果快慢指针相遇暂停
            if (fast.next==null||fast.next.next==null){
                //期间快指针快走完了，代表无环
                return null;
            }
            slow=slow.next;
            fast=fast.next.next;
        }
        //到此代表有环，让快指针从头开始每步进一，慢指针从原位置每步进一,相遇的位置就是第一个入环位置
        fast=head;
        while (fast!=slow){
            fast=fast.next;
            slow=slow.next;
        }
        return fast;
    }

    /**
     * 两个链表都无环，返回第一个相交节点，不相交返回null
     * @return
     */
    public static Node noLoop(Node head1,Node head2){
        if (head1==null||head2==null){
            return null;
        }
        //相差的n
        int n=1;
        Node n1=head1;
        while (n1.next!=null){
            n++;
            n1=n1.next;
        }
        Node n2=head2;
        while (n2.next!=null){
            n--;
            n2=n2.next;
        }
        //最后两个链表末尾如果不相等就一定无环
        if (n1!=n2){
            return null;
        }
        n1=n>0?head1:head2;//谁长谁是n1
        n2=n1==head1?head2:head2;//短的就是n2
        //对n取绝对值
        n=Math.abs(n);
        //让长的先跑n
        while (n!=0){
            n--;
            n1=n1.next;
        }
        //两个一起跑，相等时就是相交
        while (n1!=n2){
            n1=n1.next;
            n2=n2.next;
        }
        return n1;
    }

    /**
     * 两个链表都有环,一定公用环,返回第一个相交节点
     */
    public static Node bothLoop(Node head1,Node loop1,Node head2,Node loop2){
        Node n1=null;
        Node n2=null;
        if (loop1==loop2){
            //此种情况，一定相交，需要确定是在环外相交还是环上相交
            n1=head1;
            n2=head2;
            int n=1;
            while (n1!=loop1){
                n++;
                n1=n1.next;
            }
            while (n2.next!=loop2){
                n--;
                n2=n2.next;
            }
            if (n==0){
                //此时一定是入环处就是相交处
                return loop1;
            }
            //长度给n1，短的给n2
            n1=n>0?head1:head2;
            n2=n1==head1?head2:head1;
            //此时得到无环的差值,让长的先跑
            n=Math.abs(n);
            while (n!=0){
                n--;
                n1=n1.next;
            }
            //一起跑，相等处就是相交处
            while (n1!=n2){
                n1=n1.next;
                n2=n2.next;
            }
            return n1;
        }else {
            //此时只剩两种情况，1.不相交，2在环的非同一位置相交
            //让loop1一直走，如果走一圈还是没遇到loop2，就是不相交，否则loop1和loop2都可以是第一个相交节点
            n1=loop1.next;
            while (n1!=loop1){
                if (n1==loop2){
                    return loop1;//loop1\loop2都行
                }
                n1=n1.next;
            }
            return null;
        }
    }

    /**
     * 怎么不给head，只给想要删除的节点target，去删除target
     * 思路：将后一个覆盖前一个，连上后一个的后一个
     * 问题很多，最好不要
     */
    public static void deleteNoHead(Node target){

    }


    public static void main(String[] args) {
        Node node = new Node(1);
        Node node1 = new Node(3);
        Node node2 = new Node(2);
        Node node3 = new Node(4);
        Node node4 = new Node(5);
        node.next = node1;
        node1.next = node2;
        node2.next = node3;
        node3.next = node4;

//        System.out.println(listPartion(node,3));
        Node n = divi(node, 3);
        while (n != null) {
            System.out.println(n.value);
            n = n.next;
        }
    }
}
