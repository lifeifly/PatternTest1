package com.lifly.pattern.calculator.完美洗牌问题;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Test {
    /**
     * 给定一个长度为偶数的数组arr，长度记为2*N，前N哥为左部分，后N哥为右部分
     * arr就可以表示为L1,L2,L3...LN，R1,R2....RN
     * 请将数组调整成{R1,L1,R2,L2,R3,L3.....RN,LN}
     * <p>
     * 技巧1：N为偶数且符合3的k次方-1，那他的下标环的位置为3的0次方-3的k-1次方
     * 技巧2：L1,L2,L3,L4,R1,R2,R3,R4调整成L1,L2,R1,R2,L3,L4,R3,R4先将L3,L4逆序再将R3,R4逆序，最后将,L4,L3,R4,R3逆序
     * 逐渐的把普通的偶数一块一块的化成3的k次方-1
     */
    public static void discharge(int[] arr) {
        if (arr != null && arr.length != 0 && arr.length % 2 == 0) {
            shuffle(arr, 0, arr.length - 1);
        }
    }

    /**
     * 在l..r位置进行完美洗牌
     *
     * @param arr
     * @param l
     * @param r
     */
    private static void shuffle(int[] arr, int l, int r) {
        while (r - l + 1 > 0) {//切成一块一块的，每次长度满足3的k次方-1
            int len = r - l + 1;
            int base = 3;
            int k = 1;
            while (base <= (len + 1) / 3) {
                base *= 3;
                k++;
            }
            //当前要解决的块长度为base-1块，一半再除2
            int half = (base - 1) / 2;
            //l..r的中点位置
            int mid = (l + r) / 2;
            //要旋转的左部分是arr[l+half..mid],右部分是arr[mid+1..r]
            //注意这是从下标0开始的
            rotate(arr, l + half, mid, mid + half);
            //旋转完之后从l开始，长度为base-1的部分进行下标连续推
            cycles(arr, l, base - 1, k);
            //解决了前base-1个，剩下的部分进行处理
            l = l + base;
        }
    }

    /**
     * 先将l..mid逆序，再把mid+1..r逆序，最后把整体逆序
     *
     * @param arr
     * @param l
     * @param mid
     * @param r
     */
    private static void rotate(int[] arr, int l, int mid, int r) {
        int left = l;
        int right = mid;
        while (left < right) {
            int tem = arr[left];
            arr[left] = arr[right];
            arr[right] = tem;
            left++;
            right--;
        }
        left = mid + 1;
        right = r;
        while (left < right) {
            int tem = arr[left];
            arr[left] = arr[right];
            arr[right] = tem;
            left++;
            right--;
        }
        left = l;
        right = r;
        while (left < right) {
            int tem = arr[left];
            arr[left] = arr[right];
            arr[right] = tem;
            left++;
            right--;
        }
    }

    /**
     * 再arr[l..l+len]机型下标连续推，每个环的开始点是3的0次方到3的k-1次方
     *
     * @param arr
     * @param l
     * @param len
     * @param k
     */
    private static void cycles(int[] arr, int l, int len, int k) {
        //找到每个出发点trigger,一共是k个
        //出发位置从1开始，数组下标从0开始
        for (int i = 0, trigger = 1; i < k; i++, trigger *= 3) {
            int preValue = arr[trigger + l - 1];
            int cur = modifyIndex2(trigger, len);
            while (cur != trigger) {
                int temp = arr[cur + l - 1];
                arr[cur + l - 1] = preValue;
                preValue = temp;
                cur = modifyIndex2(cur, len);
            }
            arr[cur + l - 1] = preValue;
        }
    }


    /**
     * 数组的长度为len，按要求调整前的位置i，返回调整后的位置
     * 下标不从0开始，从1开始
     *
     * @return
     */
    public static int modifyIndex1(int i, int len) {
        //len左一半的
        if (i <= len / 2) {
            return 2 * i;
        } else {
            //len右一半的
            return 2 * (i - (len / 2)) - 1;
        }
    }

    /**
     * 数组的长度为len，按要求调整前的位置i，返回调整后的位置
     * 下标不从0开始，从1开始
     *
     * @return
     */
    public static int modifyIndex2(int i, int len) {
        return (2 * i) % (len + 1);
    }

    /**
     * 给定无序数组arr，请调整成依次《= 》= 《= 》=要求额外空间复杂读O（1）
     * 1.先进行堆排序，因为堆排序的额外空间复杂度是O（1）的
     * 2.偶数则像上题那样进行调整，奇数不管第一个最小的数，其它的按上题调整
     * 3.两两逆序
     */
    public static void dischargeArr(int[] arr) {
        if (arr == null || arr.length == 0) {
            return;
        }
        //1.堆排序，额外空间复杂度O（1）
        heapSort(arr);
        System.out.println(Arrays.toString(arr));
        dischargeRecursive(arr, arr.length % 2 == 0 ? 0 : 1, arr.length - 1);

    }

    private static void dischargeRecursive(int[] arr, int l, int r) {
        while (r - l + 1 > 0) {
            int len = r - l + 1;
            //2.找到最接近len的3^k-1作为长度,k从1开始
            int base = 3;
            int k = 1;
            while (base - 1 <= len) {
                base *= 3;
                k++;
            }
            //此时需要调整的长度为
            int disLen = base / 3 - 1;
            //中点
            int mid = (r + l) / 2;
            //左边需要调整的右侧边界
            int leftBound = l + disLen / 2 - 1;
            //旋转中间部分
            rotate(arr, leftBound + 1, mid, mid + disLen / 2);
            //在l..l+dislen-1区域进行下标循环推
            cycles(arr, l, disLen, k - 1);
            l += disLen;
        }
    }

    private static void heapSort(int[] arr) {
        //只需交换n-1次
        for (int i = arr.length - 1; i >= 1; i--) {
            buildHeap(arr, i);
        }
    }

    private static void buildHeap(int[] arr, int end) {
        //最后一个父节点位置
        int lastFatherIndex = (0 + end) % 2 == 0 ? (0 + end) / 2 - 1 : (0 + end) / 2;
        while (lastFatherIndex >= 0) {
            //右节点位置
            int rightIndex = lastFatherIndex * 2 + 2;
            if (rightIndex <= end) {
                if (arr[rightIndex] > arr[lastFatherIndex]) {
                    swap(arr, rightIndex, lastFatherIndex);
                }
            }
            int leftIndex = lastFatherIndex * 2 + 1;
            if (arr[leftIndex] > arr[lastFatherIndex]) {
                swap(arr, leftIndex, lastFatherIndex);
            }
            lastFatherIndex--;
        }
        swap(arr, 0, end);
        System.out.println(Arrays.toString(arr));
    }

    private static void swap(int[] arr, int rightIndex, int lastFatherIndex) {
        int temp = arr[rightIndex];
        arr[rightIndex] = arr[lastFatherIndex];
        arr[lastFatherIndex] = temp;
    }

    /**
     * 一个字符串可以分解成多种二叉树结构，如果长度为1，认为不可分解
     * 如果str长度大于1，左部分长度可以为1~n-1，右部分长度为n-1~1，形成二叉树结构
     * str1和str2是否互为旋转串
     *
     * @return
     */
    public static boolean dividerNode(String str1, String str2) {
        //1.检查是否长度相同并且字符个数相同
        if (!isSameLengthAndCount(str1, str2)) {
            return false;
        }
        return process1(str1.toCharArray(), str2.toCharArray(), 0, 0, str1.length());
    }

    /**
     * 求chs1从l1开始长length的字符串和chs2从l2出发长度为length的字符串是否互为旋转串
     *
     * @param chs1
     * @param chs2
     * @param l1
     * @param l2
     * @return
     */
    private static boolean process1(char[] chs1, char[] chs2, int l1, int l2, int size) {
        //basecase就一个字符时，相等就是旋转串
        if (size == 1) {
            return chs1[l1] == chs2[l2];
        }
        //枚举旋转的中间位置
        for (int len = 1; len < size; len++) {
            //两种可能一种l1左对l2右,l2左对l1右，另一种l1左对l2左，l1右对l2右
            if ((process1(chs1, chs2, l1, l2, len) && process1(chs1, chs2, l1 + len, l2 + len, size - len))
                    || (process1(chs1, chs2, l1, l2 + size - len, len) && process1(chs1, chs2, l1 + size - len, l2, len))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSameLengthAndCount(String str1, String str2) {
        if (str1.length() != str2.length()) {
            return false;
        }
        char[] chs1 = str1.toCharArray();
        char[] chs2 = str2.toCharArray();
        int[] map = new int[256];
        for (int i = 0; i < chs1.length; i++) {
            map[chs1[i]]++;
        }
        for (int i = 0; i < chs2.length; i++) {
            if (--map[chs2[i]] < 0) {
                return false;
            }
        }
        return true;
    }

    //动态规划
    public static boolean dividerStringDp(String str1, String str2) {
        int n = str1.length();
        char[] chs1 = str1.toCharArray();
        char[] chs2 = str2.toCharArray();
        //str1起始位置，str2起始位置，长度size
        boolean[][][] dp = new boolean[n][n][n + 1];
        for (int l1 = 0; l1 < n; l1++) {
            for (int l2 = 0; l2 < n; l2++) {
                dp[l1][l2][1] = chs1[l1] == chs2[l2];
            }
        }
        for (int size = 2; size <= n; size++) {
            for (int l1 = 0; l1 < n - size; l1++) {
                for (int l2 = 0; l2 < n - size; l2++) {
                    for (int len = 1; len < size; len++) {
                        //两种可能一种l1左对l2右,l2左对l1右，另一种l1左对l2左，l1右对l2右
                        if ((dp[l1][l2][len] && dp[l1 + len][l2 + len][size - len])
                                || (dp[l1][l2 + size - len][len] && dp[l1 + size - len][l2][len])) {
                            return true;
                        }
                    }
                }
            }
        }
        return dp[0][0][n];
    }


    public static void main(String[] args) {
        String s="abcd";
        String s1="acbd";

        System.out.println(dividerNode(s,s1));
        System.out.println(dividerStringDp(s,s1));
    }
}
