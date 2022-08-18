package com.lifly.pattern.calculator.BFPRT;

import java.util.Arrays;

public class Test {
    /**
     * 无序数组arr,求第k小的数
     *
     * @return
     */
    public static int minK1(int[] arr, int k) {
//        recursiveMinK(arr, 0, arr.length - 1);
//        System.out.println(Arrays.toString(arr));
//        return arr[k - 1];
        int s = select(arr, 0, arr.length - 1, k);
        return s;
    }

    //BFPRT
    public static int select(int[] arr, int begin, int end, int k) {
        if (begin == end) return arr[begin];
        //分组+组内排序+组成新的arr+选出新数组的上中位数作为pivot
        int pivot = medianOfMedians(arr, begin, end);
        //根据pivot做划分值 <p ==p >p 返回等于区域的左边界和右边界
        //pivotRange[0] 等于区域的左边界
        //pivotRange[1] 等于区域的右边界
        System.out.println(pivot);
        int[] pivotRange = partition1(arr, begin, end, pivot);

        System.out.println(Arrays.toString(pivotRange));


        System.out.println(Arrays.toString(arr));

        if (k >= pivotRange[0] && k <= pivotRange[1]) {
            return arr[k];
        } else if (begin < pivotRange[0]) {
            return select(arr, begin, pivotRange[0] - 1, k);
        } else {
            return select(arr, pivotRange[1] + 1, end, k);
        }
    }

    public static int select1(int[] arr, int begin, int end, int k) {
        if (begin == end) return arr[begin];
        //分组+组内排序+组成新的arr+选出新数组的上中位数作为pivot
        int pivot = medianOfMedians(arr, begin, end);
        //根据pivot做划分值 <p ==p >p 返回等于区域的左边界和右边界
        //pivotRange[0] 等于区域的左边界
        //pivotRange[1] 等于区域的右边界
        int[] pivotRange = partition2(arr, begin, end, pivot);

        if (k >= pivotRange[0] && k <= pivotRange[1]) {
            return arr[k];
        } else if (begin < pivotRange[0]) {
            return select1(arr, begin, pivotRange[0] - 1, k);
        } else {
            return select1(arr, pivotRange[1] + 1, end, k);
        }
    }

    private static int[] partition1(int[] arr, int begin, int end, int pivot) {
        int less = begin - 1;
        int more = end + 1;
        int cur = begin;
        while (cur < more) {
            if (arr[cur] == pivot) {
                cur++;
            } else if (arr[cur] > pivot) {
                swap(arr, --more, cur);
            } else {
                swap(arr, ++less, cur++);
            }
        }
        return new int[]{less + 1, more - 1};
    }

    private static int[] partition2(int[] arr, int begin, int end, int pivot) {
        int less = begin - 1;
        int more = end + 1;
        int cur = begin;
        while (cur < more) {
            if (arr[cur] == pivot) {
                cur++;
            } else if (arr[cur] > pivot) {
                swap1(arr, --more, cur);
            } else {
                swap1(arr, ++less, cur++);
            }
        }
        return new int[]{less + 1, more - 1};
    }

    private static int medianOfMedians(int[] arr, int begin, int end) {
        int num = end - begin + 1;
        int offset = num % 5 == 0 ? 0 : 1;
        int[] mArr = new int[num / 5 + offset];
        for (int i = 0; i < mArr.length; i++) {
            int beginI = begin + i * 5;
            int endI = beginI + 4;
            mArr[i] = getMedian(arr, beginI, Math.min(end, endI));
        }
        return select1(mArr, 0, mArr.length - 1, mArr.length / 2);
    }

    //插入排序
    public static void insertSort(int[] arr, int begin, int end) {
        //有begin end 做想定条件的时候注意循环条件
        for (int i = begin + 1; i < end + 1; i++) {
            for (int j = i - 1; j >= begin && arr[j] > arr[j + 1]; j--) {
                swap1(arr, j, j + 1);
            }
        }
    }

    /**
     * 获取中位数
     *
     * @param arr
     * @param beginI
     * @param endI
     * @return
     */
    private static int getMedian(int[] arr, int beginI, int endI) {
        int[] copyArr = copyArray(arr);
        insertSort(copyArr, beginI, endI);
        int sum = beginI + endI;
        int mid =  (sum/2) + (sum%2);
        return arr[mid];
    }

    public static int[] copyArray(int[] arr) {
        int[] res = new int[arr.length];
        for (int i = 0; i != res.length; i++) {
            res[i] = arr[i];
        }
        return res;
    }

    //快排partition
    public static int recursiveMinK(int[] arr, int left, int right, int k) {
        if (left == right) return arr[left];
        int[] equalsBound = partition(arr, left, right);

        if (k >= equalsBound[0] && k <= equalsBound[1]) {
            return arr[k];
        } else if (k < equalsBound[0]) {
            return recursiveMinK(arr, left, equalsBound[0] - 1, k);
        } else {
            return recursiveMinK(arr, equalsBound[1] + 1, right, k);
        }
    }

    //荷兰国旗问题{1,3,5,7,2,8,6}
    private static int[] partition(int[] arr, int left, int right) {
        int pivot = arr[right];
        int min = left - 1;
        int max = right;
        int index = left;
        while (index < max) {
            if (arr[index] == pivot) {
                //计算下个元素
                index++;
            } else if (arr[index] > pivot) {
                //拿大于区的前一个和当前元素交换,大于区左扩，小于区不变
                swap(arr, --max, index);
            } else {
                //拿小于区的后一个和当前元素交换，小于区右扩，大于区不变，计算下一个
                swap(arr, ++min, index++);
            }
        }

        //最终[left,min]是小于pivot的
        //(min,max)是等于pivot的
        //[max,right]是大于pivot的
        //将轴放到正确的位置，将pivot和max区域最左的一个交换
        swap(arr, right, max);
        return new int[]{min + 1, max - 1};
    }

    private static void swap(int[] arr, int o1, int o2) {
        System.out.println(o1 + "/" + o2);
        int temp = arr[o1];
        arr[o1] = arr[o2];
        arr[o2] = temp;
    }

    private static void swap1(int[] arr, int o1, int o2) {
        int temp = arr[o1];
        arr[o1] = arr[o2];
        arr[o2] = temp;
    }

    public static void main(String[] args) {
        int[] arr = {1, 3, 5, 7, 2, 8, 6};
        System.out.println(minK1(arr, 3));
    }
}
