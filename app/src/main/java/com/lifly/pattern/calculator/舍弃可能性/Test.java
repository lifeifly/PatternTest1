package com.lifly.pattern.calculator.舍弃可能性;

import com.lifly.pattern.calculator.graph.Node;

import java.util.Arrays;
import java.util.HashMap;

public class Test {
    /**
     * 给定一个数组，求如果排序知乎，相邻两数的最大差值，要求时间复杂度为O（n），且要求不能用非基于比较的排序
     */
    public static int sortNotCompare(int[] arr) {
        if (arr == null || arr.length < 2) {
            return 0;
        }
        //遍历获取最大值和最小值
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        int len = arr.length;
        for (int i = 0; i < len; i++) {
            max = Math.max(max, arr[i]);
            min = Math.min(min, arr[i]);
        }
        //创建3个桶信息,长度都是len+1，确保中间一定有空桶
        boolean[] hasNum = new boolean[len + 1];
        int[] maxs = new int[len + 1];
        int[] mins = new int[len + 1];

        //将每个数放入桶中
        //将每个数放入桶中
        for (int i = 0; i < len; i++) {
            //计算桶索引
            int bid = bucket(arr[i], len, max, min);
            maxs[bid] = hasNum[bid] ? Math.max(maxs[bid], arr[i]) : arr[i];
            mins[bid] = hasNum[bid] ? Math.min(mins[bid], arr[i]) : arr[i];
            hasNum[bid] = true;
        }
        //计算跨桶数中最大的差值
        int res = 0;
        int lastMax = maxs[0];//上一个非空桶的最大值
        int i = 1;
        for (; i < len + 1; i++) {
            if (hasNum[0]) {
                res = Math.max(res, mins[i] - lastMax);
                lastMax = maxs[i];
            }
        }
        return res;
    }

    /**
     * 计算桶位置，确保最小值的桶在最左，最大值的桶在最右
     *
     * @param cur
     * @param len
     * @param max
     * @param min
     * @return
     */
    private static int bucket(int cur, int len, int max, int min) {
        return (cur - min) * len / (max - min);
    }

    /**
     * 给出数组a_1,......,a_n问最多有多少个不重叠的非空区间，使得每个区间内的数字的xor（异或和）都等于0
     * 分析：1：以i结尾的前缀数组中，i在异或和为0的部分中，依赖上一个异或和相同的位置的结果
     * 2：以i结尾的前缀数组中，i不在异或和为0的部分中，依赖前一个结果
     *
     * @return
     */
    public static int notOverlapSpace(int[] arr) {
        int xor = 0;
        int n = arr.length;
        //表示以i位置结尾的前缀数组最优异或和为零的结果
        int[] dp = new int[n];
        //存放异或和最新的位置
        HashMap<Integer, Integer> xorMap = new HashMap<>();
        xorMap.put(0, -1);
        for (int i = 0; i < n; i++) {
            xor ^= arr[i];
            if (xorMap.containsKey(xor)) {
                //如果以前有异或和相同的地方，就取出这一可能性
                int pre = xorMap.get(xor);
                dp[i] = pre == -1 ? 1 : (dp[pre] + 1);
            }
            //如果当前位置不在异或和为0的部分中，依赖上一个位置的结果
            if (i > 0) {
                dp[i] = Math.max(dp[i - 1], dp[i]);
            }
            xorMap.put(xor, i);
        }
        return dp[n - 1];
    }

    /**
     * 现有n1+n2种面值的硬币，其中前n1种为普通币，可以任意取，后n2种为纪念币，每种最多只能取一枚，每种硬币有一个面值，问能用多少种方法拼出m的面值
     * 分析：枚举n1种普通币从0拼到m的每个方法数，在枚举n2对应的m-0的方法数，对应相乘，在累加
     *
     * @return
     */
    public static int mergeM(int[] n1, int[] n2, int m) {
        if (m == 0) {
            return 0;
        }

        int len1 = n1.length;
        //动态规划,dp1[i][j]代表使用前i种货币，每种任意张拼成j的方法数
        int[][] dp1 = new int[len1 + 1][m + 1];

        dp1[len1][0] = 1;

        for (int i = len1 - 1; i >= 0; i--) {
            for (int j = 0; j < m + 1; j++) {
                int count = 0;
                int curMoney = n1[i];
                int res = 0;
                while (count * curMoney <= j) {
                    res += dp1[i + 1][j - count * curMoney];
                    count++;
                }
                dp1[i][j] = res;
            }
        }


        //动态规划
        int len2 = n2.length;
        int[][] dp2 = new int[len2 + 1][m + 1];
        dp2[len2][0] = 1;

        for (int i = len2 - 1; i >= 0; i--) {
            for (int j = 0; j < m + 1; j++) {
                int curMoney = n2[i];
                int res = 0;
                //不要
                res += dp2[i + 1][j];
                //要
                if (j - curMoney >= 0) {
                    res += dp2[i + 1][j - curMoney];
                }
                dp2[i][j] = res;
            }
        }

        int res = 0;
        for (int i = 0; i < m + 1; i++) {
            res += (dp1[0][i] * dp2[0][m - i]);
        }

        //存放只用n1拼成0-m各有多少种方法
//        int[] nMap1 = new int[m + 1];
//        for (int i = 0; i <= m; i++) {
//            nMap1[i] = process3(n1, 0, i);
//        }
//        //存放只用n2拼成0-m各有多少种方法
//        int[] nMap2 = new int[m + 1];
//        for (int i = 0; i <= m; i++) {
//            nMap2[i] = process4(n2, 0, i);
//        }
//        int res=0;
//        for (int i = 0; i < m + 1; i++) {
//            res += (nMap1[i] * nMap2[m - i]);
//        }
        return res;
    }


