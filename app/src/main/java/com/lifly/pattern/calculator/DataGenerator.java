package com.lifly.pattern.calculator;

import java.util.Arrays;

public class DataGenerator {

    public static int[] generatorRandomArray(int maxSize, int maxValue) {
        int[] arr = new int[((int) ((maxSize + 1) * Math.random()))];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (int) ((maxValue + 1) * Math.random()) - (int) ((maxValue * Math.random()));
        }
        return arr;
    }

    public static int[] copy(int[] arr) {
        if (arr == null) return null;
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = arr[i];
        }
        return res;
    }

    public static void comparator(int[] arr) {
        Arrays.sort(arr);
    }

    public static boolean isEqual(int[] arr1, int[] arr2) {
        if ((arr1 != null && arr2 == null) || (arr1 == null && arr2 != null)) {
            return false;
        }
        if (arr1 == null && arr2 == null) {
            return true;
        }
        if (arr1.length != arr2.length) {
            return false;
        }
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] != arr2[i]) {
                return false;
            }
        }
        return true;
    }
}
