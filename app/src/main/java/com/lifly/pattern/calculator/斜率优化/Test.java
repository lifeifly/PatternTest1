package com.lifly.pattern.calculator.斜率优化;

import com.lifly.pattern.calculator.牛牛.Sweep;

import java.util.HashMap;
import java.util.Map;

public class Test {

    /**
     * 给定一个正数1，裂开的方法有一种，（1）
     * 给定一个正数2，裂开的方法有两种（1和1）（2）
     * 给定一个正数3，裂开的方法有3种（1和1和1）（1和2）（3）
     * 给定一个正数4，裂开的方法有5种（1，1，1，1）（1，1，2）(2,2)(1,3)(4)
     * 给定一个正数n，求裂开的方法数
     *
     * @return
     */
    //暴力递归
    public static int divider(int n) {
        if (n <= 0) {
            return 0;
        }
        return process(1, n);
    }

    /**
     * 分割n，但是分割的第一份不要比pre小
     *
     * @param pre
     * @param n
     * @return
     */
    private static int process(int pre, int n) {
        //basecase
        if (n == 0) {
            //分割完了
            return 1;
        }
        if (pre > n) {
            //当前第一份不够pre，此方案不通
            return 0;
        }
        int ans = 0;
        //枚举第一个大小的所有可能性
        for (int start = pre; start <= n; start++) {
            ans += process(start, n - start);
        }
        return ans;
    }

    //暴力递归改动态规划
    public static int dividerDp(int n) {
        if (n <= 0) {
            return 0;
        }
        //（i,j）代表分割j，但是第一份不能比i小
        int[][] dp = new int[n + 1][n + 1];
        //第一行用不到
        //初始化第一列
        for (int i = 1; i <= n; i++) {
            dp[i][0] = 1;
        }
        //从下往上，从对角线往右
        for (int pre = n; pre > 0; pre--) {
            for (int rest = pre; rest <= n; rest++) {
                //枚举第一份的可能性
                int res = 0;
                for (int first = pre; first <= rest; first++) {
                    res += dp[first][rest - first];
                }
                dp[pre][rest] = res;
            }
        }
        return dp[1][n];
    }

    //动态优化改斜率优化
    public static int dividerDpPriority(int n) {
        if (n <= 0) {
            return 0;
        }

        int[][] dp = new int[n + 1][n + 1];
        for (int i = 1; i <= n; i++) {
            dp[i][0] = 1;
        }
        for (int i = 1; i <= n; i++) {
            dp[i][i] = 1;
        }
        for (int pre = n - 1; pre > 0; pre--) {
            for (int rest = pre; rest <= n; rest++) {
                //斜率优化得右侧一个格加下面得格就是该格得值
                dp[pre][rest] = dp[pre + 1][rest] + dp[pre][rest - pre];
            }
        }
        return dp[1][n];
    }

    /**
     * 给定一个二叉树头节点head，求最大的切符合搜索二叉树条件的最大拓补结构的大小
     * 拓补结构：不是子树，只要能连的结构都算
     *
     * @return
     */
    public static int maxSearchNode(Node head) {
        //记录每个节点的左右子树能为该节点提供的拓补结构的节点大小
        Map<Node, Record> map = new HashMap<>();
        return postOrder(head, map);
    }

    private static int postOrder(Node head, Map<Node, Record> map) {
        //节点为空，不能提供拓补节点
        if (head == null) {
            return 0;
        }
        int ls = postOrder(head.left, map);
        int rs = postOrder(head.right, map);
        modifyMap(head.left, head.value, map, true);
        modifyMap(head.right, head.value, map, false);
        Record lr = map.get(head.left);
        Record rr = map.get(head.right);
        int lbst = lr == null ? 0 : lr.l + lr.r + 1;
        int rbst = rr == null ? 0 : rr.l + rr.r + 1;
        map.put(head, new Record(lbst, rbst));
        return Math.max(lbst + rbst + 1, Math.max(ls, rs));
    }

    private static int modifyMap(Node n, int value, Map<Node, Record> map, boolean isLeft) {
        if (n == null || (!map.containsKey(n))) {
            return 0;
        }
        Record r = map.get(n);
        if ((isLeft && n.value > value) || (!isLeft && n.value < value)) {
            map.remove(n);
            return r.l + r.r + 1;
        } else {
            int minus = modifyMap(isLeft ? n.right : n.left, value, map, isLeft);
            if (isLeft) {
                r.r = r.r - minus;
            } else {
                r.l = r.l - minus;
            }
            map.put(n, r);
            return minus;
        }
    }

    public static class Record {
        public int l;
        public int r;

        public Record(int l, int r) {
            this.l = l;
            this.r = r;
        }
    }

    public static class Node {
        public Node left;
        public Node right;
        public int value;

        public Node(int value) {
            this.value = value;
        }
    }

    public static void main(String[] args) {
        System.out.println(divider(8) + "/" + dividerDp(8) + "/" + dividerDpPriority(8));
    }
}
