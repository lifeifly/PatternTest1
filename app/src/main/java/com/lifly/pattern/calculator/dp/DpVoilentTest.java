package com.lifly.pattern.calculator.dp;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 动态规划
 */
public class DpVoilentTest {
    /**
     * 打印一个字符串的全部子序列
     */
    public static List<String> subs(String s) {
        char[] str = s.toCharArray();
        String path = "";
        List<String> ans = new ArrayList<>();
        //可以保证每个子序列不重复
//        HashSet<String> ans=new HashSet<>();
        process1(str, 0, ans, path);
        return ans;
    }

    private static void process1(char[] str, int index, List<String> ans, String path) {
        if (index == str.length) {
            //base case来到最后一个字符的下一个位置
            //将之前构成的String加入结果
            ans.add(path);
            return;
        }
        //当前字符不要了
        String no = path;
        process1(str, index + 1, ans, no);

        //当前字符还要
        String yes = path + String.valueOf(str[index]);
        process1(str, index + 1, ans, yes);
    }

    /**
     * 获取字符串的全排列
     */
    public static List<String> permutation(String s) {
        char[] str = s.toCharArray();
        List<String> ans = new ArrayList<>();
        //不出现重复的
//        HashSet<String> ans=new HashSet<>();
        process2(str, 0, ans);
        return ans;
    }

    /**
     * 从position到最后的字符都有机会来到i位置
     *
     * @param str
     * @param position
     * @param ans
     */
    private static void process2(char[] str, int position, List<String> ans) {
        if (position == str.length) {
            ans.add(String.valueOf(str));
            return;
        }
        //优化，分支限界
        boolean[] visit = new boolean[26];
        for (int i = position; i < str.length; i++) {
            if (!visit[str[i] - 'a']) {
                visit[str[i] - 'a'] = true;
                swap(str, position, i);
                //继续往下执行
                process2(str, position + 1, ans);
                //还原
                swap(str, position, i);
            }
        }
    }

    private static void swap(char[] str, int i, int j) {
        char tmp = str[i];
        str[i] = str[j];
        str[j] = tmp;
    }

    /**
     * 从左往右的尝试模型1
     * 规定1和A对应、2和B对应。。。。
     * 那么一个字符串111就可以转化为AAA、KA、AK
     * 给定一个数字字符串str，返回多少种转化结果
     */
    public static int trans(String str) {
        if (str == null && str.length() == 0) {
            return 0;
        }
        return process3(str.toCharArray(), 0);
    }

    /**
     * str[0...position-1]已经转化完了,求position及之后的字符串的结果有多少
     *
     * @param str
     * @param position
     * @return
     */
    private static int process3(char[] str, int position) {
        if (position == str.length) {
            return 1;
        }
        if (str[position] == '0') {
            //无法继续往下转化
            return 0;
        }
        if (str[position] == '1') {//当前总是有两种可能，一个单独转化，一个和后面一位一起转化
            int res = process3(str, position + 1);

            if (position + 1 < str.length) {
                //加上后后面一位进行转化的结果
                res += process3(str, position + 2);
            }
            return res;
        }
        if (str[position] == '2') {
            //可能有两种可能，一个单独转化，一个和后面一位一起转化
            int res = process3(str, position + 1);

            if (position + 1 < str.length && (str[position + 1] > 0 && str[position + 1] <= '6')) {
                res += process3(str, position + 2);
            }
            return res;
        }
        //当前位置是3到9，都对应只有一种可能
        return process3(str, position + 1);
    }

    /**
     * 动态规划,就一个参数position是可变的，
     */
    public static int tranDp(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        char[] s = str.toCharArray();
        int N = s.length;
        int[] dp = new int[N + 1];

        dp[N] = 1;
        for (int i = N - 1; i >= 0; i--) {
            if (s[i] == '0') {
                dp[i] = 0;
                continue;
            }
            if (s[i] == '1') {
                int res = dp[i + 1];
                if (i + 1 < s.length) {
                    res += dp[i + 2];
                }
                dp[i] = res;
                continue;
            }
            if (s[i] == '2') {
                int res = dp[i + 1];
                if (i + 1 < str.length() && (s[i + 1] >= '0' && s[i + 1] <= '6')) {
                    res += dp[i + 2];
                }
                dp[i] = res;
                continue;
            }
            dp[i] = dp[i + 1];
        }
        return dp[0];
    }

