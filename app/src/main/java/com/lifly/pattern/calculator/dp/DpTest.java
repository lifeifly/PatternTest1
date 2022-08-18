package com.lifly.pattern.calculator.dp;

public class DpTest {
    /**
     * 假设有排成一行的N个位置，记为1~N，N一定大于或等于2
     * 开始时机器人在其中一个位置上（M一定时1~N的一个）
     * 如果机器人来到1的位置，那么下一步只能往右来到2位置
     * 如果机器人来到N位置，那么下一步只能往左来到N-1位置
     * 如果机器人来到中间位置，那么下一步可以往左走或者往右走
     * 规定机器人必须走K步，最终来到P位置（也是1~N中的一个）的方法有多少种
     * 给定参数N\M\K\P
     */
    public static int robotVoilent(int N, int M, int K, int P) {
        if (N < 2 || K < 1 || M > N || P > 1) {
            return 0;
        }
        return walk(N, M, K, P);
    }

    /**
     * @param n 1~n固定
     * @param m 当前来到的位置
     * @param k 还剩必须走几步
     * @param p 最终需要到达的位置，不变
     * @return
     */
    private static int walk(int n, int m, int k, int p) {
        if (k == 0) {
            if (m != p) {
                //最后没到p
                return 0;
            } else {
                //最后来到p
                return 1;
            }
        }
        if (m == 1) {
            //只能往右走一步,把结果的获取交给下一个
            return walk(n, m + 1, k - 1, p);//此时只有一种情况，直接返回后续结果
        }
        if (m == n) {
            //只能往左走一步,把结果的获取交给下一个
            return walk(n, n - 1, k - 1, p);//此时只有一种情况，直接返回后续结果
        }
        //此时中间位置
        //往左走一步
        int toLeft = walk(n, m - 1, k - 1, p);
        //往右走一步
        int toRight = walk(n, m + 1, k - 1, p);
        return toLeft + toRight;//此时有两种情况的结果，所以相加
    }

    /**
     * 假设有排成一行的N个位置，记为1~N，N一定大于或等于2
     * 开始时机器人在其中一个位置上（M一定时1~N的一个）
     * 如果机器人来到1的位置，那么下一步只能往右来到2位置
     * 如果机器人来到N位置，那么下一步只能往左来到N-1位置
     * 如果机器人来到中间位置，那么下一步可以往左走或者往右走
     * 规定机器人必须走K步，最终来到P位置（也是1~N中的一个）的方法有多少种
     * 给定参数N\M\K\P
     * <p>
     * 这种就是动态规划，就是设置缓存，记忆化搜索
     */
    public static int robotCache(int N, int M, int K, int P) {
        if (N < 2 || K < 1 || M > N || P > 1) {
            return 0;
        }
        int[][] dp = new int[N + 1][K + 1];
        for (int i = 0; i < dp.length; i++) {
            for (int j = 0; j < dp[0].length; j++) {
                dp[i][j] = -1;
            }
        }
        return walkCache(N, M, K, P, dp);
    }

    /**
     * @param n 1~n固定
     * @param m 当前来到的位置
     * @param k 还剩必须走几步
     * @param p 最终需要到达的位置，不变
     * @return
     */
    private static int walkCache(int n, int m, int k, int p, int[][] dp) {
        if (dp[m][k] != -1) {
            return dp[m][k];
        }
        if (k == 0) {
            if (m != p) {
                //最后没到p
                //先加缓存
                dp[m][k] = 0;
                return 0;
            } else {
                //最后来到p
                dp[m][k] = 1;
                return 1;
            }
        }
        if (m == 1) {
            //只能往右走一步,把结果的获取交给下一个
            dp[m][k] = walk(n, m + 1, k - 1, p);
            return dp[m][k];//此时只有一种情况，直接返回后续结果
        }
        if (m == n) {
            //只能往左走一步,把结果的获取交给下一个
            dp[m][k] = walk(n, n - 1, k - 1, p);
            return dp[m][k];//此时只有一种情况，直接返回后续结果
        }
        //此时中间位置
        //往左走一步
        int toLeft = walk(n, m - 1, k - 1, p);
        //往右走一步
        int toRight = walk(n, m + 1, k - 1, p);
        dp[m][k] = toLeft + toRight;
        return dp[m][k];//此时有两种情况的结果，所以相加
    }

    /**
     * 动态规划,只有m和k是可变的，创建二维表可以得到规律
     */
    public static int robotDp(int N, int M, int K, int P) {
        //先创建一个二维表,行代表M，列代表K
        int[][] table = new int[N + 1][K + 1];
        //先根据上述basecase进行初始化,K=0时
        for (int i = 0; i < table.length; i++) {
            table[i][0] = i == P ? 1 : 0;
        }
        //在对过程进行推理
        //中间情况，M在1~N之间，依赖与M+1/K-1和M-1/K-1之和
        for (int i = 2; i < N; i++) {
            for (int k = 1; k < K + 1; k++) {
                table[i][k] = table[i - 1][k - 1] + table[i + 1][k - 1];
            }
        }

        //M==1时，只能往右走一步，所以依赖M+1\K-1
        for (int i = 1; i < K + 1; i++) {
            table[1][i] = table[0][i - 1];
        }
        //M==N时，只能往左走,所以依赖M-1/K-1
        for (int i = 1; i < K + 1; i++) {
            table[N][i] = table[N - 1][i - 1];
        }

        return table[M][K];
    }

    public static void main(String[] args) {
        int n=5;
        int m=2;
        int k=3;
        int p=3;
        int r1=walk(n,m,k,p);
        int r2=robotDp(n,m,k,p);
        System.out.println(r1+"/"+r2);
    }
}
