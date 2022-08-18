package com.lifly.pattern.calculator.dp;

import android.content.Intent;

import androidx.appcompat.app.WindowDecorActionBar;

import com.lifly.pattern.calculator.graph.Node;
import com.lifly.pattern.calculator.node.链表.LinkedListTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public class DpTest1 {

    /**
     * 给定一个数组Arr，求差值为k的去重数字对
     */
    public static void map(int[] arr) {
        HashMap<Integer, Integer> s = new HashMap<>();
        HashMap<Integer, Integer> r = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            if (!s.containsKey(arr[i])) {
                s.put(arr[i], 1);
            } else {
                s.put(arr[i], s.get(arr[i]) + 1);
            }
        }
        for (int i = 0; i < arr.length; i++) {
            if (s.containsKey(arr[i] + 2)) {
                r.put(arr[i], arr[i] + 2);
            }
        }
    }

    /**
     * 给一个包含n个整数元素的集合a，一个包含m个整数元素的集合b
     * 定义magic操作为，从一个集合中取出一个元素，放到另一个集合里，且操作过后每个集合的平均值都大于操作前
     * 1.不可以把一个集合的元素取空，这样就没有平均值了
     * 2.值为x的元素从集合b取出放入集合a，但集合a中已经有值为x的元素，则a的平均值不变(不存在重复的)，b的平均值可能会改变
     * 问最多可以进行多少次magic操作
     * 分析后得知：A和B的平均值不能相等，且只能将平均值大的那个集合中拿出大于等于小的平均值且
     * 小于等于大的平均值才能满足要求，且只有依次拿最小的那个符合要求的，才能拿得最多
     *
     * @return
     */
    public static int magic(int[] arr1, int[] arr2) {
        //计算arr1得平均值
        double sum1 = 0;
        for (int i = 0; i < arr1.length; i++) {
            sum1 += arr1[i];
        }
        //计算arr2得平均值
        double sum2 = 0;
        for (int i = 0; i < arr2.length; i++) {
            sum2 += arr2[i];
        }
        //两个平均值相等不可能有结果
        if (average(sum1, arr1.length) == average(sum2, arr2.length)) {
            return 0;
        }
        double sumMore;
        double sumLess;
        int moreLength;
        int lessLength;
        int[] moreArr;
        int[] lessArr;
        if (average(sum1, arr1.length) > average(sum2, arr2.length)) {
            moreArr = arr1;
            sumMore = sum1;
            moreLength = arr1.length;
            lessArr = arr2;
            sumLess = sum2;
            lessLength = arr2.length;
        } else {
            moreArr = arr2;
            sumMore = sum2;
            moreLength = arr2.length;
            lessArr = arr1;
            sumLess = sum1;
            lessLength = arr1.length;
        }

        //对平均值大得进行排序
        Arrays.sort(moreArr);
        //将平均值小数组得元素都记录下来
        HashSet<Integer> set = new HashSet<>();
        for (int i = 0; i < lessLength; i++) {
            set.add(lessArr[i]);
        }
        //依次枚举平均值大得数组得元素
        int ops = 0;
        for (int i = 0; i < moreLength; i++) {
            int cur = moreArr[i];
            if (cur > average(sumLess, lessLength) && cur < average(sumMore, moreLength) && !set.contains(cur)) {
                sumMore -= cur;
                moreLength--;
                sumLess += cur;
                lessLength++;
                set.add(cur);
                ops++;
            }
        }
        return ops;
    }

    public static double average(double total, int size) {
        return total / size;
    }

    /**
     * 一个合法的括号匹配序列有一下定义
     * 1.空串""是一个合法的括号匹配序列
     * 2.如果X和Y都是合法的括号匹配序列，"XY"也是一个合法的括号匹配序列
     * 3.如果X是一个合法的括号匹配序列，那么（X）也是一个合法的括号匹配序列
     * 4.每个合法的括号序列都可以由以上规则生成
     * 例如:""、"()"、"()()"、"(())"都是合法的括号序列
     * 对于一个合法的括号序列我们又有以下定义它的深度
     * 1.空串的深度是0
     * 2.如果字符串X的深度是x，字符串Y的深度是y，那么字符串XY的深度为max（x,y）
     * 3.如果X的深度是x，那么（X）的深度是x+1
     * 例如：“（）（）（）”的深度是1，“（（（）））”的深度是3，牛牛现在给你一个合法的括号序列，需要你计算出深度
     */
    public static int depth(String s) {
        char[] chs = s.toCharArray();
        int count = 0;
        int ans = 0;
        //依次遍历，左括号++，右括号--，最后最大值就是深度
        for (int i = 0; i < chs.length; i++) {
            if (chs[i] == '(') {
                count++;
            } else if (chs[i] == ')') {
                ans = Math.max(ans, count);
                count--;
            }
        }
        return ans;
    }

    /**
     * 找到最长的有效括号子串
     * 暴力：依次遍历以当前括号结尾的最大有效子串长度
     */
    public static int maxLength(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        char[] chs = s.toCharArray();
        //存放以当前位置字符结尾的有效子串的长度
        int[] dp = new int[chs.length];
        int res = 0;
        //首位置一定为0，且是"("的一定为0
        for (int i = 1; i < chs.length; i++) {
            //前一位置
            int pre = i - 1 - dp[i - 1];
            if (chs[i] == ')' && chs[pre] == '(') {//只有这种情况以当前字符结尾才能由可能
                dp[i] = dp[i - 1] + 2 + (pre > 0 ? dp[pre - 1] : 0);
            }
            res = Math.max(res, dp[i]);
        }
        return res;
    }

    /**
     * 编写一个程序，对一个栈里的整型数据，按升序进行排序（即排序前，栈里的数据是无序的，排序后最大元素位于栈顶），
     * 要求最多只能使用一个额外的栈存放临时数据，但不能将元素复制到别的数据结构中
     */
    public static Stack<Integer> sortStack(Stack<Integer> stack) {
        Stack<Integer> help = new Stack<>();
        while (!stack.isEmpty()) {
            int cur = stack.pop();
            while (!help.isEmpty() && (help.peek() > cur)) {
                stack.push(help.pop());
            }
            help.push(cur);
        }
        return help;
    }

    /**
     * 将给定的数转换成为字符串，原则：1对应a，2对应b。。。26对应z，例如12258可以转换为"abbeh"，"aveh"."abyh"."ibeh"."iyh"的个数为5
     * 编写一个函数，给出可以转换的不同字符串的个数
     *
     * @return
     */
    public static int diffStr(String s) {
        if (s == null || s.length() == 0) return 0;
        return process1(s.toCharArray(), 0);
    }

    /**
     * @param str 固定的参数，字符串
     * @param cur 当前转化的位置
     * @return
     */
    public static int process1(char[] str, int cur) {
        if (cur == str.length - 1) {
            //说明之前都成功转化了
            return 1;
        }
//        else if (cur > str.length) {//说明转化超过了原有的
//            return -1;
//        }
//        int ans = 0;
//        if (str[cur] == '0') {
//            ans = 0;
//        } else if (str[cur] == '1') {//可以转化成1、十几
//            int a = process1(str, cur + 1);
//            int b = process1(str, cur + 2);
//
//            ans += a + b;
//        } else if (str[cur] == '2') {//可以转化成2、二0-26
//            int a = process1(str, cur + 1);
//            int b = -1;
//            if (cur + 1 <= str.length && str[cur + 1] <= '6') {
//                b = process1(str, cur + 2);
//            }
//            ans += a + ((b == -1) ? 0 : b);
//        } else {//只能单个转化
//            ans += process1(str, cur + 1);
//        }
        if (str[cur] == '0') {
            return 0;
        }
        int res = process1(str, cur + 1);
        if (cur == str.length - 1) {
            return res;
        }
        if ((str[cur] - '0') * 10 + (str[cur + 1] - '0') < 27) {
            res += process1(str, cur + 2);
        }
        return res;
    }

    public static int dp1(String s) {
        if (s == null || s.length() == 0) return 0;
        char[] str = s.toCharArray();
        int N = s.length();
        int[] dp = new int[N + 1];
        dp[N] = 1;
        dp[N - 1] = str[N - 1] == '0' ? 0 : 1;
        for (int i = N - 2; i >= 0; i--) {
            if (str[i] == '0') {
                dp[i] = 0;
            } else {
                dp[i] = dp[i + 1] + (((str[i] - '0') * 10 + (str[i + 1] - '0') < 27) ? dp[i + 2] : 0);
            }
        }
        return dp[0];
    }

    /**
     * 二叉树每个节点都有一个int型权值，给定一颗二叉树，要求计算出从根节点到叶节点得所有路径中
     * 权值最大得路径得权值
     *
     * @return
     */
    public static int maxSum = Integer.MAX_VALUE;

    public static int maxWeight(Node root) {
        p(root, 0);
        return maxSum;
    }

    /**
     * 从到达node之前得路径和为p
     *
     * @param node 当前来到得节点
     * @param pre  之前得路径和
     */
    public static void p(Node node, int pre) {
        if (node.left == null && node.right == null) {
            maxSum = Math.max(maxSum, pre);
            return;
        }
        if (node.left != null) {
            p(node.left, pre + node.left.weight);
        }
        if (node.right != null) {
            p(node.right, pre + node.right.weight);
        }
    }

    public static int maxWeight1(Node root) {
        if (root == null) {
            return 0;
        }
        return p2(root);
    }

    private static int p2(Node node) {
        if (node.left == null && node.right == null) {
            return node.weight;
        }
        int res = Integer.MIN_VALUE;
        if (node.left != null) {
            res = p2(node.left);
        }
        if (node.right != null) {
            res = Math.max(res, p2(node.right));
        }
        return res + node.weight;
    }

    public static class Node {
        public int weight;
        public Node left;
        public Node right;

        public Node(int weight) {
            this.weight = weight;
        }
    }

    /**
     * 给定一个元素为非负整数得二维数组matrix，每行和每列都是从小到大有序得
     * 再给定一个非负整数aim，请判断aim是否再matrix中
     *
     * @return
     */
    public static boolean findAim(int[][] matrix, int target) {
        int row = 0;
        int col = matrix[0].length - 1;
        while (row < matrix.length && col >= 0) {
            if (matrix[row][col] > target) {
                //只能往左找
                col++;
            } else if (matrix[row][col] < target) {
                //只能往下找
                row++;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * 给定一个矩阵matrix，每一行最多有两种元素0和1
     * 如果同时包含两种元素，0全部再左，1全部再右
     * 求最多1的行数
     *
     * @param matrix
     * @return
     */
    public static List<Integer> maxOneOfRow(int[][] matrix) {
        int row = 0;
        int curRow = 0;
        int col = matrix[0].length - 1;
        int oneCount = 0;
        List<Integer> res = new ArrayList<>();
        while (row < matrix.length && col >= 0) {
            if (curRow != row) {
                //换行了
                curRow = row;
                if (matrix[row][col] == 1) {
                    if (col - 1 >= 0 && matrix[row][col - 1] == 1) {
                        //往左走
                        oneCount++;
                        res.clear();
                        res.add(row);
                        col--;
                    } else {
                        //往下走
                        row++;
                    }
                } else {
                    //只能往下走
                    row++;
                }
            } else {
                //没换行
                if (matrix[row][col] == 1) {
                    if (col - 1 >= 0 && matrix[row][col - 1] == 1) {
                        //往左走
                        oneCount++;
                        col--;
                    } else {
                        res.add(row);
                        //往下走
                        row++;
                    }
                } else {
                    //只能往下走
                    row++;
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        Stack<Integer> stack = new Stack<>();
        stack.push(1);
        stack.push(5);
        stack.push(3);
        stack.push(7);
        stack.push(9);
        Stack<Integer> stack1 = sortStack(stack);
        while (!stack1.isEmpty()) {
            System.out.println(stack1.pop());
        }
    }
}