    /**
     * 从左往右尝试模型+范围上尝试模型
     * 给定一个字符串，返回把str全部切成回文子串的最小分割数
     *
     * @return
     */
    public static int splitPalindromeStr(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        int len = str.length();
        char[] chs = str.toCharArray();
        //范围尝试模型,获取是否时回文串的表,(i,j)代表str[i..j]是否时回文串
        boolean[][] dp = getIsPalindrome(chs);

        //从左往右尝试模型,暴力
//        return process11(chs, 0, dp);

        //动态规划
        //i代表以i开头的字符串最少可以切几个回文串
        int[] dp1 = new int[len + 1];
        dp1[len] = 0;
        dp1[len - 1] = 1;
        for (int i = len - 2; i >= 0; i--) {
            dp1[i] = len - i;
            for (int j = i; j < len; j++) {
                if (dp[i][j]) {
                    dp1[i] = Math.min(dp1[i], dp1[j + 1] + 1);
                }
            }
        }
        return dp1[0];
    }

    /**
     * 返回chs[position..end]最少可以切出几个回文串
     *
     * @param chs
     * @param position
     * @param dp
     * @return
     */
    private static int process11(char[] chs, int position, boolean[][] dp) {
        if (position == chs.length) {
            return 0;
        }
        //从左往右尝试[i..end]end可变,如果chs[i..end]是回文串则计算结果
        int ans = Integer.MAX_VALUE;
        for (int end = chs.length - 1; end >= 0; end--) {
            if (dp[position][end]) {
                ans = Math.min(ans, process11(chs, end + 1, dp));
            }
        }
        return ans;
    }


    /**
     * (i,j)
     *
     * @param chs
     * @return
     */
    private static boolean[][] getIsPalindrome(char[] chs) {
        if (chs == null || chs.length == 0) {
            return null;
        }
        int n = chs.length;
        //(i,j)代表chs[i..j]是否是回文串
        boolean[][] dp = new boolean[n][n];
        //初始化中间第一条对角线和其右一条对角线
        for (int i = 0; i < n; i++) {
            //一个字符是回文
            dp[i][i] = true;
            //两个字符时相等就是回文串
            dp[i][i + 1] = chs[i] == chs[i + 1];
        }
        for (int start = n - 3; start >= 0; start--) {
            for (int end = start + 2; end < n; end++) {
                //两侧不相等一定不是回文串,两侧相等则看start+1..end-1部分
                dp[start][end] = chs[start] == chs[end] && dp[start + 1][end - 1];
            }
        }
        return dp;
    }

    /**
     * 给定两个长度为N的数组weights和values
     * weights[i]和values[i]分别代表i号物品的重量和价值
     * 给定一个正数bag，表示一个载重bag的袋子
     * 你装的物品重量不能超过bag，求你能装下最多价值是多少
     */
    public static int maxValue(int[] weights, int[] values, int bag) {
        return process4(weights, values, bag, 0, 0, 0);
    }

    /**
     * @param weights
     * @param values
     * @param bag      重量限制
     * @param weight   之前挑选后的重量
     * @param value    之前挑选后的价值
     * @param position 当前处在第几个物品处
     * @return
     */
    private static int process4(int[] weights, int[] values, int bag, int weight, int value, int position) {
        if (position == values.length) {
            //说明一直挑到最后重量都没超,直接返回之前的结果
            return value;
        }

        //当前物品不要
        int no = process4(weights, values, bag, weight, value, position + 1);

        //当前物品要
        int yes = 0;
        if (weight + weights[position] <= bag) {
            yes = process4(weights, values, bag, weight + weights[position], value + values[position], position + 1);
        }

        return Math.max(yes, no);
    }

    /**
     * @param weights
     * @param values
     * @param bag      重量限制
     * @param weight   之前挑选后的重量
     * @param position 0.。position-1已经做出了选择，当前处在第几个物品处
     * @return
     */
    private static int process4(int[] weights, int[] values, int bag, int weight, int position) {
        if (weight > bag) {
            //超重了
            return -1;
        }
        //重量没超
        if (position == weights.length) {
            return 0;
        }

        //当前物品不要
        int no = process4(weights, values, bag, weight, position + 1);

        //当前物品要
        int yes = process4(weights, values, bag, weight + weights[position], position + 1);

        int value = -1;
        if (yes != -1) {
            value = values[position] + yes;
        }
        return Math.max(no, value);
    }

    /**
     * 背包问题，剩余重量和当前位置是可变的，且存在重复解,可以使用动态规划,用二维表
     */
    public static int valueDp(int[] w, int[] v, int bag) {
        int N = w.length;
        int[][] dp = new int[N + 1][bag + 1];
        for (int index = N - 1; index >= 0; index--) {//一行一行填
            for (int i = 0; i < bag; i++) {//列数从左往右
                int p1 = dp[index + 1][i];
                int p2 = -1;
                if (i - w[index] >= 0) {
                    p2 = v[index] + dp[index + 1][i - w[index]];
                }
                dp[index][i] = Math.max(p1, p2);
            }
        }
        return dp[0][bag];
    }


