package com.lifly.pattern.calculator.sort;

import java.util.Arrays;

public class SortManager1 {
    private int[] mArr;

    public SortManager1(int[] arr) {
        this.mArr = arr;
    }

    /**
     * 选择排序，从前往后，依次拿出一个元素和其它元素进行比较，大于其它元素，就交换位置,只剩最后一个不需比较了，因为前面都有序了
     */
    public void selectSort() {
        for (int i = 0; i < mArr.length - 1; i++) {//外层循环比较数
            for (int j = i + 1; j < mArr.length; j++) {//内层循环被比较数
                if (mArr[i] > mArr[j]) {
                    swap(mArr, i, j);
                }
            }
        }
    }

    /**
     * 冒泡排序，两两比较，每次循环找到最大值放到最后面，跟大的冒出水面一样
     */
    public void bubbleSort() {
        for (int i = 0; i < mArr.length - 1; i++) {//外层循环需要比较的循环次数，只需排出前n-1个就有序了
            for (int j = 1; j < mArr.length - i; j++) {//内层循环，在每次需要比较的排序范围内排序
                if (mArr[j - 1] > mArr[j]) {
                    swap(mArr, j - 1, j);
                }
            }

        }
    }

    /**
     * 插入排序，假设每次前n个元素是有序的，将n+1个元素插入其中
     */
    public void insertSort() {
        for (int i = 1; i < mArr.length; i++) {//外层循环每次需要插入的元素，第0位置就一个元素，本身就有序
            for (int j = i; j > 0; j--) {
                if (mArr[j - 1] > mArr[j]) {
                    swap(mArr, j - 1, j);
                }
            }
        }
    }

    /**
     * 希尔排序，改进的插入排序
     * 每隔一个距离的元素作为一组，最后的KNUTH算法距离h=3h+1
     */
    public void shellSort() {
        //求最大h，3h+1=mArr.length()
        int h = 1;
        while (3 * h + 1 <= mArr.length) {
            h = 3 * h + 1;
        }

        for (int gap = h; gap > 0; gap = (gap - 1) / 3) {//外层循环间隔
            for (int i = gap; i < mArr.length; i += gap) {//外层循环需要插入的元素
                for (int j = i; j > 0; j -= gap) {//内层循环比较和交换
                    if (mArr[j - gap] > mArr[j]) {
                        swap(mArr, j - gap, j);
                    }
                }

            }
        }
    }

    /**
     * 快速排序,先找一个轴，小于轴的为一部分，大于轴的为一部分，接着再在左右两部分中重复，直至元素就一个不需再操作
     */
    public void quickSort() {
        quickRecursiveSort(mArr, 0, mArr.length - 1);
    }

    /**
     * 快速排序递归方法
     *
     * @param arr
     * @param left
     * @param right
     */
    private void quickRecursiveSort(int[] arr, int left, int right) {
        if (left >= right) return;
        //1.先将整个数组排序比较,获取轴的位置
        int pivot = sort(arr, left, right);
        //2.再将左侧数组进行此操作
        quickRecursiveSort(arr, left, pivot - 1);
        //3.将右侧数组进行此操作
        quickRecursiveSort(arr, pivot + 1, right);
    }

    /**
     * @param arr   待排序数组
     * @param left  左边界
     * @param right 右边界
     */
    private int sort(int[] arr, int left, int right) {
        //先以最右边为轴
        int pivot = arr[right];
        int start = left;
        int end = right - 1;
        while (start <= end) {//保证就两个数，也得进循环去比较
            //找左边第一个比轴大与等于的元素
            while (arr[start] <= pivot && start <= end) {//加等号表示大于等于轴的在左边,并且不超边界
                start++;
            }
            //找右边第一个比轴小的元素
            while (arr[end] >= pivot && start <= end) {
                end--;
            }
            if (start < end) {
                swap(arr, start, end);
            }
        }
        //将轴放在合适的中间位置，与start交换
        swap(arr, right, start);
        return start;
    }

    /**
     * 归并排序,将数组分成两部分，在分别分成两部分，重复此操作，分别排序后，在进行合并
     */
    public void mergeSort() {
        mergeRecursiveSort(mArr, 0, mArr.length - 1);
    }

    /**
     * 归并排序递归方法
     *
     * @param arr
     * @param left
     * @param right
     */
    private void mergeRecursiveSort(int[] arr, int left, int right) {
        //basecase
        if (left >= right) return;

        //1.计算出中间位置
        int middle = (left + right) / 2;
        //2.对左边进行排序
        mergeRecursiveSort(arr, left, middle);
        //3.对右边进行排序
        mergeRecursiveSort(arr, middle + 1, right);
        //4.将排好序的两侧合并
        merge(arr, left, middle, right);
    }

