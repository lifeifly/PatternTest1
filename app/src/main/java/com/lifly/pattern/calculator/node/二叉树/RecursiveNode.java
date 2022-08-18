package com.lifly.pattern.calculator.node.二叉树;

import java.util.List;
import java.util.Map;

public class RecursiveNode {
    public int value;
    public RecursiveNode left;
    public RecursiveNode right;

    public RecursiveNode(int value) {
        this.value = value;
    }

    /**
     * 给定一棵树头节点head，判断这个树是不是平衡二叉树(左树的高度和右树的高度不超过1)
     * 问左树要信息，是否平衡，高度
     */
    public static boolean isBst(RecursiveNode head) {
        return process1(head).isBalanced;
    }

    private static BstInfo process1(RecursiveNode X) {
        if (X == null) return new BstInfo(true, 0);
        //左树信息
        BstInfo leftInfo = process1(X.left);
        //右树信息
        BstInfo rightInfo = process1(X.right);
        //总结
        //这个节点的树的高度
        int height = Math.max(leftInfo.height, rightInfo.height) + 1;
        //判断是否是平衡的
        boolean isBalanced = true;
        if (!leftInfo.isBalanced || !rightInfo.isBalanced || Math.abs(leftInfo.height - rightInfo.height) > 1) {
            isBalanced = false;
        }
        return new BstInfo(isBalanced, height);
    }

    public static class BstInfo {
        public boolean isBalanced;
        public int height;

        public BstInfo(boolean isBalanced, int height) {
            this.isBalanced = isBalanced;
            this.height = height;
        }
    }

    /**
     * 给定一棵树头节点head，求整颗树最大的两个节点之间的距离
     * 可能性1：跟head无关，则是左树的最大距离和右树的最大距离取最大
     * 2：跟head有关，则左树的高度+1+右树的高度
     *
     * @return
     */
    public static int maxDistance(RecursiveNode head) {
        //问左右子树要信息：1.高度2.整颗树的最大距离
        return process2(head).maxDistance;
    }

    /**
     * @param head
     * @return
     */
    private static DistanceInfo process2(RecursiveNode head) {
        if (head == null) return new DistanceInfo(0, 0);
        //左子树信息
        DistanceInfo left = process2(head.left);
        //右子树信息
        DistanceInfo right = process2(head.right);
        //总结
        //整棵树的最大高度
        int height = Math.max(left.height, right.height) + 1;
        //比较最大距离
        int maxDistance = Math.max(Math.max(left.maxDistance, right.maxDistance), left.height + 1 + right.height);
        return new DistanceInfo(maxDistance, height);
    }

    public static class DistanceInfo {
        public int maxDistance;
        public int height;

        public DistanceInfo(int maxDistance, int height) {
            this.maxDistance = maxDistance;
            this.height = height;
        }
    }


    /**
     * 给定头节点head，返回二叉树中最大二叉搜索子树(整棵树没有重复值，左边都比头小，右边都比头大)的节点个数
     */
    public static int maxSearchNode(RecursiveNode head) {
        //要的信息1：左、右子树是否包含搜索二叉树2.左\右子树的最大值\最小值3.包含的搜索二叉树的头节点
        return process3(head).maxSearchSize;
    }

    public static SearchInfo process3(RecursiveNode X) {
        if (X == null) {
            return null;
        }
        //左子树信息
        SearchInfo left = process3(X.left);
        //右子树信息
        SearchInfo right = process3(X.right);
        //总结当前
        //当前的最大值
        int max = X.value;
        //当前最小值
        int min = X.value;
        if (left != null) {
            max = Math.max(max, left.max);
            min = Math.min(min, left.min);
        }
        if (right != null) {
            max = Math.max(max, right.max);
            min = Math.min(min, right.min);
        }
        //可能性1：跟X无关，只需得到左右子树中最大搜索二叉树节点个数
        int maxSearchSize = 0;
        if (left != null) {
            maxSearchSize = left.maxSearchSize;
        }
        if (right != null) {
            maxSearchSize = Math.max(maxSearchSize, right.maxSearchSize);
        }
        //可能性2：跟X有关
        //是否是搜索二叉树
        boolean isSearched = false;
        if ((left == null ? true : left.isSearched)
                &&
                (right == null ? true : right.isSearched)
                &&
                (right == null ? true : right.min > X.value)
                &&
                (left == null ? true : left.max < X.value)) {
            isSearched = true;
            maxSearchSize = (left == null ? 0 : left.maxSearchSize) + (right == null ? 0 : right.maxSearchSize) + 1;
        }
        return new SearchInfo(isSearched, max, min, maxSearchSize);
    }

    public static class SearchInfo {
        public boolean isSearched;
        public int max;
        public int min;
        public int maxSearchSize;

        public SearchInfo(boolean isSearched, int max, int min, int maxSearchSize) {
            this.isSearched = isSearched;
            this.max = max;
            this.min = min;
            this.maxSearchSize = maxSearchSize;
        }
    }

    class Employee {
        public int happy;
        public List<Employee> subordinates;//有哪些直接下级

    }

    /**
     * 老板是头节点，每个员工只有一个直接上级
     * 1.给员工发请柬，则这个员工的所有直接下级都不能来
     * 2.派对的整体快乐值是所有到场员工快乐值的累加
     * 3.你的目标是让派对的整体快乐值尽量大
     *
     * @return
     */
    public static int getMaxHappy(Employee boss) {
        //信息：1.X来的happy值2.X不来的happy值
        if (boss == null) return 0;
        HappyInfo info=process4(boss);
        return Math.max(info.yes,info.no);
    }

    /**
     *
     * @param X
     * @return
     */
    private static HappyInfo process4(Employee X) {
        if (X.subordinates.isEmpty()){
            return new HappyInfo(X.happy,0);
        }
        //所有子节点的来不来的最大快乐值
        int yes=X.happy;
        int no=0;
        for (int i = 0; i < X.subordinates.size(); i++) {
            //所有直接下级的信息
            HappyInfo info=process4(X.subordinates.get(i));
            yes+=info.no;
            no+=Math.max(info.yes,info.no);
        }
        return new HappyInfo(yes,no);
    }

    public static class HappyInfo{
        public int yes;//头节点来的时候返回的最大快乐值
        public int no;//头节点不来的时候返回的最大快乐值

        public HappyInfo(int yes, int no) {
            this.yes = yes;
            this.no = no;
        }
    }


    public static void main(String[] args) {

    }
}