    /**
     * 范围上尝试的模型
     */
    /**
     * 给定一个整形数组arr，代表数值不同的纸牌排成一条线
     * 玩家A和玩家B依次拿走指派
     * 规定A先拿，B后拿
     * 但是每次只能拿走最左或最右的纸牌
     * 玩家A和玩家B都绝顶聪明，请返回最后获胜者的分数
     */
    public static int win1(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        //A在arr中进行先手和B在arr中进行后手，取最大
        return Math.max(pre(arr, 0, arr.length - 1), pos(arr, 0, arr.length - 1));
    }

    /**
     * 在left-right的牌中先手拿牌
     *
     * @return
     */
    public static int pre(int[] arr, int left, int right) {
        //basecase，就剩一张牌了
        if (left == right) {
            //因为此时是先手，直接拿走
            return arr[left];
        }
        //拿走left牌，并对后续范围的牌进行后手操作
        int leftResult = arr[left] + pos(arr, left + 1, right);
        //拿走right牌，对后续范围进行后手操作
        int rightResult = arr[right] + pos(arr, left, right - 1);
        //此时是先手，且先手后手都绝顶聪明，先手一定会让自身的情况最好，所以取最大
        return Math.max(leftResult, rightResult);
    }

    /**
     * 在left-right范围的牌中进行后手操作
     *
     * @param arr
     * @param left
     * @param right
     * @return
     */
    private static int pos(int[] arr, int left, int right) {
        //basecase就剩一张牌了
        if (left == right) {
            //因为就只有一个，且此时是后手，被其它人拿走了
            return 0;
        }
        //先手拿走左边一张后，后手对剩下范围进行先手操作
        int leftResult = pre(arr, left + 1, right);
        //先手拿走右边一张后，此时对剩下范围进行先手操作
        int rightResult = pre(arr, left, right - 1);
        //因为先手后手都绝顶聪明，先手先挑一定会让后手处于最差情况，所以取最小
        return Math.min(leftResult, rightResult);
    }

    /**
     * 动态规划，只有两个参数left和right是可变的
     */
    public static int winDp(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        //创建先手二维表a
        int[][] a = new int[arr.length][arr.length];
        //创建后手表
        int[][] b = new int[arr.length][arr.length];
        for (int i = 0; i < arr.length; i++) {
            a[i][i] = arr[i];
        }
        for (int i = 1; i < arr.length; i++) {
            int row = 0;
            int col = 1;
            while (row < arr.length && col < arr.length) {
                a[row][col] = Math.max(arr[row] + b[row + 1][col], arr[col] + b[row][col - 1]);
                b[row][col] = Math.min(a[row + 1][col], a[row][col - 1]);
                row++;
                col++;
            }
        }
        return Math.max(a[0][arr.length - 1], b[0][arr.length - 1]);
    }

    /**
     * 牛牛想将一个字符串str删除0个或n个使其变成回文串,空串不是回文串
     * 一共右多少种移除方案可以使s变成回文串,如果移除的字符一次构成的序列不一样就是不同的方案
     * 保留的字符位置不一样就是一种方案
     * 实际就是求str的回文子串有多少种
     */
    public static int deleteConstructPalindrome(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        char[] chs = str.toCharArray();
        int len = str.length();
        //(i,j)代表str[i..j]字符串的回文子串有多少种
        int[][] dp = new int[len][len];
        //初始化中间对角线和右侧一条对角线
        for (int i = 0; i < len; i++) {
            //一个字符只能有一个回文
            dp[i][i] = 1;
            //两个字符不相等是两个,相等是3个
            dp[i][i + 1] = chs[i] == chs[i + 1] ? 3 : 2;
        }
        for (int start = len - 3; start >= 0; start--) {
            for (int end = start + 2; end < len; end++) {
                //1.不以start结尾,不以end结尾
                //2.不以start结尾,以end结尾
                //3.以start结尾,不以end结尾
                //4.以start结尾,以end结尾
                //包含可能性1,2
                int r1 = dp[start + 1][end];
                //包含可能性1,3
                int r2 = dp[start][end - 1];
                //只包含可能性1
                int r3 = dp[start + 1][end - 1];

                //可能性4
                int r4 = 0;
                if (chs[start] == chs[end]) {
                    //中间去掉,只留两边
                    r4 += 1;
                    //中间加上+两边
                    r4 += dp[start + 1][end - 1];
                } else {
                    //不存在此可能
                    r4 = 0;
                }
                dp[start][end] = r1 + r2 - r3 + r4;
            }
        }
        return dp[0][len - 1];
    }

