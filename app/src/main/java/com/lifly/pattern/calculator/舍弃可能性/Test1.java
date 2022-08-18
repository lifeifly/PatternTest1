package com.lifly.pattern.calculator.舍弃可能性;

public class Test1 {
    public static int getMinKthByBFPRT(int[] arr,int k){
        int[] cArr = copyArray(arr);
        int m = select(cArr,0,cArr.length-1,k-1);
        return m;
    }

    public static int[] copyArray(int[] arr) {
        int[] res = new int[arr.length];
        for (int i = 0; i != res.length; i++) {
            res[i] = arr[i];
        }
        return res;
    }

    public static int select(int[] arr,int begin,int end,int i){
        if (begin==end){
            return arr[begin];
        }

        int p = mediaOfMedia(arr,begin,end);
        int[] pArr = partition(arr,begin,end,p);
        if (i>=pArr[0]&&i<=pArr[1]){
            return arr[i];
        }else if (i<pArr[0]){
            return select(arr,begin,pArr[0]-1,i);
        }else {
            return select(arr, pArr[1] + 1, end, i);
        }
    }

    //获取等于区的数
    public static int mediaOfMedia(int[] arr,int begin,int end){
        int num = end - begin + 1;
        int offset = num%5==0 ? 0 : 1;
        int[] mArr = new int[num/5+offset];
        for (int i =0;i<mArr.length;i++){
            int beginI = begin + i * 5;
            int endI = beginI + 4;
            mArr[i] =  getMedia(arr,beginI,Math.min(endI,end));
        }
        return select(mArr,0,mArr.length-1,mArr.length/2);
    }

    //partition过程
    public static int[] partition(int[] arr,int l,int r,int m){
        int less = l-1;
        int more = r+1;
        int cur = l;

        while (cur<more){
            if (arr[cur]>m){
                swap(arr,--more,cur);
            }else if (arr[cur]<m){
                swap(arr,++less,cur++);
            }else {
                cur++;
            }
        }
        return new int[]{less+1,more-1};
    }

    //获取中位数
    public static int getMedia(int[] arr,int begin,int end){
        insertSort(arr,begin,end);
        int sum = begin + end;
        int mid = (sum/2) + (sum%2);
        return arr[mid];
    }

    //插入排序
    public static void insertSort(int[] arr,int begin,int end){
        //有begin end 做想定条件的时候注意循环条件
        for (int i = begin+1;i<end+1;i++){
            for (int j = i -1;j>=begin&&arr[j]>arr[j+1];j--){
                swap(arr,j,j+1);
            }
        }
    }

    public static void swap(int[] arr, int index1, int index2) {
        int tmp = arr[index1];
        arr[index1] = arr[index2];
        arr[index2] = tmp;
    }
}
