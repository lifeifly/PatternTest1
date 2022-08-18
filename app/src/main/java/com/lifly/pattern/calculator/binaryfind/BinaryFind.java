package com.lifly.pattern.calculator.binaryfind;

public class BinaryFind {
    /**
     * 在一个有序数组中，num是否存在
     *
     * @param sortedArr
     * @param num
     * @return
     */
    public static boolean isExist(int[] sortedArr, int num) {
        if (sortedArr == null || sortedArr.length == 0) {
            return false;
        }
        int l = 0;
        int r = sortedArr.length - 1;
        int mid = 0;

        while (l <= r) {
            mid = l + ((r - l) >> 1);//mid=(l+r)/2;
            if (sortedArr[mid] == num) {
                return true;
            } else if (sortedArr[mid] > num) {
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }
        return false;
    }

    /**
     * 在有序数组中，找>=value的最左位置
     *
     * @param value
     * @return
     */
    public static int nearestIndex(int[] sortedArr, int value) {
        if (sortedArr == null || sortedArr.length == 0) {
            return -1;
        }
        int l = 0;
        int r = sortedArr.length - 1;
        int mid = 0;
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
     * 在有序数组中，找<=value的最右位置
     *
     * @param value
     * @return
     */
    public static int nearestRightIndex(int[] sortedArr, int value) {
        if (sortedArr == null || sortedArr.length == 0) {
            return -1;
        }
        int l = 0;
        int r = sortedArr.length - 1;
        int mid;
        int target = -1;
        while (l <= r) {
            mid = l + ((r - l) >> 1);
            if (sortedArr[mid] < value) {
                target = mid;
                l = mid + 1;
            } else if (sortedArr[mid] == value) {
                target = mid;
                r = mid - 1;
            } else {
                r = mid - 1;
            }
        }
        return target;
    }

    /**
     * 无序数组中，相邻不相等，找出一个局部最小
     * [0,1] 0
     * [0,1,2] 1
     *
     * @return
     */
    public static int findPartMin(int[] arr) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        if (arr.length == 1 || arr[0] < arr[1]) {
            return 0;
        }
        if (arr[arr.length - 1] < arr[arr.length - 2]) {
            return arr.length - 1;
        }
        int left = 1;
        int right = arr.length - 2;
        int mid = 0;
        while (left < right) {
            mid = left + ((right - left) >> 1);
            if (arr[mid] > arr[mid - 1]) {
                right = mid - 1;
            } else if (arr[mid] > arr[mid + 1]) {
                left = mid + 1;
            } else {
                return mid;
            }
        }
        return -1;
    }

    /**
     * 两种数出现奇数次，其他数出现偶数次，求两种数
     *
     * @param arr
     */
    public static void printOddTimesNum2(int[] arr) {
        int result = 0;
        for (int i = 0; i < arr.length; i++) {
            result = result ^ arr[i];
        }
        //最终result=a^b，找到二进制位最右侧的1所代表的数,此时代表a和b在这一位上不相等
        int right = result & ((~result) + 1);
        int onlyOne = 0;
        //找到所有最右侧是right的一组数进行异或(相当于以a、b最右侧不相等的那一位是1、0进行分组，a、b肯定在两个组内，同时其它数一定在同一组，并且是偶数个)
        for (int i = 0; i < arr.length; i++) {
            if ((arr[i] & right) != 0) {
                onlyOne ^= arr[i];
            }
        }
        System.out.println(onlyOne + "-" + (onlyOne ^ result));
    }

    /**
     * 找一个数种二进制的1出现次数
     *
     * @return
     */
    public static int find2AppearTimes(int n) {
        int count = 0;
        while (n != 0) {
            //找出右侧第一个1
            int right = n & ((~n) + 1);
            count++;
            //将最右侧1抹除
            n ^= right;
        }
        return count;
    }

    public static int test(int[] arr, int num) {
        for (int i = arr.length - 1; i > 0; i--) {
            if (arr[i] <= num) {
                return i;
            }
        }
        return -1;
    }
}