    /**
     * 将排好序的两部分[left,middle][middle+1,right]进行合并，分别从头开始比较,小的放入结果数组
     *
     * @param arr
     * @param left
     * @param middle
     * @param right
     */
    private void merge(int[] arr, int left, int middle, int right) {
        int[] result = new int[right - left + 1];
        int start = left;
        int end = middle + 1;
        int index = 0;
        while (start <= middle && end <= right) {
            if (arr[start] <= arr[end]) {
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
    }

    /**
     * 堆排序,根据数组对应的堆公式，先将原数组变成大根堆，然后拿最大的值（头部的）与最小值交换，然后再将n-1个元素变成大跟堆，重复操作
     * 最后一个父节点的下标(start+end)/2取整-1
     * 左子节点下标2n+1，右子节点下标2n+2
     */
    public void heapSort() {
        //1.循环构建依次大根堆，就排好一个数,只需n-1次循环
        for (int i = mArr.length - 1; i > 0; i--) {
            buildHeapAndSwap(mArr, i);
        }
    }

    /**
     * 堆排序，用于构建堆，交换最大和最小值
     */
    private void buildHeapAndSwap(int[] arr, int end) {
        //1.找到最后一个父节点位置下标
        int lastFatherIndex = (0 + end) % 2 == 0 ? (0 + end) / 2 - 1 : (0 + end) / 2;
        System.out.println("lastFather" + lastFatherIndex);
        //2.从最后一个父节点位置依次减1，构建堆,相当于从后往前构建堆
        for (int i = lastFatherIndex; i >= 0; i--) {
            //放置右子节点，如果大于父节点元素，则交换
            int rightChildIndex = i * 2 + 2;
            //保证不越界
            if (rightChildIndex <= end) {
                if (arr[rightChildIndex] > arr[i]) {
                    swap(arr, rightChildIndex, i);
                }
            }
            //放置左子节点并与父节点比较,不需要判断越界，因为父节点肯定有孩子，并且大根堆是平衡二叉树
            int leftChildIndex = i * 2 + 1;
            if (arr[leftChildIndex] > arr[i]) {
                swap(arr, leftChildIndex, i);
            }
            System.out.println(Arrays.toString(mArr));
        }
        //3将最大值（位置0）和最小值（位置end）交换
        swap(arr, 0, end);
        System.out.println("--" + Arrays.toString(mArr));
    }

    /**
     * 计算排序，对一个范围内数字进行排序，具体的数字作为下标，数字的个数作为元素
     */
    public int[] calculatorSort() {
        //[0,10000)范围进行排序
        int[] result = new int[10000];
        //1.创建一个10000个长度的数组
        int[] bucket = new int[10000];
        //2.遍历所有元素，将每个数字对应桶的元素相加
        for (int i = 0; i < mArr.length; i++) {
            bucket[mArr[i]]++;
        }
        //3.保证排序是稳定的，需要进行累加数组,从下标1的元素开始，arr[i]=arr[i]+arr[i-1],这样对应的元素-1就是每种数字最后一个数字的下标
        for (int i = 1; i < bucket.length; i++) {
            bucket[i] = bucket[i] + bucket[i - 1];
        }
        System.out.println("桶" + Arrays.toString(bucket));
        //4.倒序放置每个元素
        for (int i = mArr.length - 1; i >= 0; i--) {
            result[--bucket[mArr[i]]] = mArr[i];
        }

        return result;
    }


    /**
     * 基数排序,先按个位数进行计数排序，再按十位，再按百位等等
     */
    public int[] baseSort() {
        //1.找到最大数，取最高位
        int max = 0;
        for (int i = 0; i < mArr.length; i++) {
            if (mArr[i] > max) {
                max = mArr[i];
            }
        }
        //用来存放各个位计数排序的桶,位数只有0-9，所有桶的容量为10
        int[] bucket = new int[10];
        //临时结果数组
        int[] result = new int[10000];
        //2.以位数为循环数，进行外层循环
        for (int i = 0; i < String.valueOf(max).length(); i++) {
            System.out.println(Arrays.toString(mArr) + "------------");
            //先除divisor再%10得到的就是各个位的值
            int divisor = (int) Math.pow(10, i);
            System.out.println(Arrays.toString(bucket));
            //获取各个数的位数，放到不同的桶里
            for (int j = 0; j < mArr.length; j++) {
                int index = mArr[j] / divisor % 10;
                bucket[index]++;
            }
            System.out.println(Arrays.toString(bucket));
            //累加数组，为了稳定
            for (int j = 1; j < bucket.length; j++) {
                bucket[j] = bucket[j] + bucket[j - 1];
            }
            System.out.println(Arrays.toString(bucket));
            //倒序放置
            for (int j = mArr.length - 1; j >= 0; j--) {
                int index = mArr[j] / divisor % 10;
                result[--bucket[index]] = mArr[j];
            }
            System.out.println(Arrays.toString(result));
            //完成一轮计数排序，情空数组,将临时数组copy到结果数组
            Arrays.fill(bucket, 0);
            System.arraycopy(result, 0, mArr, 0, result.length);
        }
        return result;
    }


    /**
     * 快速排序
     */
    public void quickSort1() {
        sort2(mArr, 0, mArr.length-1);
    }

    private void sort2(int[] mArr, int left, int right) {
        if (left>=right)return ;
        //1.先对整个数组进行排序
        int pivot=sort3(mArr,left,right);
        //2.对左边排序
        sort2(mArr,left,pivot-1);
        //3.对右边排序
        sort2(mArr,pivot+1,right);
    }

    private int sort3(int[] mArr, int left, int right) {
        //1.以最右侧为轴
        int pivot = mArr[right];
        int start = left;
        int end = right - 1;
        while (start <= end) {//=是为了就两个数时，进行比较
            //找左边第一个比pivot大的值
            while (mArr[start] <= pivot && start <= end) {
                start++;
            }
            //找右边第一个比pivot小的数
            while (mArr[end] >= pivot && start <= end) {
                end--;
            }
            if (start < end) {
                swap(mArr, start, end);
            }
        }
        //把轴放到合适的位置
        swap(mArr, start, right);
        return start;
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
