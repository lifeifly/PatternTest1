package com.lifly.pattern.calculator.coding;

import com.lifly.pattern.calculator.dp.DpTest1;
import com.lifly.pattern.calculator.牛牛.Sweep;

import java.util.Arrays;
import java.util.HashMap;

public class CodingTest {
    /**
     * 吧一个数字用中文表示出来，数字范围：【0，99999】
     * 为了方便输出，使用字母替换相应德中文，万 W 千 Q 百B 十S 零L
     * 使用数字取代中文数字注：对于11 应表示为一十一（1S1）而不是十一（S1）
     * <p>
     * 示例
     * 12001
     * 1W2QL1
     *
     * @param i
     */
    public static void outStr(int i) {
        System.out.println(num1_99999(i));
    }

    public static String num1_9(int i) {
        if (i < 1 || i > 9) {
            return "";
        }


        return i + "";
    }

    public static String num1_99(int i, boolean hasMore) {
        if (i < 1 || i > 99) {
            return "";
        }
        int l = i % 10;
        if (i < 10) {
            return ((l != 0 && hasMore) ? "L" : "") + num1_9(i);
        }
        int shi = i / 10;

        return shi + "S" + num1_9(i % 10);
    }

    public static String num1_999(int i, boolean hasMore) {
        if (i < 1 || i > 999) {
            return "";
        }
        int shi = i / 10;
        if (i < 100) {
            return ((hasMore && shi != 0) ? "L" : "") + num1_99(i, hasMore);
        }
        int bai = i / 100;


        return bai + "B" + num1_99(i % 100, true);
    }

    public static String num1_9999(int i, boolean hasMore) {
        if (i < 1 || i > 9999) {
            return "";
        }
        int bai = i / 100;
        if (i < 1000) {
            return ((hasMore && bai != 0) ? "L" : "") + num1_999(i, hasMore);
        }
        int qian = i / 1000;
        return qian + "Q" + num1_999(i % 1000, true);
    }

    public static String num1_99999(int i) {
        if (i < 1 || i > 99999) {
            return "";
        }
        if (i < 10000) {
            return num1_9999(i, false);
        }
        int wan = i / 10000;

        return wan + "W" + num1_9999(i % 10000, true);
    }

    /**
     * 求完全二叉树节点的个数
     *
     * @return
     */
    public static int nodeCount(DpTest1.Node node) {
        if (node == null) {
            return 0;
        }
        //获取最大深度，因为是完全二叉树因此只需计算左子节点的个数
        int mostDepth = mostLeftLevel(node, 1);
        return bs(node, 1, mostDepth);
    }

    /**
     * @param node      当前节点
     * @param curLevel  当前节点的层数
     * @param mostDepth 整个完全二叉树的最大层数
     * @return
     */
    private static int bs(DpTest1.Node node, int curLevel, int mostDepth) {
        if (curLevel == mostDepth) {
            //当前节点到达最低层
            return 1;
        }
        //获取右子节点的最左节点层数
        if (mostLeftLevel(node.right, curLevel + 1) == mostDepth) {
            //说明左子节点为头的是完全二叉树
            return (1 << (mostDepth - curLevel)) + bs(node.right, curLevel + 1, mostDepth);
        } else {
            //说明右子节点为头的是完全二叉树
//            return (1 << (mostDepth - curLevel - 1)) + bs(node.left, curLevel + 1, mostDepth);
            return (int) (Math.pow(2, mostDepth - curLevel - 1) + bs(node.left, curLevel + 1, mostDepth));
        }
    }

    /**
     * 如果node在lvevel层
     * 求已node为头节点的子树，最大深度事多少
     * node为头的子树一定是完全二叉树
     *
     * @return
     */
    public static int mostLeftLevel(DpTest1.Node node, int curLevel) {
        while (node != null) {
            curLevel++;
            node = node.left;
        }
        return curLevel - 1;
    }

    /**
     * 最长递增子序列问题
     */
    //动态规划，O（N^2）
    public static int dpMost(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        //表示必须包含以i位置结尾的最大递增序列长度
        int[] dp = new int[arr.length];
        //第0位置肯定只有自己
        dp[0] = 1;
        int res = 0;
        for (int i = 1; i < arr.length; i++) {
            //从之前的元素中找到比自己小并且最大递增序列最大的那个元素作为倒数第二个元素
            int curCount = -1;
            for (int j = 0; j < i; j++) {
                if (arr[j] < arr[i] && curCount < dp[j]) {
                    curCount = dp[j];
                }
            }

            dp[i] = curCount == -1 ? 1 : (curCount + 1);
            res = (curCount != -1 ? i : res);
            System.out.println(arr[i] + " " + curCount + " " + res);
        }
        return dp[res];
    }

