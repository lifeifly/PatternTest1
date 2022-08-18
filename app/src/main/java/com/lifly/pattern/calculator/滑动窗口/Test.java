package com.lifly.pattern.calculator.滑动窗口;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

public class Test {
    /**
     * 给定一个有序的数组arr，代表数轴上从左往右有n个点arr[0]、arr[1]...arr[n-1]
     * 给定一个正数L，代表一根长度为L的绳子，求绳子最多能覆盖其中几个点
     * 滑动窗口：维持一个窗口，左边界和右边界一直在点上，但是保证窗口的长度小于L
     */
    public static int maxDot(int[] arr, int L) {
        if (arr == null || arr.length == 0) {
            return 0;
        }

        //窗口
        int left = 0;
        int right = 0;
        int ans = 0;
        while (left < arr.length && right < arr.length) {
            if (arr[right] - arr[left] <= L) {
                //窗口小于L，向右扩张
                right++;
            } else {
                //结算
                ans = Math.max(ans, right - left);
                //窗口大于L,向右收缩
                left++;
            }
        }
        return ans;
    }

    /**
     * 给定一个数组arr，该数组无序，但每个值均为正数，再给定一个正数k，求arr的所有子数组中所有元素相加和为k的最长子数组的长度
     * 例如{1，2，1，1，1}，k=3
     * 返回3
     * 要求：时间复杂度o（N） 空间复杂度O（1）
     *
     * @return
     */
    public static int maxSubArrLength(int[] arr, int k) {
        int l = 0;
        int r = 0;
        int sum = arr[0];
        int maxLenght = arr[0] == k ? 1 : 0;
        while (l < arr.length && r < arr.length) {
            if (sum < k) {
                r++;
                if (r >= arr.length) {
                    break;
                }
                sum += arr[r];
            } else if (sum > k) {
                sum -= arr[l];
                l++;
            } else {
                maxLenght = Math.max(maxLenght, r - l + 1);
                sum -= arr[l++];
            }
        }
        return maxLenght;
    }

    /**
     * 给定一个数组arr，该数组无序，有正有负有0，再给定一个正数k，求arr的所有子数组中所有元素相加和小于等于k的最长子数组的长度
     * 例如{1，2，1，1，1}，k=3
     * 返回3
     * 要求：时间复杂度o（N） 空间复杂度O（1）
     *
     * @return
     */
    public static int maxSubSumArrLength(int[] arr, int k) {
        //记录arr中i位置元素开头的最小累加和
        int[] minSum = new int[arr.length];
        //记录arr中i位置元素开头的最小累加和的右边界
        int[] minSumEnd = new int[arr.length];
        minSum[arr.length - 1] = arr[arr.length - 1];
        minSumEnd[arr.length - 1] = arr.length - 1;
        for (int i = arr.length - 2; i >= 0; i--) {
            if (minSum[i + 1] < 0) {
                minSum[i] = arr[i] + minSum[i + 1];
                minSum[i] = minSumEnd[i + 1];
            } else {
                minSum[i] = arr[i];
                minSumEnd[i] = i;
            }
        }
        int end = 0;
        int sum = 0;
        int res = 0;
        //i作为滑动窗口的左边界
        for (int i = 0; i < arr.length; i++) {
            //计算对应i开头的累加和小于等于k的右边界+1的位置
            while (end < arr.length && sum + minSum[end] < k) {
                sum += minSum[end];
                end = minSumEnd[end] + 1;
            }
            res = Math.max(res, end - i);
            if (end > i) {
                //窗口内还有数，l向右收缩
                sum -= arr[i];
            } else {
                //以i开头的所有累加和中没有小于等于k的
                end = i + 1;
            }
        }
        return res;
    }

    /**
     * 给定一个非负数组，每个值代表该位置右几个铜板，a和b玩游戏，a先手，b后手，轮到某个人的时候，只能再一个位置上拿任意数量的铜板，但是不能不拿
     * 谁最先把铜板拿完谁赢，假设a和b都极度聪明，请返回获胜者的名字
     *
     * @return
     */
    public static String winner(int[] arr) {
        //异或和为0后手赢不为0先手赢，因为异或和为0，先手不管怎么拿，拿完的异或和都是不为0的，此时后手可以通过计算，拿完之后让结果为0
        return "";
    }

    /**
     * 一个char类型数组chs，其中所有字符都不同
     * 例如chs=[‘A’，‘B’。。。。‘Z’]；
     * 字符串和整数对应关系如下
     * A 1 B 2 Z 26 AA 27 AB 28 AZ 52 BA 53 BB 54
     * 实现根据对应关系完成字符串与整数相互转换的两个函数
     * 思路：K的伪进制，例如7的进制每位是1-7，真实进制是0-6
     *
     * @return
     */
    public static int transform(char[] chs) {
        int sum = 0;
        int base = 26;
        int s = 0;
        for (int i = chs.length - 1; i >= 0; i--) {
            s = s == 0 ? 1 : s * base;
            int index = chs[i] - 'A' + 1;
            sum += (index * s);
        }
        return sum;
    }

