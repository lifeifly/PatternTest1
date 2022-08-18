package com.lifly.pattern.calculator.node.二叉树;

import android.widget.ThemedSpinnerAdapter;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public class BinaryNode {
    public int value;
    public BinaryNode left;
    public BinaryNode right;

    public BinaryNode(int value) {
        this.value = value;
    }

    /**
     * 先序遍历
     */
    public static void pre(BinaryNode node) {
        if (node == null) {
            return;
        }
        //先打印头节点
        System.out.println(node.value);
        //处理左子树
        pre(node.left);
        //处理右子树
        pre(node.right);
    }

    /**
     * 中序遍历
     */
    public static void mid(BinaryNode node) {
        if (node == null) {
            return;
        }
        //处理左子树
        pre(node.left);
        //打印头节点
        System.out.println(node.value);
        //处理右子树
        pre(node.right);
    }

    /**
     * 后序遍历
     */
    public static void after(BinaryNode node) {
        if (node == null) {
            return;
        }
        //处理左子树
        pre(node.left);
        //处理右子树
        pre(node.right);
        //打印头节点
        System.out.println(node.value);
    }

    /**
     * 非递归先序
     *
     * @param head
     */
    public static void preNoRecursive(BinaryNode head) {
        if (head == null) return;
        Stack<BinaryNode> stack = new Stack<>();
        //先压入头节点
        stack.add(head);
        //栈不为空就打印
        while (!stack.isEmpty()) {
            BinaryNode node = stack.pop();
            //先打印
            System.out.println(node.value);
            //有右子树先压入
            if (node.right != null) {
                stack.add(node.right);
            }
            //有左子树在压左子树
            if (node.left != null) {
                stack.add(node.left);
            }
        }

    }

    /**
     * 非递归中序
     *
     * @param head
     */
    public static void midNoRecursive(BinaryNode head) {
        if (head == null) return;
        Stack<BinaryNode> stack = new Stack<>();
        BinaryNode cur = head;
        while (!stack.isEmpty() || cur != null) {
            if (cur != null) {
                //先压头
                stack.push(cur);
                //跳到左节点
                cur = cur.left;
            } else {
                //当前左子树没有了
                //先打印
                cur = stack.pop();
                System.out.println(cur.value);
                //在跳到右子树
                cur = cur.right;
            }
        }
    }

    /**
     * 非递归后序
     *
     * @param head
     */
    public static void afterNoRecursive(BinaryNode head) {
        if (head == null) return;
        Stack<BinaryNode> stack = new Stack<>();
        Stack<BinaryNode> stack1 = new Stack<>();
        //先压入头节点
        stack.push(head);
        while (!stack.isEmpty()) {
            BinaryNode node = stack.pop();
            stack1.push(node);
            if (head.left != null) {
                stack1.push(head.left);
            }
            if (head.right != null) {
                stack1.push(head.right);
            }
        }
        while (!stack1.isEmpty()) {
            BinaryNode node = stack1.pop();
            System.out.println(node.value);
        }
    }

    /**
     * 非递归后序
     *
     * @param head
     */
    public static void afterNoRecursive1(BinaryNode head) {
        if (head == null) return;
        Stack<BinaryNode> stack = new Stack<>();
        BinaryNode handled = head;//标记打印处理过的节点
        BinaryNode cur = null;
        //先压头
        stack.push(handled);
        while (!stack.isEmpty()) {
            //先让cur指向当前栈顶
            cur = stack.peek();
            if (cur.left != null && handled != cur.left && handled != cur.right) {//说明左右两个子树都没处理,先压入左子树
                stack.push(cur.left);
            } else if (cur.right != null && handled != cur.right) {//只有右子树了，且右子树没被处理
                stack.push(cur.right);
            } else {
                //左右子树都没有，代表到了底部
                //弹出并打印
                System.out.println(stack.pop().value);
                //标记到打印位置
                handled = cur;
            }
        }
    }

    /**
     * 宽度优先遍历打印
     */
    public static void widthPriority(BinaryNode head) {
        if (head == null) return;
        Queue<BinaryNode> queue = new LinkedList<>();
        //先加入头节点
        queue.add(head);
        while (!queue.isEmpty()) {
            //先弹出打印
            BinaryNode node = queue.poll();
            System.out.println(node.value);
            //有左加左
            if (node.left != null) {
                queue.add(node.left);
            }
            //有右加右
            if (node.right != null) {
                queue.add(node.right);
            }
        }
    }

    /**
     * 宽度优先遍历打印,获取最宽层的宽度,用map
     */
    public static int widthPriorityMap(BinaryNode head) {
        if (head == null) return 0;
        Queue<BinaryNode> queue = new LinkedList<>();
        //记录当前节点对应层
        Map<BinaryNode, Integer> levelMap = new HashMap<>();
        //先加入头节点
        queue.add(head);
        //设置头节点的层数
        levelMap.put(head, 1);
        //当前层
        int curLevel = 1;
        //当前层的总共的节点数量，只在从队列弹出时添加
        int curLevelCount = 0;
        //记录最大层的节点数
        int max = 0;
        while (!queue.isEmpty()) {
            //先弹出打印
            BinaryNode node = queue.poll();
            //获取当前弹出节点的层数
            int curNodeLevel = levelMap.get(node);
            System.out.println(node.value);
            //有左加左
            if (node.left != null) {
                queue.add(node.left);
                //层数+1
                levelMap.put(node.left, curNodeLevel + 1);
            }
            //有右加右
            if (node.right != null) {
                queue.add(node.right);
                //层数+1
                levelMap.put(node.left, curNodeLevel + 1);
            }
            if (curNodeLevel == curLevel) {
                curLevelCount++;
            } else {//换层了
                max = Math.max(max, curLevelCount);
                curLevel++;
                curLevelCount = 1;
            }
        }
        //最后没换层，少一次比较
        max = Math.max(max, curLevelCount);
        return max;
    }

    /**
     * 深度优先遍历打印
     */
    public static int depthPriorityNoMap(BinaryNode head) {
        if (head == null) return 0;
        Queue<BinaryNode> queue = new LinkedList<>();
        //先加入头节点
        queue.add(head);
        //队列中当前层的最后节点
        BinaryNode curEnd = head;
        //队列中下一层的最后节点
        BinaryNode nextEnd = null;
        //最大值
        int max = 0;
        //当前层的节点数量,弹出时在计算
        int curLevelCount = 0;
        while (!queue.isEmpty()) {
            //弹出当前节点
            BinaryNode node = queue.poll();
            //有左加左,同时更新队列中下一层的最后节点
            if (node.left != null) {
                queue.add(node.left);
                nextEnd = node.left;
            }
            //有右加右，同时更新队列中下一层的最后节点
            if (node.right != null) {
                queue.add(node.right);
                nextEnd = node.right;
            }
            //当前层节点数量+1
            curLevelCount++;
            //如果此时弹出的节点是当前层的最后节点，结算并换层
            if (curEnd == node) {
                max = Math.max(max, curLevelCount);
                //换层
                curLevelCount = 0;
                curEnd = nextEnd;
            }
        }
        return max;
    }

    /**
     * 先序序列化
     */
    public static void preSerial(BinaryNode head) {
        if (head == null) return;
        //将序列化结果放到队列中
        Queue<String> queue = new LinkedList<>();
        preSerial(head, queue);
    }

    private static void preSerial(BinaryNode cur, Queue<String> queue) {
        //如果当前节点是空就添加null
        if (cur == null) {
            queue.add(null);
        } else {
            //当前节点不为空，先加入当前节点，在去序列化左右子树
            queue.add(String.valueOf(cur.value));
            preSerial(cur.left, queue);
            preSerial(cur.right, queue);
        }
    }

    /**
     * 先序反序列化二叉树
     */
    public static BinaryNode buildByPreQueue(Queue<String> queue) {
        if (queue == null || queue.size() == 0) {
            return null;
        }
        return preb(queue);
    }

    private static BinaryNode preb(Queue<String> queue) {
        String value = queue.poll();
        if (value == null) {
            return null;
        }
        BinaryNode head = new BinaryNode(Integer.valueOf(value));
        head.left = preb(queue);
        head.right = preb(queue);
        return head;
    }

    /**
     * 宽度优先序列化
     */
    public static Queue<String> widthSerial(BinaryNode head) {
        Queue<String> ans = new LinkedList<>();
        if (head == null) {
            ans.add(null);
        } else {
            Queue<BinaryNode> queue = new LinkedList<>();

            queue.add(head);
            ans.add(String.valueOf(head.value));
            while (!queue.isEmpty()) {
                BinaryNode node = queue.poll();

                if (node.left != null) {
                    //加入左子树，并序列化
                    queue.add(node.left);
                    ans.add(String.valueOf(node.left.value));
                } else {
                    //只序列化
                    ans.add(null);
                }
                if (node.right != null) {
                    //加入右子树，并序列化
                    queue.add(node.right);
                    ans.add(String.valueOf(node.right.value));
                } else {
                    //只序列化
                    ans.add(null);
                }
            }
        }
        return ans;
    }

    /**
     * 宽度优先反序列化
     *
     * @return
     */
    public static BinaryNode buildByWidthQueue(Queue<String> queue) {
        if (queue == null || queue.size() == 0) {
            return null;
        }
        return widthBuild(queue);
    }

    private static BinaryNode widthBuild(Queue<String> queue) {
        String value = queue.poll();

        BinaryNode head = generateNode(value);
        if (head == null) {
            return null;
        }
        Queue<BinaryNode> queue1 = new LinkedList<>();
        queue1.add(head);
        BinaryNode node = null;
        while (!queue1.isEmpty()) {
            node = queue1.poll();
            node.left =generateNode(queue.poll());
            node.right=generateNode(queue.poll());
            if (node.left!=null){
                queue1.add(node.left);
            }
            if (node.right!=null){
                queue1.add(node.right);
            }
        }
        return head;
    }

    private static BinaryNode generateNode(String value) {
        if (value == null) return null;
        return new BinaryNode(Integer.valueOf(value));
    }


    /**
     * 设计一个函数打印二叉树结构
     */
    public static void printInOrder(BinaryNode head){
        printInOrder(head,0,"H",17);
    }

    private static void printInOrder(BinaryNode node, int height, String type, int totalLength) {
        if (node==null)return;
        //先去处理右树
        printInOrder(node.right,height+1,"v",totalLength);

        //打印的值
        String value=type+node.value+type;
        //计算左右各留多少空格
        int lenV=value.length();
        int leftSpace=(totalLength-lenV)/2;
        int rightSpace=totalLength-leftSpace-lenV;
        value=getSpace(leftSpace)+value+getSpace(rightSpace);
        //打印
        System.out.println(getSpace(totalLength*height)+value);

        //最后处理左树
        printInOrder(node.left,height+1,"^",totalLength);
    }

    /**
     * 根据空格数量返回字符串
     * @return
     */
    public static String getSpace(int len){
        String space=" ";
        StringBuffer sb=new StringBuffer();
        for (int i = 0; i < len; i++) {
            sb.append(space);
        }
        return sb.toString();
    }
    /**
     * 一个纸条不断对折，给了n次对折，最后按照从上到下的顺序打印凸或凹,就是二叉树的中序遍历
     */
    public static void printAllFolds(int n){
        //来到头节点，是凹折痕
        printProcess(1,n,true);
    }

    /**
     * 递归方法
     * @param i 第几次对折
     * @param n 总共对折几次
     * @param down 是否是凹折痕
     */
    private static void printProcess(int i, int n, boolean down) {
        if (i>n){
            return;
        }
        printProcess(i+1,n,true);//上方是凹，下方是凸
        //打印
        System.out.println(down?"凹":"凸");
        printProcess(i+1,n,false);
    }
    public static void main(String[] args) {
//        BinaryNode head=new BinaryNode(1);
//        BinaryNode node1=new BinaryNode(2);
//        BinaryNode node2=new BinaryNode(3);
//        BinaryNode node3=new BinaryNode(4);
//        BinaryNode node4=new BinaryNode(5);
//        BinaryNode node5=new BinaryNode(6);
//
//        head.left=node1;
//        head.right=node2;
//        node1.left=node3;
//        node2.left=node4;
//        node2.right=node5;
//
//        printInOrder(head);
        printAllFolds(3);
    }
}
