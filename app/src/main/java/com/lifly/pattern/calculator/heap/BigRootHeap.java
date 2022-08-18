package com.lifly.pattern.calculator.heap;

public class BigRootHeap {
    private int heapSize = 0;
    private int[] arr;
    private int limit = 64;

    public BigRootHeap(int limit) {
        this.limit = limit;
        arr = new int[limit];
    }

    public boolean isFull() {
        return heapSize == limit;
    }

    public void push(int value) {
        if (heapSize == limit) {
            throw new RuntimeException("heap is full");
        }
        arr[heapSize] = value;
        //构成大根堆
        headInsert(heapSize++);
    }

    /**
     * 将0-index得数组构成大根堆
     *
     * @param index
     */
    private void headInsert(int index) {
        while (arr[index] > arr[(index - 1) / 2]) {
            //如果当前位置的元素大于父节点元素,交换
            swap(arr, index, (index - 1) / 2);
            //上浮到父节点
            index = (index - 1) / 2;
        }
    }

    /**
     * 弹出最大值，并删除,不能做到真正删除，只能通过heapSize限制范围
     */
    public int pop() {
        if (heapSize<=0){
            throw new RuntimeException("heap is empty");
        }
        //记录最大值
        int max=arr[0];
        //将最大值和最后一个位置交换
        swap(arr,0,heapSize-1);
        //数量减1
        heapSize--;
        //依次下沉构成大根堆
        heapify(0,heapSize);
        return max;
    }

    /**
     * 从位置0开始依次下沉构成大根堆
     * @param size
     */
    private void heapify(int start,int size) {
        int left=start*2+1;
        while (left<size){//左子节点的位置不越界
            //比较左、右两个子节点获取最大值位置
            int largeIndex=left+1<size&&arr[left+1]>arr[left]?left+1:left;
            largeIndex=arr[start]<arr[largeIndex]?largeIndex:start;
            if (largeIndex==start){
                break;
            }
            swap(arr,largeIndex,start);
            //下沉到交换的子节点位置
            start=largeIndex;
            left=start*2+1;
        }
    }

    private void swap(int[] arr, int one, int other) {
        int temp = arr[one];
        arr[one] = arr[other];
        arr[other] = temp;
    }
}