    /**
     * 给定一个字符串str，求最长回文子序列
     *
     * @return
     */
    public static int maxPalindromicSubsequence(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        int n = str.length();
        char[] chs = str.toCharArray();
        //（i，j）代表str[i...j]的字符串的最长子序列是多少
        int[][] dp = new int[str.length()][str.length()];
        //第一条对角线、第二条对角线初始化
        for (int i = 0; i < n; i++) {
            dp[i][i] = 1;
            dp[i][i + 1] = chs[i] == chs[i + 1] ? 2 : 1;
        }
        for (int start = n - 3; start >= 0; start--) {
            for (int end = start + 2; end < n; end++) {
                int res = 0;
                //1.公共子序列既不以start，也不以end结尾
                int r1 = dp[start + 1][end - 1];
                //2.公共子序列不以start结尾，以end结尾
                int r2 = dp[start + 1][end];
                //3.公共子序列以start结尾，不以end结尾
                int r3 = dp[start][end - 1];
                res = Math.max(r1, Math.max(r2, r3));
                //4.start和end相等时,可能存在既以start结尾又以end结尾的可能
                if (chs[start] == chs[end]) {
                    res = Math.max(res, dp[start + 1][end - 1] + 2);
                }
                dp[start][end] = res;
            }
        }
        return dp[0][n - 1];
    }

    /**
     * 给定一个字符串str，如果可以在str的任意位置添加字符，请返回在添加字符最少的情况下，让str整体都是回文字符串的一种结果
     *
     * @return
     */
    public static int addConstructPalindrom(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        char[] chs = str.toCharArray();
        int n = chs.length;
        //（i,j）代表str[i..j]字符串至少填几个变成回文串
        int[][] dp = new int[n][n];
        //初始化第一行和第二行
        for (int i = 0; i < n; i++) {
            //就一个字符，本身已是回文串无需增加
            dp[i][i] = 0;
            //两个字符时相等就无需增加，不等至少加一个
            dp[i][i + 1] = chs[i] == chs[i + 1] ? 0 : 1;
        }

        for (int start = n - 3; start >= 0; start--) {
            for (int end = start + 2; end < n; end++) {
                int res = 0;
                //1.先搞定start..end-1,end位置最后在最前面+1搞定
                int r1 = dp[start][end - 1] + 1;
                //2.先搞定start+1..end，start位置最后在最后面+1搞定
                int r2 = dp[start + 1][end] + 1;
                res = Math.min(r1, r2);

                //3.start==end时，就是start+1..end-1的问题了
                if (chs[start] == chs[end]) {
                    res = Math.min(res, dp[start + 1][end - 1]);
                }
                dp[start][end] = res;
            }
        }
        return dp[0][n - 1];
    }

    /**
     * n皇后问题
     */
    public static int nQueue(int n) {
        if (n == 0) {
            return n;
        }
        //记录每行放的位置
        int[] record = new int[n];
        return process5(0, record, n);
    }

    /**
     * i之前的已经放好，当前位置i行的放置皇后
     *
     * @param i
     * @param record
     * @param n
     * @return
     */
    private static int process5(int i, int[] record, int n) {
        if (i == n) {
            //都放完了
            return 1;
        }
        int res = 0;
        for (int j = 0; j < n; j++) {
            //一列一列试
            if (isValid(record, i, j)) {//如果和之前不在同一列或同一斜线就可以继续往下
                record[i] = j;
                res += process5(i + 1, record, n);
            }
        }
        return res;
    }

