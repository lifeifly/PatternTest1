package com.lifly.pattern.calculator.假设答案法;

import com.lifly.pattern.calculator.贪心.GreedyTest;

public class Test {
    /**
     * arr代表职位打分，分数有正有负，求连续打分和最大的和为多少
     *
     * @param arr
     * @return
     */
    public static int maxAim(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        int max = Integer.MIN_VALUE;
        int cur = 0;
        for (int i = 0; i < arr.length; i++) {
            cur += arr[i];
            max = Math.max(cur, max);
            cur = cur < 0 ? 0 : cur;
        }
        return max;
    }

    /**
     * 返回一个矩阵中子矩阵累加和最大的那个
     *
     * @param matrix
     * @return
     */
    public static int maxAimChildMatrix(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return 0;
        }
        int max = Integer.MIN_VALUE;
        int cur = 0;
        int[] s = null;
        for (int i = 0; i < matrix.length; i++) {
            s = new int[matrix[0].length];
            for (int j = i; j < matrix.length; j++) {
                cur = 0;
                for (int k = 0; k < s.length; k++) {
                    s[k] += matrix[j][k];
                    cur += s[k];
                    max = Math.max(max, cur);
                    cur = cur < 0 ? 0 : cur;
                }
            }
        }
        return max;
    }
}
