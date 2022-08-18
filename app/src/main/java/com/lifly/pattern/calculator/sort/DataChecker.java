package com.lifly.pattern.calculator.sort;

import com.lifly.pattern.calculator.recursive.Test;

import java.util.Arrays;
import java.util.Random;

public class DataChecker {
    public static int[] generateRandomArray() {
        Random r = new Random();
        int[] arr = new int[10000];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = r.nextInt(10000);
        }
        return arr;
    }

    public static boolean check() {
        int[] arr = generateRandomArray();
//        int[] arr = {17,21,13,49,78,35,56,64,92,80};
        int[] arr2 = new int[arr.length];
        System.arraycopy(arr, 0, arr2, 0, arr.length);

        Arrays.sort(arr);
        Test.quickSort(arr2);
//        SortManager1 manager = new SortManager1(arr2);
//        manager.quickSort();
//        System.out.println(Arrays.toString(arr2));
        boolean same = true;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != arr2[i]) {
                same = false;
            }
        }
        return same;
    }
}
