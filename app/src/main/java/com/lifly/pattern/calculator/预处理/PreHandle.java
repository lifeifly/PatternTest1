package com.lifly.pattern.calculator.预处理;

import java.util.Arrays;

/**
 * 预处理结构
 */
public class PreHandle {
    /**
     * 牛牛有一些排成一行的正方形，每个正方形已经被染成红色或绿色，
     * 牛牛现在可以选择任意一个正方形然后用这两种颜色的任意一种进行染色，这个正方形将会被覆盖
     * 牛牛的目标是在完成染色之后，每一个红色R都比每个绿色G距离最左侧近
     * 牛牛想知道它最少需要涂染几个正方形
     * 示例：s=RGRGR
     * 涂染后变成RRRGG满足要求了，涂染的个数为2，没有比这个更好的方案了
     */
    public static int renctangleVoilent(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        int a = Integer.MAX_VALUE;
        for (int i = 0; i <= s.length(); i++) {
            a = Math.min(a, every(i, s));
        }
        return a;
    }

    /**
     * @param l 左侧R的个数
     *          RG   RGRRRG
     */
    private static int every(int l, String s) {
        char[] chs = s.toCharArray();
        int paintCount = 0;
        for (int i = 0; i < chs.length; i++) {
            if (l == 0) {
                if (chs[i] != 'G') {
                    paintCount++;
                }
            } else if (l == chs.length) {
                if (chs[i] != 'R') {
                    paintCount++;
                }
            } else {
                if (i < l && chs[i] != 'R') {
                    paintCount++;
                } else if (i >= l && chs[i] != 'G') {
                    paintCount++;
                }
            }
        }
        return paintCount;
    }

    /**
     * 预处理
     *
     * @return
     */
    public static int rectanglePreHandle(String s) {
        char[] chs = s.toCharArray();
        int n = s.length();
        //记录0-i位置有对少个G
        int[] gArr = new int[n];
        //记录i-n-1位置有几个R
        int[] rArr = new int[n];

        //遍历
        int gCount = 0;
        for (int i = 0; i < n; i++) {
            if (chs[i] == 'G') {
                gArr[i] = ++gCount;
            } else {
                gArr[i] = gCount;
            }
        }
        int rCount = 0;
        for (int i = n - 1; i >= 0; i--) {
            if (chs[i] == 'R') {
                rArr[i] = ++rCount;
            } else {
                rArr[i] = rCount;
            }
        }
        //以左侧R的个数依次枚举
        int ans = Integer.MAX_VALUE;
        for (int i = 0; i <= n; i++) {
            if (i == 0) {//找出0-n-1位置的R的个数
                ans = Math.min(ans, rArr[0]);
            } else if (i == n) {//找出0-n-1位置的G的个数
                ans = Math.min(ans, gArr[n - 1]);
            } else {//找出0-i-1的G的个数和i-n-1的R的个数之和
                ans = Math.min(ans, gArr[i - 1] + rArr[i]);
            }
        }
        return ans;
    }

    /**
     * 给定一个矩阵N*M，只有0和1两种值，返回边框全是1的最大正方形的边长长度
     * 例如
     * 01111
     * 01001
     * 01001
     * 01111
     * 01011
     * 其中最大全是1的正方形大小为4*4，所以返回4
     */
    public static int maxRectangle(int[][] matrix) {
        int N = matrix.length;
        int M = matrix[0].length;

        //用于查询包括该点右侧有多少个连续的1
        int[][] right = new int[N][M];
        //用于查询包括该点下侧有多少个连续的1
        int[][] down = new int[N][M];
        for (int row = 0; row < N; row++) {
            for (int col = M - 1; col >= 0; col--) {
                if (matrix[row][col] == 0) {
                    right[row][col] = 0;
                } else {
                    right[row][col] = col + 1 < M ? 1 + right[row][col + 1] : 1;
                }
            }
        }

        for (int col = 0; col < M; col++) {
            for (int row = N - 1; row >= 0; row--) {
                if (matrix[row][col] == 0) {
                    down[row][col] = 0;
                } else {
                    down[row][col] = row + 1 < N ? 1 + down[row + 1][col] : 1;
                }
            }
        }
//        01111
//        01001
//        01001
//        01111
//        01011
        int ans = 0;
        //枚举左边界点的位置
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                //枚举边长
                for (int k = 1; k <= Math.min(N - i, M - j); k++) {
                    //验证以i、j为左边界点变成为k的正方形是不是边框都是1
                    //先确定该点右侧有没有大于等于边长的1
                    if (right[i][j] < k) {
                        continue;
                    }
                    //在确定该点下册有没有大于等于边长个数的1
                    if (down[i][j] < k) {
                        continue;
                    }
                    //在确定该点下侧的点的右侧有没有大于等于边长个数的1
                    if (right[i + k - 1][j] < k) {
                        continue;
                    }
                    //在确定该点右侧的点的下侧侧有没有大于等于边长个数的1
                    if (down[i][j + k - 1] < k) {
                        continue;
                    }
                    ans = Math.max(ans, k);
                }
            }
        }
        return ans;
    }

    /**
     * 给定一个函数f，可以a~b之间的数字等概率返回一个，请加工过出c~d的数字等概率返回一个的函数g
     *
     */
    public static int f() {
        return (int) (Math.random() * 5 + 1);
    }

    /**
     * 给定一个函数f，等概率返回0和1
     */
    public static int r01() {
        int res = 0;
        do {
            res = f();
        } while (res == 3);
        return res < 3 ? 0 : 1;
    }

    /**
     * 给定一个函数f，可以1~5的数字等概率返回一个，请加工出1~7的数字等概率返回一个的函数g
     */
    public static int g() {
        int res = 0;
        do {
            res = (r01() << 2) + (r01() << 1) + r01();
        } while (res == 7);
        return res + 1;
    }

    /**
     * 给定一个函数f以p概率返回0，以1-p概率返回1，请加工出等概率返回0和1的函数g
     * 返回0 1为0 返回10为1，两个的概率都是（1-p）p
     */
    public static int g1() {
        int res = 0;
        do {
            res = (r01() << 2) + (r01() << 1) + r01();
        } while (res == 7);
        return res + 1;
    }

    public static void main(String[] args) {
        int[][] s = {
                {0, 1, 1, 1, 1},
                {0, 1, 0, 0, 1},
                {0, 1, 0, 0, 1},
                {0, 1, 1, 1, 1},
                {0, 1, 0, 1, 1}
        };
        System.out.println(maxRectangle(s));
    }
}