    //动态规划，O（N^2）
    // {2, 3, 1, 5, 8, 6, 0}
    public static int dpMostEnds(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        //表示必须包含以i位置结尾的最大递增序列长度
        int[] dp = new int[arr.length];
        //表示当前位置+1长度的递增子序列的最小结尾
        int[] ends = new int[arr.length];
        Arrays.fill(ends, Integer.MAX_VALUE);
        //第0位置肯定只有自己
        dp[0] = 1;
        ends[0] = arr[0];
        //ends中有效区最右侧的位置
        int endsEnd = 0;

        for (int i = 1; i < arr.length; i++) {
            //二分查找ends中大于arr[i]的最左侧的位置,因为是有序的，可以二分查找
            int taregt = nearestIndex(ends, endsEnd, arr[i]);
            if (taregt == -1) {
                //没找到比自身小的，往后扩充
                dp[i] = taregt + 1;
                ends[++endsEnd] = arr[i];
            } else {
                //找到了，比较是否交换
                dp[i] = 1;
                ends[taregt] = Math.min(ends[taregt], arr[i]);
            }
        }

        return endsEnd + 1;
    }

    /**
     * 在有序数组中，找>=value的最左位置
     *
     * @param value
     * @return
     */
    public static int nearestIndex(int[] sortedArr, int right, int value) {
        if (sortedArr == null || sortedArr.length == 0) {
            return -1;
        }
        int l = 0;
        int r = right;
        int mid;
        int target = -1;
        while (l <= r) {
            mid = l + ((r - l) >> 1);
            if (sortedArr[mid] >= value) {
                target = mid;
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }
        return target;
    }

    /**
     * 小Q得到一个神奇的数列：1，12，123，。。。。。。。。12345678910
     * 小Q对于能否被3整除这个性质很感兴趣
     * 希望你能帮他计算一下从数列得第L个到第R个（包含端点）有多少个数可以被3整除
     * 输入2，5
     * 输出3
     *
     * @return
     */
    public static int amazingQueue(int L, int R) {
        long total = 0;
        int res = 0;
        for (int i = L; i <= R; i++) {
            total += L;
            if (total % 3 == 0) {
                res += 1;
            }
        }
        return res;
    }

    /**
     * 给定一个整数数组A，长度为n，有1<=A[i]<=n,且对于【1，n】的整数，其中部分整数会重复出现而部分不会出现
     * 实现算法找到[1，n]中所有未出现在A中的整数
     * 输入1，3，4，3
     * 输出2
     *
     * @return
     */
    public static void notAppearCount(int[] arr) {
        //对应0位置放1，1位置放2
        for (int i : arr) {
            modify(arr, i);
        }
        //对应位置不匹配的就是缺失的
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != i + 1) {
                System.out.println(i + 1);
            }
        }
    }

    private static void modify(int[] arr, int ele) {
        while (arr[ele - 1] != ele) {
            int temp = arr[ele - 1];
            arr[ele - 1] = ele;
            ele = temp;
        }
    }

    /**
     * 假设一开始女主播的初始人气值为start，能够晋升下一轮人气需要刚好达到end，土豪给主播增加的方法有：
     * a:点赞 花费x C币，人气+2
     * b:送礼 花费y C币，人气*2
     * c:私聊 花费z C币，人气-2
     * 其中end远大于start且start,end为偶数
     * ，请写一个复制程序，计算一下最少花费多少C币可以邦族主播刚好到达end
     * x，y，z，start，end
     * 输入3，100，1，2，6
     * 输出：6
     */
    public static int minC(int x, int y, int z, int start, int end) {
        if (end >= start) {
            return -1;
        }
        //业务发现basecase普遍解，全是点赞会花费多少C币，如果期间大于limitMoney,则无效
        int limitMoney = (end - start) / 2 * x;
        //业务发现basecase，可能会存在先超过end后通过私聊方式减到end的情况，最大人气值限制不可能超过2倍end
        int limitPopularity = 2 * end;

        return process(x, y, z, end, start, 0, limitMoney, limitPopularity);
    }

    /**
     * @param x               固定点赞花费
     * @param y               固定送礼花费
     * @param z               固定私聊花费
     * @param end             固定目标值
     * @param cur             当前人气值
     * @param preMoney        当前花费
     * @param limitMoney      最多花费限制
     * @param limitPopularity 最多人气限制
     * @return
     */
    private static int process(int x, int y, int z, int end, int cur, int preMoney, int limitMoney, int limitPopularity) {
        if (preMoney > limitMoney) {
            return Integer.MAX_VALUE;
        }
        if (cur < 0) {
            return Integer.MAX_VALUE;
        }
        if (cur > limitPopularity) {
            return Integer.MAX_VALUE;
        }
        if (cur == end) {
            return preMoney;
        }
        //点赞
        int p1 = process(x, y, z, end, cur + 2, preMoney + x, limitMoney, limitPopularity);
        //送礼
        int p2 = process(x, y, z, end, cur * 2, preMoney + y, limitMoney, limitPopularity);
        //私聊
        int p3 = process(x, y, z, end, cur - 2, preMoney + z, limitMoney, limitPopularity);

        return Math.min(p1, Math.min(p2, p3));
    }

    public static int maxProfit() {
        return 0;
    }

    public static class Program {
        public int cost;
        public int profit;
        public HashMap<Integer, Integer> fromCurToEndTable = new HashMap<>();

        public Program(int cost, int profit) {
            this.cost = cost;
            this.profit = profit;
        }
    }

    /**
     * 给定一个只由0（假）、1（真）、&（逻辑与）、|（逻辑或）和^（异或）五种字符组成的字符串express
     * 在给定一个布尔值desired，返回express能有多少种组合方式，可以达到desired的结果
     * <p>
     * express=”1^0|0|1“ desired=false
     * 只有1^((0|0)|1)和1^(0|(0|1))的组合可以得到false，返回2
     * express=”1“ desired=false,返回0
     *
     * @return
     */
    public static int desired(String express, boolean desired) {
        if (express == null || express.equals("")) {
            return 0;
        }
        char[] chs = express.toCharArray();
        if (!isValid(chs)) {
            return 0;
        }
        return process1(chs, desired, 0, chs.length);
    }

    /**
     * 表示在l-r中的任意选择一个逻辑符号作为最后才来计算的符号，达到desired有多少种方式，l、r必须是在数字位置上
     *
     * @param chs
     * @param desired
     * @param l
     * @param r
     * @return
     */
    private static int process1(char[] chs, boolean desired, int l, int r) {
        if (l == r) {
            if (chs[l] == '0') {
                return desired ? 0 : 1;
            } else {
                return desired ? 1 : 0;
            }
        }
        int res = 0;
        if (desired) {
            for (int i = l + 1; i < r; i += 2) {
                switch (chs[i]) {
                    case '&':
                        res += process1(chs, true, l, i - 1) * process1(chs, true, i + 1, r);
                        break;
                    case '|':
                        res += process1(chs, true, l, i - 1) * process1(chs, false, i + 1, r);
                        res += process1(chs, false, l, i - 1) * process1(chs, true, i + 1, r);
                        res += process1(chs, true, l, i - 1) * process1(chs, true, i + 1, r);
                        break;
                    case '^':
                        res += process1(chs, true, l, i - 1) * process1(chs, false, i + 1, r);
                        res += process1(chs, false, l, i - 1) * process1(chs, true, i + 1, r);
                        break;
                }
            }
        } else {
            for (int i = l + 1; i < r; i += 2) {
                switch (chs[i]) {
                    case '&':
                        res += process1(chs, false, l, i - 1) * process1(chs, true, i + 1, r);
                        res += process1(chs, true, l, i - 1) * process1(chs, false, i + 1, r);
                        res += process1(chs, false, l, i - 1) * process1(chs, false, i + 1, r);
                        break;

                    case '|':
                        res += process1(chs, false, l, i - 1) * process1(chs, false, i + 1, r);
                        break;
                    case '^':
                        res += process1(chs, true, l, i - 1) * process1(chs, true, i + 1, r);
                        res += process1(chs, false, l, i - 1) * process1(chs, false, i + 1, r);
                        break;
                }
            }
        }
        return res;
    }

    /**
     * 检查逻辑字符串有效性，偶数位必须是0或1，奇数位必须是逻辑符号
     *
     * @param chs
     * @return
     */
    private static boolean isValid(char[] chs) {
        for (int i = 0; i < chs.length; i += 2) {
            if (chs[i] != '0' && chs[i] != '1') {
                return false;
            }
        }
        for (int i = 1; i < chs.length; i += 2) {
            if (chs[i] != '&' && chs[i] != '|' && chs[i] != '^') {
                return false;
            }
        }
        return true;
    }

    //改动态规划
    public static int desiredDp(String express, boolean desired) {
        if (express == null || express.length() == 0) {
            return 0;
        }

        char[] chs = express.toCharArray();
        int n = chs.length;
        if (!isValid(chs)) {
            return 0;
        }
        //三个可变参数，但是desired只有两个值，所以是两个二维表
        int[][] desiredTrue = new int[n][n];
        int[][] desiredFalse = new int[n][n];
        for (int i = 0; i < n; i += 2) {
            desiredTrue[i][i] = (chs[i] == '1') ? 1 : 0;
            desiredFalse[i][i] = (chs[i] == '0') ? 1 : 0;
        }

        for (int row = n - 3; row >= 0; row += 2) {
            for (int col = row + 2; col < n; col += 2) {
                for (int i = row + 1; i < col; i += 2) {
                    switch (chs[i]) {
                        case '&':
                            desiredTrue[row][col] += desiredTrue[row][i - 1] * desiredTrue[i + 1][col];
                            break;
                        case '|':
                            desiredTrue[row][col] += desiredTrue[row][i - 1] * desiredFalse[i + 1][col];
                            desiredTrue[row][col] += desiredFalse[row][i - 1] * desiredTrue[i + 1][col];
                            desiredTrue[row][col] += desiredTrue[row][i - 1] * desiredTrue[i + 1][col];
                            break;
                        case '^':
                            desiredTrue[row][col] += desiredTrue[row][i - 1] * desiredFalse[i + 1][col];
                            desiredTrue[row][col] += desiredFalse[row][i - 1] * desiredTrue[i + 1][col];
                            break;
                    }
                    switch (chs[i]) {
                        case '&':
                            desiredFalse[row][col] += desiredFalse[row][i - 1] * desiredTrue[i + 1][col];
                            desiredFalse[row][col] += desiredTrue[row][i - 1] * desiredFalse[i + 1][col];
                            desiredFalse[row][col] += desiredFalse[row][i - 1] * desiredFalse[i + 1][col];
                            break;
                        case '|':
                            desiredFalse[row][col] += desiredFalse[row][i - 1] * desiredFalse[i + 1][col];
                            break;
                        case '^':
                            desiredFalse[row][col] += desiredTrue[row][i - 1] * desiredTrue[i + 1][col];
                            desiredFalse[row][col] += desiredFalse[row][i - 1] * desiredFalse[i + 1][col];
                            break;
                    }
                }
            }
        }
        return desired ? desiredTrue[0][n - 1] : desiredFalse[0][n - 1];
    }

    /**
     * 在一个字符串中找到没有重复字符子串中最长的长度
     * 例如abcabcbb没有重复字符的最长子串是abc，长度为3
     * bbbbb长度为1
     * pwwkew，wke，长度为3
     * 要求：答案必须是子串
     * 技巧：以当前位置字符结尾的最大无重复子串是多少
     *
     * @return
     */
    public static String maxSubStr(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        String res = "";
        String push = "";
        for (int i = 0; i < str.length(); i++) {
            String cur = str.substring(i, i + 1);
            if (push.contains(cur)) {
                if (res.length() <= push.length()) {
                    res = push;
                }
                push = push.substring(push.indexOf(cur));
            } else {
                push += cur;
            }
        }
        if (res.length() <= push.length()) {
            res = push;
        }
        return res;
    }

    //动态规划
    public static int maxSubStrDp(String str) {
        if (str == null || str.length() == 0) {
            return 0;
        }
        char[] chs = str.toCharArray();
        //记录每个字符上次出现的位置
        int[] map = new int[256];
        for (int i = 0; i < 256; i++) {
            map[i] = -1;
        }
        int len = 0;
        int pre = -1;
        int cur = 0;
        for (int i = 0; i < chs.length; i++) {
            pre = Math.max(pre, map[chs[i]]);
            cur = i - pre;
            len = Math.max(len, cur);
            map[chs[i]] = i;
        }
        return len;
    }

    /**
     * 给定两个字符串str1和str2，再给定三个整数ic、dc、rc，分别代表插入、删除和替换一个字符的代价
     * 返回将str1编辑成str2的最小的代价
     * str1=“abc”  str2=“adc” ic=5，dc=3，rc=2返回最小代价2
     *
     * @return
     */
    public static int minCost(String str1, String str2, int ic, int dc, int rc) {
        if (str1 == null || str2 == null) {
            return 0;
        }
        int n1 = str1.length();
        int n2 = str2.length();
        char[] chs1 = str1.toCharArray();
        char[] chs2 = str2.toCharArray();
        //表示0-i-1行str1前缀转换程0-j-1列str2前缀的最小代价
        int[][] map = new int[n1 + 1][n2 + 1];
        map[0][0] = 0;
        for (int i = 1; i < n1 + 1; i++) {
            map[i][0] = i * ic;
        }
        for (int i = 1; i < n2 + 1; i++) {
            map[0][i] = i * ic;
        }

        for (int i = 1; i < n1 + 1; i++) {
            for (int j = 1; j < n2 + 1; j++) {
                if (chs1[i - 1] == chs2[j - 1]) {
                    map[i][j] = map[i][j - 1];
                } else {
                    map[i][j] = map[i][j - 1] + rc;
                }
                map[i][j] = Math.min(map[i][j], map[i][j - 1] + ic);
                map[i][j] = Math.min(map[i][j], map[i - 1][j] + dc);
            }
        }
        return map[n1][n2];
    }

    /**
     * 给定一个全是小写字母的字符串str，删除多余字符，使得每种字符只保留一个，并让最终结果字符串的字典序最小
     * str=“acbc” 删除“c”代价最小并且字典序最小
     *
     * @return
     */
    public static String minCost1(String str) {
        if (str == null || str.length() < 2) {
            return str;
        }
        //根据字符ASCII码存放出现的次数
        int[] map = new int[256];
        for (int i = 0; i < str.length(); i++) {
            map[str.charAt(i)]++;
        }
        //先找出第一个字符先减为零的位置
        int minAscIndex = 0;
        for (int i = 0; i < str.length(); i++) {
            if (--map[str.charAt(i)] == 0) {
                break;
            } else {
                minAscIndex = str.charAt(minAscIndex) > str.charAt(i) ? i : minAscIndex;
            }
        }

        return String.valueOf(
                str.charAt(minAscIndex)
                        + minCost1(str.substring(minAscIndex + 1)
                        .replaceAll(String.valueOf(str.charAt(minAscIndex)), ""))
        );
    }

    /**
     * 一个字符串从a-z，所有的子序列先按个数、后按字典序依次排序，给你一个子序列，求该子序列在所有子序列中的位置
     *
     * @return
     */
    public static int findPosition(String subStr) {
        char[] chs = subStr.toCharArray();
        int sum = 0;
        int len = subStr.length();
        //所有比自己短的排前面
        for (int i = 0; i < len; i++) {
            sum += f(i);
        }
        //当前头字符的ascma
        int firstASC = subStr.charAt(0) - 'a' + 1;
        //比头节点ASC小的开头的相同长度的排前面
        for (int i = 0; i < firstASC; i++) {
            sum += g(i, len);
        }
        int pre = firstASC;
        for (int i = 1; i < len; i++) {//枚举每个长度
            int cur = subStr.charAt(i) - 'a' + 1;
            for (int j = pre + 1; j < cur; j++) {//头节点到第二个节点中有多少可能性
                sum += g(i, len - i);
            }
            pre = cur;
        }
        return sum + 1;
    }

    /**
     * 0-25分别对应a-z
     * 以当前字符开头的、长度为len的子序列有多少个
     *
     * @param i
     * @param len
     * @return
     */
    public static int g(int i, int len) {
        if (len == 1) {
            return 1;
        }
        int sum = 0;
        for (int j = 0; j < 26; j++) {
            sum += g(j, len - 1);
        }
        return 0;
    }

    /**
     * 0-25分别对应a-z
     * 长度为len的子序列有多少个
     *
     * @return
     */
    public static int f(int len) {
        int sum = 0;
        for (int i = 0; i < 26; i++) {
            sum += g(i, len);
        }
        return sum;
    }


    public static void main(String[] args) {
        String s = "bbbbb";
        System.out.println(maxSubStr(s));
    }

}
