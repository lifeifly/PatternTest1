package com.lifly.pattern.calculator.recursive;

import java.util.Arrays;
import java.util.Stack;

public class Test {

    /**
     * 求数组arr中最大值
     *
     * @param arr
     */
    public static void getMax(int[] arr) {
        recursiveGetMax(arr, 0, arr.length - 1);
    }

    private static int recursiveGetMax(int[] arr, int left, int right) {
        if (left == right) return arr[left];
        int mid = left + ((right - left) >> 1);
        //1.从left-mid中找一个最大值
        int leftMax = recursiveGetMax(arr, left, mid);
        //2.从mid+1-right中找一个最大值
        int rightMax = recursiveGetMax(arr, mid + 1, right);
        //3.比较两侧最大值返回
        return Math.max(leftMax, rightMax);
    }

    public static void mergeSortNoRecursive(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        int N = arr.length;
        int mergeSize = 1;
        while (mergeSize < N) {
            int L = 0;
            while (L < N) {
                int M = L + mergeSize - 1;
                if (M >= N) {
                    break;
                }
                int R = Math.min(M + mergeSize, N - 1);
                merge(arr, L, M, R);
                L = R + 1;
            }
            if (mergeSize > N / 2) {
                break;
            }
            mergeSize <<= 1;
        }
    }

    /**
     * 将排好序的两部分[left,middle][middle+1,right]进行合并，分别从头开始比较,小的放入结果数组,合并过程中，在右边数组内依次比较每左边每个数
     *
     * @param arr
     * @param left
     * @param middle
     * @param right
     */
    private static int merge(int[] arr, int left, int middle, int right) {
        int[] result = new int[right - left + 1];
        int start = left;
        int end = middle + 1;
        int index = 0;
        //小和
        int res = 0;
        while (start <= middle && end <= right) {
            if (arr[start] < arr[end]) {
                result[index++] = arr[start];
                start++;
                //小和，右边有几个比左边数大得，就有几个左边的数相加
                res += (right - end + 1) * arr[start];
            } else if (arr[start] == arr[end]) {
                result[index++] = arr[start];
                start++;
            } else {
                result[index++] = arr[end];
                end++;
            }
        }
        //左边未比较完
        if (start <= middle) {
            while (start <= middle) {
                result[index++] = arr[start++];
            }
        }
        //右边未比较完
        if (end <= right) {
            while (end <= right) {
                result[index++] = arr[end++];
            }
        }
        //将排好序的结果数组复制到原数组
        for (int i = 0, j = left; i < result.length; i++, j++) {
            arr[j] = result[i];
        }
        return res;
    }

    /**
     * 归并排序,将数组分成两部分，在分别分成两部分，重复此操作，分别排序后，在进行合并,求小和
     */
    public static int mergeSort(int[] arr) {
        return mergeRecursiveSort(arr, 0, arr.length - 1);
    }

    /**
     * 归并排序递归方法
     *
     * @param arr
     * @param left
     * @param right
     */
    private static int mergeRecursiveSort(int[] arr, int left, int right) {
        //basecase
        if (left >= right) return 0;

        //1.计算出中间位置
        int middle = (left + right) / 2;
        //2.对左边进行排序
        int a = mergeRecursiveSort(arr, left, middle);
        //3.对右边进行排序
        int b = mergeRecursiveSort(arr, middle + 1, right);
        //4.将排好序的两侧合并
        int c = merge(arr, left, middle, right);
        return a + b + c;
    }


    /**
     * 快速排序
     */
    public static void quickSort(int[] arr) {
        actualQuickSort(arr, 0, arr.length - 1);
    }

    private static void actualQuickSort(int[] arr, int left, int right) {
        if (left >= right) return;
        //对left-right进行分区排序
        int pivot = sort1(arr, 0, right);
//        System.out.println(Arrays.toString(arr));
        //对左边排序
        actualQuickSort(arr, left, pivot - 1);
        //对右边排序
        actualQuickSort(arr, pivot + 1, right);
    }

    /**
     * 荷兰国旗问题，<num ==num >num
     *
     * @param arr
     * @param left
     * @param right
     */
    private static int sort1(int[] arr, int left, int right) {
        //以right为轴
        int pivot = arr[right];
        int end = right - 1;
        //小于区索引
        int smallIndex = -1;
        //大于区索引
        int bigIndex = right;
        //遍历索引
        int index = left;
        //依次遍历
        while (index < bigIndex) {
            if (arr[index] == pivot) {
                //等于时直接往后移
                index++;
            } else if (arr[index] > pivot) {
                //和大于区前一个元素交换,大于区往前移，index不变
                swap(arr, index, --bigIndex);
            } else {
                //和小于区后一个元素交换，小于区往后移，index++
                swap(arr, index, ++smallIndex);
                index++;
            }
        }
        //最终smallIndex-bigIndex期间时等于pivot的元素,直接把pivot和第一个大于区的数交换
        swap(arr, bigIndex, right);
        return bigIndex;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    /**
     * 汉诺塔问题
     */
    public static void hanoi2(int n,String from,String to,String other){
        func(n,from,to,other);
    }

    private static void func(int n, String from, String to, String other) {
        if (n==1){
            System.out.println("move 1 from "+ from+" to "+to);
        }else {
            //先将上n-1个移到另一个上
            func(n-1,from,other,to);
            //最后剩一个直接移动
            System.out.println("move "+n+" from "+from+" to "+to);
            //再将n-1个移动到to上
            func(n-1,other,to,from);
        }
    }
    public static class Record{
        public boolean finish1;
        public int base;
        public String from;
        public String to;
        public String other;

        public Record(boolean f,int b,String from, String to, String other) {
            this.finish1=f;
            this.base=b;
            this.from = from;
            this.to = to;
            this.other = other;
        }
    }
    /**
     * 非递归汉诺塔
     */
    public static void hanoi3(int n){
        if (n<1){
            return;
        }
        Stack<Record> stack=new Stack<>();
        stack.add(new Record(false,n,"left","right","mid"));
    }

    /**
     * 将栈逆序，不使用额外空间，并使用递归
     */
    public static void reverseStack(Stack<Integer> origin){
        if (origin.isEmpty()){
            return;
        }
        int i=getBottom(origin);
        reverseStack(origin);
        origin.push(i);
    }

    /**
     * 将栈底部取出并返回，其它顺序不变
     */
    public static int getBottom(Stack<Integer> stack){
        int result=stack.pop();
        if (stack.isEmpty()){
            return result;
        }else {
            int last=getBottom(stack);
            stack.push(result);
            return last;
        }
    }
}
