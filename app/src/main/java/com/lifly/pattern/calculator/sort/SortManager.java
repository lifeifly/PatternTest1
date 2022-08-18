package com.lifly.pattern.calculator.sort;


import java.util.Arrays;

public class SortManager {
    private int[] arr;

    public SortManager(int[] arr) {
        this.arr = arr;
    }

    /**
     * 选择排序
     * 思想：一遍一遍过滤数组，每次找到最小的数放到前面
     * 时间复杂度为O(n2)
     */
    public int[] selectionSort() {
        //外层控制第几个数，内层控制比较的数
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                int compare1 = arr[i];
                int compare2 = arr[j];
                if (compare1 > compare2) {
                    swap(i, j, arr);
                }
            }
        }
        return arr;
    }


    /**
     * 冒泡排序，两两比较，小的往前放，大的往后去，就像泡泡一样浮到最上面
     * 每次都会找到最大的数放在最后面或者找到最小的数放最前面
     */
    public void bubbleSort() {
        for (int i = 0; i < arr.length - 1; i++) {//外层记录第几次比较，总共只需进行n-1次
            for (int j = 0; j < arr.length - i - 1; j++) {//每次循环都会找到最大的数放后面，因此可以省略最后的i的元素
                int compare1 = arr[j];
                int compare2 = arr[j + 1];
                if (compare1 > compare2) {
                    swap(j, j + 1, arr);
                }
            }
        }
    }

    /**
     * 插入排序，空间复杂度O（1），时间复杂度O（n2）,最好时间复杂度O(n)
     */
    public void insertSort() {
        for (int i = 1; i < arr.length; i++) {//外层控制第几个需要插入的数
            for (int j = i; j > 0; j--) {
                if (arr[j] < arr[j - 1]) {
                    swap(j, j - 1, arr);
                }
            }
        }
    }

    /**
     * 改进的插入排序,设置间隔t，每隔t的一串数作为一组，每组排序
     */
    public void shellSort() {
        //knuth最好的间隔h=1...h=3*h+1
        int h = 1;
        //计算最大的那个h，然后作为间隔，反推
        while (h <= arr.length / 3) {
            h = h * 3 + 1;
        }
        int gap = h;
        while (gap > 0) {
            for (int i = gap; i < arr.length; i += gap) {//外层控制每组数据比较开始的时候第一个需要插入的数
                for (int j = i + gap; j > 0 && j < arr.length; j -= gap) {
                    if (arr[j - gap] > arr[j]) {
                        swap(j - gap, j, arr);
                    }
                }
            }
            gap = (gap - 1) / 3;
        }
    }

    /**
     * 归并排序，分成两部分左右排好顺序，然后归并
     * 时间复杂度O（nlog 2 n）
     * 空间复杂度O(n)
     */
    public void mergeSort() {
        sort(0, arr.length - 1, arr);
    }

    public void sort(int start, int end, int[] arr) {
        if (start == end) return;
        //分成两半
        int mid = (start + end) / 2;
        //排左边
        sort(start, mid, arr);
        //排右边
        sort(mid + 1, end, arr);
        //合并
        merge(start, mid, end, arr);
    }

    /**
     * 归并排序，不断的将数组一分二，二份四。。。。。。，每两部分在合并时进行不断比较
     */
    public void merge(int start, int middle, int end, int[] arr) {
        if (start >= end) return;
        //分成两部分，进行比较
        int left = start;
        int right = middle + 1;
        int[] temp = new int[end - start + 1];
        int index = 0;
        while (left <= middle && right <= end) {
            if (arr[left] <= arr[right]) {
                temp[index++] = arr[left++];
            } else {
                temp[index++] = arr[right++];
            }
        }
        while (left <= middle) {
            //左半部分有元素未被比较，直接copy到临时数组
            temp[index++] = arr[left++];
        }
        while (right <= end) {
            //右半部分有元素未被比较，直接copy到临时数组
            temp[index++] = arr[right++];
        }
        //复制到原数组
        System.arraycopy(temp, 0, arr, start, temp.length);
    }

    /**
     * 快速排序，时间复杂度：Nlog2N
     */
    public void quickSort() {
        sort1(arr, 0, arr.length - 1);
    }

    private void sort1(int[] ars, int left, int right) {
        if (left >= right) {
            return;
        }
        //分区
        int pivotPos = partion(ars, left, right);
        //对左边进行排序
        sort1(ars, left, pivotPos - 1);
        //对右边进行排序
        sort1(ars, pivotPos + 1, right);
    }

    private int partion(int[] ars, int left, int right) {
        int pivot = ars[right];
        int start = left;
        int end = right - 1;

        while (start <= end) {
            //找左边部分第一个比轴大得
            while (ars[start] <= pivot && start <= end) {
                start++;
            }

            //找右边部分第一个比轴小得
            while (ars[end] > pivot && start <= end) {
                end--;
            }

            if (start < end) {//两个位置相对不变才能交换
                //交换
                swap(start, end, ars);
            }
        }
        //将轴的位置放到合适的位置
        swap(start, right, ars);
        return start;
    }

    /**
     * 计数排序，量大但是范围小，记录每个数出现多少次
     * 空间复杂度n+k
     */
    public int[] calculatorSort() {
        //结果数组
        int[] result = new int[arr.length];
        //记录每个数出现多少次的数组
        int[] count = new int[10000];
        //遍历每个数，在对应的位置+1
        for (int i = 0; i < arr.length; i++) {
            count[arr[i]]++;
        }
        //根据每个数出现的次数，赋值给result
//        for (int i=0,j=0;j<count.length;j++){//不稳定
//            while (count[j]-->0){
//                result[i++]=j;
//            }
//        }
        //改进稳定，累加数组，从位置1开始，count[i]=count[i]+count[i-1],则每个元素代表该索引对应的数的最后一个位置
        for (int i = 1; i < count.length; i++) {
            count[i] = count[i] + count[i - 1];
        }
        //倒序根据位置一一放置对象
        for (int j = arr.length - 1; j >= 0; j--) {
            while (count[j]-- > 0) {
                result[count[j]] = j;
            }
        }
        return result;
    }

    /**
     * 从个位依次到最高位，个位数一样得分别放在一个桶里，进行计数排序，十位数一样得进行基数排序
     */
    public int[] baseSort() {
        //先找到最高位
        int max = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }

        //结果数组
        int[] result = new int[arr.length];
        //记录每个数出现多少次的数组
        int[] count = new int[10];
        for (int n = 0; n < String.valueOf(max).length(); n++) {
            int division = (int) Math.pow(10, n);
            for (int i = 0; i < arr.length; i++) {
                int num = (arr[i] / division) % 10;
                count[num]++;
            }
            //累加数组，获取每个桶的最后一个位置
            for (int j = 1; j < count.length; j++) {
                count[j] = count[j] + count[j - 1];
            }
            //倒序放置每个元素
            for (int k = arr.length - 1; k >= 0; k--) {
                //获取该元素对应桶的索引
                int index = (arr[k] / division) % 10;
                //根据位置插入
                result[--count[index]] = arr[k];
            }
            //复制到原数组，为了下次操作
            System.arraycopy(result, 0, arr, 0, arr.length);
            //初始化数组
            Arrays.fill(count, 0);
        }
        return arr;
    }

    /**
     * 堆必须是完全二叉树，从上至下，从左至右没有放置元素
     * 先把数组构造成一个大顶堆（父节点大于其子节点），然后把堆顶（数组的最大值，数组第一个元素）和数组最后一个元素交换，这样
     * 就把最大值放到了数组最后，把数组长度n-1，再进行构造堆，把剩余的第二大值放到堆顶，输出堆顶（放到剩余未排序数组最后面）
     * ，依次类推
     */
    public void heapSort() {
        //1.先把数组构造成堆结构，也是数组结构
        //4.实现循环，每次都表示一个元素有序
        for (int end = arr.length - 1; end > 0; end--) {
            //2.将堆结构调整成大根堆
            maxHeap(arr, end);
            //3.将堆顶元素与最后一个叶子节点交换位置
            swap(0, end, arr);
        }
    }

    /**
     * 构建大根堆
     * 结构中最后一个带有孩子的节点的下标为(（start+end）/2)向上取整-1
     * 假设某个节点在数组的下标为n，左孩子下标为2n+1，右孩子下标为2n+2
     */
    private void maxHeap(int[] array, int end) {
        //1.根据公式计算最后一个父节点的位置
        int lastFather = (0 + end) % 2 == 0 ? (0 + end) / 2 - 1 : (0 + end) / 2;
        //5.从最后一个父节点下标减1，保证之后的下标都是父节点
        for (int father = lastFather; father >=0 ; father--) {
            //2.根据父节点下标推算左右孩子下标左2n+1，右2n+2
            int leftC=father*2+1;
            int rightC=father*2+2;
            //3.保证右孩子不越界的情况下，先于父节点比较，大于则交换
            if (rightC<=end){
                if (array[rightC]>array[father]){
                    swap(rightC,father,array);
                }
            }
            //4.左孩子和父节点比较，大于则交换
            if (array[leftC]>array[father]){
                swap(leftC,father,array);
            }
        }
    }

    private void swap(int i, int j, int[] arr) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