    /**
     * 判断第i行第j列是否跟之前在同一列或同一斜线
     *
     * @param record
     * @param i
     * @param j
     * @return
     */
    private static boolean isValid(int[] record, int i, int j) {
        for (int k = 0; k < i; k++) {
            if (record[k] == j || (Math.abs(record[k] - j) == Math.abs(k - i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * n皇后问题优化
     *
     * @return
     */
    public static int nQueue1(int n) {
        if (n < 1 || n > 32) {
            return 0;
        }
        //让limit的后几位为1的数量和n相同
        int limit = n == 32 ? -1 : (1 << n) - 1;
        return process6(limit, 0, 0, 0);
    }

    /**
     * n皇后递归方法优化，使用位运算优化
     *
     * @param limit       几皇后问题  后几位就是几个1，其它位都是0,划定问题的规模，固定的
     * @param colLim      列的限制，1的位置不能放皇后，0可以
     * @param leftDiaLim  左斜线限制，1的位置不能，0可以
     * @param rightDiaLim 右斜线的限制，1不能，0可以
     * @return
     */
    public static int process6(int limit, int colLim, int leftDiaLim, int rightDiaLim) {
        if (colLim == limit) {
            //代表每一列都放完了
            return 1;
        }
        int res = 0;
        //获取此时放皇后的总限制
        int totalLimit = colLim | leftDiaLim | rightDiaLim;
        //将可以放置皇后的0位置变1，不能放皇后的1变0，然后去除左侧不相关0
        int pos = limit & (~totalLimit);
        //记录每个可以放皇后的1位置,首先获取最右侧为1的数
        int mostRight = 0;
        while (pos != 0) {//有1的位置，可以放皇后
            //可以为下一个位置做准备
            mostRight = pos & (~pos + 1);
            pos = pos - mostRight;
            //开始进行下一行位置的放置
            res += process6(limit, colLim | mostRight, (leftDiaLim | mostRight) << 1, (rightDiaLim | mostRight) >>> 1);
        }
        return res;
    }

    /**
     * 给定一个都是正数且无重复值的数组arr，每个元素代表一个货币值，每个货币可以使用任意张，怎么组合成target，返回组合数
     *
     * @param arr
     * @param target
     * @return
     */
    public static int mostMVoilent(int[] arr, int target) {
        if (arr == null || arr.length == 0 || target == 0) {
            return 0;
        }
        return process7(arr, 0, target);
    }

    /**
     * 当前位置来到第position处的货币进行选择，position之前的货币都已经选择完
     *
     * @param arr      固定不变的可选货币
     * @param left     剩余的目标值
     * @param position 当前来到第几个货币处
     * @return
     */
    private static int process7(int[] arr, int position, int left) {
        //来到这代表left不为0，并且又没有可选的，代表前面选择的都是错误的
        if (position == arr.length) {
            return left == 0 ? 1 : 0;
        }
        //当前选择的货币的个数
        int i = 0;
        int res = 0;
        while (i * arr[position] <= left) {
            res += process7(arr, position + 1, left - i * arr[position]);
            i++;
        }
        return res;
    }

    /**
     * 给定一个都是正数且无重复值的数组arr，每个元素代表一个货币值，每个货币可以使用任意张，怎么组合成target，返回组合数
     *
     * @param arr
     * @param target
     * @return
     */
    public static int mostMVoilentCache(int[] arr, int target) {
        if (arr == null || arr.length == 0 || target == 0) {
            return 0;
        }
        HashMap<String, Integer> cache = new HashMap<>();
        return process7Cache(arr, cache, 0, target);
    }

    /**
     * 当前位置来到第position处的货币进行选择，position之前的货币都已经选择完
     *
     * @param arr      固定不变的可选货币
     * @param left     剩余的目标值
     * @param position 当前来到第几个货币处
     * @return
     */
    private static int process7Cache(int[] arr, HashMap<String, Integer> cache, int position, int left) {
        String key = position + "_" + left;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        //来到这代表left不为0，并且又没有可选的，代表前面选择的都是错误的
        if (position == arr.length) {
            return left == 0 ? 1 : 0;
        }
        //当前选择的货币的个数
        int i = 0;
        int res = 0;
        while (i * arr[position] <= left) {
            res += process7Cache(arr, cache, position + 1, left - i * arr[position]);
            i++;
        }
        cache.put(key, res);
        return res;
    }

    /**
     * 动态规划，有位置position和剩余left两个可变参数,且有重复计算，可以动态规划
     *
     * @return
     */
    public static int mostMDp(int[] money, int left) {
        if (money == null || money.length == 0 || left <= 0) {
            return 0;
        }
        //创建一个数组，position为行，left为列，行数大于长度1，因为最终position可能会来到位置n+1上
        int n = money.length;
        int[][] dp = new int[n + 1][left + 1];
        //初始化,left=0,除了最终位置都为1
        dp[n][0] = 1;

        //从前往后，从下往上，一列一列
        for (int i = n - 1; i >= 0; i--) {
            for (int j = 0; j < left + 1; j++) {
                //当前货币选用的个数
                int count = 0;
                //总可能性
                int res = 0;
                while (count * money[i] <= j) {
                    res += dp[i + 1][j - count * money[i]];
                    count++;
                }
                dp[i][j] = res;
            }
        }
        return dp[0][left];
    }

    /**
     * 动态规划，有位置position和剩余left两个可变参数,且有重复计算，可以动态规划,优化
     *
     * @return
     */
    public static int mostMDp1(int[] money, int left) {
        if (money == null || money.length == 0 || left <= 0) {
            return 0;
        }
        //创建一个数组，position为行，left为列，行数大于长度1，因为最终position可能会来到位置n+1上
        int n = money.length;
        int[][] dp = new int[n + 1][left + 1];
        //初始化,left=0,除了最终位置都为1
        dp[n][0] = 1;

        //从前往后，从下往上，一列一列
        for (int i = n - 1; i >= 0; i--) {
            for (int j = 0; j < left + 1; j++) {
                dp[i][j] = dp[i + 1][j];
                if (j - money[i] >= 0) {
                    dp[i][j] += dp[i][j - money[i]];
                }
            }
        }
        return dp[0][left];
    }

    /**
     * 给定一个字符串str，给定一个字符串类型的数组arr
     * arr里的每一个字符串，代表一张贴纸，你可以把单个字符串剪开成单独的字符使用，目的是频出str来
     * 返回至少需要多少张贴纸才可以完成任务
     * 例如 str=“babac”  arr={"ba","c","abcd"};
     * 至少需要两张贴纸“ba”，“abcd”，因为使用这两张贴纸，把每一个字符单独开来，可以拼出str
     */
    public static int appendMin(String[] arr, String str) {
        //行代表arr中第几个字符串，列代表每个字符串中字符ascII码位置对应的个数
        int[][] strTable = new int[arr.length][26];
        //初始化
        for (int i = 0; i < arr.length; i++) {
            char[] s = arr[i].toCharArray();
            for (int j = 0; j < s.length; j++) {
                strTable[i][s[j] - 'a']++;
            }
        }
        //缓存,key表示剩余需要拼接的字符串，value对应结果
        Map<String, Integer> cache = new HashMap<>();
        //结果剩余空时，需要0张先加入缓存
        cache.put("", 0);
        return process8(strTable, cache, str);
    }

    /**
     * @param strTable 固定参数，贴纸
     * @param cache    固定，缓存，可以增加
     * @param str      可变参数,每次选定贴纸就会生成新的
     * @return
     */
    private static int process8(int[][] strTable, Map<String, Integer> cache, String str) {
        if (cache.containsKey(str)) {
            return cache.get(str);
        }
        //生成剩余需要拼接数组，索引对应ascII码，元素对应个数
        int[] map = new int[26];
        for (char c : str.toCharArray()) {
            map[c - 'a']++;
        }
        //结果
        int ans = Integer.MAX_VALUE;

        //枚举每个贴纸
        for (int i = 0; i < strTable.length; i++) {
            //优化，先从当前剩余对象的头部开始消除，如果当前贴纸包含当前剩余对象的头部，就可以使用
            //这样也防止当前剩余的字符，每个贴纸都不包含所需字符导致一直递归
            if (strTable[i][map[0] - 'a'] == 0) {
                //当前贴纸不包含第一个所需字符，直接跳过
                continue;
            }
            //当前贴纸选用了一张，开始构建成新的所需字符串
            StringBuilder sb = new StringBuilder();
            //枚举每个字符
            for (int j = 0; j < 26; j++) {
                int count = Math.max(0, map[j] - strTable[i][j]);
                for (int k = 0; k < count; k++) {
                    sb.append(((char) ('a' + j)));
                }
            }
            //当前选择的结果，交给下一个过程选取贴纸
            int s = process8(strTable, cache, str);
            //取最小的那个
            if (s != -1) {
                ans = Math.min(s + 1, ans);
            }
        }
        cache.put(str, ans == Integer.MAX_VALUE ? -1 : ans);
        return cache.get(str);
    }
    /**
     * 两个样本重复尝试
     */
    /**
     * 两个字符串最长公共子序列问题
     * 动态规划：转化问题为以str1的每个字符长度为行str2的每个字符长度为列，求以i行j列为终止位置的最大子序列
     *
     * @param str1
     * @param str2
     * @return
     */
    public static int maxSub(String str1, String str2) {
        //构建表
        int[][] table = new int[str1.length()][str2.length()];
        char[] s1 = str1.toCharArray();
        char[] s2 = str2.toCharArray();
        //第一个位置，如果相等就可以有一个最大子序列
        table[0][0] = s1[0] == s2[0] ? 1 : 0;
        //第0行初始化,此时str1就一个字符，最多也只有一个最大公共子序列
        for (int i = 1; i < s2.length; i++) {
            table[0][i] = table[0][i - 1] == 1 || table[0][i] == s1[0] ? 1 : 0;
        }
        //第0列初始化
        for (int i = 1; i < s1.length; i++) {
            table[i][0] = table[i - 1][0] == 0 || s1[i] == s2[0] ? 1 : 0;
        }
        //从左往右一行一行
        for (int row = 1; row < s1.length; row++) {
            for (int col = 1; col < s2.length; col++) {
                //4种可能性，1：都不以最后的字符为结尾，则dp[row-1][col-1]的结果就是当前位置的结果
                // 2.以s1的最后一个为结尾，不以s2的的最后一个结尾，则dp[row][col-1]的结果就是当前位置的结果
                //3.不以s1的最后一个结尾，以s2的最后一个结尾，则dp[row-1][col]的结果与当前位置的结果相等
                //4.在s1和s2的最后一个字符相等时，两个都是以最后一个结尾，则在dp[row-1][col-1]+1
                //可能性1,比较时可以忽略，因为每个位置都是比可能性1大，他们都是可能性1比较来的结果
                int r1 = table[row - 1][col - 1];
                //可能性2
                int r2 = table[row][col - 1];
                //可能性3
                int r3 = table[row - 1][col];
                //前三种取最大
                int max = Math.max(r1, r2);
                max = Math.max(max, r3);
                if (s1[row] == s2[col]) {
                    max = Math.max(r1 + 1, max);
                }
                table[row][col] = max;
            }
        }
        return table[s1.length - 1][s2.length - 1];
    }
    /**
     * 业务限制的尝试模型
     */
    /**
     * 给定一个数组，代表每个人喝完咖啡准备刷杯子的时间，有序的
     * 只有一台咖啡机，一次只能洗一个杯子，时间耗费a，洗完才能洗下一个
     * 每个咖啡杯也可以自己挥发干净，时间耗费b，咖啡杯可以并行挥发
     * 返回让所有咖啡杯变干净的最早完成时间
     *
     * @return
     */
    public static int quickClean(int[] arr, int a, int b) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        return process9(arr, a, b, 0, 0);
    }

    /**
     * @param arr      固定参数，每个人喝完咖啡的时间
     * @param a        咖啡机洗一个的时间
     * @param b        挥发一个杯子的时间
     * @param position 可变参数，当前来到第几个人
     * @param washline 当前咖啡机何时可用
     * @return
     */
    private static int process9(int[] arr, int a, int b, int position, int washline) {
        //basecase，来到了最后没有人的位置
        if (position == arr.length - 1) {
            return Math.min(
                    Math.max(washline, arr[position]) + a,
                    arr[position] + b
            );
        }
        //当前杯子用咖啡机洗,取咖啡机和喝完的最后时间，有可能咖啡机可用，而还没喝完
        int mW = Math.max(washline, arr[position]) + a;
        //后续杯子洗完的时间
        int mWB = process9(arr, a, b, position + 1, mW);
        //两者取最大，就是当前和后续杯子都能洗完
        int i = Math.max(mW, mWB);

        //当前杯子自己挥发
        int vW = arr[position] + b;
        //后续
        int vWB = process9(arr, a, b, position + 1, washline);
        int j = Math.max(vW, vWB);
        return Math.min(i, j);
    }

    /**
     * 动态规划
     *
     * @param drinks
     * @param a
     * @param b
     */
    private static int washDp(int[] drinks, int a, int b) {
        int N = drinks.length;
        if (a > b) {
            //机器还没挥发快，全部自己挥发
            return drinks[N - 1] + b;
        }
        int limit = 0;
        //获取全部用机器洗的时间
        for (int i = 0; i < N; i++) {
            limit = Math.max(drinks[i], limit) + a;
        }
        //每一杯为行，每一个咖啡机洗完的时间为列
        int[][] dp = new int[N][limit + 1];

        for (int washLine = 0; washLine < limit + 1; washLine++) {
            dp[N - 1][washLine] = Math.min(Math.max(drinks[N - 1], washLine) + a, drinks[N - 1] + b);
        }

        for (int index = N - 2; index >= 0; index--) {
            for (int washLine = 0; washLine < limit + 1; washLine++) {
                //机器洗
                int p1 = Integer.MAX_VALUE;
                int s1 = Math.max(drinks[index], washLine) + a;
                if (s1 <= limit) {
                    int next1 = dp[index + 1][s1];
                    //大于limit无意义，就会舍弃该选项
                    p1 = Math.max(s1, next1);
                }
                //挥发
                int p2 = Math.max(drinks[index] + b, dp[index + 1][washLine]);

                dp[index][washLine] = Math.min(p1, p2);
            }
        }
        return dp[0][0];
    }

    /**
     * 小红书
     * [0,4,7]：0表示石头没有颜色，如果变红代价是4，如果变蓝代价是7
     * [1,X,X]：1表示这里石头已经是红，而且不能改颜色，所以后两个参数无意义
     * [2,X,X]：2表示石头已经变蓝，而且不能改颜色，所以后两个参数无意义
     * 颜色只可能是0、1、2,代价一定>=0
     * 给你一批这样的小数组，要求最后所有石头都有颜色，且红色和蓝色一样多，返回最小代价
     * 如果怎么都无法做到所有石头都有颜色、且红色和蓝色一样多，返回-1
     */
    public static int changeColorMinCost(int[][] stone) {
        if ((stone.length & 1) != 0) {
            //奇数，不可能
            return -1;
        }
        //记录当前红色和蓝色的数量
        return process10(stone, 0, 0, 0, 0);
    }

    /**
     * 当前来到第index的石头进行选择
     *
     * @param stone 固定
     * @param red   之前红色的数量
     * @param blue  之前蓝色的数量
     * @param cost  之前花费
     * @return
     */
    private static int process10(int[][] stone, int red, int blue, int cost, int index) {
        if (index == stone.length && red == blue) {
            //basecase来到最后一个位置，如果红色和蓝色相同就返回之前的花费
            return cost;
        } else if (index == stone.length) {
            //红蓝不相等
            return -1;
        }
        //当前的石头
        int[] cur = stone[index];
        int ans = Integer.MAX_VALUE;
        if (cur[0] == 0) {
            //没颜色
            //如果变红
            int p1 = process10(stone, red + 1, blue, cost + cur[1], index + 1);
            //如果变蓝
            int p2 = process10(stone, red, blue + 1, cost + cur[2], index + 1);

            ans = Math.min(p1 == -1 ? Integer.MAX_VALUE : p1, p2 == -1 ? Integer.MAX_VALUE : p2);
        } else if (cur[0] == 1) {
            //红色
            return process10(stone, red + 1, blue, cost, index + 1);
        } else if (cur[0] == 2) {
            //蓝色
            return process10(stone, red, blue + 1, cost, index + 1);
        }

        return ans == Integer.MAX_VALUE ? -1 : ans;
    }

    public static int changeColorMinCost1(int[][] l) {
        int n = l.length;
        if ((n & 1) != 0) {
            return -1;
        }
        int red = 0;
        int blue = 0;
        int totalRed = 0;
        int totalBlue = 0;
        //记录每个可以改变的红减蓝的差值
        int[] rDB = new int[n];
        for (int i = 0; i < n; i++) {
            if (l[i][0] == 1) {
                red++;
            } else if (l[i][0] == 2) {
                blue++;
            } else {
                totalRed += l[i][1];
                totalBlue += l[i][2];
                rDB[i] = l[i][1] - l[i][2];
            }
        }
        if (red > (n / 2) || blue > (n / 2)) {
            return -1;
        }
        if (red >= blue) {
            //将差值按从小到大排序
            Arrays.sort(rDB);
            //先都转成红色
            for (int i = 0; i < red - blue; i++) {
                totalRed -= rDB[i];
            }
        } else {
            //将差值按从大到小排序
            Arrays.sort(rDB);
        }
        return 0;
    }

    /**
     * 给定正整数数组arr代表每个小朋友的得分，任何相邻的小朋友如果得分一样，怎么分糖果无所谓
     * 但如果得分不一样，分数高的糖果一定比分数低的多
     * 假设所有小朋友做成一个环形
     * 需要的最少糖果数
     *
     * @param score
     * @return
     */
    public static int minSugar(int[] score) {
        //计算一个局部最小卡两端
        //计算左坡度计算右坡度，取最大
        return 0;
    }

    /**
     * 给定整数组weights，代表每个人的体重
     * 给定正整数limit代表船的载重，所有船的载重相同
     * 每个人体重一定不大于船的载重
     * 1，可以一个人单独做一艘船
     * 2.两个人坐一个，体重和必须是偶数且总体重小于船的载重
     * 3.一艘船最多坐两个人
     *
     * @return
     */
    public static int minShip(int[] weights, int limit) {
        return 0;
    }

    /**
     * 给定一个数组arr和整数sum
     * 返回连续的累加和等于sum的子数组有多少个
     *
     * @return
     */
    public static int childArr(int[] arr, int sum) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        //前缀和出现了几次
        HashMap<Integer, Integer> preSmMap = new HashMap<>();
        //当前前缀和记录
        int all = 0;
        //所有等于sum子数组数量
        int ans = 0;
        preSmMap.put(0, 1);
        //以当前位置结尾的子数组
        for (int i = 0; i < arr.length; i++) {
            //当前位置前缀和
            all += arr[i];
            //目标前缀和
            int aim = all - arr[i];
            if (preSmMap.containsKey(aim)) {
                ans += preSmMap.get(aim);
            }
            if (!preSmMap.containsKey(all)) {
                preSmMap.put(all, 1);
            } else {
                preSmMap.put(all, preSmMap.get(all) + 1);
            }
        }
        return ans;
    }

    /**
     * 把一个01字符串切成多个部分，要求每一部分的0，1比例一样，同时要求尽可能多的划分
     * 比如：01010101
     * 01 01 01 01这是一种切法，0和1比例为1：1
     * 0101 0101也是一种切法，0和1比例为1：1
     * 两种切法都符合要求，但是尽可能多的划分就是4
     * 比如：00001111
     * 只有一种切法就是00001111作为整体，部份数是1
     * 给定一个01字符串，假设长度为N，要求返回一个长度为N的数组ans
     * 其中ans[i]=str[0....i]这个前缀串
     * 输入：010100001
     * 输出：ans=[1,1,1,2,1,2,1,1,3]
     *
     * @return
     */
    public static int[] split() {
        return null;
    }

    public static void main(String[] args) {
        int[][] stones = {{0, 1, 3}, {1, 4, 5}, {2, 7, 1}, {0, 1, 5}};

        System.out.println();
    }
}