    /**
     * 当前剩余货币left，来到cur位置货币做选择
     *
     * @param n1
     * @param cur
     * @param left
     */
    private static int process3(int[] n1, int cur, int left) {
        if (left == 0) {
            return 1;
        }
        if (cur == n1.length) {
            return 0;
        }
        int count = 0;
        int curMoney = n1[cur];
        int res = 0;
        while (count * curMoney <= left) {
            res += process3(n1, cur + 1, left - count * curMoney);
            count++;
        }
        return res;
    }

    /**
     * 当前剩余left，来到cur位置货币做选择
     *
     * @param n2
     * @param cur
     * @param left
     * @return
     */
    private static int process4(int[] n2, int cur, int left) {
        if (cur == n2.length) {
            return left == 0 ? 1 : 0;
        }
        int res = 0;
        //当前货币要结果
        res += process4(n2, cur + 1, left - n2[cur]);
        //当前货币不要的结果
        res += process4(n2, cur + 1, left);
        return res;
    }

    /**
     * 给定两个一维数组A和B
     * 其中A的长度为m、元素从小到大排好序的有序数组
     * B的长度为n、元素从小到大排好序的有序数组
     * 希望从A和B的数组种，找出最大的数字K，要求：使用尽量少的比较次数
     *
     * @return
     */
    public static int findMax(int[] arr1, int[] arr2, int kth) {
        if (arr1 == null || arr2 == null) {
            throw new RuntimeException("Arr is null");
        }
        if (kth < 1 || kth > arr1.length + arr2.length) {
            throw new RuntimeException("kth is invalid");
        }
        int[] longs = arr1.length >= arr2.length ? arr1 : arr2;
        int[] shorts = arr1.length < arr2.length ? arr1 : arr2;
        int l = longs.length;
        int s = shorts.length;
        if (kth <= s) {
            return getUpMedian(arr1, 0, kth - 1, arr2, 0, kth - 1);
        }
        if (kth > l) {
            if (shorts[kth - l - 1] >= longs[l - 1]) {
                return shorts[kth - l - 1];
            }
            if (longs[kth - s - 1] >= shorts[s - 1]) {
                return longs[kth - s - 1];
            }
            return getUpMedian(shorts, kth - l, s - 1, longs, kth - s, l - 1);
        }
        if (longs[kth - s - 1] >= shorts[s - 1]) {
            return longs[kth - s - 1];
        }
        return getUpMedian(shorts, 0, s - 1, longs, kth - s, kth - 1);
    }

    /**
     * 两个等长的数组，求上中位数
     *
     * @param a1
     * @param l1
     * @param r1
     * @param a2
     * @param l2
     * @param r2
     * @return
     */
    public static int getUpMedian(int[] a1, int l1, int r1, int[] a2, int l2, int r2) {
        int mid1 = 0;
        int mid2 = 0;
        int offset = 0;
        while (l1 < r1) {
            mid1 = (l1 + r1) / 2;
            mid2 = (l2 + r2) / 2;
            offset = ((r1 - l1 + 1) & 1) ^ 1;
            if (a1[mid1] > a2[mid2]) {
                r1 = mid1;
                l2 = mid2 + offset;
            } else if (a1[mid1] < a2[mid2]) {
                r2 = mid2;
                l1 = mid1 + offset;
            } else {
                return a1[mid1];
            }
        }
        return Math.min(a1[l1], a2[l2]);
    }

    /**
     * 长度为i，数到m就杀人，求最后剩下的那个
     * 公式1；长度为i，数到m杀死的编号 h=(m-1)%i+1
     * 公式2：被杀死的前的编号 旧编号=（新编号+m-1）%i+1
     *
     * @return
     */
    public static Node josephusKill(Node head, int m) {
        if (head == null || head.next == head || m < 1) {
            return head;
        }
        Node cur = head.next;
        int temp = 1;
        while (cur != head) {
            temp++;
            cur = cur.next;
        }
        temp = getLive(temp, m);
        while (--temp != 0) {
            head = head.next;
        }
        head.next = head;
        return head;
    }

    public static int getLive(int i, int m) {
        if (i == 1) {
            return 1;
        }
        return (getLive(i - 1, m) + m - 1) % i + 1;
    }

    public static class Node {
        public int value;
        public Node next;
    }

    /**
     * 一群人围城一个圈，每数到m旧淘汰，m依次使用ax中的数，求最后一个剩下的编号
     *
     * @return
     */
    public static int findJob(int n, int[] ax) {
        return no(n, ax, 0);
    }

    /**
     * 还剩i个人，当前取用的数字是arr【index】，并且从index出发，循环取用，返回哪个人会活（原始编号）
     *
     * @param i
     * @param arr
     * @param index
     * @return
     */
    public static int no(int i, int[] arr, int index) {
        if (i == 1) {
            return 1;
        }
        return (no(i - 1, arr, nextIndex(arr.length, index)) + arr[index] - 1) % i + 1;
    }

    /**
     * 如果数组长度为size，当前下标为index，返回循环的下一个index
     *
     * @param size
     * @param index
     * @return
     */
    public static int nextIndex(int size, int index) {
        return index == size - 1 ? 0 : index + 1;
    }

    public static void main(String[] args) {
        int[] n1 = {1, 2};
        int[] n2 = {2, 3};

        System.out.println(mergeM(n1, n2, 5));
    }

}
