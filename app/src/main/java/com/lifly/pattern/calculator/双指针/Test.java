package com.lifly.pattern.calculator.双指针;

import java.util.Arrays;

public class Test {

    /**
     * 给定一个数组Arr，长度为N且每个值都是整数，代表N个人的体重，在给定一个整数limit，代表一艘船的载重
     * 每艘船最多只能做两人
     * 乘客的体重不能超过limit
     * 同时让N个人过河最少需要几条船
     *
     * @return
     */
    public static int minShip(int[] arr, int limit) {
        //先排序基数排序
        arr = baseSort(arr);
        //分割点<=limit/2
        int divider = limit / 2;
        if (arr[arr.length - 1] < divider) {
            return (arr.length + 1) / 2;
        }
        if (arr[0] > divider) {
            return arr.length;
        }
        int lessR = -1;
        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] <= (limit / 2)) {
                lessR = i;
                break;
            }
        }

        int L = lessR;
        int R = lessR + 1;
        int lessUnused = 0;
        while (L >= 0) {
            int solved = 0;
            while (R < arr.length && arr[L] + arr[R] <= limit) {
                R++;
                solved++;
            }
            if (solved == 0) {
                lessUnused++;
                L--;
            } else {
                L = Math.max(-1, L - solved);
            }
        }
        int lessAll = lessR + 1;
        int lessUsed = lessAll - lessUnused;
        int moreUnsolved = arr.length - lessR - 1 - lessUsed;
        return lessUsed + ((lessUnused + 1) >> 1) + moreUnsolved;
    }

    /**
     * 基数排序
     *
     * @param arr
     */
    public static int[] baseSort(int[] arr) {
        //放置每个位数的桶
        int[] bucket = new int[10];
        //放置每次调整后的数组
        int[] afterArr = new int[arr.length];

        //找到最大数
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }

        for (int i = 0; i < String.valueOf(max).length(); i++) {
            int divider = (int) Math.pow(10, i);
            //放到相应的桶
            for (int j = 0; j < arr.length; j++) {
                int index = arr[i] / divider % 10;
                bucket[index]++;
            }
            //累加数组，使排序稳定
            for (int j = 1; j < arr.length; j++) {
                bucket[j] = bucket[j] + bucket[j - 1];
            }

            for (int j = 0; j < arr.length; j++) {
                int index = arr[i] / divider % 10;
                afterArr[bucket[index]--] = arr[i];
            }
            //重置桶
            Arrays.fill(bucket, 0);
            System.arraycopy(afterArr, 0, arr, 0, arr.length);
        }
        return arr;
    }


}
