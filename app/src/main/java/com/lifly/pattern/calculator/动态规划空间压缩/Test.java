package com.lifly.pattern.calculator.动态规划空间压缩;

public class Test {
    /**
     * 求两个字符串的最长公共子串
     *
     * @param str1
     * @param str2
     * @return
     */
    public static String maxSubBetween(String str1, String str2) {
        if (str1 == null || str1.length() == 0 || str2 == null || str2.length() == 0) {
            return null;
        }
        char[] chs1 = str1.toCharArray();
        char[] chs2 = str2.toCharArray();
        int len1 = chs1.length;
        int len2 = chs2.length;
        //(i,j)代表最长公共子串必须以str1的i元素和str2的j元素结尾，最大的子串长度
        int[][] dp = new int[len1][len2];
        //初始化第0行
        for (int i = 0; i < len2; i++) {
            //此时最长公共子串只有一个长度，只需判断是否和chs1的0号字符相同即可
            dp[0][i] = chs1[0] == chs2[i] ? 1 : 0;
        }
        //同理初始化第0列
        for (int i = 1; i < len1; i++) {
            dp[i][0] = chs1[i] == chs2[0] ? 1 : 0;
        }
        int maxLen = 0;
        int end = -1;
        for (int i = 1; i < len1; i++) {
            for (int j = 1; j < len2; j++) {
                //只需判断i位置的元素和j位置的元素是否相同，不相同代表没有公共子串，相同则就是（i-1,j-1）的问题
                if (chs1[i] == chs2[j]) {
                    dp[i][j] = 1 + dp[i - 1][j - 1];
                    if (dp[i][j] > maxLen) {
                        maxLen = dp[i][j];
                        end = i;
                    }
                }
            }
        }
        return str1.substring(end - maxLen + 1, end + 1);
    }

    //空间压缩
    public static String maxSubBetween1(String str1, String str2) {
        if (str1 == null || str1.length() == 0 || str2 == null || str2.length() == 0) {
            return null;
        }
        char[] chs1 = str1.toCharArray();
        char[] chs2 = str2.toCharArray();
        int len1 = chs1.length;
        int len2 = chs2.length;
        int row = 0;
        int col = len2 - 1;
        //用于记录每个（i，j）的长度
        int t = 0;

        int maxLen = 0;
        int end = 0;
        //枚举行
        while (row < len1) {
            int i = row;
            int j = col;
            int len = 0;
            while (i < len1 && j < len2) {
                if (chs1[i] == chs2[j]) {
                    len++;
                } else {
                    len = 0;
                }
                if (len > maxLen) {
                    maxLen = len;
                    end = i;
                }
                i++;
                j++;
            }
            if (col > 0) {
                col--;
            } else {
                row++;
            }
        }
        return str1.substring(end - maxLen + 1, end + 1);
    }

    public static void main(String[] args) {
        String str1 = "abc123";
        String str2 = "bc";
        System.out.println(maxSubBetween(str1, str2));
    }
}
