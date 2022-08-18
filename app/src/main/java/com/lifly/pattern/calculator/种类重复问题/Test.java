package com.lifly.pattern.calculator.种类重复问题;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class Test {
    /**
     * 给定String数组arr，规定两个字符串的字符种类相同并且数量一样，这两个字符串就是一个种类
     * 求有多少个种类
     */
    public static int type(String[] arr) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < arr.length; i++) {
            boolean[] dp = new boolean[26];
            char[] chs = arr[i].toCharArray();
            for (int j = 0; j < chs.length; j++) {
                dp[chs[j] - 'a'] = true;
            }
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < dp.length; j++) {
                sb.append(dp[j] ? (char) ('a' + j) : "");
            }
            set.add(sb.toString());
        }
        return set.size();
    }

    //优化，位运算加速
    public static int type1(String[] arr) {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < arr.length; i++) {
            boolean[] dp = new boolean[26];
            char[] chs = arr[i].toCharArray();
            int key = 0;
            for (int j = 0; j < chs.length; j++) {
                key |= (1 << (chs[j] - 'a'));
            }
            set.add(key);
        }
        return set.size();
    }

    /**
     * 给定一个非负数组arr，和一个正数m
     * 返回arr的所有子序列中累加和%m之后的最大值
     *
     * @return
     */
    //暴力解，收集所有子序列累加和，遍历得出%m的最大值
    public static int max1(int[] arr, int m) {
        HashSet<Integer> set = new HashSet<>();
        process1(arr, 0, 0, set);
        Iterator<Integer> iterator = set.iterator();
        int max = 0;
        while (iterator.hasNext()) {
            max = Math.max(max, iterator.next() % m);
        }
        return max;
    }

    /**
     * 递归决定每个值要还是不要，最终将累加和放到set中
     *
     * @param arr
     * @param i
     * @param set
     */
    private static void process1(int[] arr, int i, int preTotal, HashSet<Integer> set) {
        //basecase
        if (i == arr.length) {
            set.add(preTotal);
        }
        //如果不要
        process1(arr, i + 1, preTotal, set);
        //如果要
        process1(arr, i + 1, preTotal + arr[i], set);
    }

    //优化1：动态规划,适用总累加和小的样本
    public static int max2(int[] arr, int m) {
        int total = 0;
        for (int i = 0; i < arr.length; i++) {
            total += arr[i];
        }
        //dp[i][j]表示arr[0..i]任选任意个能否组成累加和为j
        boolean[][] dp = new boolean[arr.length][total + 1];
        //第0列都可以满足累加和为0
        for (int i = 0; i < arr.length; i++) {
            dp[i][0] = true;
        }
        //第一行
        for (int i = 1; i <= total; i++) {
            if (i % arr[0] == 0) {
                dp[0][i] = true;
            }
        }
        for (int row = 1; row < arr.length; row++) {
            for (int col = 1; col <= total; col++) {
                //可能性1：不要当前元素，只靠前row-1个元素凑成累加和为col
                boolean r1 = dp[row - 1][col];
                //可能性2：要当前元素，前row-1个元素凑成累加和为col-row
                boolean r2 = dp[row - 1][col - row];

                dp[row][col] = r1 || r2;
            }
        }
        //只用遍历dp最后一行就行了
        int max = 0;
        for (int i = 0; i <= total; i++) {
            max = Math.max(max, i % m);
        }
        return max;
    }

    //优化2：动态规划，适用于样本累加和远远大于m
    public static int max3(int[] arr, int m) {
        int n = arr.length;
        //dp[i][j]表示arr[0..i]能否达成任意累加和%m
        boolean[][] dp = new boolean[n][m];
        //第0列都可以达成
        for (int i = 0; i < n; i++) {
            dp[i][0] = true;
        }
        //第一行
        for (int i = 1; i < m; i++) {
            if (arr[0] % m == i) {
                dp[0][i] = true;
            }
        }
        for (int row = 1; row < n; row++) {
            for (int col = 1; col < m; col++) {
                //可能性1：不要当前元素，只靠前row-1个元素累加和达成%m为col
                boolean r1=dp[row-1][col];
                //可能性2，3：自身%m，靠前row-1个元素累加和%m补上结果为col-自身%m或
                int cur=arr[row]%m;
                boolean r2=false;
                //两个分支只会中一个
                if (col-cur>=0){
                    r2=dp[row-1][col-cur];
                }
                if (col+m-cur<m){
                    r2=dp[row-1][col+m-cur];
                }
                dp[row][col]=r1||r2;
            }
        }
        int max=0;
        for (int i = m-1; i >=0 ; i--) {
            if (dp[n-1][i]){
                max=i;
                break;
            }
        }
        return max;
    }
    //优化3：适用于累加和很大，m很大，但是arr的长度很短
    public static int max4(int[] arr,int m){
        if (arr.length==1){
            return arr[0]%m;
        }
        int mid=arr.length-1/2;
        TreeSet<Integer> sortSet1=new TreeSet<>();
        process4(arr,0,0,mid,m,sortSet1);
        TreeSet<Integer> sortSet2=new TreeSet<>();
        process4(arr,mid+1,0,arr.length-1,m,sortSet2);
        int ans=0;
        for(Integer i:sortSet1){
            ans=Math.max(ans,i+sortSet2.floor(m-1-i));
        }
        return ans;
    }

    private static void process4(int[] arr, int index, int sum, int end, int m, TreeSet<Integer> set) {
        if (index==end+1){
            set.add(sum%m);
        }else {
            process4(arr,index+1,sum,end,m,set);
            process4(arr,index+1,sum+arr[index],end,m,set);
        }
    }

    public static void main(String[] args) {
        String[] arr = {"abc", "bca", "cad"};
        System.out.println(type(arr));
    }
}