    public static char[] transform(int a) {
        int base = 26;
        int s = 1;
        int sum = a;
        TreeMap<Integer, Integer> map = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });
        while (sum >= s) {
            map.put(s, 1);
            sum -= s;
            s *= base;
        }
        s /= base;
        while (sum != 0) {
            int times = sum / s;
            map.put(s, map.get(s) + times);
            sum -= times * s;
            s /= base;
        }
        char[] chs = new char[map.size()];
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            chs[i++] = (char) (entry.getValue() + 'A' - 1);
        }
        return chs;
    }

    /**
     * 给定一个二维数组matrix，每个单元都是一个整数，有正有负，最开始小Q操作一条长度为0的蛇从最左侧任选一个单元格进入地图
     * 蛇每次只能够到达当前位置的右上相邻，右侧相邻和右下相邻的单元格，每到达一个单元格，自身的长度就会瞬间加上该单元格的值，任何情况下，长度为负游戏结束
     * 小Q是一个天才，可以再游戏开始的时候把地图中的某一个节点的值变为其相反数（最多只能改变一个节点），再游戏过程中，蛇的最长长度可以到多少
     * 1 -4 10
     * 3 -2 -1
     * 2 -1 0
     * 0 5 -2
     * 最由陆劲从最左侧3开始，3->-4(利用能力变成4)->10 所以返回17
     */
    public static int maxLength(int[][] matrix) {
        int ans = Integer.MIN_VALUE;
        //枚举每个位置作为结束位置的情况
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                Info info = process(matrix, i, j);
                ans = Math.max(ans, Math.max(info.no, info.yes));
            }
        }
        return ans;
    }

    /**
     * 到达row、col的最大长度
     *
     * @param matrix
     * @param row
     * @param col
     * @return
     */
    public static Info process(int[][] matrix, int row, int col) {
        //basecase
        if (col == 0) {
            return new Info(matrix[row][col], -matrix[row][col]);
        }
        //之前路径使用能力和不使用能力的最大路径
        int preNo = -1;
        int preYes = -1;
        //如果row大于0，一定有左上位置
        if (row > 0) {
            Info leftTop = process(matrix, row - 1, col - 1);
            if (leftTop.no >= 0) {
                preNo = leftTop.no;
            }
            if (leftTop.yes > 0) {
                preYes = leftTop.yes;
            }
        }
        //一定存在左侧节点
        Info left = process(matrix, row, col - 1);
        if (left.no >= 0) {
            preNo = Math.max(left.no, preNo);
        }
        if (left.yes > 0) {
            preYes = Math.max(left.yes, preYes);
        }

        //如果row小于length-1一定存在左下
        if (row < matrix.length - 1) {
            Info leftBottom = process(matrix, row + 1, col - 1);
            if (leftBottom.no >= 0) {
                preNo = Math.max(preNo, leftBottom.no);
            }
            if (leftBottom.yes >= 0) {
                preYes = Math.max(preYes, leftBottom.yes);
            }
        }
        int yes = -1;
        int no = -1;

        if (preNo >= 0) {
            //一次都不用能力
            no = preNo + matrix[row][col];
            //前面不用能力，当前使用能力
            yes = preNo - matrix[row][col];
        }

        if (preYes >= 0) {
            //之前使用能力
            yes = preYes + matrix[row][col];
        }
        return new Info(yes, no);
    }

    //记忆化搜索
    public static int maxLength1(int[][] matrix) {
        int ans = Integer.MIN_VALUE;
        Info[][] dp = new Info[matrix.length][matrix[0].length];
        //枚举每个位置作为结束位置的情况
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                Info info = process1(matrix, i, j, dp);
                ans = Math.max(ans, Math.max(info.no, info.yes));
            }
        }
        return ans;
    }

    /**
     * 到达row、col的最大长度
     *
     * @param matrix
     * @param row
     * @param col
     * @return
     */
    public static Info process1(int[][] matrix, int row, int col, Info[][] dp) {
        if (dp[row][col] != null) {
            return dp[row][col];
        }
        //basecase
        if (col == 0) {
            dp[row][col] = new Info(matrix[row][col], -matrix[row][col]);
            return dp[row][col];
        }
        //之前路径使用能力和不使用能力的最大路径
        int preNo = -1;
        int preYes = -1;
        //如果row大于0，一定有左上位置
        if (row > 0) {
            Info leftTop = process(matrix, row - 1, col - 1);
            if (leftTop.no >= 0) {
                preNo = leftTop.no;
            }
            if (leftTop.yes > 0) {
                preYes = leftTop.yes;
            }
        }
        //一定存在左侧节点
        Info left = process(matrix, row, col - 1);
        if (left.no >= 0) {
            preNo = Math.max(left.no, preNo);
        }
        if (left.yes > 0) {
            preYes = Math.max(left.yes, preYes);
        }

        //如果row小于length-1一定存在左下
        if (row < matrix.length - 1) {
            Info leftBottom = process(matrix, row + 1, col - 1);
            if (leftBottom.no >= 0) {
                preNo = Math.max(preNo, leftBottom.no);
            }
            if (leftBottom.yes >= 0) {
                preYes = Math.max(preYes, leftBottom.yes);
            }
        }
        int yes = -1;
        int no = -1;

        if (preNo >= 0) {
            //一次都不用能力
            no = preNo + matrix[row][col];
            //前面不用能力，当前使用能力
            yes = preNo - matrix[row][col];
        }

        if (preYes >= 0) {
            //之前使用能力
            yes = preYes + matrix[row][col];
        }
        dp[row][col] = new Info(yes, no);
        return dp[row][col];
    }

    public static class Info {
        //用了能力获取的长度
        public int yes;
        //不用能力获取的长度
        public int no;

        public Info(int yes, int no) {
            this.yes = yes;
            this.no = no;
        }
    }

    /**
     * 给定一个字符串str，str表示一个公式，公式里可能有整数、加减乘除符号和左右括号，返回公式的结果
     *
     * @return
     */
    public static int calResult(String formula) {
        if (formula == null || formula.length() == 0) {
            return 0;
        }

        return value(formula.toCharArray(), 0)[0];
    }

    /**
     * 当前来到position处进行计算
     *
     * @param chs
     * @param position
     * @return 0:当前位置计算的值 1：代表当前位置
     */
    private static int[] value(char[] chs, int position) {
        Stack<String> stack = new Stack<>();
        int num = 0;
        int[] bra = null;
        while (position < chs.length && chs[position] != ')') {
            if (chs[position] >= '0' && chs[position] <= '9') {
                num = num * 10 + chs[position++] - '0';
            } else if (chs[position] != '(') {
                //遇到的是运算符
                addNum(stack, num);
                //将运算符加到堆
                stack.push(String.valueOf(chs[position++]));
                num = 0;
            } else {
                //遇到的是左括号，交给下一个递归
                bra = value(chs, position + 1);
                num = bra[0];
                position = bra[1] + 1;
            }
        }
        addNum(stack, num);
        return new int[]{getNum(stack), position};
    }

    private static int getNum(Stack<String> stack) {
        int sum= Integer.parseInt(stack.pop());
        boolean add=true;
        while(!stack.isEmpty()){
            String s=stack.pop();
            if (s.equals("+")){
                add=true;
            }else if (s.equals("-")){
                add=false;
            }else {
                sum=add?(sum+Integer.parseInt(s)):(sum-Integer.parseInt(s));
            }
        }
        return sum;
    }

    private static void addNum(Stack<String> stack, int num) {
        if (!stack.isEmpty()) {
            String top = stack.peek();
            if (top.equals("*")) {
                stack.pop();
                int lastValue = Integer.parseInt(stack.pop());
                num = lastValue * num;
            } else if (top.equals("/")) {
                stack.pop();
                int lastValue = Integer.parseInt(stack.pop());
                num = lastValue / num;
            }
        }
        stack.push(String.valueOf(num));
    }
    /**
     * 给定字符串str1和str2，求str1的子串种含有str2所有字符的最小子串长度
     * str1=“abcde” str2=“ac” str1的子串“abc”最短且含有“ac”
     * @return
     */
    public static int minSubString(String str1,String str2){
        //记录str2欠款字符表,key 字符  value 字符个数
        int[] map=new int[256];
        char[] chs2=str2.toCharArray();
        int all=chs2.length;
        for (int i = 0; i < chs2.length; i++) {
            map[chs2[i]]++;
        }

        //枚举每个开头的位置
        int len=str1.length();
        char[] chs1=str1.toCharArray();
        int left=0;
        int right=0;
        int minRes=Integer.MAX_VALUE;
//        String str1="abcd";
//        String str2="cd";
        while(right<len){
            map[chs1[right]]--;

            if (map[chs1[right]]>=0){//表示这个字符减去后是有效的
                all--;
            }
            System.out.println("right"+right);
            if (all==0){
                while(map[chs1[left]]<0){
                    //表示是多余的
                    map[chs1[left]]++;
                    left++;
                }
                System.out.println("left"+left);
                minRes=Math.min(right-left+1,minRes);

                map[chs1[left]]++;
                left++;
                all++;
            }
            right++;
        }
        return minRes;
    }
    public static void main(String[] args) {
        String str1="abcddbefdef";
        String str2="de";
        System.out.println(minSubString(str1,str2));
    }
}
